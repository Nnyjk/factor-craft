package com.factorcraft.module.cycle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 吸收结构基类 - 消耗 Factor 生产物品
 * 基于 docs/17_factor_cycle_structures.md
 */
public class FactorSinkBlockEntity extends BlockEntity {
    protected double currentFactor = 0.0;
    protected double consumptionRate = 1000.0;
    protected int progress = 0;
    protected final int maxProgress = 100;

    public FactorSinkBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntityTypes.FACTOR_SINK, pos, state);
    }

    public void addFactor(double amount) {
        this.currentFactor += amount;
        markDirty();
    }

    public boolean startCrafting() {
        if (this.currentFactor >= this.consumptionRate) {
            this.currentFactor -= this.consumptionRate;
            this.progress = 0;
            markDirty();
            return true;
        }
        return false;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (this.progress < this.maxProgress) {
            this.progress++;
            if (this.progress >= this.maxProgress) {
                onCraftComplete();
                this.progress = 0;
            }
            markDirty();
        }
    }

    protected void onCraftComplete() {
        // 子类实现
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("CurrentFactor", this.currentFactor);
        nbt.putInt("Progress", this.progress);
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.currentFactor = nbt.getDouble("CurrentFactor");
        this.progress = nbt.getInt("Progress");
    }

    public double getCurrentFactor() { return currentFactor; }
    public int getProgress() { return progress; }
}
