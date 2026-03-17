package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.MultiblockDetector;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
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
 * 物品槽:
 * - 输入槽 (0): 输入材料
 * - 输出槽 (1): 合成产物
 */
public class SynthesizerCoreBlockEntity extends MachineBlockEntity implements MachineInventory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Synthesizer");
    
    // ==================== 物品槽 ====================
    
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }
    
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
        
        // 处理合成进度
        if (currentRecipeId != null) {
            tickCrafting(world);
        }
        
        markDirty();
    }
    
    /**
     * 检查输出槽是否可以接受产物
     */
    private boolean canAcceptOutput(String outputItem, int outputCount) {
        ItemStack outputStack = getStack(OUTPUT_SLOT);
        
        // 输出槽为空，可以接受
        if (outputStack.isEmpty()) {
            return true;
        }
        
        // 检查是否是相同物品
        if (!outputStack.getItem().toString().equals(outputItem)) {
            return false;
        }
        
        // 检查是否可以堆叠
        int maxCount = outputStack.getMaxCount();
        return outputStack.getCount() + outputCount <= maxCount;
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
     * 完成合成，产出物品
     */
    private void completeCrafting(World world, SynthesisConfig.UpgradeRecipe recipe) {
        String outputItem = recipe.outputItem();
        int outputCount = recipe.outputCount();
        
        // 尝试产出物品到输出槽
        int produced = outputItemsToOutputSlot(outputItem, outputCount);
        
        if (produced > 0) {
            LOGGER.info("合成完成：{} x{}", outputItem, produced);
        }
        
        if (produced < outputCount) {
            // 部分产出失败（输出槽满），掉落剩余物品
            int dropped = outputCount - produced;
            dropItems(world, outputItem, dropped);
            LOGGER.info("输出槽满，掉落 {} x{}", outputItem, dropped);
        }
        
        // 重置合成状态
        currentRecipeId = null;
        craftProgress = 0;
        craftTimeTotal = 0;
        factorNeeded = 0;
        factorConsumed = 0;
    }
    
    /**
     * 产出物品到输出槽
     * 
     * @param itemId 物品 ID
     * @param count 数量
     * @return 实际产出数量
     */
    private int outputItemsToOutputSlot(String itemId, int count) {
        ItemStack outputStack = getStack(OUTPUT_SLOT);
        
        // 输出槽为空，创建新堆
        if (outputStack.isEmpty()) {
            // 创建物品堆
            net.minecraft.item.Item item = net.minecraft.registry.Registries.ITEM.get(
                net.minecraft.util.Identifier.tryParse(itemId)
            );
            if (item == null) {
                LOGGER.warn("未知物品：{}", itemId);
                return 0;
            }
            
            int maxCount = Math.min(count, item.getMaxCount());
            setStack(OUTPUT_SLOT, new ItemStack(item, maxCount));
            return maxCount;
        }
        
        // 检查是否是相同物品
        if (!outputStack.getItem().toString().equals(itemId)) {
            LOGGER.warn("输出槽物品不匹配：期望 {}, 实际 {}", itemId, outputStack.getItem().toString());
            return 0;
        }
        
        // 堆叠到现有物品堆
        int maxCount = outputStack.getMaxCount();
        int space = maxCount - outputStack.getCount();
        int toAdd = Math.min(count, space);
        
        if (toAdd > 0) {
            outputStack.increment(toAdd);
        }
        
        return toAdd;
    }
    
    /**
     * 掉落物品到世界
     */
    private void dropItems(World world, String itemId, int count) {
        if (world.isClient || count <= 0) return;
        
        net.minecraft.item.Item item = net.minecraft.registry.Registries.ITEM.get(
            net.minecraft.util.Identifier.tryParse(itemId)
        );
        if (item == null) {
            LOGGER.warn("无法掉落未知物品：{}", itemId);
            return;
        }
        
        // 在方块上方掉落物品
        ItemEntity itemEntity = new ItemEntity(
            world,
            pos.getX() + 0.5,
            pos.getY() + 1.0,
            pos.getZ() + 0.5,
            new ItemStack(item, count)
        );
        
        // 设置拾取延迟
        itemEntity.setToDefaultPickupDelay();
        
        world.spawnEntity(itemEntity);
        LOGGER.debug("掉落物品：{} x{}", itemId, count);
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
            LOGGER.debug("已有合成进行中");
            return false;
        }
        
        SynthesisConfig.UpgradeRecipe recipe = SynthesisConfig.UPGRADE_RECIPES.get(recipeId);
        if (recipe == null) {
            LOGGER.debug("配方不存在：{}", recipeId);
            return false;
        }
        
        // 检查 Tier 是否匹配
        if (recipe.fromTier() != currentTier) {
            LOGGER.debug("配方 Tier 不匹配：需要 T{}, 当前 T{}", recipe.fromTier(), currentTier);
            return false;
        }
        
        // 检查输出槽是否可以接受产物
        if (!canAcceptOutput(recipe.outputItem(), recipe.outputCount())) {
            LOGGER.debug("输出槽无法接受产物");
            return false;
        }
        
        this.currentRecipeId = recipeId;
        this.craftProgress = 0;
        this.factorNeeded = recipe.factorCost();
        this.factorConsumed = 0;
        
        // 计算实际合成时间
        // 注意：这里需要维度信息，实际实现中需要传入
        this.craftTimeTotal = recipe.craftTime();
        
        LOGGER.info("开始合成：{}, Factor: {}, 时间：{} ticks", 
            recipeId, factorNeeded, craftTimeTotal);
        
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
        // 使用 id 作为显示名称，未来可以添加翻译
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
        
        // 物品库存
        writeInventoryNbt(nbt, registries);
        
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
        
        // 物品库存
        readInventoryNbt(nbt, registries);
        
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
