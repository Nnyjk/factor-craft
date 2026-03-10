package com.factorcraft.module.technology.machine;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 释放器核心 - 跨维度传输 Factor
 */
public class FactorEmitterCoreBlockEntity extends FactorMachineBlockEntity {
    
    private int emitProgress;
    private double factorBuffer;
    private String targetDimension = "minecraft:overworld";
    
    public FactorEmitterCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.emitProgress = 0;
        this.factorBuffer = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // TODO: 实现跨维度传输逻辑
        if (factorBuffer > 0) {
            emitProgress += 1;
            if (emitProgress >= 100) {
                emitProgress = 0;
                transmitFactor();
            }
        }
        
        markDirty();
    }
    
    private void transmitFactor() {
        // TODO: 调用 FactorNetworkManager
        factorBuffer = 0;
    }
    
    public void setTargetDimension(String dimensionId) {
        this.targetDimension = dimensionId;
    }
    
    public String getTargetDimension() { return targetDimension; }
    public double getFactorBuffer() { return factorBuffer; }
    public int getEmitProgress() { return emitProgress; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("EmitProgress", emitProgress);
        nbt.putDouble("FactorBuffer", factorBuffer);
        nbt.putString("TargetDimension", targetDimension);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        emitProgress = nbt.getInt("EmitProgress");
        factorBuffer = nbt.getDouble("FactorBuffer");
        targetDimension = nbt.getString("TargetDimension");
    }
}
