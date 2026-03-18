package com.factorcraft.module.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/**
 * Factor 剑
 * 
 * 消耗 Factor 增加伤害
 */
public class FactorSwordItem extends FactorToolItem {
    
    protected final double damageBoost;
    
    public FactorSwordItem(ToolMaterial material, int tier, Settings settings) {
        super(material, tier, settings);
        this.damageBoost = getDamageBoostForTier(tier);
    }
    
    protected static double getDamageBoostForTier(int tier) {
        return switch (tier) {
            case 1 -> 2.0;
            case 2 -> 3.0;
            case 3 -> 5.0;
            case 4 -> 7.0;
            case 5 -> 10.0;
            default -> 2.0;
        };
    }
    
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 检查是否可以使用 Factor 加成伤害
        if (attacker instanceof ServerPlayerEntity player) {
            if (canUseFactorBoost(stack, player)) {
                // 额外伤害通过事件处理，这里只消耗 Factor
                consumeFactor(stack, getFactorCostPerUse());
                // 标记攻击为 Factor 增强
                stack.set(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED, true);
            } else {
                stack.set(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED, false);
            }
        }
        
        return super.postHit(stack, target, attacker);
    }
    
    /**
     * 获取 Factor 加成后的额外伤害
     */
    public double getExtraDamage(ItemStack stack) {
        Boolean boosted = stack.get(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED);
        return Boolean.TRUE.equals(boosted) ? damageBoost : 0.0;
    }
    
    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
        setFactorStorage(stack, 0.0);
    }
}