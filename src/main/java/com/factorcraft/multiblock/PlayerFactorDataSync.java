package com.factorcraft.multiblock;

import com.factorcraft.factor.FactorType;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.network.NetworkConfig;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * R3.3 玩家 Factor 数据同步管理器
 * 
 * 负责在玩家加入游戏或切换维度时同步其周围的 Factor 数据
 * 支持增量同步和批量传输优化
 */
public class PlayerFactorDataSync {
    
    private static final Identifier PACKET_ID = Identifier.of("factorcraft", "player_factor_sync");
    
    static {
        // 注册网络包
        PayloadTypeRegistry.playS2C().register(FactorDataPacket.ID, FactorDataPacket.CODEC);
    }
    
    /**
     * 玩家加入游戏或切换维度时同步 Factor 数据
     * 
     * @param player 玩家
     * @param world 世界
     */
    public static void syncForPlayer(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world) {
        int syncRadius = (int) NetworkConfig.MACHINE_SYNC_RADIUS;
        
        Map<String, Float> factorData = new HashMap<>();
        
        // 收集玩家周围区块的 Factor 数据
        ChunkPos playerChunk = new ChunkPos(player.getBlockPos());
        
        for (int x = -syncRadius; x <= syncRadius; x++) {
            for (int z = -syncRadius; z <= syncRadius; z++) {
                ChunkPos chunkPos = new ChunkPos(playerChunk.x + x, playerChunk.z + z);
                
                ChunkFactorState state = ChunkFactorManager.getOrCreateState(world, chunkPos);
                if (state != null) {
                    // 使用当前区块的总浓度
                    double concentration = state.getCurrentConcentration();
                    if (concentration > 0.001) {
                        // 使用简化的 key 格式
                        String key = chunkPos.x + "," + chunkPos.z;
                        factorData.put(key, (float) concentration);
                    }
                }
            }
        }
        
        // 批量发送
        if (!factorData.isEmpty()) {
            FactorDataPacket packet = new FactorDataPacket(factorData);
            ServerPlayNetworking.send(player, packet);
        }
    }
    
    /**
     * 从 NBT 加载玩家 Factor 进度
     */
    public static void loadPlayerProgress(@NotNull ServerPlayerEntity player, @NotNull NbtCompound nbt) {
        if (nbt.contains("FactorProgress", NbtElement.LIST_TYPE)) {
            NbtList progressList = nbt.getList("FactorProgress", NbtElement.COMPOUND_TYPE);
            
            for (int i = 0; i < progressList.size(); i++) {
                NbtCompound progressNbt = progressList.getCompound(i);
                String factorId = progressNbt.getString("FactorId");
                float progress = progressNbt.getFloat("Progress");
                
                // 可以在这里处理玩家 Factor 进度数据
            }
        }
    }
    
    /**
     * 保存玩家 Factor 进度到 NBT
     */
    public static void savePlayerProgress(@NotNull ServerPlayerEntity player, @NotNull NbtCompound nbt) {
        NbtList progressList = new NbtList();
        
        // 保存玩家 Factor 进度数据
        // 这里可以根据需要保存玩家的 Factor 相关进度
        
        nbt.put("FactorProgress", progressList);
    }
    
    /**
     * Factor 数据包（record 类实现）
     */
    public record FactorDataPacket(
        Map<String, Float> factorData
    ) implements CustomPayload {
        
        public static final Id<FactorDataPacket> ID = new Id<>(PACKET_ID);
        
        public static final PacketCodec<PacketByteBuf, FactorDataPacket> CODEC = new PacketCodec<>() {
            @Override
            public FactorDataPacket decode(PacketByteBuf buf) {
                int size = buf.readVarInt();
                Map<String, Float> factorData = new HashMap<>(size);
                for (int i = 0; i < size; i++) {
                    String key = buf.readString();
                    float value = buf.readFloat();
                    factorData.put(key, value);
                }
                return new FactorDataPacket(factorData);
            }
            
            @Override
            public void encode(PacketByteBuf buf, FactorDataPacket packet) {
                buf.writeVarInt(packet.factorData.size());
                for (Map.Entry<String, Float> entry : packet.factorData.entrySet()) {
                    buf.writeString(entry.getKey());
                    buf.writeFloat(entry.getValue());
                }
            }
        };
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
