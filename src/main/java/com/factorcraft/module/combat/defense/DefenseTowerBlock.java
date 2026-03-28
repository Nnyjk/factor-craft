package com.factorcraft.module.combat.defense;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 防御塔基类
 */
public abstract class DefenseTowerBlock extends Block {
    
    protected final int range;
    protected final int damage;
    protected final int fireRate;
    
    public DefenseTowerBlock(Settings settings, int range, int damage, int fireRate) {
        super(settings);
        this.range = range;
        this.damage = damage;
        this.fireRate = fireRate;
    }
    
    /**
     * 获取防御塔类型名称
     */
    public abstract String getTowerType();
    
    /**
     * 攻击目标
     */
    public abstract void attackTarget(World world, BlockPos pos, LivingEntity target);
}
