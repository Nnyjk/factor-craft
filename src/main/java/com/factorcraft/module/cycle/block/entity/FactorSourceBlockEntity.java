package com.factorcraft.module.cycle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * 释放结构基类 - 消耗物品产生 Factor
 * 基于 docs/17_factor_cycle_structures.md
 */
public class FactorSourceBlockEntity extends BlockEntity {
    protected double generatedFactor = 0.0;
    protected double baseReleaseRate = 50.0;
    protected double dimensionMultiplier = 1.0;

    public FactorSourceBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntityTypes.FACTOR_SOURCE, pos, state);
    }

    public void processItem(int itemCount) {
        double released = baseReleaseRate * dimensionMultiplier * itemCount;
        this.generatedFactor += released;
        markDirty();
    }

    public double getGeneratedFactor() {
        return generatedFactor * dimensionMultiplier;
    }

    public void setDimensionMultiplier(double multiplier) {
        this.dimensionMultiplier = multiplier;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("GeneratedFactor", this.generatedFactor);
        nbt.putDouble("DimensionMultiplier", this.dimensionMultiplier);
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.generatedFactor = nbt.getDouble("GeneratedFactor");
        this.dimensionMultiplier = nbt.getDouble("DimensionMultiplier");
    }
}
