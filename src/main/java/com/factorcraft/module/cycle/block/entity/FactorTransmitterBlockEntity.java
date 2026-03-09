package com.factorcraft.module.cycle.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

/**
 * 跨维度传递器 - 传输 Factor 到其他维度
 * 基于 docs/17_factor_cycle_structures.md
 */
public class FactorTransmitterBlockEntity extends BlockEntity {
    protected double bufferFactor = 0.0;
    protected String targetDimension = "";
    protected double efficiency = 0.8;
    protected boolean isSending = false;

    public FactorTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntityTypes.FACTOR_TRANSMITTER, pos, state);
    }

    public void setTarget(String dimensionKey) {
        this.targetDimension = dimensionKey;
        markDirty();
    }

    public void setEfficiency(double eff) {
        this.efficiency = eff;
        markDirty();
    }

    public double transmit(double amount, double transferMultiplier) {
        double toSend = amount * transferMultiplier * efficiency;
        this.bufferFactor -= amount;
        return toSend;
    }

    public void receiveFactor(double amount) {
        this.bufferFactor += amount;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putDouble("BufferFactor", this.bufferFactor);
        nbt.putString("TargetDimension", this.targetDimension);
        nbt.putDouble("Efficiency", this.efficiency);
    }

    @Override
    protected void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.bufferFactor = nbt.getDouble("BufferFactor");
        this.targetDimension = nbt.getString("TargetDimension");
        this.efficiency = nbt.getDouble("Efficiency");
    }
}
