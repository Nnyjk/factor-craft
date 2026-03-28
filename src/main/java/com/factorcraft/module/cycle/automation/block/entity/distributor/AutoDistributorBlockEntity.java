package com.factorcraft.module.cycle.automation.block.entity.distributor;

import com.factorcraft.module.cycle.automation.block.distributor.AutoDistributorBlock;
import com.factorcraft.module.cycle.automation.block.entity.AutomationBlockEntities;
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

/**
 * 自动分配器 BlockEntity
 * 将输入物品均匀分配到相邻的输出容器
 */
public class AutoDistributorBlockEntity extends BlockEntity implements SidedInventory, NamedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private int distributeTimer = 0;
    private static final int DISTRIBUTE_INTERVAL = 20; // 每 20 tick 分配一次
    
    public AutoDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(AutomationBlockEntities.AUTO_DISTRIBUTOR, pos, state);
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        distributeTimer++;
        if (distributeTimer >= DISTRIBUTE_INTERVAL) {
            distributeTimer = 0;
            distributeItems(world, pos);
        }
    }
    
    /**
     * 将物品分配到相邻容器
     */
    private void distributeItems(World world, BlockPos pos) {
        for (Direction side : Direction.values()) {
            if (side == Direction.UP) continue; // 跳过顶部（输入面）
            
            BlockPos neighborPos = pos.offset(side);
            BlockEntity neighbor = world.getBlockEntity(neighborPos);
            
            if (neighbor instanceof SidedInventory targetInventory) {
                for (int i = 0; i < size(); i++) {
                    ItemStack stack = getStack(i);
                    if (stack.isEmpty()) continue;
                    
                    // 尝试插入到相邻容器
                    ItemStack remainder = insertIntoInventory(targetInventory, stack, side.getOpposite());
                    setStack(i, remainder);
                    
                    if (remainder.isEmpty()) break;
                }
            }
        }
    }
    
    /**
     * 尝试将物品插入到目标容器
     */
    private ItemStack insertIntoInventory(SidedInventory target, ItemStack stack, Direction side) {
        int[] slots = target.getAvailableSlots(side);
        if (slots == null || slots.length == 0) return stack;
        
        for (int slot : slots) {
            ItemStack targetStack = target.getStack(slot);
            
            // 如果目标槽位为空，直接插入
            if (targetStack.isEmpty()) {
                ItemStack copy = stack.copy();
                int maxCount = Math.min(copy.getMaxCount(), target.getMaxCountPerStack());
                copy.setCount(Math.min(stack.getCount(), maxCount));
                target.setStack(slot, copy);
                
                ItemStack remainder = stack.copy();
                remainder.setCount(stack.getCount() - copy.getCount());
                return remainder;
            }
            
            // 如果可以堆叠，尝试合并
            if (ItemStack.canCombine(targetStack, stack)) {
                int maxCount = Math.min(targetStack.getMaxCount(), target.getMaxCountPerStack());
                int space = maxCount - targetStack.getCount();
                
                if (space > 0) {
                    int transferAmount = Math.min(space, stack.getCount());
                    targetStack.increment(transferAmount);
                    
                    ItemStack remainder = stack.copy();
                    remainder.setCount(stack.getCount() - transferAmount);
                    return remainder;
                }
            }
        }
        
        return stack; // 无法插入，返回原物品
    }
    
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}; // 顶部可输入
        }
        return new int[]{}; // 其他面不可直接访问
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction side) {
        return side == Direction.UP;
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return false; // 自动分配，不手动提取
    }
    
    @Override
    public int size() {
        return inventory.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
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
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventories.canPlayerUse(player, this);
    }
    
    @Override
    public void clear() {
        inventory.clear();
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.auto_distributor");
    }
    
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // TODO: 实现分配器 ScreenHandler
        return null;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("DistributeTimer", distributeTimer);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
        distributeTimer = nbt.getInt("DistributeTimer");
    }
}
