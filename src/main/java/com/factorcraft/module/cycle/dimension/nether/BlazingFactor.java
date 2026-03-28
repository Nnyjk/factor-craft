package com.factorcraft.module.cycle.dimension.nether;

import com.factorcraft.module.cycle.factor.FactorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 炽热 Factor 组件
 * 下界特有 Factor，具有燃烧效果和快速扩散特性
 */
public class BlazingFactor implements FactorComponent {
    public static final String ID = "blazing_factor";
    public static final int DIFFUSION_RATE = 3; // 快速扩散
    public static final double CONCENTRATION_THRESHOLD = 50.0; // 触发效果的浓度阈值
    public static final int BURN_DURATION = 100; // 燃烧持续时间 (ticks)
    
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
        // 高浓度时产生火焰粒子
        if (concentration >= CONCENTRATION_THRESHOLD && !world.isClient) {
            // 环境效果可以在这里添加
        }
    }
    
    @Override
    public void applyEntityEffect(Entity entity, double concentration) {
        // 炽热 Factor 对周围生物提供火焰抗性
        if (entity instanceof LivingEntity living && concentration >= CONCENTRATION_THRESHOLD) {
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, BURN_DURATION, 0, true, false));
        }
    }
}
