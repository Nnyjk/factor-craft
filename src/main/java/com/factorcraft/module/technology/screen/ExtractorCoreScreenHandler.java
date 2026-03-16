package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.ExtractorCoreBlockEntity;
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
 * 提取核心 ScreenHandler
 * 
 * 负责同步服务器数据到客户端 GUI
 */
public class ExtractorCoreScreenHandler extends ScreenHandler {
    
    private final BlockEntity blockEntity;
    private final ServerWorld world;
    private final ScreenHandlerContext context;
    private final BlockPos pos;
    
    // 同步数据
    private double factorStorage = 0;
    private double maxStorage = 1000;
    private double efficiency = 1.0;
    private double dimensionEfficiency = 1.0;
    private double extractRate = 0;
    private int progressPercent = 0;
    private int tier = 1;
    private boolean structureValid = false;
    private String dimension = "";
    private String recommendedDimension = "主世界";
    
    // 无参构造器供客户端使用
    public ExtractorCoreScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, ScreenHandlerContext.EMPTY, BlockPos.ORIGIN);
    }
    
    // 服务端构造器
    public ExtractorCoreScreenHandler(int syncId, PlayerInventory playerInventory, 
                                       BlockEntity blockEntity, ScreenHandlerContext context, BlockPos pos) {
        super(ModScreens.EXTRACTOR_CORE, syncId);
        this.blockEntity = blockEntity;
        this.context = context;
        this.pos = pos;
        this.world = (playerInventory.player instanceof ServerPlayerEntity sp) 
            ? sp.getServerWorld() : null;
        
        // 初始化数据
        if (blockEntity instanceof ExtractorCoreBlockEntity extractor) {
            updateFromBlockEntity(extractor);
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return context.get((world, pos) -> {
            return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
        }, true);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // 暂不实现快速移动（未来添加物品槽后实现）
        return ItemStack.EMPTY;
    }
    
    /**
     * 从 BlockEntity 更新数据
     */
    private void updateFromBlockEntity(ExtractorCoreBlockEntity extractor) {
        if (extractor == null || extractor.getWorld() == null) return;
        
        this.factorStorage = extractor.getFactorStorage();
        this.maxStorage = extractor.getMaxStorage();
        this.efficiency = extractor.getStructureEfficiency();
        this.dimensionEfficiency = extractor.getDimensionEfficiency(extractor.getWorld());
        this.extractRate = extractor.getLastExtractRate();
        this.progressPercent = extractor.getProgressPercentage();
        this.tier = extractor.getCurrentTier();
        this.structureValid = extractor.isStructureValid();
        this.dimension = extractor.getWorld().getRegistryKey().getValue().toString();
        this.recommendedDimension = extractor.getRecommendedDimensionName();
    }
    
    /**
     * 接收同步数据（从网络包）
     */
    public void receiveSyncData(double storage, double max, double eff, double dimEff, 
                                double rate, double progress, int tier, boolean valid, 
                                String dim, String recDim) {
        this.factorStorage = storage;
        this.maxStorage = max;
        this.efficiency = eff;
        this.dimensionEfficiency = dimEff;
        this.extractRate = rate;
        this.progressPercent = (int) progress;
        this.tier = tier;
        this.structureValid = valid;
        this.dimension = dim;
        this.recommendedDimension = recDim;
    }
    
    // ==================== Getters ====================
    
    public double getFactorStorage() { return factorStorage; }
    public double getMaxStorage() { return maxStorage; }
    
    public double getStoragePercentage() { 
        return maxStorage > 0 ? (factorStorage / maxStorage) * 100 : 0; 
    }
    
    public double getEfficiency() { return efficiency; }
    public double getDimensionEfficiency() { return dimensionEfficiency; }
    public double getExtractRate() { return extractRate; }
    public int getProgressPercentage() { return progressPercent; }
    public int getTier() { return tier; }
    public boolean isStructureValid() { return structureValid; }
    public String getDimension() { return dimension; }
    public String getRecommendedDimension() { return recommendedDimension; }
    
    public BlockPos getPos() { return pos; }
    public BlockEntity getBlockEntity() { return blockEntity; }
}