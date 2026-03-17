package com.factorcraft.component;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.component.type.FactorStorage;
import com.factorcraft.component.type.ScanHistory;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor Craft 自定义 Data Components 注册
 * 
 * Minecraft 1.21.4+ 使用 Data Component 系统替代传统 NBT
 */
public class FactorCraftDataComponents {
    
    // ========== 网络扫描相关 ==========
    
    /**
     * Factor 扫描仪历史数据
     * 存储多次扫描结果用于趋势分析
     */
    public static final ComponentType<ScanHistory> SCAN_HISTORY = register(
        "scan_history",
        ComponentType.<ScanHistory>builder()
            .codec(ScanHistory.CODEC)
            .packetCodec(ScanHistory.STREAM_CODEC)
            .build()
    );
    
    // ========== Factor 存储相关 ==========
    
    /**
     * Factor 电池存储数据
     * 存储 Factor 量和类型
     */
    public static final ComponentType<FactorStorage> FACTOR_STORAGE = register(
        "factor_storage",
        ComponentType.<FactorStorage>builder()
            .codec(FactorStorage.CODEC)
            .packetCodec(FactorStorage.STREAM_CODEC)
            .build()
    );
    
    /**
     * 注册 Data Component 类型
     */
    private static <T> ComponentType<T> register(String name, ComponentType<T> type) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, name),
            type
        );
    }
    
    /**
     * 注册所有 Data Components
     */
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft] Data Components registered");
    }
}
