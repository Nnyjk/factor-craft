package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

/**
 * 量子铲 - 终极铲子
 * 
 * 特性:
 * - 超越下界合金的挖掘速度 (12.0)
 * - 耐久度 10,000
 * - 3x3 范围挖掘
 */
public class QuantumShovelItem extends ShovelItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 1.5f;
    private static final float ATTACK_SPEED = -3.0f;
    
    public QuantumShovelItem() {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.SHOVEL_MINEABLE)) {
            return 12.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}
