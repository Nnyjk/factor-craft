package com.factorcraft.module.machine.synthesizer;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorData;
import com.factorcraft.factor.Factor;
import com.factorcraft.factor.synthesis.FactorSynthesisRecipe;
import com.factorcraft.factor.synthesis.FactorSynthesisRegistry;
import com.factorcraft.factor.synthesis.FactorSynthesizer;
import com.factorcraft.factor.synthesis.FactorSynthesizer.SynthesisOutput;
import com.factorcraft.module.loot.FactorItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Factor 合成器 BlockEntity
 * 
 * 执行 Factor 合成操作
 * 使用已有的 FactorSynthesisRecipe 配方系统
 */
public class SynthesizerBlockEntity extends BlockEntity 
    implements NamedScreenHandlerFactory, SidedInventory {
    
    // ========== 常量 ==========
    
    /** 输入槽数量 */
    private static final int INPUT_SLOTS = 4;
    /** 输出槽数量 */
    private static final int OUTPUT_SLOTS = 1;
    /** 总槽位数 */
    public static final int INVENTORY_SIZE = INPUT_SLOTS + OUTPUT_SLOTS;
    
    /** 处理时间（ticks） */
    private static final int PROCESSING_TIME = 200; // 10秒
    
    // ========== 物品槽位 ==========
    
    /** 物品列表 */
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    
    // ========== 处理状态 ==========
    
    /** 当前处理进度 */
    private int processingProgress = 0;
    /** 是否正在处理 */
    private boolean isProcessing = false;
    /** 当前配方 */
    @Nullable
    private FactorSynthesisRecipe currentRecipe = null;
    
    // ========== 构造器 ==========
    
    public SynthesizerBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.FACTOR_SYNTHESIZER, pos, state);
    }
    
    public SynthesizerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    // ========== Tick 处理 ==========
    
    public static void tick(World world, BlockPos pos, BlockState state, SynthesizerBlockEntity entity) {
        if (world.isClient) return;
        
        // 检查是否有正在进行的处理
        if (entity.isProcessing) {
            entity.processingProgress++;
            
            // 检查是否完成
            if (entity.processingProgress >= PROCESSING_TIME) {
                entity.completeProcessing();
            }
            
            entity.markDirty();
        } else {
            // 尝试开始新的处理
            entity.tryStartProcessing();
        }
        
        // 更新活跃状态
        boolean shouldBeActive = entity.isProcessing;
        if (state.get(SynthesizerBlock.ACTIVE) != shouldBeActive) {
            world.setBlockState(pos, state.with(SynthesizerBlock.ACTIVE, shouldBeActive));
        }
    }
    
    // ========== 处理逻辑 ==========
    
    /**
     * 尝试开始处理
     */
    private void tryStartProcessing() {
        // 检查输入槽是否有物品
        if (!hasInputItems()) return;
        
        // 查找匹配的配方
        FactorSynthesisRecipe recipe = findMatchingRecipe();
        if (recipe == null) return;
        
        // 检查输出槽是否有空间
        if (!canOutputFit()) return;
        
        // 开始处理
        this.currentRecipe = recipe;
        this.processingProgress = 0;
        this.isProcessing = true;
        
        markDirty();
    }
    
    /**
     * 完成处理
     */
    private void completeProcessing() {
        if (currentRecipe == null) {
            isProcessing = false;
            return;
        }
        
        // 提取输入 Factor
        List<Factor> inputFactors = extractInputFactors();
        if (inputFactors.isEmpty()) {
            isProcessing = false;
            currentRecipe = null;
            return;
        }
        
        // 执行合成
        SynthesisOutput result = FactorSynthesizer.synthesize(currentRecipe, inputFactors);
        
        if (result.getResult() == FactorSynthesizer.SynthesisResult.SUCCESS) {
            // 成功：输出结果
            for (Factor output : result.getOutputs()) {
                ItemStack outputStack = createFactorItemStack(output);
                insertOutput(outputStack);
            }
        }
        
        // 返还失败物品
        if (!result.getReturned().isEmpty()) {
            for (Factor returned : result.getReturned()) {
                ItemStack returnedStack = createFactorItemStack(returned);
                insertOutput(returnedStack);
            }
        }
        
        // 清空输入槽
        clearInputSlots();
        
        // 重置状态
        isProcessing = false;
        processingProgress = 0;
        currentRecipe = null;
        
        markDirty();
    }
    
    // ========== 辅助方法 ==========
    
    private boolean hasInputItems() {
        for (int i = 0; i < INPUT_SLOTS; i++) {
            if (!inventory.get(i).isEmpty()) return true;
        }
        return false;
    }
    
    private boolean canOutputFit() {
        ItemStack outputSlot = inventory.get(INPUT_SLOTS);
        return outputSlot.isEmpty();
    }
    
    @Nullable
    private FactorSynthesisRecipe findMatchingRecipe() {
        List<Factor> inputFactors = extractInputFactors();
        for (FactorSynthesisRecipe recipe : FactorSynthesisRegistry.getInstance().getAllRecipes()) {
            if (recipe.matchesInput(inputFactors)) {
                return recipe;
            }
        }
        return null;
    }
    
    private List<Factor> extractInputFactors() {
        List<Factor> factors = new ArrayList<>();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                Optional<Factor> factor = FactorItem.getFactor(stack);
                factor.ifPresent(factors::add);
            }
        }
        return factors;
    }
    
    private ItemStack createFactorItemStack(Factor factor) {
        return FactorItem.createFactorStack(factor);
    }
    
    private void insertOutput(ItemStack stack) {
        if (stack.isEmpty()) return;
        inventory.set(INPUT_SLOTS, stack.copy());
    }
    
    private void clearInputSlots() {
        for (int i = 0; i < INPUT_SLOTS; i++) {
            inventory.set(i, ItemStack.EMPTY);
        }
    }
    
    // ========== ScreenHandler ==========
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.factor_synthesizer");
    }
    
    @Override
    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SynthesizerScreenHandler(syncId, playerInventory, this);
    }
    
    // ========== Inventory 实现 ==========
    
    @Override
    public int size() {
        return INVENTORY_SIZE;
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }
    
    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            return inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            inventory.set(slot, stack);
            markDirty();
        }
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public void clear() {
        inventory.clear();
    }
    
    // ========== SidedInventory 实现 ==========
    
    @Override
    public int[] getAvailableSlots(Direction side) {
        // 输入槽：0-3，输出槽：4
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3}; // 上方只能输入
        } else if (side == Direction.DOWN) {
            return new int[]{4}; // 下方只能输出
        }
        return new int[]{0, 1, 2, 3, 4}; // 侧面全部
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot < INPUT_SLOTS; // 只能插入输入槽
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == INPUT_SLOTS; // 只能从输出槽提取
    }
    
    /** 是否正在合成 */
    public boolean isCrafting() {
        return isProcessing;
    }
    
    // ========== NBT 序列化 ==========
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("ProcessingProgress", processingProgress);
        nbt.putBoolean("IsProcessing", isProcessing);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.readNbt(nbt, inventory, registryLookup);
        processingProgress = nbt.getInt("ProcessingProgress");
        isProcessing = nbt.getBoolean("IsProcessing");
    }
    
    // ========== 进度访问器 ==========
    
    public int getProcessingProgress() {
        return processingProgress;
    }
    
    public int getMaxProcessingProgress() {
        return PROCESSING_TIME;
    }
    
    public boolean isProcessing() {
        return isProcessing;
    }
}