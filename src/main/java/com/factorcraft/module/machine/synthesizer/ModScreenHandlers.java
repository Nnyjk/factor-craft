package com.factorcraft.module.machine.synthesizer;

import com.factorcraft.FactorCraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * Factor 合成器 ScreenHandler 注册
 */
public class ModScreenHandlers {
    
    // ========== ScreenHandler 类型 ==========
    
    public static final ScreenHandlerType<SynthesizerScreenHandler> FACTOR_SYNTHESIZER = 
        new ScreenHandlerType<>(SynthesizerScreenHandler::new, FeatureSet.empty());
    
    // ========== 注册方法 ==========
    
    public static void init() {
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_synthesizer"),
            FACTOR_SYNTHESIZER
        );
    }
}