package com.factorcraft.module.cycle.automation.endgame.block.entity;

import com.factorcraft.module.cycle.automation.endgame.block.FactorPumpMK2Block;
import com.factorcraft.module.cycle.automation.endgame.screen.FactorPumpMK2ScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factor 泵 MK-II BlockEntity
 * 高速 Factor 液体传输逻辑
 */
public class FactorPumpMK2BlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final int MAX_TANK_CAPACITY = 100000; // 100k mB
    public static final int TRANSFER_RATE = 1000; // 1000 mB/tick
    
    private int factorAmount = 0;
    private Map<Direction, Boolean> outputSides = new EnumMap<>(Direction.class);
    private boolean isActive = false;
    
    public FactorPumpMK2BlockEntity(BlockPos pos, BlockState state) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities.FACTOR_PUMP_MK2, pos, state);
        
        // 初始化输出方向
        for (Direction dir : Direction.values()) {
            outputSides.put(dir, false);
        }
        outputSides.put(Direction.UP, true); // 默认向上输出
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.factor_pump_mk2");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FactorPumpMK2ScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("FactorAmount", factorAmount);
        nbt.putBoolean("IsActive", isActive);
        
        NbtList sidesList = new NbtList();
        for (Map.Entry<Direction, Boolean> entry : outputSides.entrySet()) {
            NbtCompound sideNbt = new NbtCompound();
            sideNbt.putString("Direction", entry.getKey().getName());
            sideNbt.putBoolean("Output", entry.getValue());
            sidesList.add(sideNbt);
        }
        nbt.put("OutputSides", sidesList);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        factorAmount = nbt.getInt("FactorAmount");
        isActive = nbt.getBoolean("IsActive");
        
        if (nbt.contains("OutputSides", NbtElement.LIST_TYPE)) {
            NbtList sidesList = nbt.getList("OutputSides", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < sidesList.size(); i++) {
                NbtCompound sideNbt = sidesList.getCompound(i);
                String dirName = sideNbt.getString("Direction");
                boolean isOutput = sideNbt.getBoolean("Output");
                for (Direction dir : Direction.values()) {
                    if (dir.getName().equals(dirName)) {
                        outputSides.put(dir, isOutput);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * _tick_方法 - 每 tick 执行
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorPumpMK2BlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        entity.transferFactor();
        entity.markDirty();
    }
    
    private void transferFactor() {
        if (factorAmount < TRANSFER_RATE) {
            return;
        }
        
        // 向配置的输出方向传输 Factor
        for (Map.Entry<Direction, Boolean> entry : outputSides.entrySet()) {
            if (entry.getValue() && factorAmount >= TRANSFER_RATE) {
                Direction outputDir = entry.getKey();
                BlockPos targetPos = pos.offset(outputDir);
                
                if (world != null) {
                    BlockEntity targetEntity = world.getBlockEntity(targetPos);
                    // 简化的传输逻辑 - 实际实现需要检查目标是否接受 Factor
                    // 这里仅作占位实现
                }
            }
        }
    }
    
    public int getFactorAmount() {
        return factorAmount;
    }
    
    public int getMaxCapacity() {
        return MAX_TANK_CAPACITY;
    }
    
    public void addFactor(int amount) {
        factorAmount = Math.min(MAX_TANK_CAPACITY, factorAmount + amount);
    }
    
    public boolean isOutputSide(Direction side) {
        return outputSides.getOrDefault(side, false);
    }
    
    public void toggleOutputSide(Direction side) {
        outputSides.put(side, !outputSides.getOrDefault(side, false));
        markDirty();
    }
    
    public boolean isActive() {
        return isActive;
    }
}
