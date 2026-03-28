package com.factorcraft.module.technology.machine;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.module.network.MachineStateSyncPayload;
import com.factorcraft.module.network.NetworkConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 机器基类
 * 
 * R3.1 优化:
 * - 条件同步：使用 MachineStateSyncPayload.conditionalSendTo()
 * - 同步冷却：500ms 间隔限制 (可配置)
 * - 变化检测：仅当状态变化时才同步
 */
public abstract class MachineBlockEntity extends BlockEntity implements IFactorContainer {
    
    /** 上次同步时间 (毫秒) */
    protected long lastSyncTime = 0L;
    
    /** 上次同步的工作状态 */
    protected boolean lastWorkingState = false;
    
    /** 上次同步的进度 */
    protected double lastProgress = 0.0;
    
    /** 上次同步的 Factor 存储 */
    protected double lastFactorStorage = 0.0;
    
    /** 上次同步的能量 */
    protected int lastEnergy = 0;
    
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    /**
     * 每 tick 调用
     */
    public abstract void tick(World world, BlockPos pos, BlockState state);
    
    @Override
    public double addFactor(double amount) {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double extractFactor(double amount) {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double getFactorStorage() {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 0.0; // 默认实现，子类覆盖
    }
    
    /**
     * 检查是否应该同步状态
     * 
     * 使用冷却时间 + 变化检测
     * 
     * @return true 如果应该同步
     */
    protected boolean shouldSync() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSyncTime < NetworkConfig.MACHINE_SYNC_COOLDOWN_MS) {
            return false;
        }
        
        // 检查状态是否发生变化
        boolean working = isWorking();
        double progress = getProgress();
        double factorStorage = getFactorStorage();
        int energy = getEnergyStored();
        
        boolean changed = working != lastWorkingState ||
                         Math.abs(progress - lastProgress) > 0.01 ||
                         Math.abs(factorStorage - lastFactorStorage) > 1.0 ||
                         Math.abs(energy - lastEnergy) > 10;
        
        return changed;
    }
    
    /**
     * 同步状态到附近玩家 (条件同步)
     * 
     * 使用 MachineStateSyncPayload.conditionalSendTo() 仅当必要时才发送
     */
    protected void syncToNearbyPlayers() {
        if (!shouldSync()) {
            return;
        }
        
        if (getWorld() instanceof ServerWorld serverWorld) {
            double syncRadius = NetworkConfig.MACHINE_SYNC_RADIUS;
            BlockPos pos = getPos();
            
            serverWorld.getPlayers(player -> 
                player.getPos().distanceTo(pos.toCenterPos()) < syncRadius
            ).forEach(player -> {
                MachineStateSyncPayload.builder(pos, getMachineType())
                    .working(isWorking())
                    .progress(getProgress())
                    .factorStorage(getFactorStorage(), getMaxFactorStorage())
                    .energy(getEnergyStored(), getMaxEnergy())
                    .conditionalSendTo(player);
            });
            
            // 更新上次同步状态
            updateLastSyncState();
        }
    }
    
    /**
     * 更新上次同步状态
     */
    protected void updateLastSyncState() {
        lastSyncTime = System.currentTimeMillis();
        lastWorkingState = isWorking();
        lastProgress = getProgress();
        lastFactorStorage = getFactorStorage();
        lastEnergy = getEnergyStored();
    }
    
    /**
     * 强制同步 (忽略冷却)
     * 
     * 用于重要状态变化 (如机器启动/停止)
     */
    protected void forceSync() {
        if (getWorld() instanceof ServerWorld serverWorld) {
            double syncRadius = NetworkConfig.MACHINE_SYNC_RADIUS;
            BlockPos pos = getPos();
            
            serverWorld.getPlayers(player -> 
                player.getPos().distanceTo(pos.toCenterPos()) < syncRadius
            ).forEach(player -> {
                MachineStateSyncPayload.builder(pos, getMachineType())
                    .working(isWorking())
                    .progress(getProgress())
                    .factorStorage(getFactorStorage(), getMaxFactorStorage())
                    .energy(getEnergyStored(), getMaxEnergy())
                    .forceSendTo(player);
            });
            
            updateLastSyncState();
        }
    }
    
    /**
     * 获取机器类型字符串
     * 
     * 子类应覆盖此方法返回具体的机器类型
     */
    protected String getMachineType() {
        String className = getClass().getSimpleName();
        if (className.contains("Extractor")) return "extractor";
        if (className.contains("Synthesizer")) return "synthesizer";
        if (className.contains("Consumer")) return "consumer";
        if (className.contains("Cultivator")) return "cultivator";
        if (className.contains("Breeder")) return "breeder";
        if (className.contains("Transmitter")) return "transmitter";
        return "unknown";
    }
    
    /**
     * 检查机器是否在工作中
     * 
     * 子类应覆盖此方法
     */
    protected boolean isWorking() {
        return false;
    }
    
    /**
     * 获取机器进度 (0.0 - 1.0)
     * 
     * 子类应覆盖此方法
     */
    protected double getProgress() {
        return 0.0;
    }
    
    /**
     * 获取能量存储
     * 
     * 子类应覆盖此方法
     */
    public int getEnergyStored() {
        return 0;
    }
    
    /**
     * 获取最大能量
     * 
     * 子类应覆盖此方法
     */
    public int getMaxEnergy() {
        return 10000;
    }
}
