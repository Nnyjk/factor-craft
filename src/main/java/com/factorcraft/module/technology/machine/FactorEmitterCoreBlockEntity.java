package com.factorcraft.module.technology.machine;

import com.factorcraft.module.network.FactorNetworkManager;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 释放器核心 - 跨维度传输 Factor
 */
public class FactorEmitterCoreBlockEntity extends FactorMachineBlockEntity {
    
    private int emitProgress;
    private double factorBuffer;
    private String targetDimension = "minecraft:overworld";
    private BlockPos targetPos = BlockPos.ORIGIN;
    
    public FactorEmitterCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.emitProgress = 0;
        this.factorBuffer = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // 实现跨维度传输逻辑
        if (factorBuffer > 0 && world instanceof ServerWorld serverWorld) {
            emitProgress += 1;
            if (emitProgress >= 100) {
                emitProgress = 0;
                transmitFactor(serverWorld, pos);
            }
        }
        
        markDirty();
    }
    
    private void transmitFactor(ServerWorld world, BlockPos pos) {
        // 调用 FactorNetworkManager 进行跨维度传输
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        if (manager == null) {
            return;
        }
        
        // 获取目标世界
        var server = world.getServer();
        var targetKey = parseDimensionKey(targetDimension);
        ServerWorld targetWorld = server.getWorld(targetKey);
        
        if (targetWorld != null && factorBuffer > 0) {
            // 计算传输后的量（考虑维度基准比和效率）
            int amount = (int) factorBuffer;
            double received = manager.transferFactor(
                world, pos,
                targetWorld, targetPos,
                amount, 0.9 // 90% 效率
            );
            
            factorBuffer = 0;
        }
    }
    
    private net.minecraft.registry.RegistryKey<net.minecraft.world.World> parseDimensionKey(String dimId) {
        if (dimId.contains("the_nether")) {
            return net.minecraft.world.World.NETHER;
        } else if (dimId.contains("the_end")) {
            return net.minecraft.world.World.END;
        }
        return net.minecraft.world.World.OVERWORLD;
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
