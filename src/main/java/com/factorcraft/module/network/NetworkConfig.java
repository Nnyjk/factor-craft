package com.factorcraft.module.network;

import com.factorcraft.FactorCraftMod;
import net.minecraft.server.MinecraftServer;

/**
 * 网络配置系统
 * 
 * 提供可配置的网络同步参数，优化多人游戏性能
 * 
 * 配置项:
 * - FACTOR_SYNC_INTERVAL_TICKS: Factor 浓度同步间隔 (ticks)
 * - FACTOR_SYNC_THRESHOLD: Factor 浓度变化同步阈值
 * - MACHINE_SYNC_COOLDOWN_MS: 机器状态同步冷却时间 (ms)
 * - MACHINE_STATE_CHANGE_ONLY: 仅当状态变化时同步
 * - ENABLE_COMPRESSION: 启用网络包压缩
 * - COMPRESSION_THRESHOLD_BYTES: 压缩阈值 (字节)
 */
public class NetworkConfig {
    
    // ==================== 通用同步配置 ====================
    
    /**
     * 通用同步间隔 (毫秒)
     * 用于异步网络同步的频率限制
     * 默认 500ms
     */
    public static int SYNC_INTERVAL_MS = 500;
    
    // ==================== Factor 同步配置 ====================
    
    /**
     * Factor 浓度同步间隔 (ticks)
     * 默认 20 ticks = 1 秒
     */
    public static int FACTOR_SYNC_INTERVAL_TICKS = 20;
    
    /**
     * Factor 浓度变化同步阈值
     * 只有浓度变化超过此值时才同步
     * 默认 0.1 (10% 变化)
     */
    public static double FACTOR_SYNC_THRESHOLD = 0.1;
    
    // ==================== 机器状态同步配置 ====================
    
    /**
     * 机器状态同步冷却时间 (毫秒)
     * 同一机器的最小同步间隔
     * 默认 500ms
     */
    public static int MACHINE_SYNC_COOLDOWN_MS = 500;
    
    /**
     * 机器状态同步半径 (方块)
     * 只有在此范围内的玩家才会收到同步
     * 默认 32 方块
     */
    public static double MACHINE_SYNC_RADIUS = 32.0;
    
    /**
     * 仅当状态变化时同步
     * 启用后，相同状态不会重复同步
     * 默认 true
     */
    public static boolean MACHINE_STATE_CHANGE_ONLY = true;
    
    // ==================== 网络包压缩配置 ====================
    
    /**
     * 启用网络包压缩
     * 对大数据包使用 zlib 压缩
     * 默认 true
     */
    public static boolean ENABLE_COMPRESSION = true;
    
    /**
     * 压缩阈值 (字节)
     * 超过此大小的包才会压缩
     * 默认 256 字节
     */
    public static int COMPRESSION_THRESHOLD_BYTES = 256;
    
    // ==================== 性能统计 ====================
    
    /** 已发送的 Factor 同步包数量 */
    public static long factorSyncPacketsSent = 0;
    
    /** 已发送的机器状态包数量 */
    public static long machineSyncPacketsSent = 0;
    
    /** 因冷却跳过的同步次数 */
    public static long syncSkippedCooldown = 0;
    
    /** 因无变化跳过的同步次数 */
    public static long syncSkippedNoChange = 0;
    
    /** 压缩的包数量 */
    public static long compressedPacketsCount = 0;
    
    /**
     * 重置统计
     */
    public static void resetStats() {
        factorSyncPacketsSent = 0;
        machineSyncPacketsSent = 0;
        syncSkippedCooldown = 0;
        syncSkippedNoChange = 0;
        compressedPacketsCount = 0;
    }
    
    /**
     * 获取统计信息
     */
    public static String getStats() {
        return String.format(
            "网络统计:\n" +
            "- Factor 同步包：%d\n" +
            "- 机器状态包：%d\n" +
            "- 跳过 (冷却): %d\n" +
            "- 跳过 (无变化): %d\n" +
            "- 压缩包：%d",
            factorSyncPacketsSent,
            machineSyncPacketsSent,
            syncSkippedCooldown,
            syncSkippedNoChange,
            compressedPacketsCount
        );
    }
    
    /**
     * 从服务器配置加载
     */
    public static void loadFromServerConfig(MinecraftServer server) {
        // TODO: 从服务器配置文件加载配置
        // 当前使用默认值
        FactorCraftMod.LOGGER.info("NetworkConfig loaded with defaults:");
        FactorCraftMod.LOGGER.info("  FACTOR_SYNC_INTERVAL_TICKS: {}", FACTOR_SYNC_INTERVAL_TICKS);
        FactorCraftMod.LOGGER.info("  FACTOR_SYNC_THRESHOLD: {}", FACTOR_SYNC_THRESHOLD);
        FactorCraftMod.LOGGER.info("  MACHINE_SYNC_COOLDOWN_MS: {}", MACHINE_SYNC_COOLDOWN_MS);
        FactorCraftMod.LOGGER.info("  MACHINE_STATE_CHANGE_ONLY: {}", MACHINE_STATE_CHANGE_ONLY);
        FactorCraftMod.LOGGER.info("  ENABLE_COMPRESSION: {}", ENABLE_COMPRESSION);
        FactorCraftMod.LOGGER.info("  COMPRESSION_THRESHOLD_BYTES: {}", COMPRESSION_THRESHOLD_BYTES);
    }
}
