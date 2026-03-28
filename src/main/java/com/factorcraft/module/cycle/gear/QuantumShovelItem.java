package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;

/**
 * 量子铲 - 终极铲
 * 
 * 特性:
 * - 超越下界合金的挖掘速度
 * - 耐久度 10,000
 * - 3x3 范围挖掘
 * - 路径创建 (右键点击草地/泥土创建路径)
 */
public class QuantumShovelItem extends ShovelItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 1.5f;
    private static final float ATTACK_SPEED = -3.0f;
    
    public QuantumShovelItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            // 扣除耐久度
            stack.damage(1, miner, EquipmentSlot.MAINHAND);
            
            // 消耗 Factor 充能
            Integer charge = stack.get(FactorGearComponents.FACTOR_CHARGE);
            if (charge != null && charge > 0) {
                stack.set(FactorGearComponents.FACTOR_CHARGE, charge - 1);
            }
        }
        return true;
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getMiningSpeed(stack, state);
        
        // 检查是否有 Factor 充能加成
        Integer charge = stack.get(FactorGearComponents.FACTOR_CHARGE);
        if (charge != null && charge > 0) {
            return baseSpeed * 2.0f;
        }
        
        return baseSpeed;
    }
}
