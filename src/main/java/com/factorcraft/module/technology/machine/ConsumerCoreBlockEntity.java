package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 消耗核心 - 消耗物品获得 Factor
 * 
 * 结构: 灵魂燃烧器 T1 → 灵魂熔炉 T2 → 深渊吞噬者 T3 → 混沌裂隙 T4 → 永恒炉心 T5
 */
public class ConsumerCoreBlockEntity extends MachineBlockEntity {
    
    private int consumeProgress;
    private double factorStorage;
    private double maxStorage = 500.0;
    private double baseOutput = 50.0;
    private int currentTier = 1;
    
    public ConsumerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.consumeProgress = 0;
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
        
        // 消耗逻辑（待实现物品输入）
        // TODO: 实现物品槽位和消耗逻辑
        
        markDirty();
    }
    
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            if (pattern.getId().contains("consumer") && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    private void updateStatsByTier(int tier) {
        maxStorage = 500.0 * Math.pow(2, tier - 1);
        baseOutput = 50.0 * Math.pow(3, tier - 1);
    }
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public int getCurrentTier() { return currentTier; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("ConsumeProgress", consumeProgress);
        nbt.putDouble("FactorStorage", factorStorage);
        nbt.putInt("CurrentTier", currentTier);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        consumeProgress = nbt.getInt("ConsumeProgress");
        factorStorage = nbt.getDouble("FactorStorage");
        currentTier = nbt.getInt("CurrentTier");
    }
}