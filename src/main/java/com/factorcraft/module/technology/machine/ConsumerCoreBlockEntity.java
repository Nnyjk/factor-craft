package com.factorcraft.module.technology.machine;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
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
public class ConsumerCoreBlockEntity extends MachineBlockEntity implements MachineInventory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Consumer");
    
    // 物品槽
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    
    // 状态
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
        super(ModMachines.CONSUMER_CORE, pos, state);
        this.factorStorage = 0.0;
        this.maxStorage = ConsumptionConfig.MAX_STORAGE_T1;
        this.currentTier = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
        this.consumeProgress = 0;
    }
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
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
        
        // 自动从输入槽开始消耗
        if (currentRecipeId == null && !getStack(INPUT_SLOT).isEmpty()) {
            tryStartConsumingFromInput(world);
        }
        
        // 处理消耗进度
        if (currentRecipeId != null) {
            tickConsuming(serverWorld);
        }
        
        markDirty();
    }
    
    /**
     * 尝试从输入槽开始消耗
     */
    private void tryStartConsumingFromInput(World world) {
        ItemStack inputStack = getStack(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        
        String itemId = inputStack.getItem().toString();
        String dimension = world.getRegistryKey().getValue().toString();
        
        ConsumptionConfig.ConsumptionRecipe recipe = ConsumptionConfig.getRecipeForInput(itemId);
        if (recipe == null) return;
        
        // 检查 Tier
        if (currentTier < recipe.minTier()) return;
        
        // 检查存储空间
        double output = ConsumptionConfig.calculateActualOutput(recipe, currentTier, dimension);
        if (factorStorage + output > maxStorage) return;
        
        // 开始消耗
        this.currentRecipeId = recipe.id();
        this.consumeProgress = 0;
        this.consumeTimeTotal = recipe.consumeTime();
        this.factorToOutput = output;
        
        // 消耗一个物品
        inputStack.decrement(1);
        
        LOGGER.debug("开始消耗: {}, 产出: {} Factor", itemId, output);
    }
    
    /**
     * 处理消耗进度
     */
    /**
     * 处理消耗进度
     */
    private void tickConsuming(ServerWorld world) {
        consumeProgress++;
        
        if (consumeProgress >= consumeTimeTotal) {
            // 完成消耗，产出 Factor
            factorStorage = Math.min(maxStorage, factorStorage + factorToOutput);
            LOGGER.debug("消耗完成：+{} Factor, 存储：{}/{}", factorToOutput, factorStorage, maxStorage);
            
            // 重置
            currentRecipeId = null;
            consumeProgress = 0;
            consumeTimeTotal = 0;
            factorToOutput = 0;
            
            // 尝试输出 Factor 到区块
            tryOutputFactorToChunk(world);
        }
    }
    
    /**
     * 尝试输出 Factor 到区块
     * 当存储量达到阈值时，自动输出一部分到所在区块
     */
    private void tryOutputFactorToChunk(ServerWorld world) {
        double threshold = maxStorage * ConsumptionConfig.OUTPUT_THRESHOLD;
        if (factorStorage < threshold) return;
        
        // 计算输出量
        double toOutput = factorStorage * ConsumptionConfig.OUTPUT_RATIO;
        if (toOutput <= 0) return;
        
        // 输出到区块 Factor 状态
        ChunkFactorState state = ChunkFactorManager.getOrCreateState(world, new net.minecraft.util.math.ChunkPos(pos));
        state.setCurrentConcentration(state.getCurrentConcentration() + toOutput);
        factorStorage -= toOutput;
        
        LOGGER.info("消耗器输出 Factor 到区块：+{} (剩余：{}/{})", 
                    toOutput, factorStorage, maxStorage);
        
        markDirty();
    }
    
    
    /**
     * 取消当前消耗
     */
    public void cancelConsuming() {
        // 取消不返还物品（已被消耗）
        currentRecipeId = null;
        consumeProgress = 0;
        consumeTimeTotal = 0;
        factorToOutput = 0;
    }
    
    /**
     * 提取存储的 Factor
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
    
    public double getConsumeProgressPercentage() {
        return consumeTimeTotal == 0 ? 0 : (consumeProgress * 100.0) / consumeTimeTotal;
    }
    
    public double getStoragePercentage() {
        return maxStorage > 0 ? (factorStorage / maxStorage) * 100 : 0;
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
        
        // 物品库存
        writeInventoryNbt(nbt, registries);
        
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
        
        // 物品库存
        readInventoryNbt(nbt, registries);
        
        // 消耗状态
        if (nbt.contains("CurrentRecipeId")) {
            currentRecipeId = nbt.getString("CurrentRecipeId");
            consumeProgress = nbt.getInt("ConsumeProgress");
            consumeTimeTotal = nbt.getInt("ConsumeTimeTotal");
            factorToOutput = nbt.getDouble("FactorToOutput");
        }
        
        if (maxStorage == 0) {
            maxStorage = ConsumptionConfig.getMaxStorage(currentTier);
        }
    }
}