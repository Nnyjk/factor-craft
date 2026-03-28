package com.factorcraft.module.cycle.automation.endgame.block.entity;

import com.factorcraft.module.cycle.automation.endgame.block.QuantumStorageBlock;
import com.factorcraft.module.cycle.automation.endgame.screen.QuantumStorageScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 量子仓储单元 BlockEntity
 * 每格可存储 1,000,000 个物品
 */
public class QuantumStorageBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final int SLOT_COUNT = 27; // 3 行 9 列
    public static final int MAX_STACK_SIZE = 1_000_000; // 每格 100 万
    
    private SimpleInventory inventory = new SimpleInventory(SLOT_COUNT) {
        @Override
        public int getMaxCountPerStack() {
            return MAX_STACK_SIZE;
        }
    };
    
    public QuantumStorageBlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities.QUANTUM_STORAGE, pos, state);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.quantum_storage");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new QuantumStorageScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        NbtCompound invNbt = new NbtCompound();
        Inventories.writeNbt(invNbt, inventory.getHeldStacks(), lookup);
        nbt.put("Inventory", invNbt);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        if (nbt.contains("Inventory")) {
            Inventories.readNbt(nbt.getCompound("Inventory"), inventory.getHeldStacks(), lookup);
        }
    }
    
    /**
     * _tick_方法 - 每 tick 执行（用于网络同步等）
     */
    public static void tick(World world, BlockPos pos, BlockState state, QuantumStorageBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 量子仓储单元不需要 tick 逻辑，但保持方法用于未来扩展
        entity.markDirty();
    }
    
    public SimpleInventory getInventory() {
        return inventory;
    }
    
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null) return false;
        return player.squaredDistanceTo((double)this.pos.getX() + 0.5, 
                                        (double)this.pos.getY() + 0.5, 
                                        (double)this.pos.getZ() + 0.5) <= 64.0;
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
}
