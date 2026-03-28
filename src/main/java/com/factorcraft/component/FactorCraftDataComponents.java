package com.factorcraft.component;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.component.type.FactorData;
import com.factorcraft.component.type.FactorStorage;
import com.factorcraft.component.type.ScanHistory;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
     * Factor 数据组件
     * 存储完整的 Factor 对象，用于 Factor 物品
     */
    public static final ComponentType<FactorData> FACTOR_DATA = register(
        "factor_data",
        ComponentType.<FactorData>builder()
            .codec(FactorData.CODEC)
            .packetCodec(FactorData.PACKET_CODEC)
            .build()
    );
    
    // ========== Factor 工具相关 ==========
    
    /**
     * Factor 加速状态标记
     * 用于工具挖掘时标记是否使用 Factor 加速
     */
    public static final ComponentType<Boolean> FACTOR_BOOSTED = register(
        "factor_boosted",
        ComponentType.<Boolean>builder()
            .codec(com.mojang.serialization.Codec.BOOL)
            .packetCodec(net.minecraft.network.codec.PacketCodecs.BOOLEAN)
            .build()
    );
    
    // ========== 维度传送相关 ==========
    
    /**
     * 位置数据组件
     * 用于存储传送门绑定的坐标
     */
    public static final ComponentType<BlockPos> POSITION_DATA = register(
        "position_data",
        ComponentType.<BlockPos>builder()
            .codec(BlockPos.CODEC)
            .packetCodec(BlockPos.PACKET_CODEC)
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
