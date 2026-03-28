package com.factorcraft.module.cycle.energy.screen;

import com.factorcraft.FactorCraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * Factor 能源模块 ScreenHandler 类型注册
 */
public class FactorEnergyScreenHandlers {
    
    public static ScreenHandlerType<FactorCompressorScreenHandler> FACTOR_COMPRESSOR_HANDLER;
    public static ScreenHandlerType<FactorReactorScreenHandler> FACTOR_REACTOR_HANDLER;
    public static ScreenHandlerType<FactorStabilizerScreenHandler> FACTOR_STABILIZER_HANDLER;
    
    /**
     * 初始化并注册所有 ScreenHandler 类型
     */
    public static void init() {
        FACTOR_COMPRESSOR_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "factor_compressor"),
            new ScreenHandlerType<>(FactorCompressorScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
        
        FACTOR_REACTOR_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "factor_reactor"),
            new ScreenHandlerType<>(FactorReactorScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
        
        FACTOR_STABILIZER_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "factor_stabilizer"),
            new ScreenHandlerType<>(FactorStabilizerScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
        
        FactorCraftMod.LOGGER.info("Factor Energy ScreenHandlers registered");
    }
}
