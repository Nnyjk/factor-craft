package com.factorcraft.command;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.performance.*;
import com.factorcraft.module.factor.optimization.FactorCalculationCache;
import com.factorcraft.module.entity.optimization.EntityActivationRange;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * 性能监控命令
 * 
 * 提供 `/fc profile` 命令用于性能分析和监控
 */
public class PerformanceCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, 
                                CommandRegistryAccess registryAccess, 
                                CommandManager.RegistrationEnvironment environment) {
        
        var root = CommandManager.literal("factorcraft")
            .requires(source -> source.hasPermissionLevel(2));
        
        var profile = CommandManager.literal("profile")
            .executes(ctx -> executeProfileStatus(ctx.getSource()))
            .then(CommandManager.literal("start")
                .executes(ctx -> executeProfileStart(ctx.getSource())))
            .then(CommandManager.literal("stop")
                .executes(ctx -> executeProfileStop(ctx.getSource())))
            .then(CommandManager.literal("status")
                .executes(ctx -> executeProfileStatus(ctx.getSource())))
            .then(CommandManager.literal("memory")
                .executes(ctx -> executeMemoryStatus(ctx.getSource())))
            .then(CommandManager.literal("tps")
                .executes(ctx -> executeTpsStatus(ctx.getSource())))
            .then(CommandManager.literal("entities")
                .executes(ctx -> executeEntityStatus(ctx.getSource())))
            .then(CommandManager.literal("cache")
                .executes(ctx -> executeCacheStatus(ctx.getSource())))
            .then(CommandManager.literal("network")
                .executes(ctx -> executeNetworkStatus(ctx.getSource())))
            .then(CommandManager.literal("reload")
                .executes(ctx -> executeReloadConfig(ctx.getSource())));
        
        var setCmd = CommandManager.literal("set")
            .then(CommandManager.literal("maxChunksPerTick")
                .then(CommandManager.argument("value", IntegerArgumentType.integer(1, 100))
                    .executes(ctx -> executeSetMaxChunks(ctx.getSource(), 
                        IntegerArgumentType.getInteger(ctx, "value")))))
            .then(CommandManager.literal("enableBatchedSync")
                .then(CommandManager.argument("value", BoolArgumentType.bool())
                    .executes(ctx -> executeSetBatchedSync(ctx.getSource(),
                        BoolArgumentType.getBool(ctx, "value")))));
        
        root.then(profile).then(setCmd);
        dispatcher.register(root);
    }
    
    private static int executeProfileStart(ServerCommandSource source) {
        ServerProfiler profiler = ServerProfiler.getInstance();
        
        if (profiler.isProfiling()) {
            source.sendFeedback(() -> Text.literal("§c性能分析已在运行中"), false);
            return 1;
        }
        
        profiler.startProfiling();
        source.sendFeedback(() -> Text.literal("§a性能分析已启动"), false);
        
        return 1;
    }
    
    private static int executeProfileStop(ServerCommandSource source) {
        ServerProfiler profiler = ServerProfiler.getInstance();
        
        if (!profiler.isProfiling()) {
            source.sendFeedback(() -> Text.literal("§c性能分析未运行"), false);
            return 1;
        }
        
        profiler.stopProfiling();
        source.sendFeedback(() -> Text.literal("§a性能分析已停止，报告已保存到 logs/factorcraft-profiler/"), false);
        
        return 1;
    }
    
    private static int executeProfileStatus(ServerCommandSource source) {
        ServerProfiler profiler = ServerProfiler.getInstance();
        PerformanceMetrics metrics = PerformanceMetrics.getInstance();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 性能分析状态 ===\n");
        sb.append("分析状态：").append(profiler.isProfiling() ? "§a运行中" : "§c已停止").append("\n");
        
        if (profiler.isProfiling()) {
            sb.append("运行时长：").append(profiler.getDuration()).append(" ms\n");
            sb.append("Tick 数：").append(profiler.getTickCount()).append("\n");
        }
        
        sb.append("\n=== 服务器性能 ===\n");
        sb.append("TPS: §").append(metrics.getAverageTps() >= 18 ? "a" : "c")
            .append(String.format("%.2f", metrics.getAverageTps())).append("\n");
        sb.append("平均 Tick 耗时：").append(String.format("%.2f ms", metrics.getAverageTickTime())).append("\n");
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeMemoryStatus(ServerCommandSource source) {
        PerformanceMetrics metrics = PerformanceMetrics.getInstance();
        var heap = metrics.getHeapMemoryUsage();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 内存使用 ===\n");
        sb.append("已使用：").append(heap.getUsed() / (1024 * 1024)).append(" MB\n");
        sb.append("已提交：").append(heap.getCommitted() / (1024 * 1024)).append(" MB\n");
        sb.append("最大值：").append(heap.getMax() / (1024 * 1024)).append(" MB\n");
        sb.append("使用率：§").append(metrics.getMemoryUsagePercent() < 80 ? "a" : "c")
            .append(String.format("%.1f%%", metrics.getMemoryUsagePercent())).append("\n");
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeTpsStatus(ServerCommandSource source) {
        PerformanceMetrics metrics = PerformanceMetrics.getInstance();
        
        double tps = metrics.getAverageTps();
        double tickTime = metrics.getAverageTickTime();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== TPS 状态 ===\n");
        sb.append("当前 TPS: §").append(tps >= 18 ? "a" : tps >= 15 ? "e" : "c")
            .append(String.format("%.2f", tps)).append("\n");
        sb.append("目标 TPS: 20.00\n");
        sb.append("平均 Tick 耗时：§").append(tickTime <= 50 ? "a" : tickTime <= 60 ? "e" : "c")
            .append(String.format("%.2f ms", tickTime)).append("\n");
        sb.append("目标 Tick 耗时：50.00 ms\n");
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeEntityStatus(ServerCommandSource source) {
        PerformanceMetrics metrics = PerformanceMetrics.getInstance();
        
        long active = metrics.getActiveEntities();
        long inactive = metrics.getInactiveEntities();
        long total = active + inactive;
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 实体状态 ===\n");
        sb.append("总实体数：").append(total).append("\n");
        sb.append("活跃实体：§a").append(active).append("\n");
        sb.append("非活跃实体：§7").append(inactive).append("\n");
        
        if (total > 0) {
            double activeRate = (double) active / total * 100;
            sb.append("活跃率：§").append(activeRate > 50 ? "a" : "e")
                .append(String.format("%.1f%%", activeRate)).append("\n");
        }
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeCacheStatus(ServerCommandSource source) {
        // 这里需要访问 FactorCalculationCache，暂时返回占位信息
        StringBuilder sb = new StringBuilder();
        sb.append("=== Factor 计算缓存 ===\n");
        sb.append("缓存状态：§a已启用\n");
        sb.append("注：详细统计需要集成到 ChunkFactorManager\n");
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeNetworkStatus(ServerCommandSource source) {
        PerformanceMetrics metrics = PerformanceMetrics.getInstance();
        BatchedNetworkSync batchedSync = BatchedNetworkSync.getInstance();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== 网络状态 ===\n");
        sb.append("发送包数：").append(metrics.getPacketsSent()).append("\n");
        sb.append("接收包数：").append(metrics.getPacketsReceived()).append("\n");
        sb.append("发送字节：").append(metrics.getBytesSent() / 1024).append(" KB\n");
        sb.append("接收字节：").append(metrics.getBytesReceived() / 1024).append(" KB\n");
        sb.append("\n=== 批量同步 ===\n");
        sb.append(batchedSync.getStats()).append("\n");
        
        source.sendFeedback(() -> Text.literal(sb.toString()), false);
        
        return 1;
    }
    
    private static int executeReloadConfig(ServerCommandSource source) {
        PerformanceConfig.getInstance().reload();
        
        source.sendFeedback(() -> Text.literal("§a性能配置已重新加载"), false);
        
        return 1;
    }
    
    private static int executeSetMaxChunks(ServerCommandSource source, int value) {
        PerformanceConfig config = PerformanceConfig.getInstance();
        config.maxChunksPerTick = value;
        
        source.sendFeedback(() -> Text.literal("§a已设置每 tick 最大计算 Chunk 数：" + value), false);
        
        return 1;
    }
    
    private static int executeSetBatchedSync(ServerCommandSource source, boolean value) {
        PerformanceConfig config = PerformanceConfig.getInstance();
        config.enableBatchedSync = value;
        
        source.sendFeedback(() -> Text.literal("§a批量网络同步已" + (value ? "启用" : "禁用")), false);
        
        return 1;
    }
}
