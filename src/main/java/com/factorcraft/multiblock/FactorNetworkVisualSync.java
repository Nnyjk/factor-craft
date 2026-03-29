package com.factorcraft.multiblock;

import com.factorcraft.factor.FactorType;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.network.NetworkConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * R3.3 Factor 网络可视化同步
 * 
 * 负责同步 Factor 网络的可视化数据给客户端
 * 支持浓度梯度可视化和网络连线渲染
 */
public class FactorNetworkVisualSync {
    
    private static final Identifier PACKET_ID = Identifier.of("factorcraft", "factor_visual_sync");
    
    static {
        // 注册网络包
        PayloadTypeRegistry.playS2C().register(VisualSyncPayload.ID, VisualSyncPayload.CODEC);
    }
    
    /**
     * 同步 Factor 网络可视化数据给玩家
     * 
     * @param player 玩家
     * @param world 世界
     * @param center 中心位置
     */
    public static void syncVisualData(@NotNull ServerPlayerEntity player, 
                                       @NotNull ServerWorld world, 
                                       @NotNull Vec3d center) {
        int syncRadius = (int) NetworkConfig.MACHINE_SYNC_RADIUS;
        
        VisualDataBatch batch = new VisualDataBatch();
        
        // 收集周围 chunk 的可视化数据
        ChunkPos centerChunk = new ChunkPos((int) center.getX() >> 4, (int) center.getZ() >> 4);
        
        for (int dx = -syncRadius; dx <= syncRadius; dx++) {
            for (int dz = -syncRadius; dz <= syncRadius; dz++) {
                ChunkPos pos = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                VisualDataPacket packet = buildVisualPacket(world, pos);
                if (packet != null && !packet.isEmpty()) {
                    batch.addPacket(packet);
                }
            }
        }
        
        // 发送数据
        if (!batch.isEmpty()) {
            sendToPlayer(player, batch);
        }
    }
    
    /**
     * 增量更新 Factor 浓度变化
     * 
     * @param player 玩家
     * @param world 世界
     * @param changedChunks 变化的 chunk 列表
     */
    public static void syncIncrementalUpdate(@NotNull ServerPlayerEntity player, 
                                              @NotNull ServerWorld world, 
                                              @NotNull List<ChunkPos> changedChunks) {
        VisualDataBatch batch = new VisualDataBatch();
        
        for (ChunkPos pos : changedChunks) {
            VisualDataPacket packet = buildVisualPacket(world, pos);
            if (packet != null && !packet.isEmpty()) {
                batch.addPacket(packet);
            }
        }
        
        if (!batch.isEmpty()) {
            sendToPlayer(player, batch);
        }
    }
    
    /**
     * 构建可视化数据包
     */
    @NotNull
    private static VisualDataPacket buildVisualPacket(@NotNull ServerWorld world, @NotNull ChunkPos pos) {
        VisualDataPacket packet = new VisualDataPacket(pos);
        
        // 获取区块 Factor 状态
        var state = ChunkFactorManager.getOrCreateState(world, pos);
        if (state == null) {
            return packet;
        }
        
        // 获取当前浓度
        double concentration = state.getCurrentConcentration();
        if (concentration > 0.001) {
            // 简化处理：将总浓度分配给所有 Factor 类型
            FactorType[] types = FactorType.values();
            if (types.length > 0) {
                float perTypeConcentration = (float) (concentration / types.length);
                for (FactorType type : types) {
                    packet.addFactorConcentration(Identifier.of("factorcraft", type.asString()), perTypeConcentration);
                }
            }
        }
        
        // 收集相邻 chunk 的浓度梯度（用于可视化连线）
        packet.addGradientData(pos, world);
        
        return packet;
    }
    
    /**
     * 发送可视化数据到玩家
     */
    private static void sendToPlayer(@NotNull ServerPlayerEntity player, @NotNull VisualDataBatch batch) {
        VisualSyncPayload payload = new VisualSyncPayload(batch.toPacketByteBuf());
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 可视化数据包 - 单个 chunk
     */
    public static class VisualDataPacket {
        public final ChunkPos pos;
        private final Map<Identifier, Float> concentrations;
        private final Map<Direction, Float> gradients;
        
        public VisualDataPacket(@NotNull ChunkPos pos) {
            this.pos = pos;
            this.concentrations = new HashMap<>();
            this.gradients = new HashMap<>();
        }
        
        public void addFactorConcentration(@NotNull Identifier type, float concentration) {
            concentrations.put(type, concentration);
        }
        
        public void addGradientData(@NotNull ChunkPos center, @NotNull ServerWorld world) {
            // 计算四个方向的浓度梯度
            gradients.put(Direction.NORTH, calculateGradient(world, center, Direction.NORTH));
            gradients.put(Direction.SOUTH, calculateGradient(world, center, Direction.SOUTH));
            gradients.put(Direction.EAST, calculateGradient(world, center, Direction.EAST));
            gradients.put(Direction.WEST, calculateGradient(world, center, Direction.WEST));
        }
        
        private float calculateGradient(@NotNull ServerWorld world, 
                                         @NotNull ChunkPos center, 
                                         @NotNull Direction dir) {
            ChunkPos neighbor = dir.getNeighbor(center);
            ChunkFactorState centerState = ChunkFactorManager.getOrCreateState(world, center);
            ChunkFactorState neighborState = ChunkFactorManager.getOrCreateState(world, neighbor);
            float centerConc = (float) (centerState != null ? centerState.getCurrentConcentration() : 0);
            float neighborConc = (float) (neighborState != null ? neighborState.getCurrentConcentration() : 0);
            return centerConc - neighborConc;
        }
        
        public boolean isEmpty() {
            return concentrations.isEmpty();
        }
        
        public void write(@NotNull PacketByteBuf buf) {
            buf.writeInt(pos.x);
            buf.writeInt(pos.z);
            buf.writeVarInt(concentrations.size());
            for (Map.Entry<Identifier, Float> entry : concentrations.entrySet()) {
                buf.writeIdentifier(entry.getKey());
                buf.writeFloat(entry.getValue());
            }
            buf.writeVarInt(gradients.size());
            for (Map.Entry<Direction, Float> entry : gradients.entrySet()) {
                buf.writeVarInt(entry.getKey().ordinal());
                buf.writeFloat(entry.getValue());
            }
        }
        
        public static @NotNull VisualDataPacket read(@NotNull PacketByteBuf buf) {
            int x = buf.readInt();
            int z = buf.readInt();
            ChunkPos pos = new ChunkPos(x, z);
            VisualDataPacket packet = new VisualDataPacket(pos);
            
            int concCount = buf.readVarInt();
            for (int i = 0; i < concCount; i++) {
                Identifier type = buf.readIdentifier();
                float concentration = buf.readFloat();
                packet.addFactorConcentration(type, concentration);
            }
            
            int gradientCount = buf.readVarInt();
            for (int i = 0; i < gradientCount; i++) {
                Direction dir = Direction.values()[buf.readVarInt()];
                float gradient = buf.readFloat();
                packet.gradients.put(dir, gradient);
            }
            
            return packet;
        }
    }
    
    /**
     * 方向枚举
     */
    public enum Direction {
        NORTH(0, -1),
        SOUTH(0, 1),
        EAST(1, 0),
        WEST(-1, 0);
        
        private final int dx;
        private final int dz;
        
        Direction(int dx, int dz) {
            this.dx = dx;
            this.dz = dz;
        }
        
        public @NotNull ChunkPos getNeighbor(@NotNull ChunkPos pos) {
            return new ChunkPos(pos.x + dx, pos.z + dz);
        }
    }
    
    /**
     * 可视化数据批次
     */
    public static class VisualDataBatch {
        private final List<VisualDataPacket> packets;
        
        public VisualDataBatch() {
            this.packets = new ArrayList<>();
        }
        
        public void addPacket(@NotNull VisualDataPacket packet) {
            packets.add(packet);
        }
        
        public boolean isEmpty() {
            return packets.isEmpty();
        }
        
        public void write(@NotNull PacketByteBuf buf) {
            buf.writeVarInt(packets.size());
            for (VisualDataPacket packet : packets) {
                packet.write(buf);
            }
        }
        
        public @NotNull PacketByteBuf toPacketByteBuf() {
            PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
            write(buf);
            return buf;
        }
        
        public static @NotNull VisualDataBatch read(@NotNull PacketByteBuf buf) {
            int count = buf.readVarInt();
            VisualDataBatch batch = new VisualDataBatch();
            for (int i = 0; i < count; i++) {
                batch.addPacket(VisualDataPacket.read(buf));
            }
            return batch;
        }
    }
    
    /**
     * 可视化同步 Payload（record 类实现）
     */
    public record VisualSyncPayload(
        PacketByteBuf data
    ) implements CustomPayload {
        
        public static final Id<VisualSyncPayload> ID = new Id<>(PACKET_ID);
        
        public static final PacketCodec<PacketByteBuf, VisualSyncPayload> CODEC = new PacketCodec<>() {
            @Override
            public VisualSyncPayload decode(PacketByteBuf buf) {
                // 读取数据长度
                int length = buf.readVarInt();
                byte[] bytes = new byte[length];
                buf.readBytes(bytes);
                PacketByteBuf dataBuf = new PacketByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(bytes));
                return new VisualSyncPayload(dataBuf);
            }
            
            @Override
            public void encode(PacketByteBuf buf, VisualSyncPayload payload) {
                // 写入数据
                byte[] bytes = payload.data.array();
                buf.writeVarInt(bytes.length);
                buf.writeBytes(bytes);
            }
        };
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
