package com.factorcraft.module.cycle.dimension.end;

import com.factorcraft.module.cycle.factor.FactorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 虚空 Factor 组件
 * 末地特有 Factor，具有稳定浓度和缓慢扩散特性
 */
public class VoidFactor implements FactorComponent {
    public static final String ID = "void_factor";
    public static final int DIFFUSION_RATE = 1; // 缓慢扩散
    public static final double CONCENTRATION_THRESHOLD = 30.0; // 触发效果的浓度阈值
    public static final int STABILITY_DURATION = 100; // 稳定性效果持续时间
    
    @Override
    public String getId() {
        return ID;
    }
    
    @Override
    public int getDiffusionRate() {
        return DIFFUSION_RATE;
    }
    
    @Override
    public double getConcentrationThreshold() {
        return CONCENTRATION_THRESHOLD;
    }
    
    @Override
    public void applyEnvironmentEffect(World world, BlockPos pos, double concentration) {
        // 虚空 Factor 稳定环境
        if (concentration >= CONCENTRATION_THRESHOLD && !world.isClient) {
            // 环境效果可以在这里添加
        }
    }
    
    @Override
    public void applyEntityEffect(Entity entity, double concentration) {
        // 虚空 Factor 提供缓落效果
        if (entity instanceof LivingEntity living && concentration >= CONCENTRATION_THRESHOLD) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, STABILITY_DURATION, 0, true, false));
        }
    }
}
