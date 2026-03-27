package com.factorcraft.module.gear;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 装备强化管理器
 * 
 * 处理装备强化逻辑和验证
 */
public class GearUpgradeManager {
    private static GearUpgradeManager instance;
    
    private GearUpgradeManager() {
    }
    
    public static GearUpgradeManager getInstance() {
        if (instance == null) {
            instance = new GearUpgradeManager();
        }
        return instance;
    }
    
    /**
     * 尝试强化装备
     * 
     * @param player 玩家
     * @param gear 装备
     * @param material 强化材料
     * @return 是否成功
     */
    public boolean tryUpgrade(PlayerEntity player, ItemStack gear, ItemStack material) {
        if (!(gear.getItem() instanceof IGear)) {
            player.sendMessage(Text.literal("❌ 这不是 Factor 装备").formatted(Formatting.RED), true);
            return false;
        }
        
        IGear iGear = (IGear) gear.getItem();
        GearUpgradeLevel currentLevel = iGear.getUpgradeLevel(gear);
        
        // 检查是否已满级
        if (currentLevel == GearUpgradeLevel.T5) {
            player.sendMessage(Text.literal("❌ 装备已达到最高强化等级").formatted(Formatting.RED), true);
            return false;
        }
        
        // 检查材料是否匹配
        if (!isValidUpgradeMaterial(gear, material)) {
            player.sendMessage(Text.literal("❌ 强化材料不匹配").formatted(Formatting.RED), true);
            return false;
        }
        
        // 执行强化
        GearUpgradeLevel nextLevel = GearUpgradeLevel.fromLevel(currentLevel.getLevel() + 1);
        iGear.setUpgradeLevel(gear, nextLevel);
        
        // 如果达到 T3，自动解锁能力
        if (nextLevel.getLevel() >= 3) {
            iGear.unlockAbility(gear);
        }
        
        player.sendMessage(
            Text.literal("✅ 强化成功！装备升级为 ")
                .append(Text.literal(nextLevel.getDisplayName()).formatted(Formatting.GOLD)),
            true
        );
        
        return true;
    }
    
    /**
     * 验证强化材料是否匹配
     */
    private boolean isValidUpgradeMaterial(ItemStack gear, ItemStack material) {
        // TODO: 根据装备类型和当前等级验证材料
        // 这里简化实现，返回 true
        return !material.isEmpty();
    }
    
    /**
     * 修复装备耐久
     */
    public boolean repairGear(PlayerEntity player, ItemStack gear, ItemStack repairMaterial) {
        if (gear.isDamageable() && !gear.isDamaged()) {
            player.sendMessage(Text.literal("ℹ️ 装备耐久已满").formatted(Formatting.YELLOW), true);
            return false;
        }
        
        // TODO: 实现修复逻辑
        return true;
    }
    
    /**
     * 获取强化所需材料数量
     */
    public int getUpgradeMaterialCount(GearUpgradeLevel currentLevel) {
        // T1->T2: 4 个，T2->T3: 8 个，T3->T4: 16 个，T4->T5: 32 个
        return 4 * (1 << currentLevel.getLevel());
    }
}
