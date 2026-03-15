package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消耗核心 - 消耗物品获得 Factor
 * 
 * 结构: 灵魂燃烧器 T1 → 灵魂熔炉 T2 → 深渊吞噬者 T3 → 混沌裂隙 T4 → 永恒炉心 T5
 * 
 * 消耗公式:
 * 实际产出 = 基础产出 × 结构效率 × 维度倍率 × 维度效率
 */
public class ConsumerCoreBlockEntity extends MachineBlockEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Consumer");
    
    // ==================== 状态 ====================
    
    private double factorStorage;
    private double maxStorage;
    private int currentTier;
    private boolean structureValid;
    
    // 当前消耗
    private String currentRecipeId;
    private int consumeProgress;
    private int consumeTimeTotal;
    private double factorToOutput;
    
    // 缓存
    private long lastStructureCheck;
    private static final long STRUCTURE_CHECK_INTERVAL = 100;
    
    public ConsumerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.factorStorage = 0.0;
        this.maxStorage = ConsumptionConfig.MAX_STORAGE_T1;
        this.currentTier = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
        this.consumeProgress = 0;
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
                LOGGER.debug("消耗结构等级变更: T{} at {}", currentTier, pos);
            }
        }
        
        // 处理消耗进度
        if (currentRecipeId != null) {
            tickConsuming();
        }
        
        markDirty();
    }
    
    /**
     * 处理消耗进度
     */
    private void tickConsuming() {
        consumeProgress++;
        
        if (consumeProgress >= consumeTimeTotal) {
            // 完成消耗，产出 Factor
            factorStorage = Math.min(maxStorage, factorStorage + factorToOutput);
            LOGGER.debug("消耗完成: +{} Factor, 存储: {}/{}", factorToOutput, factorStorage, maxStorage);
            
            // 重置
            currentRecipeId = null;
            consumeProgress = 0;
            consumeTimeTotal = 0;
            factorToOutput = 0;
        }
    }
    
    /**
     * 开始消耗物品
     * 
     * @param itemId 物品 ID
     * @param count 物品数量
     * @param dimension 当前维度
     * @return 实际消耗的数量（0 表示无法消耗）
     */
    public int startConsuming(String itemId, int count, String dimension) {
        if (currentRecipeId != null) {
            return 0; // 已有消耗进行中
        }
        
        ConsumptionConfig.ConsumptionRecipe recipe = ConsumptionConfig.getRecipeForInput(itemId);
        if (recipe == null) {
            LOGGER.debug("无配方: {}", itemId);
            return 0;
        }
        
        // 检查 Tier
        if (currentTier < recipe.minTier()) {
            LOGGER.debug("Tier 不足: 需要 T{}, 当前 T{}", recipe.minTier(), currentTier);
            return 0;
        }
        
        // 检查存储空间
        double outputPerItem = ConsumptionConfig.calculateActualOutput(recipe, currentTier, dimension) / recipe.inputCount();
        double totalOutput = outputPerItem * count;
        
        if (factorStorage + totalOutput > maxStorage) {
            // 调整数量以适应存储
            double space = maxStorage - factorStorage;
            count = (int) Math.floor(space / outputPerItem);
            if (count <= 0) {
                LOGGER.debug("存储已满");
                return 0;
            }
            totalOutput = outputPerItem * count;
        }
        
        // 开始消耗
        this.currentRecipeId = recipe.id();
        this.consumeProgress = 0;
        this.consumeTimeTotal = recipe.consumeTime();
        this.factorToOutput = totalOutput;
        
        LOGGER.debug("开始消耗: {} x{}, 产出: {} Factor", itemId, count, totalOutput);
        
        return count;
    }
    
    /**
     * 取消当前消耗
     */
    public void cancelConsuming() {
        // 注意：取消不返还物品（已被消耗）
        currentRecipeId = null;
        consumeProgress = 0;
        consumeTimeTotal = 0;
        factorToOutput = 0;
    }
    
    /**
     * 提取存储的 Factor
     * 
     * @param amount 请求量
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
    
    /**
     * 检测多方块结构等级
     */
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            String patternId = pattern.getId();
            // 匹配消耗器相关的蓝图
            if ((patternId.contains("consumer") || patternId.contains("furnace") || 
                 patternId.contains("burner") || patternId.contains("incinerator"))
                && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    /**
     * 根据等级更新属性
     */
    private void updateStatsByTier(int tier) {
        maxStorage = ConsumptionConfig.getMaxStorage(tier);
    }
    
    // ==================== Getters ====================
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public int getCurrentTier() { return currentTier; }
    public boolean isStructureValid() { return structureValid; }
    public boolean isConsuming() { return currentRecipeId != null; }
    public String getCurrentRecipeId() { return currentRecipeId; }
    public int getConsumeProgress() { return consumeProgress; }
    public int getConsumeTimeTotal() { return consumeTimeTotal; }
    public double getFactorToOutput() { return factorToOutput; }
    
    /**
     * 获取消耗进度百分比
     */
    public double getConsumeProgressPercentage() {
        if (consumeTimeTotal == 0) return 0;
        return (consumeProgress * 100.0) / consumeTimeTotal;
    }
    
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
        if (currentRecipeId != null) {
            return String.format("T%d | %s | %.0f/%.0f F | %.1f%%",
                currentTier, currentRecipeId, factorStorage, maxStorage, getConsumeProgressPercentage());
        }
        return String.format("T%d | %.0f/%.0f F | Idle",
            currentTier, factorStorage, maxStorage);
    }
    
    // ==================== NBT ====================
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putDouble("FactorStorage", factorStorage);
        nbt.putDouble("MaxStorage", maxStorage);
        nbt.putInt("CurrentTier", currentTier);
        nbt.putBoolean("StructureValid", structureValid);
        nbt.putLong("LastStructureCheck", lastStructureCheck);
        
        // 消耗状态
        if (currentRecipeId != null) {
            nbt.putString("CurrentRecipeId", currentRecipeId);
            nbt.putInt("ConsumeProgress", consumeProgress);
            nbt.putInt("ConsumeTimeTotal", consumeTimeTotal);
            nbt.putDouble("FactorToOutput", factorToOutput);
        }
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        factorStorage = nbt.getDouble("FactorStorage");
        maxStorage = nbt.getDouble("MaxStorage");
        currentTier = nbt.getInt("CurrentTier");
        structureValid = nbt.getBoolean("StructureValid");
        lastStructureCheck = nbt.getLong("LastStructureCheck");
        
        // 消耗状态
        if (nbt.contains("CurrentRecipeId")) {
            currentRecipeId = nbt.getString("CurrentRecipeId");
            consumeProgress = nbt.getInt("ConsumeProgress");
            consumeTimeTotal = nbt.getInt("ConsumeTimeTotal");
            factorToOutput = nbt.getDouble("FactorToOutput");
        }
        
        // 兼容旧数据
        if (maxStorage == 0) {
            maxStorage = ConsumptionConfig.getMaxStorage(currentTier);
        }
    }
}