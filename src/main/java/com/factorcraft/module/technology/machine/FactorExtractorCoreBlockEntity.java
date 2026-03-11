package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 提取器核心 - 从矿石/维度提取 Factor
 */
public class FactorExtractorCoreBlockEntity extends FactorMachineBlockEntity {
    
    private int extractProgress;
    private double factorStorage;
    private double maxStorage = 1000.0;
    private double extractRate = 10.0;
    
    public FactorExtractorCoreBlockEntity(BlockPos pos, BlockState state) {
        // BlockEntityType 会由 FabricBlockEntityTypeBuilder 自动设置
        super(null, pos, state);
        this.extractProgress = 0;
        this.factorStorage = 0.0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        int altarTier = getAltarTier(world, pos);
        updateStatsByTier(altarTier);
        
        if (factorStorage < maxStorage) {
            extractProgress += 1;
            if (extractProgress >= 100) {
                extractProgress = 0;
                addFactor(extractRate);
            }
        }
        
        markDirty();
    }
    
    private int getAltarTier(World world, BlockPos pos) {
        // 遍历所有蓝图检测结构等级
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            if (MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1; // 默认 T1
    }
    
    private void updateStatsByTier(int tier) {
        switch (tier) {
            case 1: maxStorage = 1000.0; extractRate = 10.0; break;
            case 2: maxStorage = 5000.0; extractRate = 50.0; break;
            case 3: maxStorage = 25000.0; extractRate = 250.0; break;
            case 4: maxStorage = 125000.0; extractRate = 1250.0; break;
            case 5: maxStorage = 625000.0; extractRate = 6250.0; break;
        }
    }
    
    private void addFactor(double amount) {
        this.factorStorage = Math.min(maxStorage, factorStorage + amount);
    }
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public int getExtractProgress() { return extractProgress; }
    public double getExtractRate() { return extractRate; }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("ExtractProgress", extractProgress);
        nbt.putDouble("FactorStorage", factorStorage);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        extractProgress = nbt.getInt("ExtractProgress");
        factorStorage = nbt.getDouble("FactorStorage");
    }
}
