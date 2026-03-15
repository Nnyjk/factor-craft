package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.ConsumerCoreBlockEntity;
import com.factorcraft.module.technology.machine.ConsumptionConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * 消耗核心 ScreenHandler
 * 
 * 显示 Factor 产出、消耗进度
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
        
        if (blockEntity instanceof ConsumerCoreBlockEntity consumer) {
            updateFromBlockEntity(consumer);
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return context.get((world, pos) -> 
            player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
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
}