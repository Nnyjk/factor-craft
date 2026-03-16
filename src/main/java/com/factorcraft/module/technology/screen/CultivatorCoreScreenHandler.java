package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.CultivatorCoreBlockEntity;
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
 * 培育核心 ScreenHandler
 * 
 * 显示特性注入进度、槽位信息
 */
public class CultivatorCoreScreenHandler extends ScreenHandler {
    
    private final BlockEntity blockEntity;
    private final ServerWorld world;
    private final ScreenHandlerContext context;
    private final BlockPos pos;
    
    // 客户端缓存数据
    private int tier = 1;
    private int traitSlots = 1;
    private int infusionProgress = 0;
    private double factorBuffer = 0;
    
    public CultivatorCoreScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, ScreenHandlerContext.EMPTY, BlockPos.ORIGIN);
    }
    
    public CultivatorCoreScreenHandler(int syncId, PlayerInventory playerInventory, 
                                        BlockEntity blockEntity, ScreenHandlerContext context, BlockPos pos) {
        super(ModScreens.CULTIVATOR_CORE, syncId);
        this.blockEntity = blockEntity;
        this.context = context;
        this.pos = pos;
        this.world = (playerInventory.player instanceof ServerPlayerEntity sp) 
            ? sp.getServerWorld() : null;
        
        if (blockEntity instanceof CultivatorCoreBlockEntity cultivator) {
            updateFromBlockEntity(cultivator);
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
    
    private void updateFromBlockEntity(CultivatorCoreBlockEntity cultivator) {
        if (cultivator == null) return;
        
        this.tier = cultivator.getCurrentTier();
        this.traitSlots = cultivator.getTraitSlots();
        this.infusionProgress = 0; // cultivator.getInfusionProgress() if available
        this.factorBuffer = 0; // cultivator.getFactorBuffer() if available
    }
    
    // ==================== Getters ====================
    
    public int getTier() { return tier; }
    public int getTraitSlots() { return traitSlots; }
    public int getInfusionProgress() { return infusionProgress; }
    public double getFactorBuffer() { return factorBuffer; }
    
    public String getStructureName() {
        return switch (tier) {
            case 1 -> "命运织机";
            case 2 -> "灵魂编织器";
            case 3 -> "命运祭坛";
            case 4 -> "命运圣所";
            case 5 -> "轮回之门";
            default -> "基础结构";
        };
    }
}