package com.factorcraft.multiblock;

import com.factorcraft.module.network.NetworkConfig;
import com.factorcraft.module.technology.machine.MachineBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * R3.3 机器状态广播器
 * 
 * 负责向周围玩家广播机器状态变化
 * 支持状态变化检测和增量更新
 */
public class MachineStateBroadcaster {
    
    private static final Identifier PACKET_ID = Identifier.of("factorcraft", "machine_state_broadcast");
    private static final Identifier BATCH_PACKET_ID = Identifier.of("factorcraft", "machine_state_batch");
    
    static {
        // 注册网络包
        PayloadTypeRegistry.playS2C().register(MachineStatePacket.ID, MachineStatePacket.CODEC);
        PayloadTypeRegistry.playS2C().register(MachineStateBatch.ID, MachineStateBatch.CODEC);
    }
    
    /**
     * 机器状态变化时广播给周围玩家
     * 
     * @param world 世界
     * @param pos 机器位置
     * @param machine 机器实体
     */
    public static void broadcastStateChange(@NotNull ServerWorld world, 
                                            @NotNull BlockPos pos, 
                                            @NotNull MachineBlockEntity machine) {
        List<ServerPlayerEntity> nearbyPlayers = getNearbyPlayers(world, pos);
        
        if (nearbyPlayers.isEmpty()) {
            return;
        }
        
        // 构建状态包
        MachineStatePacket packet = buildStatePacket(pos, machine);
        if (packet == null) {
            return;
        }
        
        // 广播给所有附近玩家
        for (ServerPlayerEntity player : nearbyPlayers) {
            ServerPlayNetworking.send(player, packet);
        }
    }
    
    /**
     * 玩家进入范围时同步机器状态
     * 
     * @param player 玩家
     * @param world 世界
     * @param center 中心位置
     */
    public static void syncForPlayer(@NotNull ServerPlayerEntity player, 
                                      @NotNull ServerWorld world, 
                                      @NotNull Vec3d center) {
        int syncRadius = (int) NetworkConfig.MACHINE_SYNC_RADIUS;
        int blockRadius = syncRadius * 16; // chunk 转 block
        
        List<MachineStatePacket> packets = new ArrayList<>();
        
        // 收集范围内所有机器 - 遍历区块而非玩家
        int chunkRadius = syncRadius;
        int centerX = (int) center.x >> 4;
        int centerZ = (int) center.z >> 4;
        
        for (int cx = centerX - chunkRadius; cx <= centerX + chunkRadius; cx++) {
            for (int cz = centerZ - chunkRadius; cz <= centerZ + chunkRadius; cz++) {
                var chunk = world.getChunk(cx, cz);
                for (var entry : chunk.getBlockEntities().entrySet()) {
                    if (entry.getValue() instanceof MachineBlockEntity machine) {
                        MachineStatePacket packet = buildStatePacket(entry.getKey(), machine);
                        if (packet != null) {
                            packets.add(packet);
                        }
                    }
                }
            }
        }
        
        // 批量发送
        if (!packets.isEmpty()) {
            MachineStateBatch batch = new MachineStateBatch(packets);
            ServerPlayNetworking.send(player, batch);
        }
    }
    
    /**
     * 构建机器状态数据包
     */
    @Nullable
    private static MachineStatePacket buildStatePacket(@NotNull BlockPos pos, @NotNull MachineBlockEntity machine) {
        return new MachineStatePacket(
            pos,
            machine.getEnergyStored(),
            machine.getMaxEnergy(),
            machine.getFactorStorage(),
            machine.getMaxFactorStorage(),
            false // 暂不支持 isActive 方法
        );
    }
    
    /**
     * 获取周围玩家列表
     */
    @NotNull
    private static List<ServerPlayerEntity> getNearbyPlayers(@NotNull ServerWorld world, @NotNull BlockPos pos) {
        List<ServerPlayerEntity> players = new ArrayList<>();
        double radius = NetworkConfig.MACHINE_SYNC_RADIUS * 16; // chunk 转 block
        
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.getBlockPos().isWithinDistance(pos, radius)) {
                players.add(player);
            }
        }
        
        return players;
    }
    
    /**
     * 机器状态数据包（record 类实现）
     */
    public record MachineStatePacket(
        BlockPos pos,
        int energy,
        int maxEnergy,
        double factorAmount,
        double maxFactor,
        boolean isActive
    ) implements CustomPayload {
        
        public static final Id<MachineStatePacket> ID = new Id<>(PACKET_ID);
        
        public static final PacketCodec<PacketByteBuf, MachineStatePacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, MachineStatePacket::pos,
            PacketCodecs.INTEGER, MachineStatePacket::energy,
            PacketCodecs.INTEGER, MachineStatePacket::maxEnergy,
            PacketCodecs.DOUBLE, MachineStatePacket::factorAmount,
            PacketCodecs.DOUBLE, MachineStatePacket::maxFactor,
            PacketCodecs.BOOLEAN, MachineStatePacket::isActive,
            MachineStatePacket::new
        );
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    /**
     * 机器状态批次包（record 类实现）
     */
    public record MachineStateBatch(
        List<MachineStatePacket> packets
    ) implements CustomPayload {
        
        public static final Id<MachineStateBatch> ID = new Id<>(BATCH_PACKET_ID);
        
        public static final PacketCodec<PacketByteBuf, MachineStateBatch> CODEC = new PacketCodec<>() {
            @Override
            public MachineStateBatch decode(PacketByteBuf buf) {
                int size = buf.readVarInt();
                List<MachineStatePacket> packets = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    packets.add(MachineStatePacket.CODEC.decode(buf));
                }
                return new MachineStateBatch(packets);
            }
            
            @Override
            public void encode(PacketByteBuf buf, MachineStateBatch batch) {
                buf.writeVarInt(batch.packets.size());
                for (MachineStatePacket packet : batch.packets) {
                    MachineStatePacket.CODEC.encode(buf, packet);
                }
            }
        };
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
