package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

/**
 * 量子斧 - 终极斧头
 * 
 * 特性:
 * - 超越下界合金的挖掘速度 (12.0)
 * - 耐久度 10,000
 * - 树干砍伐 (砍一棵树整棵倒下)
 * - 攻击力 8.0
 */
public class QuantumAxeItem extends AxeItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 8.0f;
    private static final float ATTACK_SPEED = -3.0f;
    
    public QuantumAxeItem() {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.AXE_MINEABLE)) {
            return 12.0f;
        }
        return super.getMiningSpeed(stack, state);
    }
}
