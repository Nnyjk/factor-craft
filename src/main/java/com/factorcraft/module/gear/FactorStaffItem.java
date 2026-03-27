package com.factorcraft.module.gear;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Factor 法杖
 * 
 * 特性：
 * - 右键释放 Factor 能量（消耗 Factor 造成伤害/效果）
 * - T3 解锁：范围效果
 * - T5 解锁：自定义能量释放模式
 */
public class FactorStaffItem extends Item implements IGear {
    
    private final int tier;
    private final double maxFactorStorage;
    private final double factorCostPerUse;
    
    public FactorStaffItem(int tier, Settings settings) {
        super(settings.maxCount(1));
        this.tier = tier;
        this.maxFactorStorage = 200.0 * tier;
        this.factorCostPerUse = 10.0 * tier;
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        if (!world.isClient()) {
            if (canUseFactorBoost(stack, user)) {
                consumeFactor(stack, factorCostPerUse);
                user.sendMessage(Text.literal("⚡ 法杖释放能量！").formatted(Formatting.LIGHT_PURPLE), true);
            } else {
                user.sendMessage(Text.literal("❌ Factor 能量不足").formatted(Formatting.RED), true);
            }
        }
        
        return ActionResult.SUCCESS;
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
        return (int)maxFactorStorage;
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
        return getFactorStorage(stack) >= factorCostPerUse;
    }
    
    public int getTier() {
        return tier;
    }
}
