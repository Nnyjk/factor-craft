package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 培育核心 - 消耗 Factor 产出物品
 * 
 * 结构: 灵魂花园 T1 → 灵魂温室 T2 → 灵魂果园 T3 → 灵魂花园 T4 → 灵魂圣所 T5
 * 
 * 培育公式:
 * 实际成本 = 基础成本 × 结构效率 × 维度效率
 */
public class BreederCoreBlockEntity extends MachineBlockEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Breeder");
    
    // ==================== 物品槽 ====================
    
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int NUM_SLOTS = 2;
    
    private final List<ItemStack> inventory;
    
    // ==================== 状态 ====================
    
    private double factorBuffer;
    private double maxBuffer;
    private int currentTier;
    private boolean structureValid;
    
    // 当前培育
    private String currentRecipeId;
    private int breedProgress;
    private int breedTimeTotal;
    private double factorCost;
    private double factorConsumed;
    
    // 缓存
    private long lastStructureCheck;
    private static final long STRUCTURE_CHECK_INTERVAL = 100;
    
    public BreederCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.inventory = new ArrayList<>(NUM_SLOTS);
        for (int i = 0; i < NUM_SLOTS; i++) {
            inventory.add(ItemStack.EMPTY);
        }
        this.factorBuffer = 0.0;
        this.maxBuffer = BreedingConfig.MAX_BUFFER_T1;
        this.currentTier = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
        this.breedProgress = 0;
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
                LOGGER.debug("培育结构等级变更: T{} at {}", currentTier, pos);
            }
        }
        
        // 处理培育进度
        if (currentRecipeId != null) {
            tickBreeding(world);
        }
        
        markDirty();
    }
    
    /**
     * 处理培育进度
     */
    private void tickBreeding(World world) {
        BreedingConfig.BreedingRecipe recipe = BreedingConfig.getRecipe(currentRecipeId);
        if (recipe == null) {
            cancelBreeding();
            return;
        }
        
        // 每tick消耗 Factor
        double factorPerTick = factorCost / breedTimeTotal;
        if (factorBuffer >= factorPerTick) {
            factorBuffer -= factorPerTick;
            factorConsumed += factorPerTick;
            breedProgress++;
            
            if (breedProgress >= breedTimeTotal) {
                completeBreeding(world, recipe);
            }
        } else {
            // Factor 不足，暂停
            LOGGER.debug("Factor 不足，暂停培育");
        }
    }
    
    /**
     * 完成培育
     */
    private void completeBreeding(World world, BreedingConfig.BreedingRecipe recipe) {
        // TODO: 产出物品到库存
        LOGGER.info("培育完成: {} x{}", recipe.outputItem(), recipe.outputCount());
        
        // 重置
        currentRecipeId = null;
        breedProgress = 0;
        breedTimeTotal = 0;
        factorCost = 0;
        factorConsumed = 0;
    }
    
    /**
     * 开始培育
     * 
     * @param recipeId 配方 ID
     * @param dimension 当前维度
     * @return 是否成功开始
     */
    public boolean startBreeding(String recipeId, String dimension) {
        if (currentRecipeId != null) {
            LOGGER.debug("已有培育进行中");
            return false;
        }
        
        BreedingConfig.BreedingRecipe recipe = BreedingConfig.getRecipe(recipeId);
        if (recipe == null) {
            LOGGER.debug("配方不存在: {}", recipeId);
            return false;
        }
        
        // 检查 Tier
        if (currentTier < recipe.minTier()) {
            LOGGER.debug("Tier 不足: 需要 T{}, 当前 T{}", recipe.minTier(), currentTier);
            return false;
        }
        
        // 检查维度
        if (!BreedingConfig.canBreed(recipeId, currentTier, dimension)) {
            LOGGER.debug("维度不满足: {}", dimension);
            return false;
        }
        
        // 计算成本和时间
        double actualCost = BreedingConfig.calculateActualCost(recipe, currentTier, dimension);
        int actualTime = BreedingConfig.calculateActualTime(recipe, currentTier);
        
        // 检查 Factor 是否足够开始
        if (factorBuffer < actualCost * 0.1) {
            LOGGER.debug("Factor 不足开始培育: 需要 {}, 当前 {}", actualCost * 0.1, factorBuffer);
            return false;
        }
        
        this.currentRecipeId = recipeId;
        this.breedProgress = 0;
        this.breedTimeTotal = actualTime;
        this.factorCost = actualCost;
        this.factorConsumed = 0;
        
        LOGGER.info("开始培育: {} x{}, 成本: {} Factor, 时间: {} ticks", 
            recipe.outputItem(), recipe.outputCount(), actualCost, actualTime);
        
        return true;
    }
    
    /**
     * 取消培育
     */
    public void cancelBreeding() {
        // 返还部分 Factor（50%）
        double refund = factorConsumed * 0.5;
        factorBuffer = Math.min(maxBuffer, factorBuffer + refund);
        
        currentRecipeId = null;
        breedProgress = 0;
        breedTimeTotal = 0;
        factorCost = 0;
        factorConsumed = 0;
        
        LOGGER.debug("培育已取消，返还 {} Factor", refund);
    }
    
    /**
     * 输入 Factor
     */
    public void inputFactor(double amount) {
        factorBuffer = Math.min(maxBuffer, factorBuffer + amount);
        markDirty();
    }
    
    /**
     * 提取 Factor
     */
    public double extractFactor(double amount) {
        double actual = Math.min(factorBuffer, amount);
        factorBuffer -= actual;
        markDirty();
        return actual;
    }
    
    /**
     * 检测多方块结构等级
     */
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            String patternId = pattern.getId();
            // 匹配培育器相关的蓝图
            if ((patternId.contains("breeder") || patternId.contains("garden") || 
                 patternId.contains("greenhouse") || patternId.contains("orchard"))
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
        maxBuffer = BreedingConfig.getMaxBuffer(tier);
    }
    
    // ==================== Getters ====================
    
    // ==================== 物品槽接口 ====================
    
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= NUM_SLOTS) return ItemStack.EMPTY;
        return inventory.get(slot);
    }
    
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= NUM_SLOTS) return;
        inventory.set(slot, stack);
        markDirty();
    }
    
    public boolean canInsert(int slot, ItemStack stack) {
        if (slot != INPUT_SLOT) return false;
        // 只允许种子/植物类物品作为输入
        return !stack.isEmpty() && stack.getItem().toString().contains("seed");
    }
    
    public void clearInventory() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        currentRecipeId = null;
        breedProgress = 0;
        markDirty();
    }
    
    // ==================== Getters ====================
    
    public double getFactorBuffer() { return factorBuffer; }
    public double getMaxBuffer() { return maxBuffer; }
    public int getCurrentTier() { return currentTier; }
    public boolean isStructureValid() { return structureValid; }
    public boolean isBreeding() { return currentRecipeId != null; }
    public String getCurrentRecipeId() { return currentRecipeId; }
    public int getBreedProgress() { return breedProgress; }
    public int getBreedTimeTotal() { return breedTimeTotal; }
    public double getFactorCost() { return factorCost; }
    public double getFactorConsumed() { return factorConsumed; }
    
    /**
     * 获取培育进度百分比
     */
    public double getBreedProgressPercentage() {
        if (breedTimeTotal == 0) return 0;
        return (breedProgress * 100.0) / breedTimeTotal;
    }
    
    /**
     * 获取缓冲区百分比
     */
    public double getBufferPercentage() {
        return maxBuffer > 0 ? (factorBuffer / maxBuffer) * 100 : 0;
    }
    
    /**
     * 获取调试信息
     */
    public String getDebugInfo() {
        if (currentRecipeId != null) {
            return String.format("T%d | %s | %.0f/%.0f F | %.1f%%",
                currentTier, currentRecipeId, factorBuffer, maxBuffer, getBreedProgressPercentage());
        }
        return String.format("T%d | %.0f/%.0f F | Idle",
            currentTier, factorBuffer, maxBuffer);
    }
    
    // ==================== NBT ====================
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putDouble("FactorBuffer", factorBuffer);
        nbt.putDouble("MaxBuffer", maxBuffer);
        nbt.putInt("CurrentTier", currentTier);
        nbt.putBoolean("StructureValid", structureValid);
        nbt.putLong("LastStructureCheck", lastStructureCheck);
        
        // 物品栏
        NbtList itemsNbt = new NbtList();
        for (int i = 0; i < NUM_SLOTS; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                NbtCompound itemNbt = (NbtCompound) stack.toNbt(registries);
                itemNbt.putByte("Slot", (byte) i);
                itemsNbt.add(itemNbt);
            }
        }
        nbt.put("Items", itemsNbt);
        
        // 培育状态
        if (currentRecipeId != null) {
            nbt.putString("CurrentRecipeId", currentRecipeId);
            nbt.putInt("BreedProgress", breedProgress);
            nbt.putInt("BreedTimeTotal", breedTimeTotal);
            nbt.putDouble("FactorCost", factorCost);
            nbt.putDouble("FactorConsumed", factorConsumed);
        }
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        factorBuffer = nbt.getDouble("FactorBuffer");
        maxBuffer = nbt.getDouble("MaxBuffer");
        currentTier = nbt.getInt("CurrentTier");
        structureValid = nbt.getBoolean("StructureValid");
        lastStructureCheck = nbt.getLong("LastStructureCheck");
        
        // 物品栏
        inventory.clear();
        for (int i = 0; i < NUM_SLOTS; i++) {
            inventory.add(ItemStack.EMPTY);
        }
        
        NbtList itemsNbt = nbt.getList("Items", net.minecraft.nbt.NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < itemsNbt.size(); i++) {
            NbtCompound itemNbt = itemsNbt.getCompound(i);
            byte slot = itemNbt.getByte("Slot");
            if (slot >= 0 && slot < NUM_SLOTS) {
                inventory.set(slot, ItemStack.fromNbt(registries, itemNbt).orElse(ItemStack.EMPTY));
            }
        }
        
        // 培育状态
        if (nbt.contains("CurrentRecipeId")) {
            currentRecipeId = nbt.getString("CurrentRecipeId");
            breedProgress = nbt.getInt("BreedProgress");
            breedTimeTotal = nbt.getInt("BreedTimeTotal");
            factorCost = nbt.getDouble("FactorCost");
            factorConsumed = nbt.getDouble("FactorConsumed");
        }
        
        // 兼容旧数据
        if (maxBuffer == 0) {
            maxBuffer = BreedingConfig.getMaxBuffer(currentTier);
        }
    }
}