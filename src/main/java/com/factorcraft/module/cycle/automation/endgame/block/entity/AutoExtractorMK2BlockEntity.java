package com.factorcraft.module.cycle.automation.endgame.block.entity;

import com.factorcraft.module.cycle.automation.endgame.block.AutoExtractorMK2Block;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities;
import com.factorcraft.module.cycle.automation.endgame.screen.AutoExtractorMK2ScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 自动提取器 MK-II BlockEntity
 * 高速 Factor 提取设备逻辑
 */
public class AutoExtractorMK2BlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final int SLOT_COUNT = 9;
    public static final int MAX_OVERCLOCK = 4;
    public static final int BASE_PROGRESS = 20;
    
    private final SimpleInventory inventory = new SimpleInventory(SLOT_COUNT);
    private int progress = 0;
    private int maxProgress = BASE_PROGRESS;
    private int overClockLevel = 1;
    private int factorCharge = 0;
    private static final int MAX_FACTOR_CHARGE = 10000;
    
    public AutoExtractorMK2BlockEntity(BlockPos pos, BlockState state) {
        super(EndgameAutomationBlockEntities.AUTO_EXTRACTOR_MK2, pos, state);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.auto_extractor_mk2");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoExtractorMK2ScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
        nbt.putInt("OverClockLevel", overClockLevel);
        nbt.putInt("FactorCharge", factorCharge);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), lookup);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
        overClockLevel = nbt.getInt("OverClockLevel");
        factorCharge = nbt.getInt("FactorCharge");
        Inventories.readNbt(nbt, inventory.getHeldStacks(), lookup);
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, AutoExtractorMK2BlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        entity.updateOverclock();
        
        if (entity.factorCharge >= entity.getFactorCostPerTick()) {
            entity.factorCharge -= entity.getFactorCostPerTick();
            entity.progress += entity.overClockLevel;
            
            if (entity.progress >= entity.maxProgress) {
                entity.progress = 0;
                entity.extractItems();
            }
        }
        
        entity.markDirty();
    }
    
    private void updateOverclock() {
        if (world == null) return;
        int targetOverclock = Math.min(MAX_OVERCLOCK, (factorCharge / 1000) + 1);
        if (targetOverclock != overClockLevel) {
            overClockLevel = targetOverclock;
            maxProgress = BASE_PROGRESS / overClockLevel;
        }
    }
    
    private void extractItems() {
        if (world == null) return;
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (inventory.getStack(i).isEmpty()) {
                break;
            }
        }
    }
    
    private int getFactorCostPerTick() {
        return 10 * overClockLevel;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public int getMaxProgress() {
        return maxProgress;
    }
    
    public int getOverClockLevel() {
        return overClockLevel;
    }
    
    public int getFactorCharge() {
        return factorCharge;
    }
    
    public void addFactorCharge(int amount) {
        factorCharge = Math.min(MAX_FACTOR_CHARGE, factorCharge + amount);
    }
    
    public SimpleInventory getInventory() {
        return inventory;
    }
}
