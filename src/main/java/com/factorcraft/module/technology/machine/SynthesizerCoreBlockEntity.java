package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 合成核心 - 用 Factor 合成物品（材料升级）
 * 
 * 结构：远古合成阵 T1 → 远古锻造台 T2 → 命运铸造炉 T3 → 创世熔炉 T4 → 本源祭坛 T5
 * 
 * 合成公式:
 * 实际合成时间 = 基础时间 / (结构效率 × 维度效率)
 * 
 * 物品槽布局:
 * - 槽位 0: 输入槽 (64 格，存放待升级材料)
 * - 槽位 1: 输出槽 (64 格，存放合成产物)
 */
public class SynthesizerCoreBlockEntity extends MachineBlockEntity implements Inventory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Synthesizer");
    
    // ==================== 物品槽 ====================
    
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int NUM_SLOTS = 2;
    
    private final DefaultedList<ItemStack> inventory;
    
    // ==================== 状态 ====================
    
    private double factorBuffer;
    private double maxBuffer;
    private int currentTier;
    private boolean structureValid;
    
    // 当前合成
    private String currentRecipeId;
    private int craftProgress;
    private int craftTimeTotal;
    private double factorNeeded;
    private double factorConsumed;
    
    // 缓存
    private long lastStructureCheck;
    private static final long STRUCTURE_CHECK_INTERVAL = 100;
    
    public SynthesizerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModMachines.SYNTHESIZER_CORE, pos, state);
        this.inventory = DefaultedList.ofSize(NUM_SLOTS, ItemStack.EMPTY);
        this.factorBuffer = 0.0;
        this.maxBuffer = SynthesisConfig.MAX_BUFFER_T1;
        this.currentTier = 1;
        this.structureValid = false;
        this.lastStructureCheck = 0;
        this.craftProgress = 0;
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
                LOGGER.debug("合成结构等级变更：T{} at {}", currentTier, pos);
            }
        }
        
        // 自动开始合成（如果有输入且没有进行中的合成）
        if (currentRecipeId == null && !getStack(INPUT_SLOT).isEmpty()) {
            tryStartCrafting(world);
        }
        
        // 处理合成进度
        if (currentRecipeId != null) {
            tickCrafting(world);
        }
        
        markDirty();
    }
    
    /**
     * 尝试根据输入物品自动开始合成
     */
    private void tryStartCrafting(World world) {
        ItemStack inputStack = getStack(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        
        String itemId = Registries.ITEM.getId(inputStack.getItem()).toString();
        SynthesisConfig.UpgradeRecipe recipe = SynthesisConfig.getRecipeForInput(itemId);
        
        if (recipe != null && recipe.fromTier() == currentTier) {
            // 检查输入数量是否足够
            if (inputStack.getCount() >= recipe.inputCount()) {
                // 检查输出槽是否有空间
                if (canInsertOutput(recipe.outputItem(), recipe.outputCount())) {
                    startCrafting(recipe.id());
                }
            }
        }
    }
    
    /**
     * 处理合成进度
     */
    private void tickCrafting(World world) {
        SynthesisConfig.UpgradeRecipe recipe = SynthesisConfig.UPGRADE_RECIPES.get(currentRecipeId);
        if (recipe == null) {
            cancelCrafting();
            return;
        }
        
        // 检查输入物品是否仍然存在且数量足够
        ItemStack inputStack = getStack(INPUT_SLOT);
        String itemId = Registries.ITEM.getId(inputStack.getItem()).toString();
        if (!itemId.equals(recipe.inputItem()) || inputStack.getCount() < recipe.inputCount()) {
            LOGGER.debug("输入物品不足，取消合成");
            cancelCrafting();
            return;
        }
        
        // 获取维度并计算实际合成时间
        String dimension = world.getRegistryKey().getValue().toString();
        int actualCraftTime = SynthesisConfig.getActualCraftTime(currentTier, dimension);
        
        // 更新总时间（如果维度效率变化）
        if (craftTimeTotal != actualCraftTime) {
            // 按比例调整当前进度
            double progressRatio = (double) craftProgress / craftTimeTotal;
            craftProgress = (int) (actualCraftTime * progressRatio);
            craftTimeTotal = actualCraftTime;
        }
        
        // 检查是否有足够 Factor
        double factorPerTick = factorNeeded / craftTimeTotal;
        if (factorBuffer >= factorPerTick) {
            // 消耗 Factor
            factorBuffer -= factorPerTick;
            factorConsumed += factorPerTick;
            craftProgress++;
            
            // 检查是否完成
            if (craftProgress >= craftTimeTotal) {
                completeCrafting(world, recipe);
            }
        }
        // Factor 不足，暂停但不取消
    }
    
    /**
     * 完成合成
     */
    private void completeCrafting(World world, SynthesisConfig.UpgradeRecipe recipe) {
        LOGGER.info("合成完成：{} x{} → {} x{}", 
            recipe.inputItem(), recipe.inputCount(),
            recipe.outputItem(), recipe.outputCount());
        
        // 消耗输入物品
        ItemStack inputStack = getStack(INPUT_SLOT);
        inputStack.decrement(recipe.inputCount());
        
        // 产出物品到输出槽
        ItemStack outputStack = getStack(OUTPUT_SLOT);
        Item outputItem = Registries.ITEM.get(Identifier.of(recipe.outputItem()));
        
        if (outputStack.isEmpty()) {
            // 输出槽为空，直接放入
            setStack(OUTPUT_SLOT, new ItemStack(outputItem, recipe.outputCount()));
        } else if (outputStack.getItem() == outputItem) {
            // 输出槽已有相同物品，堆叠
            outputStack.increment(recipe.outputCount());
        } else {
            // 输出槽物品不匹配，丢弃（不应该发生）
            LOGGER.warn("输出槽物品不匹配，丢弃产物：{}", recipe.outputItem());
            setStack(OUTPUT_SLOT, new ItemStack(outputItem, recipe.outputCount()));
        }
        
        // 重置状态
        currentRecipeId = null;
        craftProgress = 0;
        craftTimeTotal = 0;
        factorNeeded = 0;
        factorConsumed = 0;
        
        markDirty();
    }
    
    /**
     * 检查输出槽是否可以插入指定物品
     */
    private boolean canInsertOutput(String itemId, int count) {
        ItemStack outputStack = getStack(OUTPUT_SLOT);
        
        if (outputStack.isEmpty()) {
            return true;
        }
        
        Item item = Registries.ITEM.get(Identifier.of(itemId));
        if (outputStack.getItem() == item) {
            return outputStack.getCount() + count <= outputStack.getMaxCount();
        }
        
        return false;
    }
    
    /**
     * 取消合成
     */
    public void cancelCrafting() {
        // 不返还已消耗的 Factor
        currentRecipeId = null;
        craftProgress = 0;
        craftTimeTotal = 0;
        factorNeeded = 0;
        factorConsumed = 0;
    }
    
    /**
     * 开始合成
     * 
     * @param recipeId 配方 ID
     * @return 是否成功开始
     */
    public boolean startCrafting(String recipeId) {
        if (currentRecipeId != null) {
            return false; // 已有合成进行中
        }
        
        SynthesisConfig.UpgradeRecipe recipe = SynthesisConfig.UPGRADE_RECIPES.get(recipeId);
        if (recipe == null) {
            return false;
        }
        
        // 检查 Tier 是否匹配
        if (recipe.fromTier() != currentTier) {
            LOGGER.debug("配方 Tier 不匹配：需要 T{}, 当前 T{}", recipe.fromTier(), currentTier);
            return false;
        }
        
        // 检查输入物品
        ItemStack inputStack = getStack(INPUT_SLOT);
        String itemId = Registries.ITEM.getId(inputStack.getItem()).toString();
        if (!itemId.equals(recipe.inputItem()) || inputStack.getCount() < recipe.inputCount()) {
            LOGGER.debug("输入物品不足：需要 {} x{}, 当前 {}", 
                recipe.inputItem(), recipe.inputCount(), 
                inputStack.isEmpty() ? "空" : inputStack.getCount());
            return false;
        }
        
        // 检查输出槽空间
        if (!canInsertOutput(recipe.outputItem(), recipe.outputCount())) {
            LOGGER.debug("输出槽空间不足");
            return false;
        }
        
        // 获取维度并计算实际合成时间
        String dimension = "minecraft:overworld";
        if (world != null) {
            dimension = world.getRegistryKey().getValue().toString();
        }
        int actualCraftTime = SynthesisConfig.getActualCraftTime(currentTier, dimension);
        double dimensionEfficiency = SynthesisConfig.getDimensionEfficiency(dimension, currentTier);
        
        this.currentRecipeId = recipeId;
        this.craftProgress = 0;
        this.factorNeeded = recipe.factorCost();
        this.factorConsumed = 0;
        this.craftTimeTotal = actualCraftTime;
        
        LOGGER.info("开始合成：{} (T{}), Factor: {}, 时间：{} ticks (维度效率：{:.1f}%)", 
            recipeId, currentTier, factorNeeded, craftTimeTotal, dimensionEfficiency * 100);
        
        return true;
    }
    
    /**
     * 向缓冲区添加 Factor
     * 
     * @param amount 添加量
     * @return 实际添加量
     */
    public double addFactor(double amount) {
        double space = maxBuffer - factorBuffer;
        double actual = Math.min(space, amount);
        factorBuffer += actual;
        markDirty();
        return actual;
    }
    
    /**
     * 从缓冲区提取 Factor
     * 
     * @param amount 请求量
     * @return 实际提取量
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
            // 匹配合成器相关的蓝图
            if ((patternId.contains("synthesizer") || patternId.contains("forge") || 
                 patternId.contains("altar") || patternId.contains("workbench"))
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
        maxBuffer = SynthesisConfig.getMaxBuffer(tier);
    }
    
    // ==================== Inventory 接口实现 ====================
    
    @Override
    public int size() {
        return NUM_SLOTS;
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }
    
    @Override
    public ItemStack getStack(int slot) {
        if (slot < 0 || slot >= NUM_SLOTS) return ItemStack.EMPTY;
        return inventory.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot < 0 || slot >= NUM_SLOTS) return ItemStack.EMPTY;
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        
        ItemStack removed = stack.split(amount);
        markDirty();
        return removed;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        if (slot < 0 || slot >= NUM_SLOTS) return ItemStack.EMPTY;
        ItemStack stack = inventory.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        
        inventory.set(slot, ItemStack.EMPTY);
        markDirty();
        return stack;
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= NUM_SLOTS) return;
        inventory.set(slot, stack);
        markDirty();
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
    }
    
    @Override
    public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) {
        if (world == null || pos == null) return false;
        return player.squaredDistanceTo(
            (double) pos.getX() + 0.5,
            (double) pos.getY() + 0.5,
            (double) pos.getZ() + 0.5
        ) <= 64.0;
    }
    
    public void clear() {
        inventory.clear();
        markDirty();
    }
    
    // ==================== Getters ====================
    
    public double getFactorBuffer() { return factorBuffer; }
    public double getMaxBuffer() { return maxBuffer; }
    public int getCurrentTier() { return currentTier; }
    public boolean isStructureValid() { return structureValid; }
    public boolean isCrafting() { return currentRecipeId != null; }
    public String getCurrentRecipeId() { return currentRecipeId; }
    public int getCraftProgress() { return craftProgress; }
    public int getCraftTimeTotal() { return craftTimeTotal; }
    public double getFactorNeeded() { return factorNeeded; }
    public double getFactorConsumed() { return factorConsumed; }
    
    /**
     * 获取合成进度百分比
     */
    public double getCraftProgressPercentage() {
        if (craftTimeTotal == 0) return 0;
        return (craftProgress * 100.0) / craftTimeTotal;
    }
    
    /**
     * 获取缓冲区百分比
     */
    public double getBufferPercentage() {
        return maxBuffer > 0 ? (factorBuffer / maxBuffer) * 100 : 0;
    }
    
    /**
     * 获取结构名称
     */
    public String getStructureName() {
        return switch (currentTier) {
            case 1 -> "远古合成阵";
            case 2 -> "远古锻造台";
            case 3 -> "命运铸造炉";
            case 4 -> "创世熔炉";
            case 5 -> "本源祭坛";
            default -> "基础结构";
        };
    }
    
    /**
     * 获取当前配方名称（本地化）
     */
    public String getRecipeDisplayName() {
        if (currentRecipeId == null) return "无";
        SynthesisConfig.UpgradeRecipe recipe = SynthesisConfig.UPGRADE_RECIPES.get(currentRecipeId);
        if (recipe == null) return currentRecipeId;
        return recipe.id();
    }
    
    /**
     * 获取所有可用配方
     */
    public java.util.List<SynthesisConfig.UpgradeRecipe> getAvailableRecipes() {
        return SynthesisConfig.UPGRADE_RECIPES.values().stream()
            .filter(r -> r.fromTier() == currentTier)
            .toList();
    }
    
    /**
     * 获取调试信息
     */
    public String getDebugInfo() {
        if (currentRecipeId != null) {
            return String.format("T%d | %s | %.0f/%.0f F | %.1f%%",
                currentTier, currentRecipeId, factorBuffer, maxBuffer, getCraftProgressPercentage());
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
        Inventories.writeNbt(nbt, inventory, registries);
        
        // 合成状态
        if (currentRecipeId != null) {
            nbt.putString("CurrentRecipeId", currentRecipeId);
            nbt.putInt("CraftProgress", craftProgress);
            nbt.putInt("CraftTimeTotal", craftTimeTotal);
            nbt.putDouble("FactorNeeded", factorNeeded);
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
        Inventories.readNbt(nbt, inventory, registries);
        
        // 合成状态
        if (nbt.contains("CurrentRecipeId")) {
            currentRecipeId = nbt.getString("CurrentRecipeId");
            craftProgress = nbt.getInt("CraftProgress");
            craftTimeTotal = nbt.getInt("CraftTimeTotal");
            factorNeeded = nbt.getDouble("FactorNeeded");
            factorConsumed = nbt.getDouble("FactorConsumed");
        }
        
        // 兼容旧数据
        if (maxBuffer == 0) {
            maxBuffer = SynthesisConfig.getMaxBuffer(currentTier);
        }
    }
}
