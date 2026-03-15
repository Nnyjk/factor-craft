package com.factorcraft.module.technology.machine;

import com.factorcraft.module.factor.DimensionType;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提取核心 - 从环境提取 Factor
 * 
 * 结构: 星辰收集器 T1 → 星辰阵列 T2 → 星云汲取器 T3 → 宇宙共鸣器 T4 → 虚空漩涡 T5
 * 
 * 提取公式:
 * 实际提取 = 基础速率 × 维度活性系数 × 浓度系数 × 结构效率 × 维度效率
 */
public class ExtractorCoreBlockEntity extends MachineBlockEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Extractor");
    
    // 状态
    private int extractProgress;
    private double factorStorage;
    private double maxStorage;
    private double lastExtractRate;
    private int currentTier;
    private boolean structureValid;
    
    // 缓存
    private long lastStructureCheck;
    private static final long STRUCTURE_CHECK_INTERVAL = 100; // 5 秒检查一次结构
    
    public ExtractorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.extractProgress = 0;
        this.factorStorage = 0.0;
        this.maxStorage = ExtractionConfig.MAX_STORAGE_T1;
        this.lastExtractRate = 0.0;
        this.currentTier = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        ServerWorld serverWorld = (ServerWorld) world;
        long currentTick = world.getTime();
        
        // 定期检测多方块结构
        if (currentTick - lastStructureCheck >= STRUCTURE_CHECK_INTERVAL) {
            lastStructureCheck = currentTick;
            int detectedTier = detectStructureTier(world, pos);
            if (detectedTier != currentTier) {
                currentTier = detectedTier;
                structureValid = true;
                updateStatsByTier(currentTier);
                LOGGER.debug("结构等级变更: T{} at {}", currentTier, pos);
            }
        }
        
        // 提取逻辑
        if (factorStorage < maxStorage) {
            extractProgress++;
            
            if (extractProgress >= ExtractionConfig.EXTRACTION_INTERVAL) {
                extractProgress = 0;
                performExtraction(serverWorld, pos);
            }
        } else {
            // 存储已满，停止提取
            lastExtractRate = 0;
        }
        
        markDirty();
    }
    
    /**
     * 执行一次提取操作
     */
    private void performExtraction(ServerWorld world, BlockPos pos) {
        // 1. 获取维度活性系数
        String dimensionKey = world.getRegistryKey().getValue().toString();
        DimensionType dimensionType = DimensionType.fromKey(dimensionKey);
        long worldTick = world.getTime();
        double activityCoefficient = dimensionType.calculateFactor(worldTick);
        
        // 2. 获取区块浓度
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkFactorState chunkState = ChunkFactorManager.getOrCreateState(world, chunkPos);
        double concentration = chunkState.getCurrentConcentration();
        
        // 3. 检查最低浓度阈值
        if (concentration < ExtractionConfig.MIN_CONCENTRATION_THRESHOLD) {
            lastExtractRate = 0;
            LOGGER.trace("区块浓度过低，无法提取: {} at {}", concentration, chunkPos);
            return;
        }
        
        // 4. 计算各项系数
        double baseRate = ExtractionConfig.getBaseRate(currentTier);
        double concentrationCoeff = ExtractionConfig.getConcentrationCoefficient(concentration);
        double structureEfficiency = ExtractionConfig.getEfficiency(currentTier);
        double dimensionEfficiency = ExtractionConfig.getDimensionEfficiency(dimensionKey, currentTier);
        
        // 5. 计算实际提取量
        double actualExtract = baseRate 
            * activityCoefficient 
            * concentrationCoeff 
            * structureEfficiency 
            * dimensionEfficiency;
        
        // 6. 从区块消耗
        double drainAmount = actualExtract * 0.1; // 提取 10% 消耗区块浓度
        chunkState.setCurrentConcentration(concentration - drainAmount);
        
        // 7. 存入缓冲区
        factorStorage = Math.min(maxStorage, factorStorage + actualExtract);
        lastExtractRate = actualExtract;
        
        LOGGER.trace("提取: base={}, activity={:.2f}, concCoeff={:.1f}, structEff={:.1f}, dimEff={:.1f} → {}",
            baseRate, activityCoefficient, concentrationCoeff, structureEfficiency, dimensionEfficiency, actualExtract);
    }
    
    /**
     * 检测多方块结构等级
     */
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            String patternId = pattern.getId();
            // 匹配提取器相关的蓝图
            if ((patternId.contains("extractor") || patternId.contains("collector") || patternId.contains("resonance"))
                && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        // 未检测到有效结构，使用最低等级
        return 1;
    }
    
    /**
     * 根据等级更新属性
     */
    private void updateStatsByTier(int tier) {
        maxStorage = ExtractionConfig.getMaxStorage(tier);
    }
    
    /**
     * 提取存储的 Factor
     * @param amount 请求提取量
     * @return 实际提取量
     */
    public double extractFactor(double amount) {
        double actual = Math.min(factorStorage, amount);
        factorStorage -= actual;
        markDirty();
        return actual;
    }
    
    /**
     * 向存储中添加 Factor
     */
    public void addFactor(double amount) {
        factorStorage = Math.min(maxStorage, factorStorage + amount);
        markDirty();
    }
    
    // ==================== Getters ====================
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public int getCurrentTier() { return currentTier; }
    public double getLastExtractRate() { return lastExtractRate; }
    public boolean isStructureValid() { return structureValid; }
    public int getExtractProgress() { return extractProgress; }
    
    /**
     * 获取存储百分比
     */
    public double getStoragePercentage() {
        return maxStorage > 0 ? (factorStorage / maxStorage) * 100 : 0;
    }
    
    /**
     * 获取调试信息
     */
    public String getDebugInfo() {
        return String.format("T%d | %.0f/%.0f (%.1f%%) | %.2f/tick",
            currentTier, factorStorage, maxStorage, getStoragePercentage(), lastExtractRate);
    }
    
    // ==================== NBT ====================
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("ExtractProgress", extractProgress);
        nbt.putDouble("FactorStorage", factorStorage);
        nbt.putDouble("MaxStorage", maxStorage);
        nbt.putDouble("LastExtractRate", lastExtractRate);
        nbt.putInt("CurrentTier", currentTier);
        nbt.putBoolean("StructureValid", structureValid);
        nbt.putLong("LastStructureCheck", lastStructureCheck);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        extractProgress = nbt.getInt("ExtractProgress");
        factorStorage = nbt.getDouble("FactorStorage");
        maxStorage = nbt.getDouble("MaxStorage");
        lastExtractRate = nbt.getDouble("LastExtractRate");
        currentTier = nbt.getInt("CurrentTier");
        structureValid = nbt.getBoolean("StructureValid");
        lastStructureCheck = nbt.getLong("LastStructureCheck");
        
        // 兼容旧数据
        if (maxStorage == 0) {
            maxStorage = ExtractionConfig.getMaxStorage(currentTier);
        }
    }
}