package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.ConsumerCoreBlockEntity;
import com.factorcraft.module.technology.machine.ConsumptionConfig;
import com.factorcraft.module.technology.machine.MachineInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * 消耗核心 ScreenHandler
 * 
 * 显示 Factor 产出、消耗进度，支持物品输入
 */
public class ConsumerCoreScreenHandler extends ScreenHandler {
    
    private final BlockEntity blockEntity;
    private final ServerWorld world;
    private final ScreenHandlerContext context;
    private final BlockPos pos;
    
    // 客户端缓存数据
    private double factorStorage = 0;
    private double maxStorage = 1000;
    private int tier = 1;
    private boolean structureValid = false;
    private String currentRecipeId = "";
    private int consumeProgress = 0;
    private int consumeTimeTotal = 0;
    private double factorToOutput = 0;
    private double efficiency = 1.0;
    
    public ConsumerCoreScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, ScreenHandlerContext.EMPTY, BlockPos.ORIGIN);
    }
    
    public ConsumerCoreScreenHandler(int syncId, PlayerInventory playerInventory, 
                                       BlockEntity blockEntity, ScreenHandlerContext context, BlockPos pos) {
        super(ModScreens.CONSUMER_CORE, syncId);
        this.blockEntity = blockEntity;
        this.context = context;
        this.pos = pos;
        this.world = (playerInventory.player instanceof ServerPlayerEntity sp) 
            ? sp.getServerWorld() : null;
        
        // 添加机器物品槽
        if (blockEntity instanceof MachineInventory inventory) {
            // 输入槽
            addSlot(new Slot(inventory, ConsumerCoreBlockEntity.INPUT_SLOT, 56, 35));
            // 输出槽
            addSlot(new Slot(inventory, ConsumerCoreBlockEntity.OUTPUT_SLOT, 116, 35));
        }
        
        // 添加玩家物品栏
        addPlayerInventorySlots(playerInventory);
        
        if (blockEntity instanceof ConsumerCoreBlockEntity consumer) {
            updateFromBlockEntity(consumer);
        }
    }
    
    /**
     * 添加玩家物品栏槽位
     */
    private void addPlayerInventorySlots(PlayerInventory playerInventory) {
        // 玩家背包 (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 
                    8 + col * 18, 84 + row * 18));
            }
        }
        
        // 玩家快捷栏 (1x9)
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return context.get((world, pos) -> 
            player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            // 机器槽位: 0-1, 玩家背包: 2-37
            if (invSlot < 2) {
                // 从机器移到玩家背包
                if (!this.insertItem(originalStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到机器输入槽
                if (!this.insertItem(originalStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return newStack;
    }
    
    private void updateFromBlockEntity(ConsumerCoreBlockEntity consumer) {
        if (consumer == null) return;
        
        this.factorStorage = consumer.getFactorStorage();
        this.maxStorage = consumer.getMaxStorage();
        this.tier = consumer.getCurrentTier();
        this.structureValid = consumer.isStructureValid();
        this.currentRecipeId = consumer.getCurrentRecipeId() != null ? consumer.getCurrentRecipeId() : "";
        this.consumeProgress = consumer.getConsumeProgress();
        this.consumeTimeTotal = consumer.getConsumeTimeTotal();
        this.factorToOutput = consumer.getFactorToOutput();
        this.efficiency = ConsumptionConfig.getEfficiency(tier);
    }
    
    // ==================== Getters ====================
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    public double getStoragePercentage() { return maxStorage > 0 ? (factorStorage / maxStorage) * 100 : 0; }
    public int getTier() { return tier; }
    public boolean isStructureValid() { return structureValid; }
    public String getCurrentRecipeId() { return currentRecipeId; }
    public int getConsumeProgress() { return consumeProgress; }
    public int getConsumeTimeTotal() { return consumeTimeTotal; }
    public double getFactorToOutput() { return factorToOutput; }
    public double getEfficiency() { return efficiency; }
    public boolean isConsuming() { return currentRecipeId != null && !currentRecipeId.isEmpty(); }
    public double getConsumeProgressPercentage() { return consumeTimeTotal > 0 ? (consumeProgress * 100.0) / consumeTimeTotal : 0; }
    
    public String getStructureName() {
        return switch (tier) {
            case 1 -> "灵魂燃烧器";
            case 2 -> "灵魂熔炉";
            case 3 -> "深渊吞噬者";
            case 4 -> "混沌裂隙";
            case 5 -> "永恒炉心";
            default -> "基础结构";
        };
    }
    
    public BlockPos getPos() { return pos; }
    public BlockEntity getBlockEntity() { return blockEntity; }
}