package com.factorcraft.module.cycle.automation.block.entity.harvester;

import com.factorcraft.module.cycle.automation.block.entity.AutomationBlockEntities;
import com.factorcraft.module.cycle.automation.block.harvester.AutoHarvesterBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

/**
 * 自动收割机 BlockEntity
 */
public class AutoHarvesterBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, SidedInventory {
    private final DefaultedList<ItemStack> inventory;
    private static final int INVENTORY_SIZE = 27; // 3x9 物品栏
    private int harvestTimer;
    private static final int HARVEST_INTERVAL = 100; // 5 秒
    
    public AutoHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(AutomationBlockEntities.AUTO_HARVESTER, pos, state);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.harvestTimer = 0;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.auto_harvester");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null; // 简化
    }
    
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }
    
    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] slots = new int[INVENTORY_SIZE];
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            slots[i] = i;
        }
        return slots;
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false; // 收割机只输出不输入
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true; // 所有槽都可以提取
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
        if (!(blockEntity instanceof AutoHarvesterBlockEntity harvester)) {
            return;
        }
        
        if (world.isClient) {
            return;
        }
        
        harvester.harvestTimer++;
        
        if (harvester.harvestTimer >= HARVEST_INTERVAL) {
            harvester.harvestTimer = 0;
            harvester.tryHarvest(world, pos);
        }
    }
    
    private void tryHarvest(World world, BlockPos pos) {
        // 检查下方是否为农田
        BlockPos farmlandPos = pos.down();
        BlockState farmlandState = world.getBlockState(farmlandPos);
        
        if (farmlandState.getBlock() != Blocks.FARMLAND) {
            return;
        }
        
        // 检查周围 4 个方向是否有作物
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        
        for (Direction dir : directions) {
            BlockPos cropPos = pos.offset(dir);
            BlockState cropState = world.getBlockState(cropPos);
            
            if (cropState.getBlock() instanceof CropBlock crop) {
                if (crop.isMature(cropState)) {
                    // 收获作物
                    var drops = cropState.getDrops(world, cropPos, null);
                    for (ItemStack drop : drops) {
                        addDrop(drop);
                    }
                    
                    // 重新种植
                    world.setBlockState(cropPos, crop.withAge(0));
                    markDirty();
                    break; // 每次只收获一个方向的作物
                }
            }
        }
    }
    
    private void addDrop(ItemStack stack) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack slot = getStack(i);
            if (slot.isEmpty()) {
                setStack(i, stack.copy());
                return;
            } else if (ItemStack.areItemsAndComponentsEqual(slot, stack) && slot.getCount() < slot.getMaxCount()) {
                slot.increment(stack.getCount());
                return;
            }
        }
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, inventory, lookup);
        harvestTimer = nbt.getInt("HarvestTimer");
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, inventory, lookup);
        nbt.putInt("HarvestTimer", harvestTimer);
    }
}
