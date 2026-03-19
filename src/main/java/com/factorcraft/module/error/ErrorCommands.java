package com.factorcraft.module.error;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

/**
 * 调试命令
 */
public class ErrorCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // /factorcraft debug - 调试命令组
        dispatcher.register(literal("factorcraft")
            .then(literal("debug")
                .requires(source -> source.hasPermissionLevel(3))
                // 查看调试状态
                .then(literal("status")
                    .executes(ErrorCommands::debugStatus))
                // 开关调试模式
                .then(literal("enable")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ErrorCommands::setDebugEnabled)))
                // 机器调试
                .then(literal("machine")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ErrorCommands::setMachineDebug)))
                // 网络调试
                .then(literal("network")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ErrorCommands::setNetworkDebug)))
                // 任务调试
                .then(literal("quest")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ErrorCommands::setQuestDebug)))
                // 性能调试
                .then(literal("performance")
                    .then(argument("enabled", BoolArgumentType.bool())
                        .executes(ErrorCommands::setPerformanceDebug)))
                // 错误统计
                .then(literal("errors")
                    .executes(ErrorCommands::errorStats))
                // 清除错误统计
                .then(literal("clear-errors")
                    .executes(ErrorCommands::clearErrors))
                // 日志级别
                .then(literal("level")
                    .then(argument("level", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            for (DebugConfig.LogLevel level : DebugConfig.LogLevel.values()) {
                                builder.suggest(level.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ErrorCommands::setLogLevel)))
                // 重载配置
                .then(literal("reload")
                    .executes(ErrorCommands::reloadDebugConfig))
            )
        );
    }
    
    private static int debugStatus(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("§6=== 调试状态 ==="), false);
        source.sendFeedback(() -> Text.literal(String.format("§e全局调试: §f%s", 
            DebugConfig.isDebugEnabled() ? "§a开启" : "§c关闭")), false);
        source.sendFeedback(() -> Text.literal(String.format("§e机器调试: §f%s", 
            DebugConfig.isMachineDebugEnabled() ? "§a开启" : "§c关闭")), false);
        source.sendFeedback(() -> Text.literal(String.format("§e网络调试: §f%s", 
            DebugConfig.isNetworkDebugEnabled() ? "§a开启" : "§c关闭")), false);
        source.sendFeedback(() -> Text.literal(String.format("§e任务调试: §f%s", 
            DebugConfig.isQuestDebugEnabled() ? "§a开启" : "§c关闭")), false);
        source.sendFeedback(() -> Text.literal(String.format("§e性能调试: §f%s", 
            DebugConfig.isPerformanceDebugEnabled() ? "§a开启" : "§c关闭")), false);
        source.sendFeedback(() -> Text.literal(String.format("§e日志级别: §f%s", 
            DebugConfig.getGlobalLogLevel().name())), false);
        
        return 1;
    }
    
    private static int setDebugEnabled(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        DebugConfig.setDebugEnabled(enabled);
        
        source.sendFeedback(() -> Text.literal(String.format("§a全局调试模式已%s", 
            enabled ? "开启" : "关闭")), true);
        
        return 1;
    }
    
    private static int setMachineDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        DebugConfig.setMachineDebugEnabled(enabled);
        
        source.sendFeedback(() -> Text.literal(String.format("§a机器调试模式已%s", 
            enabled ? "开启" : "关闭")), true);
        
        return 1;
    }
    
    private static int setNetworkDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        DebugConfig.setNetworkDebugEnabled(enabled);
        
        source.sendFeedback(() -> Text.literal(String.format("§a网络调试模式已%s", 
            enabled ? "开启" : "关闭")), true);
        
        return 1;
    }
    
    private static int setQuestDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        DebugConfig.setQuestDebugEnabled(enabled);
        
        source.sendFeedback(() -> Text.literal(String.format("§a任务调试模式已%s", 
            enabled ? "开启" : "关闭")), true);
        
        return 1;
    }
    
    private static int setPerformanceDebug(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        
        DebugConfig.setPerformanceDebugEnabled(enabled);
        
        source.sendFeedback(() -> Text.literal(String.format("§a性能调试模式已%s", 
            enabled ? "开启" : "关闭")), true);
        
        return 1;
    }
    
    private static int errorStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        String stats = ErrorHandler.getErrorStats();
        source.sendFeedback(() -> Text.literal("§6" + stats.replace("\n", "\n§7")), false);
        
        return 1;
    }
    
    private static int clearErrors(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        ErrorHandler.clearStats();
        
        source.sendFeedback(() -> Text.literal("§a错误统计已清除"), true);
        
        return 1;
    }
    
    private static int setLogLevel(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String levelStr = StringArgumentType.getString(context, "level").toUpperCase();
        
        try {
            DebugConfig.LogLevel level = DebugConfig.LogLevel.valueOf(levelStr);
            DebugConfig.setGlobalLogLevel(level);
            
            source.sendFeedback(() -> Text.literal(String.format("§a日志级别已设置为: %s", level.name())), true);
        } catch (IllegalArgumentException e) {
            source.sendError(Text.literal("§c无效的日志级别。可选: TRACE, DEBUG, INFO, WARN, ERROR, OFF"));
            return 0;
        }
        
        return 1;
    }
    
    private static int reloadDebugConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        DebugConfig.reload();
        
        source.sendFeedback(() -> Text.literal("§a调试配置已重载"), true);
        
        return 1;
    }
}