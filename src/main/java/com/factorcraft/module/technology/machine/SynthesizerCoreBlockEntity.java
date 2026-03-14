package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 合成核心 - 用 Factor 合成物品（材料升级）
 * 
 * 结构: 远古合成阵 T1 → 远古锻造台 T2 → 命运铸造炉 T3 → 创世熔炉 T4 → 本源祭坛 T5
 */
public class SynthesizerCoreBlockEntity extends MachineBlockEntity {
    
    private int craftProgress;
    private double factorBuffer;
    private int currentTier = 1;
    
    public SynthesizerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.craftProgress = 0;
        this.factorBuffer = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // 检测多方块结构等级
        int detectedTier = detectStructureTier(world, pos);
        if (detectedTier != currentTier) {
            currentTier = detectedTier;
        }
        
        // 合成逻辑（待实现）
        // TODO: 实现材料升级合成逻辑
        
        markDirty();
    }
    
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            if (pattern.getId().contains("synthesizer") && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    public int getCurrentTier() { return currentTier; }
    public double getFactorBuffer() { return factorBuffer; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("CraftProgress", craftProgress);
        nbt.putDouble("FactorBuffer", factorBuffer);
        nbt.putInt("CurrentTier", currentTier);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        craftProgress = nbt.getInt("CraftProgress");
        factorBuffer = nbt.getDouble("FactorBuffer");
        currentTier = nbt.getInt("CurrentTier");
    }
}