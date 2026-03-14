package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 提取核心 - 从环境提取 Factor
 * 
 * 结构: 星辰收集器 T1 → 星辰阵列 T2 → 星云汲取器 T3 → 宇宙共鸣器 T4 → 虚空漩涡 T5
 */
public class ExtractorCoreBlockEntity extends MachineBlockEntity {
    
    private int extractProgress;
    private double factorStorage;
    private double maxStorage = 1000.0;
    private double extractRate = 10.0;
    private int currentTier = 1;
    
    public ExtractorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.extractProgress = 0;
        this.factorStorage = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // 检测多方块结构等级
        int detectedTier = detectStructureTier(world, pos);
        if (detectedTier != currentTier) {
            currentTier = detectedTier;
            updateStatsByTier(currentTier);
        }
        
        // 提取逻辑
        if (factorStorage < maxStorage) {
            extractProgress += 1;
            if (extractProgress >= 100) {
                extractProgress = 0;
                addFactor(extractRate);
            }
        }
        
        markDirty();
    }
    
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            if (pattern.getId().contains("extractor") && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    private void updateStatsByTier(int tier) {
        // 根据科技树设计更新属性
        maxStorage = 1000.0 * Math.pow(2.5, tier - 1);
        extractRate = 10.0 * Math.pow(2, tier - 1);
    }
    
    private void addFactor(double amount) {
        factorStorage = Math.min(maxStorage, factorStorage + amount);
    }
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public int getCurrentTier() { return currentTier; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("ExtractProgress", extractProgress);
        nbt.putDouble("FactorStorage", factorStorage);
        nbt.putDouble("MaxStorage", maxStorage);
        nbt.putDouble("ExtractRate", extractRate);
        nbt.putInt("CurrentTier", currentTier);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        extractProgress = nbt.getInt("ExtractProgress");
        factorStorage = nbt.getDouble("FactorStorage");
        maxStorage = nbt.getDouble("MaxStorage");
        extractRate = nbt.getDouble("ExtractRate");
        currentTier = nbt.getInt("CurrentTier");
    }
}