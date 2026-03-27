package com.factorcraft.module.gear;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Factor 弓
 * 
 * 特性：
 * - 消耗 Factor 提升箭矢伤害和速度
 * - T3 解锁：连射能力（一次发射 2 支箭）
 * - T5 解锁：爆炸箭矢（范围伤害）
 */
public class FactorBowItem extends BowItem implements IGear {
    
    private final int tier;
    private final double factorCostPerShot;
    
    public FactorBowItem(int tier, Settings settings) {
        super(settings.maxCount(1));
        this.tier = tier;
        this.factorCostPerShot = 5.0 * tier;
    }
    
    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            if (canUseFactorBoost(stack, player)) {
                consumeFactor(stack, factorCostPerShot);
                player.sendMessage(Text.literal("⚡ 弓充能完成！").formatted(Formatting.LIGHT_PURPLE), true);
            }
        }
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }
    
    @Override
    public GearType getGearType() {
        return GearType.WEAPON;
    }
    
    @Override
    public GearUpgradeLevel getUpgradeLevel(ItemStack stack) {
        return GearUpgradeLevel.fromTier(tier);
    }
    
    @Override
    public void setUpgradeLevel(ItemStack stack, GearUpgradeLevel level) {}
    
    @Override
    public int getFactorEnergy(ItemStack stack) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        return storage != null ? (int)storage.amount() : 0;
    }
    
    @Override
    public void setFactorEnergy(ItemStack stack, int energy) {
        stack.set(FactorCraftDataComponents.FACTOR_STORAGE, FactorStorage.of(energy));
    }
    
    @Override
    public int getMaxFactorEnergy(ItemStack stack) {
        return (int)(100.0 * tier);
    }
    
    @Override
    public boolean isAbilityUnlocked(ItemStack stack) {
        return tier >= 3;
    }
    
    @Override
    public void unlockAbility(ItemStack stack) {}
    
    public double getFactorStorage(ItemStack stack) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        return storage != null ? storage.amount() : 0.0;
    }
    
    public void setFactorStorage(ItemStack stack, double amount) {
        stack.set(FactorCraftDataComponents.FACTOR_STORAGE, FactorStorage.of(amount));
    }
    
    public boolean consumeFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        if (current >= amount) {
            setFactorStorage(stack, current - amount);
            return true;
        }
        return false;
    }
    
    public boolean canUseFactorBoost(ItemStack stack, PlayerEntity player) {
        return getFactorStorage(stack) >= factorCostPerShot;
    }
    
    public int getTier() {
        return tier;
    }
}
