package com.factorcraft.module.integration.detection;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.integration.config.IntegrationConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 第三方模组内容自动检测器。
 * 扫描已加载模组的物品并识别可集成内容。
 */
public final class ModContentDetector {
    private final IntegrationConfig config;
    private final Set<String> detectedMods = new HashSet<>();
    private final List<DetectedContent> detectedItems = new ArrayList<>();
    
    public ModContentDetector(IntegrationConfig config) {
        this.config = config;
    }
    
    /**
     * 执行内容检测。
     */
    public void detect() {
        if (!config.enabled() || !config.autoDetect()) {
            FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 自动检测已禁用");
            return;
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 开始自动检测第三方模组内容...");
        
        // 获取所有已加载模组
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            String modId = modContainer.getMetadata().getId();
            // 跳过 Minecraft 和 Fabric 核心模组
            if (isCoreMod(modId)) {
                return;
            }
            
            // 检查黑白名单
            if (isBlacklisted(modId)) {
                FactorCraftMod.LOGGER.debug("[FactorCraft:Integration] 模组 {} 在黑名单中，跳过", modId);
                return;
            }
            
            if (!config.whitelist().isEmpty() && !config.whitelist().contains(modId)) {
                return; // 有白名单但不在白名单中
            }
            
            detectedMods.add(modId);
            FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 检测到模组: {}", modId);
        });
        
        // 扫描物品注册表
        scanItemRegistry();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 检测完成: {} 个模组, {} 个可集成物品", 
            detectedMods.size(), detectedItems.size());
    }
    
    /**
     * 扫描物品注册表。
     */
    private void scanItemRegistry() {
        Registries.ITEM.forEach(item -> {
            Identifier id = Registries.ITEM.getId(item);
            String namespace = id.getNamespace();
            
            // 只处理已检测模组的物品
            if (!detectedMods.contains(namespace)) {
                return;
            }
            
            // 分析物品类型
            DetectedContent.ContentCategory category = analyzeItemCategory(item);
            if (category != null) {
                detectedItems.add(new DetectedContent(
                    id.toString(),
                    namespace,
                    category,
                    item
                ));
            }
        });
    }
    
    /**
     * 分析物品类别。
     */
    private DetectedContent.ContentCategory analyzeItemCategory(Item item) {
        // 基于物品属性推断类别
        var itemComponents = item.getComponents();
        
        // 检查是否为工具
        if (itemComponents.get(net.minecraft.component.DataComponentTypes.TOOL) != null) {
            // 进一步区分工具类型
            String itemId = Registries.ITEM.getId(item).getPath();
            if (itemId.contains("sword") || itemId.contains("dagger") || itemId.contains("katana")) {
                return DetectedContent.ContentCategory.WEAPON;
            }
            return DetectedContent.ContentCategory.TOOL;
        }
        
        // 检查是否为护甲
        if (item instanceof net.minecraft.item.ArmorItem) {
            return DetectedContent.ContentCategory.ARMOR;
        }
        
        // 家具检测较复杂，暂时跳过
        return null;
    }
    
    /**
     * 判断是否为核心模组。
     */
    private boolean isCoreMod(String modId) {
        return modId.equals("minecraft") ||
               modId.equals("fabric") ||
               modId.equals("fabricloader") ||
               modId.startsWith("fabric-") ||
               modId.startsWith("java");
    }
    
    /**
     * 判断是否在黑名单中。
     */
    private boolean isBlacklisted(String modId) {
        return config.blacklist().contains(modId);
    }
    
    /**
     * 获取检测到的模组列表。
     */
    public Set<String> getDetectedMods() {
        return detectedMods;
    }
    
    /**
     * 获取检测到的可集成内容。
     */
    public List<DetectedContent> getDetectedItems() {
        return detectedItems;
    }
    
    /**
     * 重置检测结果。
     */
    public void reset() {
        detectedMods.clear();
        detectedItems.clear();
    }
}