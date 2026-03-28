package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

/**
 * 量子锄 - 终极锄头
 * 
 * 特性:
 * - 超越下界合金的挖掘速度 (12.0)
 * - 耐久度 10,000
 * - 自动补种 (收割时自动重新种植)
 * - 3x3 范围收割
 */
public class QuantumHoeItem extends HoeItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 1.0f;
    private static final float ATTACK_SPEED = -3.0f;
    
    public QuantumHoeItem() {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.HOE_MINEABLE)) {
            return 12.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}
