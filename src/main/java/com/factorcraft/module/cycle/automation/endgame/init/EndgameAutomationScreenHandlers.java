package com.factorcraft.module.cycle.automation.endgame.init;

import com.factorcraft.module.cycle.automation.endgame.screen.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * 终局自动化 ScreenHandler 注册表
 */
public class EndgameAutomationScreenHandlers {
    
    public static final String MOD_ID = "factorcraft";
    
    // 自动提取器 MK-II
    public static final ScreenHandlerType<AutoExtractorMK2ScreenHandler> AUTO_EXTRACTOR_MK2 = registerScreenHandler(
        "auto_extractor_mk2",
        AutoExtractorMK2ScreenHandler::new
    );
    
    // Factor 泵 MK-II
    public static final ScreenHandlerType<FactorPumpMK2ScreenHandler> FACTOR_PUMP_MK2 = registerScreenHandler(
        "factor_pump_mk2",
        FactorPumpMK2ScreenHandler::new
    );
    
    // 高级合成器
    public static final ScreenHandlerType<AdvancedCrafterScreenHandler> ADVANCED_CRAFTER = registerScreenHandler(
        "advanced_crafter",
        AdvancedCrafterScreenHandler::new
    );
    
    // 量子仓储单元
    public static final ScreenHandlerType<QuantumStorageScreenHandler> QUANTUM_STORAGE = registerScreenHandler(
        "quantum_storage",
        QuantumStorageScreenHandler::new
    );
    
    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(
        String name,
        ScreenHandlerType.Factory<T> factory
    ) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<ScreenHandlerType<?>> key = RegistryKey.of(RegistryKeys.SCREEN_HANDLER, id);
        
        return Registry.register(Registries.SCREEN_HANDLER, key, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }
    
    public static void init() {
        // 初始化方法，触发静态字段注册
    }
}
