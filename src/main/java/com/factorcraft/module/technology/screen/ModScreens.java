package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.network.ExtractorCoreSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * Screen 类型注册
 */
public class ModScreens {
    
    public static final ScreenHandlerType<FactorExtractorScreenHandler> FACTOR_EXTRACTOR = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "factor_extractor"),
            new ScreenHandlerType<>(FactorExtractorScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    public static final ScreenHandlerType<ExtractorCoreScreenHandler> EXTRACTOR_CORE = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "extractor_core"),
            new ScreenHandlerType<>(ExtractorCoreScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    public static final ScreenHandlerType<SynthesizerCoreScreenHandler> SYNTHESIZER_CORE =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("factorcraft", "synthesizer_core"),
            new ExtendedScreenHandlerType<>(
                SynthesizerCoreScreenHandler::new,
                SynthesizerCoreScreenHandler.SyncData.PACKET_CODEC
            )
        );
    
    /**
     * 注册所有 Screen
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