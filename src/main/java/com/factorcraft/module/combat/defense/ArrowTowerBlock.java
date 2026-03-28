package com.factorcraft.module.combat.defense;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 箭塔 - 基础物理攻击防御塔
 */
public class ArrowTowerBlock extends DefenseTowerBlock {
    
    public ArrowTowerBlock(Settings settings) {
        super(settings, 12, 4, 20);
    }
    
    @Override
    public String getTowerType() {
        return "arrow";
    }
    
    @Override
    public void attackTarget(World world, BlockPos pos, LivingEntity target) {
        if (!world.isClient && world instanceof ServerWorld serverWorld && target instanceof MobEntity) {
            ArrowEntity arrow = new ArrowEntity(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, ItemStack.EMPTY, ItemStack.EMPTY);
            
            double dx = target.getX() - arrow.getX();
            double dy = target.getY() + target.getStandingEyeHeight() - arrow.getY();
            double dz = target.getZ() - arrow.getZ();
            arrow.setVelocity(dx, dy, dz, 1.5F, 1.0F);
            arrow.setDamage(4.0D);
            arrow.setCritical(true);
            
            serverWorld.spawnEntity(arrow);
        }
    }
}
