package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * 机器状态同步 Payload
 * 
 * 同步机器 BlockEntity 的核心状态到客户端
 * 
 * 优化功能:
 * - 条件同步：仅当状态变化时同步
 * - 频率限制：按配置冷却时间同步（默认 500ms）
 * - 状态哈希：使用哈希值快速检测状态变化
 * 
 * 支持所有机器类型：
 * - Extractor (提取器)
 * - Synthesizer (合成器)
 * - Consumer (消耗器)
 * - Cultivator (培育器)
 * - Breeder (繁殖器)
 * - Transmitter (传输器)
 */
public record MachineStateSyncPayload(
    BlockPos pos,
    String machineType,
    boolean isWorking,
    double progress,
    double factorStorage,
    double maxStorage,
    int energyStored,
    int maxEnergy
) implements CustomPayload {
    
    public static final CustomPayload.Id<MachineStateSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "machine_state_sync"));
    
    public static final PacketCodec<RegistryByteBuf, MachineStateSyncPayload> CODEC = 
        PacketCodec.of(MachineStateSyncPayload::write, MachineStateSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(machineType);
        buf.writeBoolean(isWorking);
        buf.writeDouble(progress);
        buf.writeDouble(factorStorage);
        buf.writeDouble(maxStorage);
        buf.writeInt(energyStored);
        buf.writeInt(maxEnergy);
    }
    
    private static MachineStateSyncPayload read(RegistryByteBuf buf) {
        return new MachineStateSyncPayload(
            buf.readBlockPos(),
            buf.readString(),
            buf.readBoolean(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readInt()
        );
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 计算状态哈希值
     */
    private int calculateStateHash() {
        int result = machineType.hashCode();
        result = 31 * result + Boolean.hashCode(isWorking);
        result = 31 * result + Double.hashCode(progress);
        result = 31 * result + Double.hashCode(factorStorage);
        result = 31 * result + Double.hashCode(maxStorage);
        result = 31 * result + Integer.hashCode(energyStored);
        result = 31 * result + Integer.hashCode(maxEnergy);
        return result;
    }
    
    /**
     * 条件同步机器状态
     * 
     * 仅当满足以下条件时才发送：
     * 1. 距离上次同步超过配置冷却时间
     * 2. 状态哈希值发生变化（如果启用状态变化检测）
     * 
     * @param player 目标玩家
     */
    public void conditionalSync(ServerPlayerEntity player) {
        int stateHash = calculateStateHash();
        
        // 检查是否应该同步
        if (!NetworkSyncTracker.shouldSyncMachine(pos, stateHash)) {
            return; // 跳过同步
        }
        
        // 发送同步包
        ServerPlayNetworking.send(player, this);
        
        // 记录已同步
        NetworkSyncTracker.markMachineSynced(pos, stateHash);
    }
    
    /**
     * 强制同步机器状态（无条件）
     * 
     * 用于玩家刚接近机器时的初始同步
     * 
     * @param player 目标玩家
     */
    public void forceSync(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this);
        NetworkSyncTracker.markMachineSynced(pos, calculateStateHash());
    }
    
    /**
     * 发送机器状态同步给指定玩家（传统方法，向后兼容）
     * 
     * @deprecated 请使用 {@link #conditionalSync} 或 {@link #forceSync}
     */
    @Deprecated
    public static void sendToPlayer(ServerPlayerEntity player, 
                                    BlockPos pos,
                                    String machineType,
                                    boolean isWorking,
                                    double progress,
                                    double factorStorage,
                                    double maxStorage,
                                    int energyStored,
                                    int maxEnergy) {
        MachineStateSyncPayload payload = new MachineStateSyncPayload(
            pos, machineType, isWorking, progress, 
            factorStorage, maxStorage, energyStored, maxEnergy
        );
        payload.forceSync(player);
    }
    
    /**
     * 创建构建器
     */
    public static Builder builder(BlockPos pos, String machineType) {
        return new Builder(pos, machineType);
    }
    
    public static class Builder {
        private final BlockPos pos;
        private final String machineType;
        private boolean isWorking = false;
        private double progress = 0.0;
        private double factorStorage = 0.0;
        private double maxStorage = 100.0;
        private int energyStored = 0;
        private int maxEnergy = 10000;
        
        public Builder(BlockPos pos, String machineType) {
            this.pos = pos;
            this.machineType = machineType;
        }
        
        public Builder working(boolean working) {
            this.isWorking = working;
            return this;
        }
        
        public Builder progress(double progress) {
            this.progress = progress;
            return this;
        }
        
        public Builder factorStorage(double current, double max) {
            this.factorStorage = current;
            this.maxStorage = max;
            return this;
        }
        
        public Builder energy(int stored, int max) {
            this.energyStored = stored;
            this.maxEnergy = max;
            return this;
        }
        
        public MachineStateSyncPayload build() {
            return new MachineStateSyncPayload(
                pos, machineType, isWorking, progress,
                factorStorage, maxStorage, energyStored, maxEnergy
            );
        }
        
        /**
         * 条件同步发送到玩家
         */
        public void conditionalSendTo(ServerPlayerEntity player) {
            build().conditionalSync(player);
        }
        
        /**
         * 强制同步发送到玩家
         */
        public void forceSendTo(ServerPlayerEntity player) {
            build().forceSync(player);
        }
    }
}
