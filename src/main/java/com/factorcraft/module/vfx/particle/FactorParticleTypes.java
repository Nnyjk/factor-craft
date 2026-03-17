package com.factorcraft.module.vfx.particle;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 粒子类型注册
 * 
 * 定义各种机器工作时的粒子效果类型
 */
public class FactorParticleTypes {
    
    /** 提取器粒子 - 向上飘散的 Factor 粒子 */
    public static SimpleParticleType EXTRACTION;
    
    /** 合成器粒子 - 旋转汇聚的粒子 */
    public static SimpleParticleType SYNTHESIS;
    
    /** 传递器粒子 - 传输束流粒子 */
    public static SimpleParticleType TRANSMISSION;
    
    /** 培育器粒子 - 特性注入粒子 */
    public static SimpleParticleType CULTIVATION;
    
    /** 消耗器粒子 - 燃烧消耗粒子 */
    public static SimpleParticleType CONSUMPTION;
    
    /**
     * 注册所有粒子类型
     */
    public static void register() {
        // 使用 FabricParticleTypes 工厂方法创建 SimpleParticleType
        EXTRACTION = Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "extraction"),
            FabricParticleTypes.simple(true));
        
        SYNTHESIS = Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "synthesis"),
            FabricParticleTypes.simple(true));
        
        TRANSMISSION = Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "transmission"),
            FabricParticleTypes.simple(true));
        
        CULTIVATION = Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "cultivation"),
            FabricParticleTypes.simple(true));
        
        CONSUMPTION = Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "consumption"),
            FabricParticleTypes.simple(true));
    }
}
