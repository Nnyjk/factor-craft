package com.factorcraft.module.research;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

/**
 * 研究系统命令
 */
public class ResearchCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("research")
            .requires(source -> source.hasPermissionLevel(0))
            // 列出所有研究
            .then(CommandManager.literal("list")
                .executes(ResearchCommands::listResearch))
            // 查看研究详情
            .then(CommandManager.literal("info")
                .then(CommandManager.argument("id", StringArgumentType.string())
                    .executes(ResearchCommands::researchInfo)))
            // 开始研究
            .then(CommandManager.literal("start")
                .then(CommandManager.argument("id", StringArgumentType.string())
                    .executes(ResearchCommands::startResearch)))
            // 查看进度
            .then(CommandManager.literal("progress")
                .executes(ResearchCommands::showProgress))
            // 重置研究（管理员）
            .then(CommandManager.literal("reset")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ResearchCommands::resetResearch))
        );
    }
    
    private static int listResearch(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ResearchManager manager = ResearchModule.getInstance().getResearchManager();
        
        source.sendFeedback(() -> Text.literal("§6=== 研究列表 ==="), false);
        
        for (Research research : manager.getAllResearch()) {
            Research.State state = manager.getResearchState(research.getId(), 
                source.getPlayer());
            String stateColor = getStateColor(state);
            String stateText = getStateText(state);
            
            source.sendFeedback(() -> Text.literal(
                String.format("§7- §f%s §8[%s§8] §7(%s)", 
                    research.getName(), 
                    stateColor + stateText,
                    research.getType().name().toLowerCase())
            ), false);
        }
        
        return 1;
    }
    
    private static int researchInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String id = StringArgumentType.getString(context, "id");
        ResearchManager manager = ResearchModule.getInstance().getResearchManager();
        
        Research research = manager.getResearch(id);
        if (research == null) {
            source.sendError(Text.literal("§c研究不存在: " + id));
            return 0;
        }
        
        source.sendFeedback(() -> Text.literal("§6=== " + research.getName() + " ==="), false);
        source.sendFeedback(() -> Text.literal("§7ID: §f" + research.getId()), false);
        source.sendFeedback(() -> Text.literal("§7类型: §f" + research.getType().name()), false);
        source.sendFeedback(() -> Text.literal("§7描述: §f" + research.getDescription()), false);
        
        // 研究时间
        int seconds = research.getResearchTime() / 20;
        source.sendFeedback(() -> Text.literal("§7研究时间: §f" + seconds + " 秒"), false);
        
        // 前置研究
        if (!research.getPrerequisites().isEmpty()) {
            source.sendFeedback(() -> Text.literal("§7前置: §f" + 
                String.join(", ", research.getPrerequisites())), false);
        }
        
        // Factor 消耗
        if (!research.getFactorCosts().isEmpty()) {
            StringBuilder costs = new StringBuilder();
            for (var entry : research.getFactorCosts().entrySet()) {
                if (costs.length() > 0) costs.append(", ");
                costs.append(entry.getKey()).append(": ").append(entry.getValue());
            }
            source.sendFeedback(() -> Text.literal("§7Factor 消耗: §f" + costs), false);
        }
        
        // 物品要求
        if (!research.getItemRequirements().isEmpty()) {
            StringBuilder items = new StringBuilder();
            for (var entry : research.getItemRequirements().entrySet()) {
                if (items.length() > 0) items.append(", ");
                items.append(entry.getKey().getName().getString())
                    .append(" x").append(entry.getValue());
            }
            source.sendFeedback(() -> Text.literal("§7物品要求: §f" + items), false);
        }
        
        // 效果
        if (!research.getEffects().isEmpty()) {
            StringBuilder effects = new StringBuilder();
            for (var entry : research.getEffects().entrySet()) {
                if (effects.length() > 0) effects.append(", ");
                effects.append(entry.getKey()).append(": ").append(entry.getValue());
            }
            source.sendFeedback(() -> Text.literal("§7效果: §a" + effects), false);
        }
        
        return 1;
    }
    
    private static int startResearch(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String id = StringArgumentType.getString(context, "id");
        
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("§c只有玩家可以执行此命令"));
            return 0;
        }
        
        ResearchManager manager = ResearchModule.getInstance().getResearchManager();
        
        if (manager.startResearch(id, player)) {
            Research research = manager.getResearch(id);
            int seconds = research.getResearchTime() / 20;
            source.sendFeedback(() -> Text.literal("§a开始研究: §f" + research.getName() + 
                " §7(需要 " + seconds + " 秒)"), false);
            return 1;
        } else {
            source.sendError(Text.literal("§c无法开始研究，请检查前置条件"));
            return 0;
        }
    }
    
    private static int showProgress(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("§c只有玩家可以执行此命令"));
            return 0;
        }
        
        ResearchManager manager = ResearchModule.getInstance().getResearchManager();
        ResearchProgress progress = manager.getProgress(player);
        
        source.sendFeedback(() -> Text.literal("§6=== 研究进度 ==="), false);
        source.sendFeedback(() -> Text.literal("§7已完成: §f" + progress.getCompletedCount() + " 个研究"), false);
        
        // 进行中的研究
        if (!progress.getInProgressResearch().isEmpty()) {
            source.sendFeedback(() -> Text.literal("§7进行中:"), false);
            long currentTick = player.getServerWorld().getTime();
            
            for (var entry : progress.getInProgressResearch().entrySet()) {
                Research research = manager.getResearch(entry.getKey());
                float percent = progress.getProgress(entry.getKey(), currentTick, research) * 100;
                source.sendFeedback(() -> Text.literal(
                    String.format("§7- §f%s §7(%.1f%%)", research.getName(), percent)
                ), false);
            }
        }
        
        return 1;
    }
    
    private static int resetResearch(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("§c只有玩家可以执行此命令"));
            return 0;
        }
        
        ResearchManager manager = ResearchModule.getInstance().getResearchManager();
        ResearchProgress progress = manager.getProgress(player);
        
        progress.reset();
        
        source.sendFeedback(() -> Text.literal("§a已重置所有研究进度"), false);
        
        return 1;
    }
    
    private static String getStateColor(Research.State state) {
        return switch (state) {
            case LOCKED -> "§7";
            case AVAILABLE -> "§e";
            case IN_PROGRESS -> "§b";
            case COMPLETED -> "§a";
        };
    }
    
    private static String getStateText(Research.State state) {
        return switch (state) {
            case LOCKED -> "已锁定";
            case AVAILABLE -> "可研究";
            case IN_PROGRESS -> "研究中";
            case COMPLETED -> "已完成";
        };
    }
}