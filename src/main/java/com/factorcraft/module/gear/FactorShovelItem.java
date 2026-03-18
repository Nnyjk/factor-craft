package com.factorcraft.module.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 铲
 * 
 * 消耗 Factor 加速挖掘泥土、沙砾等
 */
public class FactorShovelItem extends FactorToolItem {
    
    public FactorShovelItem(ToolMaterial material, int tier, Settings settings) {
        super(material, tier, settings);
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getMiningSpeed(stack, state);
        
        if (baseSpeed > 1.0f) {
            Boolean boosted = stack.get(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED);
            if (Boolean.TRUE.equals(boosted)) {
                return baseSpeed + getEfficiencyBoost();
            }
        }
        
        return baseSpeed;
    }
    
    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (miner instanceof ServerPlayerEntity) {
            ItemStack stack = miner.getMainHandStack();
            if (stack.getItem() instanceof FactorShovelItem) {
                if (canUseFactorBoost(stack, miner)) {
                    stack.set(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED, true);
                    consumeFactor(stack, getFactorCostPerUse());
                } else {
                    stack.set(com.factorcraft.component.FactorCraftDataComponents.FACTOR_BOOSTED, false);
                }
            }
        }
        return true;
    }
    
    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
        setFactorStorage(stack, 0.0);
    }
}