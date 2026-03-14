package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 培育核心 - 给物品注入特性
 * 
 * 结构: 命运织机 T1 → 灵魂编织器 T2 → 命运祭坛 T3 → 命运圣所 T4 → 轮回之门 T5
 */
public class CultivatorCoreBlockEntity extends MachineBlockEntity {
    
    private int infusionProgress;
    private double factorBuffer;
    private int currentTier = 1;
    private int traitSlots = 1;
    
    public CultivatorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.infusionProgress = 0;
        this.factorBuffer = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // 检测多方块结构等级
        int detectedTier = detectStructureTier(world, pos);
        if (detectedTier != currentTier) {
            currentTier = detectedTier;
            updateTraitSlots(currentTier);
        }
        
        // 特性注入逻辑（待实现）
        // TODO: 实现特性注入逻辑
        
        markDirty();
    }
    
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            if (pattern.getId().contains("cultivator") && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    private void updateTraitSlots(int tier) {
        // T1-T2: 1槽, T3-T4: 2槽, T5: 3槽
        if (tier <= 2) traitSlots = 1;
        else if (tier <= 4) traitSlots = 2;
        else traitSlots = 3;
    }
    
    public int getCurrentTier() { return currentTier; }
    public int getTraitSlots() { return traitSlots; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("InfusionProgress", infusionProgress);
        nbt.putDouble("FactorBuffer", factorBuffer);
        nbt.putInt("CurrentTier", currentTier);
        nbt.putInt("TraitSlots", traitSlots);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        infusionProgress = nbt.getInt("InfusionProgress");
        factorBuffer = nbt.getDouble("FactorBuffer");
        currentTier = nbt.getInt("CurrentTier");
        traitSlots = nbt.getInt("TraitSlots");
    }
}