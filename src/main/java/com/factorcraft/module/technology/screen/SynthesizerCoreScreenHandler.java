package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.SynthesizerCoreBlockEntity;
import com.factorcraft.module.technology.machine.SynthesisConfig;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成核心屏幕处理器
 * 
 * 显示配方列表、合成进度、Factor 缓冲区
 */
public class SynthesizerCoreScreenHandler extends MachineCoreScreenHandler {
    
    /**
     * 同步数据记录
     */
    public record SyncData(
        int tier,
        double factorBuffer,
        double maxBuffer,
        boolean structureValid,
        double efficiency,
        String dimension,
        String currentRecipeId,
        int craftProgress,
        int craftTimeTotal,
        double factorNeeded,
        double factorConsumed
    ) {
        public static final PacketCodec<RegistryByteBuf, SyncData> PACKET_CODEC = 
            new PacketCodec<RegistryByteBuf, SyncData>() {
                @Override
                public SyncData decode(RegistryByteBuf buf) {
                    return new SyncData(
                        buf.readInt(),
                        buf.readDouble(),
                        buf.readDouble(),
                        buf.readBoolean(),
                        buf.readDouble(),
                        buf.readString(),
                        buf.readString(),
                        buf.readInt(),
                        buf.readInt(),
                        buf.readDouble(),
                        buf.readDouble()
                    );
                }

                @Override
                public void encode(RegistryByteBuf buf, SyncData data) {
                    buf.writeInt(data.tier());
                    buf.writeDouble(data.factorBuffer());
                    buf.writeDouble(data.maxBuffer());
                    buf.writeBoolean(data.structureValid());
                    buf.writeDouble(data.efficiency());
                    buf.writeString(data.dimension());
                    buf.writeString(data.currentRecipeId());
                    buf.writeInt(data.craftProgress());
                    buf.writeInt(data.craftTimeTotal());
                    buf.writeDouble(data.factorNeeded());
                    buf.writeDouble(data.factorConsumed());
                }
            };
    }
    
    private final SynthesizerCoreBlockEntity synthesizer;
    
    // 同步数据
    private int tier;
    private double factorBuffer;
    private double maxBuffer;
    private boolean structureValid;
    private double efficiency;
    private String dimension;
    
    // 合成状态
    private String currentRecipeId;
    private int craftProgress;
    private int craftTimeTotal;
    private double factorNeeded;
    private double factorConsumed;
    
    // 可用配方列表
    private List<SynthesisConfig.UpgradeRecipe> availableRecipes;
    
    /**
     * 服务端构造函数
     */
    public SynthesizerCoreScreenHandler(int syncId, PlayerInventory playerInventory,
                                          SynthesizerCoreBlockEntity blockEntity) {
        super(ModScreens.SYNTHESIZER_CORE, syncId, playerInventory, blockEntity);
        this.synthesizer = blockEntity;
        this.availableRecipes = new ArrayList<>();
        
        syncFromBlockEntity();
        updateAvailableRecipes();
    }
    
    /**
     * 客户端构造函数 - 从 SyncData 创建
     */
    public SynthesizerCoreScreenHandler(int syncId, PlayerInventory playerInventory, SyncData data) {
        super(ModScreens.SYNTHESIZER_CORE, syncId, playerInventory, null);
        this.synthesizer = null;
        this.availableRecipes = new ArrayList<>();
        
        // 从同步数据填充
        this.tier = data.tier();
        this.factorBuffer = data.factorBuffer();
        this.maxBuffer = data.maxBuffer();
        this.structureValid = data.structureValid();
        this.efficiency = data.efficiency();
        this.dimension = data.dimension();
        this.currentRecipeId = data.currentRecipeId();
        this.craftProgress = data.craftProgress();
        this.craftTimeTotal = data.craftTimeTotal();
        this.factorNeeded = data.factorNeeded();
        this.factorConsumed = data.factorConsumed();
    }
    
    /**
     * 从 BlockEntity 同步数据
     */
    private void syncFromBlockEntity() {
        if (synthesizer != null) {
            this.tier = synthesizer.getCurrentTier();
            this.factorBuffer = synthesizer.getFactorBuffer();
            this.maxBuffer = synthesizer.getMaxBuffer();
            this.structureValid = synthesizer.isStructureValid();
            this.efficiency = SynthesisConfig.getEfficiency(tier);
            
            if (synthesizer.getWorld() != null) {
                this.dimension = synthesizer.getWorld().getRegistryKey().getValue().toString();
            } else {
                this.dimension = "minecraft:overworld";
            }
            
            // 合成状态
            this.currentRecipeId = synthesizer.getCurrentRecipeId() != null 
                ? synthesizer.getCurrentRecipeId() : "";
            this.craftProgress = synthesizer.getCraftProgress();
            this.craftTimeTotal = synthesizer.getCraftTimeTotal();
            this.factorNeeded = synthesizer.getFactorNeeded();
            this.factorConsumed = synthesizer.getFactorConsumed();
        }
    }
    
    /**
     * 更新可用配方列表
     */
    private void updateAvailableRecipes() {
        availableRecipes.clear();
        
        // 根据当前 Tier 获取可用配方
        for (SynthesisConfig.UpgradeRecipe recipe : SynthesisConfig.UPGRADE_RECIPES.values()) {
            if (recipe.fromTier() == tier) {
                availableRecipes.add(recipe);
            }
        }
    }
    
    /**
     * 获取同步数据
     */
    public SyncData getSyncData() {
        syncFromBlockEntity();
        return new SyncData(
            tier, factorBuffer, maxBuffer, structureValid, efficiency, dimension,
            currentRecipeId != null ? currentRecipeId : "",
            craftProgress, craftTimeTotal, factorNeeded, factorConsumed
        );
    }
    
    // ==================== Getters ====================
    
    public int getTier() { return tier; }
    public double getFactorBuffer() { return factorBuffer; }
    public double getMaxBuffer() { return maxBuffer; }
    public boolean isStructureValid() { return structureValid; }
    public double getEfficiency() { return efficiency; }
    public String getDimension() { return dimension; }
    
    public String getCurrentRecipeId() { return currentRecipeId; }
    public int getCraftProgress() { return craftProgress; }
    public int getCraftTimeTotal() { return craftTimeTotal; }
    public double getFactorNeeded() { return factorNeeded; }
    public double getFactorConsumed() { return factorConsumed; }
    
    public boolean isCrafting() { return currentRecipeId != null && !currentRecipeId.isEmpty(); }
    
    public List<SynthesisConfig.UpgradeRecipe> getAvailableRecipes() { 
        return availableRecipes; 
    }
    
    public double getBufferPercentage() {
        return maxBuffer > 0 ? (factorBuffer / maxBuffer) * 100 : 0;
    }
    
    public double getCraftProgressPercentage() {
        return craftTimeTotal > 0 ? (craftProgress * 100.0) / craftTimeTotal : 0;
    }
    
    /**
     * 获取 Factor 消耗速率（每 tick）
     */
    public double getFactorConsumptionRate() {
        if (!isCrafting() || craftTimeTotal <= 0) return 0;
        return factorNeeded / craftTimeTotal;
    }
    
    public double getDimensionEfficiency() {
        return SynthesisConfig.getDimensionEfficiency(dimension, tier);
    }
    
    public String getRecommendedDimension() {
        return SynthesisConfig.getRecommendedDimension(tier);
    }
    
    /**
     * 开始指定配方的合成
     */
    public boolean startCrafting(String recipeId) {
        if (synthesizer != null) {
            return synthesizer.startCrafting(recipeId);
        }
        return false;
    }
    
    /**
     * 取消当前合成
     */
    public void cancelCrafting() {
        if (synthesizer != null) {
            synthesizer.cancelCrafting();
        }
    }
}