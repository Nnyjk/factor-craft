package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.network.ExtractorCoreSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * Screen 类型注册
 */
public class ModScreens {
    
    private static final String MOD_ID = "factorcraft";
    
    // ==================== ScreenHandler Types ====================
    
    public static final ScreenHandlerType<FactorExtractorScreenHandler> FACTOR_EXTRACTOR = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "factor_extractor"),
            new ScreenHandlerType<>(FactorExtractorScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    public static final ScreenHandlerType<ExtractorCoreScreenHandler> EXTRACTOR_CORE = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "extractor_core"),
            new ScreenHandlerType<>(ExtractorCoreScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    public static final ScreenHandlerType<ConsumerCoreScreenHandler> CONSUMER_CORE = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "consumer_core"),
            new ScreenHandlerType<>(ConsumerCoreScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    public static final ScreenHandlerType<SynthesizerCoreScreenHandler> SYNTHESIZER_CORE =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "synthesizer_core"),
            new ExtendedScreenHandlerType<>(
                SynthesizerCoreScreenHandler::new,
                SynthesizerCoreScreenHandler.SyncData.PACKET_CODEC
            )
        );
    
    public static final ScreenHandlerType<CultivatorCoreScreenHandler> CULTIVATOR_CORE = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "cultivator_core"),
            new ScreenHandlerType<>(CultivatorCoreScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    /**
     * 注册所有 Screen（服务端调用）
     */
    public static void register() {
        // 已通过 Registry.register 完成
        // 注册客户端网络包处理器
        registerClientNetworking();
    }
    
    /**
     * 注册客户端网络包处理
     */
    private static void registerClientNetworking() {
        // 这部分需要在客户端初始化时调用
    }
    
    /**
     * 客户端初始化 - 在 FabricClientModInitializer 中调用
     */
    public static void initClient() {
        // 注册 HandledScreens
        HandledScreens.register(FACTOR_EXTRACTOR, FactorExtractorScreen::new);
        HandledScreens.register(EXTRACTOR_CORE, ExtractorCoreScreen::new);
        HandledScreens.register(CONSUMER_CORE, ConsumerCoreScreen::new);
        HandledScreens.register(SYNTHESIZER_CORE, SynthesizerCoreScreen::new);
        HandledScreens.register(CULTIVATOR_CORE, CultivatorCoreScreen::new);
        
        // 注册同步包接收器
        ClientPlayNetworking.registerGlobalReceiver(ExtractorCoreSyncPayload.ID, 
            (payload, context) -> {
                context.client().execute(() -> {
                    if (context.client().currentScreen instanceof ExtractorCoreScreen screen) {
                        screen.getScreenHandler().receiveSyncData(
                            payload.factorStorage(),
                            payload.maxStorage(),
                            payload.efficiency(),
                            payload.dimensionEfficiency(),
                            payload.extractRate(),
                            payload.progress(),
                            payload.tier(),
                            payload.structureValid(),
                            payload.dimension(),
                            payload.recommendedDimension()
                        );
                    }
                });
            });
    }
}