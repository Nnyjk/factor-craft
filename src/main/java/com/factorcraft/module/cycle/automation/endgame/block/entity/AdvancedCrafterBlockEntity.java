package com.factorcraft.module.cycle.automation.endgame.block.entity;

import com.factorcraft.module.cycle.automation.endgame.block.AdvancedCrafterBlock;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities;
import com.factorcraft.module.cycle.automation.endgame.screen.AdvancedCrafterScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 高级合成器 BlockEntity
 * 支持 4 个并行合成任务
 */
public class AdvancedCrafterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final int PARALLEL_COUNT = 4;
    public static final int INPUT_SLOTS = 9;
    public static final int OUTPUT_SLOT = 9;
    public static final int SLOTS_PER_TASK = INPUT_SLOTS + 1;
    
    private SimpleInventory[] taskInventories = new SimpleInventory[PARALLEL_COUNT];
    private int[] progress = new int[PARALLEL_COUNT];
    private int[] maxProgress = new int[PARALLEL_COUNT];
    private boolean[] isActive = new boolean[PARALLEL_COUNT];
    
    public AdvancedCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(EndgameAutomationBlockEntities.ADVANCED_CRAFTER, pos, state);
        
        for (int i = 0; i < PARALLEL_COUNT; i++) {
            taskInventories[i] = new SimpleInventory(SLOTS_PER_TASK);
            maxProgress[i] = 20;
        }
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.advanced_crafter");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AdvancedCrafterScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        
        for (int i = 0; i < PARALLEL_COUNT; i++) {
            NbtCompound taskNbt = new NbtCompound();
            Inventories.writeNbt(taskNbt, taskInventories[i].getHeldStacks(), lookup);
            nbt.put("TaskInventory" + i, taskNbt);
            nbt.putInt("Progress" + i, progress[i]);
            nbt.putBoolean("IsActive" + i, isActive[i]);
        }
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        
        for (int i = 0; i < PARALLEL_COUNT; i++) {
            if (nbt.contains("TaskInventory" + i)) {
                NbtCompound taskNbt = nbt.getCompound("TaskInventory" + i);
                DefaultedList<ItemStack> stacks = DefaultedList.ofSize(SLOTS_PER_TASK, ItemStack.EMPTY);
                Inventories.readNbt(taskNbt, stacks, lookup);
                taskInventories[i] = new SimpleInventory(stacks.toArray(new ItemStack[0]));
            }
            progress[i] = nbt.getInt("Progress" + i);
            isActive[i] = nbt.getBoolean("IsActive" + i);
        }
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, AdvancedCrafterBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        for (int i = 0; i < PARALLEL_COUNT; i++) {
            entity.updateTask(i);
        }
        
        entity.markDirty();
    }
    
    private void updateTask(int taskIndex) {
        if (isActive[taskIndex]) {
            progress[taskIndex]++;
            
            if (progress[taskIndex] >= maxProgress[taskIndex]) {
                progress[taskIndex] = 0;
                completeTask(taskIndex);
            }
        } else {
            if (canCraft(taskIndex)) {
                isActive[taskIndex] = true;
            }
        }
    }
    
    private boolean canCraft(int taskIndex) {
        SimpleInventory inv = taskInventories[taskIndex];
        for (int i = 0; i < INPUT_SLOTS; i++) {
            if (!inv.getStack(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    private void completeTask(int taskIndex) {
        isActive[taskIndex] = false;
        SimpleInventory inv = taskInventories[taskIndex];
        for (int i = 0; i < INPUT_SLOTS; i++) {
            inv.setStack(i, ItemStack.EMPTY);
        }
        inv.setStack(OUTPUT_SLOT, new ItemStack(net.minecraft.item.Items.IRON_INGOT, 1));
    }
    
    public SimpleInventory getTaskInventory(int taskIndex) {
        return taskInventories[taskIndex];
    }
    
    public int getProgress(int taskIndex) {
        return progress[taskIndex];
    }
    
    public int getMaxProgress(int taskIndex) {
        return maxProgress[taskIndex];
    }
    
    public boolean isActive(int taskIndex) {
        return isActive[taskIndex];
    }
    
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null) {
            return false;
        }
        return this.world.getBlockState(this.pos).getBlock() == this.getCachedState().getBlock()
            && player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }
}
