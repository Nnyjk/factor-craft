package com.factorcraft.module.technology.machine;

import com.factorcraft.module.material.trait.TraitDefinition;
import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitRegistry;
import com.factorcraft.module.material.trait.TraitService;
import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import net.minecraft.util.math.random.Random;

/**
 * 培育核心 - 给物品注入特性
 * 
 * 结构：命运织机 T1 → 灵魂编织器 T2 → 命运祭坛 T3 → 命运圣所 T4 → 轮回之门 T5
 * 
 * 特性注入公式:
 * - 基础概率：30%
 * - 等级加成：每级 +10% (T5: 70%)
 * - Factor 消耗：基础值 × (1 + 特性数量 × 0.5)
 * 
 * 特性槽位:
 * - T1-T2: 1 槽
 * - T3-T4: 2 槽
 * - T5: 3 槽
 */
public class CultivatorCoreBlockEntity extends MachineBlockEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Cultivator");
    
    // ==================== 配置常量 ====================
    
    private static final double BASE_SUCCESS_RATE = 0.30;  // 基础成功率 30%
    private static final double TIER_SUCCESS_BONUS = 0.10; // 每级 +10%
    private static final double BASE_FACTOR_COST = 100.0;  // 基础 Factor 消耗
    private static final double TRAIT_COST_MULTIPLIER = 0.5; // 每特性 +50% 成本
    private static final int INFUSION_TIME_TICKS = 200;    // 注入时间 (10 秒)
    
    // ==================== 物品槽 ====================
    
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int NUM_SLOTS = 2;
    
    private final List<ItemStack> inventory;
    
    // ==================== 状态 ====================
    
    private int currentTier;
    private int traitSlots;
    private boolean structureValid;
    
    // 特性注入状态
    private int infusionProgress;
    private int infusionTimeTotal;
    private double factorCost;
    private double factorConsumed;
    private List<String> targetTraits;  // 待注入的特性 ID 列表
    private ItemStack workingItem;      // 正在处理的物品副本
    
    // 缓存
    private long lastStructureCheck;
    private static final long STRUCTURE_CHECK_INTERVAL = 100;
    
    public CultivatorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
        this.inventory = new ArrayList<>(NUM_SLOTS);
        for (int i = 0; i < NUM_SLOTS; i++) {
            inventory.add(ItemStack.EMPTY);
        }
        this.currentTier = 1;
        this.traitSlots = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
        this.infusionProgress = 0;
        this.targetTraits = new ArrayList<>();
        this.workingItem = ItemStack.EMPTY;
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
                updateTraitSlots(currentTier);
                LOGGER.debug("培育结构等级变更：T{} at {}", currentTier, pos);
            }
        }
        
        // 处理特性注入
        if (infusionProgress > 0 && !targetTraits.isEmpty()) {
            tickInfusion(world);
        } else {
            // 空闲时尝试开始新的注入
            tryStartInfusion();
        }
        
        markDirty();
    }
    
    /**
     * 处理特性注入进度
     */
    private void tickInfusion(World world) {
        // 每 tick 消耗 Factor
        double factorPerTick = factorCost / infusionTimeTotal;
        
        // 从缓冲区扣除 Factor（需要外部输入）
        // 这里简化处理：假设有外部 Factor 供应
        // 实际使用时需要通过 FactorNetwork 或其他方式输入
        
        infusionProgress++;
        factorConsumed += factorPerTick;
        
        if (infusionProgress >= infusionTimeTotal) {
            completeInfusion(world);
        }
    }
    
    /**
     * 尝试开始特性注入
     */
    private void tryStartInfusion() {
        ItemStack inputStack = inventory.get(INPUT_SLOT);
        ItemStack outputStack = inventory.get(OUTPUT_SLOT);
        
        // 检查输入槽是否有物品
        if (inputStack.isEmpty()) {
            return;
        }
        
        // 检查输出槽是否为空
        if (!outputStack.isEmpty()) {
            return;
        }
        
        // 检查物品是否可以添加特性（已有特性数量 < 槽位数）
        List<TraitInstance> existingTraits = TraitService.getTraits(inputStack);
        if (existingTraits.size() >= traitSlots) {
            LOGGER.debug("物品已满特性槽：{}/{}", existingTraits.size(), traitSlots);
            return;
        }
        
        // 计算可注入的特性数量
        int availableSlots = traitSlots - existingTraits.size();
        
        // 根据等级随机选择特性（T1: 1 个，T2: 1-2 个，T3-T4: 1-2 个，T5: 1-3 个）
        int traitCount = Math.min(availableSlots, currentTier);
        
        // 生成随机特性
        Random random = world.getRandom();
        double positiveChance = 0.7; // 70% 概率获得正面特性
        List<TraitInstance> newTraits = TraitService.generateRandomTraits(currentTier, traitCount, random, positiveChance);
        
        if (newTraits.isEmpty()) {
            LOGGER.debug("未能生成特性");
            return;
        }
        
        // 计算 Factor 消耗
        factorCost = calculateFactorCost(newTraits.size());
        
        // 开始注入
        startInfusion(inputStack, newTraits);
    }
    
    /**
     * 开始特性注入
     */
    private void startInfusion(ItemStack item, List<TraitInstance> traits) {
        this.workingItem = item.copy();
        this.targetTraits = new ArrayList<>();
        for (TraitInstance trait : traits) {
            targetTraits.add(trait.traitId());
        }
        this.infusionProgress = 0;
        this.infusionTimeTotal = INFUSION_TIME_TICKS;
        this.factorConsumed = 0;
        
        LOGGER.info("开始特性注入：{} 个特性，消耗 {} Factor", traits.size(), factorCost);
    }
    
    /**
     * 完成特性注入
     */
    private void completeInfusion(World world) {
        if (workingItem.isEmpty() || targetTraits.isEmpty()) {
            resetInfusion();
            return;
        }
        
        // 计算成功率
        double successRate = calculateSuccessRate();
        
        if (world.random.nextDouble() <= successRate) {
            // 成功：注入特性
            for (String traitId : targetTraits) {
                TraitService.addTrait(workingItem, traitId, 1);
            }
            
            // 移动到输出槽
            inventory.set(OUTPUT_SLOT, workingItem.copy());
            inventory.set(INPUT_SLOT, ItemStack.EMPTY);
            
            LOGGER.info("特性注入成功：注入 {} 个特性，成功率 {:.1f}%", targetTraits.size(), successRate * 100);
        } else {
            // 失败：返还输入物品
            inventory.set(OUTPUT_SLOT, workingItem.copy());
            inventory.set(INPUT_SLOT, ItemStack.EMPTY);
            
            LOGGER.info("特性注入失败：成功率 {:.1f}%", successRate * 100);
        }
        
        resetInfusion();
    }
    
    /**
     * 重置注入状态
     */
    private void resetInfusion() {
        this.infusionProgress = 0;
        this.infusionTimeTotal = 0;
        this.factorCost = 0;
        this.factorConsumed = 0;
        this.targetTraits.clear();
        this.workingItem = ItemStack.EMPTY;
    }
    
    /**
     * 计算成功率
     */
    private double calculateSuccessRate() {
        double rate = BASE_SUCCESS_RATE + (currentTier * TIER_SUCCESS_BONUS);
        return Math.min(0.95, rate); // 最高 95%
    }
    
    /**
     * 计算 Factor 消耗
     */
    private double calculateFactorCost(int traitCount) {
        return BASE_FACTOR_COST * (1 + traitCount * TRAIT_COST_MULTIPLIER);
    }
    
    /**
     * 检测多方块结构等级
     */
    private int detectStructureTier(World world, BlockPos pos) {
        for (var pattern : MultiblockDetector.getAllPatterns()) {
            String patternId = pattern.getId();
            // 匹配培育器相关的蓝图
            if ((patternId.contains("cultivator") || patternId.contains("loom") || 
                 patternId.contains("weaver") || patternId.contains("altar") ||
                 patternId.contains("sanctum") || patternId.contains("gate"))
                && MultiblockDetector.detect(world, pos, pattern)) {
                return pattern.getTier();
            }
        }
        return 1;
    }
    
    /**
     * 根据等级更新特性槽位
     */
    private void updateTraitSlots(int tier) {
        if (tier <= 2) {
            traitSlots = 1;
        } else if (tier <= 4) {
            traitSlots = 2;
        } else {
            traitSlots = 3;
        }
        LOGGER.debug("特性槽位更新：T{} -> {} 槽", tier, traitSlots);
    }
    
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
        // 允许所有非空物品注入特性（排除 Factor 相关物品）
        return !stack.isEmpty() && !stack.getItem().toString().contains("factor");
    }
    
    public void insertItem(int slot, ItemStack stack) {
        if (canInsert(slot, stack)) {
            inventory.set(slot, stack);
            markDirty();
        }
    }
    
    public void clearInventory() {
        for (int i = 0; i < NUM_SLOTS; i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
        resetInfusion();
        markDirty();
    }
    
    // ==================== Getters ====================
    
    public int getCurrentTier() { return currentTier; }
    public int getTraitSlots() { return traitSlots; }
    public boolean isStructureValid() { return structureValid; }
    public boolean isInfusing() { return infusionProgress > 0; }
    public int getInfusionProgress() { return infusionProgress; }
    public int getInfusionTimeTotal() { return infusionTimeTotal; }
    public double getFactorCost() { return factorCost; }
    public double getFactorConsumed() { return factorConsumed; }
    public List<String> getTargetTraits() { return new ArrayList<>(targetTraits); }
    
    /**
     * 获取注入进度百分比
     */
    public double getInfusionProgressPercentage() {
        if (infusionTimeTotal == 0) return 0;
        return (infusionProgress * 100.0) / infusionTimeTotal;
    }
    
    /**
     * 获取当前成功率
     */
    public double getCurrentSuccessRate() {
        return calculateSuccessRate();
    }
    
    /**
     * 获取调试信息
     */
    public String getDebugInfo() {
        if (isInfusing()) {
            return String.format("T%d | %d 槽 | %.1f%% | %.1f%% 成功",
                currentTier, traitSlots, getInfusionProgressPercentage(), getCurrentSuccessRate() * 100);
        }
        return String.format("T%d | %d 槽 | Idle", currentTier, traitSlots);
    }
    
    // ==================== NBT ====================
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("CurrentTier", currentTier);
        nbt.putInt("TraitSlots", traitSlots);
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
        
        // 注入状态
        nbt.putInt("InfusionProgress", infusionProgress);
        nbt.putInt("InfusionTimeTotal", infusionTimeTotal);
        nbt.putDouble("FactorCost", factorCost);
        nbt.putDouble("FactorConsumed", factorConsumed);
        
        // 目标特性
        NbtList traitsNbt = new NbtList();
        for (String traitId : targetTraits) {
            traitsNbt.add(net.minecraft.nbt.NbtString.of(traitId));
        }
        nbt.put("TargetTraits", traitsNbt);
        
        // 工作物品
        if (!workingItem.isEmpty()) {
            nbt.put("WorkingItem", workingItem.toNbt(registries));
        }
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        currentTier = nbt.getInt("CurrentTier");
        traitSlots = nbt.getInt("TraitSlots");
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
        
        // 注入状态
        infusionProgress = nbt.getInt("InfusionProgress");
        infusionTimeTotal = nbt.getInt("InfusionTimeTotal");
        factorCost = nbt.getDouble("FactorCost");
        factorConsumed = nbt.getDouble("FactorConsumed");
        
        // 目标特性
        targetTraits.clear();
        NbtList traitsNbt = nbt.getList("TargetTraits", net.minecraft.nbt.NbtElement.STRING_TYPE);
        for (int i = 0; i < traitsNbt.size(); i++) {
            targetTraits.add(traitsNbt.getString(i));
        }
        
        // 工作物品
        if (nbt.contains("WorkingItem")) {
            workingItem = ItemStack.fromNbt(registries, nbt.getCompound("WorkingItem")).orElse(ItemStack.EMPTY);
        } else {
            workingItem = ItemStack.EMPTY;
        }
        
        // 兼容旧数据
        if (traitSlots == 0) {
            updateTraitSlots(currentTier);
        }
    }
}
