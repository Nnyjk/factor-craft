package com.factorcraft.module.cycle.automation.block.entity.crafter;

import com.factorcraft.module.cycle.automation.block.crafter.AutoCrafterBlock;
import com.factorcraft.module.cycle.automation.block.entity.AutomationBlockEntities;
import com.factorcraft.module.cycle.automation.component.CraftingJob;
import com.factorcraft.module.cycle.automation.component.RecipePattern;
import com.factorcraft.module.cycle.energy.component.FactorConsumerComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

/**
 * 自动合成器 BlockEntity
 */
public class AutoCrafterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, SidedInventory {
    private final DefaultedList<ItemStack> inventory;
    private RecipePattern currentPattern;
    private CraftingJob currentJob;
    private int craftTime;
    private static final int CRAFT_TIME_TOTAL = 200; // 10 秒 (20 tick/秒)
    private static final int INVENTORY_SIZE = 10; // 9 格输入 + 1 格输出
    
    public AutoCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(AutomationBlockEntities.AUTO_CRAFTER, pos, state);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.craftTime = 0;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.auto_crafter");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        // 简化：返回 null，实际需要创建 AutoCrafterScreenHandler
        return null;
    }
    
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }
    
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}; // 输入槽
        } else if (side == Direction.DOWN) {
            return new int[]{9}; // 输出槽
        }
        return new int[0];
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot < 9; // 前 9 格可以插入
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 9; // 只有输出格可以提取
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
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
        inventory.set(slot, stack);
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public void clear() {
        inventory.clear();
    }
    
    /**
     * 每 tick 调用
     */
    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
        if (!(blockEntity instanceof AutoCrafterBlockEntity crafter)) {
            return;
        }
        
        if (world.isClient) {
            return;
        }
        
        // 检查是否有能源
        // FactorConsumerComponent consumer = crafter.getComponent(FactorConsumerComponent.KEY);
        // if (consumer != null && !consumer.hasFactor(1)) {
        //     return;
        // }
        
        // 如果有正在进行的任务
        if (crafter.currentJob != null && !crafter.currentJob.isCompleted()) {
            crafter.currentJob.tick();
            crafter.craftTime++;
            
            if (crafter.currentJob.isCompleted()) {
                // 任务完成，输出物品
                ItemStack output = crafter.currentJob.getOutput();
                crafter.setStack(9, output);
                crafter.currentJob = null;
                crafter.craftTime = 0;
                
                // 更新活跃状态
                world.setBlockState(pos, state.with(AutoCrafterBlock.ACTIVE, false));
            }
            
            crafter.markDirty();
            return;
        }
        
        // 尝试开始新任务
        if (crafter.canCraft()) {
            crafter.startCrafting();
            world.setBlockState(pos, state.with(AutoCrafterBlock.ACTIVE, true));
        }
    }
    
    private boolean canCraft() {
        // 检查是否有配方和足够的材料
        if (currentPattern == null) {
            return false;
        }
        
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            inputs.add(getStack(i));
        }
        
        return currentPattern.matches(DefaultedList.copyOf(inputs));
    }
    
    private void startCrafting() {
        if (currentPattern != null) {
            currentJob = new CraftingJob(currentPattern, CRAFT_TIME_TOTAL);
            craftTime = 0;
        }
    }
    
    public void setPattern(RecipePattern pattern) {
        this.currentPattern = pattern;
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, inventory, lookup);
        craftTime = nbt.getInt("CraftTime");
        if (nbt.contains("Pattern")) {
            currentPattern = RecipePattern.fromNbt(nbt.getCompound("Pattern"), lookup);
        }
        if (nbt.contains("Job")) {
            currentJob = CraftingJob.fromNbt(nbt.getCompound("Job"), lookup);
        }
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, inventory, lookup);
        nbt.putInt("CraftTime", craftTime);
        if (currentPattern != null) {
            nbt.put("Pattern", currentPattern.toNbt(lookup));
        }
        if (currentJob != null) {
            nbt.put("Job", currentJob.toNbt(lookup));
        }
    }
}
