package com.factorcraft.command;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.material.trait.TraitService;
import com.factorcraft.module.quest.QuestModule;
import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplate;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.*;

/**
 * Factor Craft 命令系统
 */
public class FactorCraftCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // /factor info - 查看当前区块 Factor 信息
        dispatcher.register(literal("factor")
            .then(literal("info")
                .executes(FactorCraftCommands::factorInfo))
            .then(literal("set")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("value", IntegerArgumentType.integer(0, 200))
                    .executes(FactorCraftCommands::factorSet)))
            .then(literal("reset")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(FactorCraftCommands::factorReset))
        );
        
        // /trait info - 查看手持物品特性
        dispatcher.register(literal("trait")
            .then(literal("info")
                .executes(FactorCraftCommands::traitInfo))
            .then(literal("add")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("trait_id", StringArgumentType.string())
                    .then(argument("level", IntegerArgumentType.integer(1, 3))
                        .executes(FactorCraftCommands::traitAdd))))
            .then(literal("remove")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("trait_id", StringArgumentType.string())
                    .executes(FactorCraftCommands::traitRemove)))
            .then(literal("clear")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(FactorCraftCommands::traitClear))
        );
        
        // /factorcraft reload - 重载配置
        dispatcher.register(literal("factorcraft")
            .then(literal("reload")
                .requires(source -> source.hasPermissionLevel(3))
                .executes(FactorCraftCommands::reloadConfig))
            .then(literal("stats")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(FactorCraftCommands::showStats))
            .then(literal("profile")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("start")
                    .executes(FactorCraftCommands::profileStart))
                .then(literal("stop")
                    .executes(FactorCraftCommands::profileStop))
                .then(literal("report")
                    .executes(FactorCraftCommands::profileReport)))
            .then(literal("perf")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(FactorCraftCommands::perfReport))
            // Factor 管理命令
            .then(literal("factor")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("chunk")
                    .then(argument("x", IntegerArgumentType.integer())
                        .then(argument("z", IntegerArgumentType.integer())
                            .executes(FactorCraftCommands::factorChunkInfo)))))
            // 机器管理命令
            .then(literal("machine")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("info")
                    .executes(FactorCraftCommands::machineInfo))
                .then(literal("reset")
                    .executes(FactorCraftCommands::machineReset)))
            // 任务管理命令
            .then(literal("quest")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("list")
                    .executes(FactorCraftCommands::questListSelf)
                    .then(argument("player", StringArgumentType.string())
                        .executes(FactorCraftCommands::questList)))
                .then(literal("complete")
                    .then(argument("player", StringArgumentType.string())
                        .then(argument("quest", StringArgumentType.string())
                            .executes(FactorCraftCommands::questComplete))))
                .then(literal("reset")
                    .executes(FactorCraftCommands::questResetSelf)
                    .then(argument("player", StringArgumentType.string())
                        .executes(FactorCraftCommands::questReset))))
        );
    }
    
    private static int factorInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        net.minecraft.util.math.BlockPos blockPos = net.minecraft.util.math.BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = source.getWorld().getChunk(blockPos).getPos();
        
        ChunkFactorManager.getState(chunkPos).ifPresentOrElse(
            state -> {
                source.sendFeedback(() -> Text.literal("§6=== Factor 信息 ==="), false);
                source.sendFeedback(() -> Text.literal("§e区块: §f" + chunkPos.x + ", " + chunkPos.z), false);
                source.sendFeedback(() -> Text.literal("§e浓度: §f" + String.format("%.1f", state.getCurrentConcentration())), false);
                source.sendFeedback(() -> Text.literal("§e等级: §f" + getConcentrationTier(state.getCurrentConcentration())), false);
                source.sendFeedback(() -> Text.literal("§e锚定: §f" + (state.isAnchored() ? "是" : "否")), false);
            },
            () -> source.sendFeedback(() -> Text.literal("§c该区块无 Factor 数据"), false)
        );
        
        return 1;
    }
    
    private static int factorSet(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int value = IntegerArgumentType.getInteger(context, "value");
        net.minecraft.util.math.BlockPos blockPos = net.minecraft.util.math.BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = source.getWorld().getChunk(blockPos).getPos();
        
        ChunkFactorManager.getState(chunkPos).ifPresent(state -> {
            state.setCurrentConcentration(value);
            source.sendFeedback(() -> Text.literal("§a已将区块 Factor 浓度设置为: " + value), true);
        });
        
        return 1;
    }
    
    private static int factorReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        net.minecraft.util.math.BlockPos blockPos = net.minecraft.util.math.BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = source.getWorld().getChunk(blockPos).getPos();
        
        ChunkFactorManager.getState(chunkPos).ifPresent(state -> {
            double initial = state.getInitialConcentration();
            state.setCurrentConcentration(initial);
            source.sendFeedback(() -> Text.literal("§a已重置区块 Factor 浓度"), true);
        });
        
        return 1;
    }
    
    private static int traitInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ItemStack stack = source.getPlayer().getMainHandStack();
        
        var traits = TraitService.getTraits(stack);
        
        if (traits.isEmpty()) {
            source.sendFeedback(() -> Text.literal("§c该物品无特性"), false);
        } else {
            source.sendFeedback(() -> Text.literal("§6=== 物品特性 ==="), false);
            traits.forEach(trait -> {
                trait.getDefinition().ifPresent(def -> {
                    String color = def.isPositive() ? "§a" : "§c";
                    source.sendFeedback(() -> Text.literal(color + def.name() + " §fLv." + trait.level()), false);
                });
            });
            
            double resonance = TraitService.calculateResonanceBonus(traits);
            if (resonance > 1.0) {
                source.sendFeedback(() -> Text.literal("§e共振加成: §f×" + resonance), false);
            }
        }
        
        return 1;
    }
    
    private static int traitAdd(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String traitId = StringArgumentType.getString(context, "trait_id");
        int level = IntegerArgumentType.getInteger(context, "level");
        ItemStack stack = source.getPlayer().getMainHandStack();
        
        if (TraitService.addTrait(stack, traitId, level)) {
            source.sendFeedback(() -> Text.literal("§a已添加特性: " + traitId + " Lv." + level), true);
        } else {
            source.sendFeedback(() -> Text.literal("§c无法添加特性: " + traitId), false);
        }
        
        return 1;
    }
    
    private static int traitRemove(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String traitId = StringArgumentType.getString(context, "trait_id");
        ItemStack stack = source.getPlayer().getMainHandStack();
        
        if (TraitService.removeTrait(stack, traitId)) {
            source.sendFeedback(() -> Text.literal("§a已移除特性: " + traitId), false);
        } else {
            source.sendFeedback(() -> Text.literal("§c未找到特性: " + traitId), false);
        }
        
        return 1;
    }
    
    private static int traitClear(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ItemStack stack = source.getPlayer().getMainHandStack();
        
        var traits = TraitService.getTraits(stack);
        int count = traits.size();
        TraitService.clearTraits(stack);
        
        source.sendFeedback(() -> Text.literal("§a已清除 " + count + " 个特性"), false);
        
        return 1;
    }
    
    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        com.factorcraft.performance.ConfigHotReloader.reloadAll();
        source.sendFeedback(() -> Text.literal("§a配置已重新加载"), true);
        
        return 1;
    }
    
    private static int showStats(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        var stats = com.factorcraft.performance.PerformanceMonitor.getSystemStats();
        
        source.sendFeedback(() -> Text.literal("§6=== Factor Craft 统计 ==="), false);
        source.sendFeedback(() -> Text.literal("§e总 Tick 数: §f" + stats.totalTicks()), false);
        source.sendFeedback(() -> Text.literal("§e平均 Tick 时间: §f" + String.format("%.2f", stats.avgTickTimeNanos() / 1_000_000.0) + " ms"), false);
        source.sendFeedback(() -> Text.literal("§e缓存区块数: §f" + stats.cachedChunks()), false);
        
        return 1;
    }
    
    private static int profileStart(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        com.factorcraft.performance.PerformanceProfiler.startProfiling();
        source.sendFeedback(() -> Text.literal("§a性能分析已启动"), true);
        
        return 1;
    }
    
    private static int profileStop(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        com.factorcraft.performance.PerformanceProfiler.stopProfiling();
        source.sendFeedback(() -> Text.literal("§a性能分析已停止"), true);
        
        return 1;
    }
    
    private static int profileReport(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        var report = com.factorcraft.performance.PerformanceProfiler.generateReport(source.getServer());
        var lines = com.factorcraft.performance.PerformanceProfiler.formatReport(report);
        
        for (Text line : lines) {
            source.sendFeedback(() -> line, false);
        }
        
        return 1;
    }
    
    private static int perfReport(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        // 生成完整性能报告
        var report = com.factorcraft.performance.PerformanceAnalysisReport.generateFullReport();
        
        for (Text line : report) {
            source.sendFeedback(() -> line, false);
        }
        
        // 性能评分
        int score = com.factorcraft.performance.PerformanceAnalysisReport.getPerformanceScore();
        String grade = com.factorcraft.performance.PerformanceAnalysisReport.getPerformanceGrade();
        
        source.sendFeedback(() -> Text.literal(String.format("§e性能评分: §f%d §7(%s 级)", score, grade)), false);
        
        return 1;
    }
    
    private static String getConcentrationTier(double concentration) {
        if (concentration < 20) return "DEPLETED";
        if (concentration < 50) return "LOW_ENERGY";
        if (concentration < 80) return "STABLE";
        if (concentration < 100) return "HIGH_ENERGY";
        return "OVERLOAD";
    }
    
    // ==================== Factor 管理命令 ====================
    
    private static int factorChunkInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int chunkX = IntegerArgumentType.getInteger(context, "x");
        int chunkZ = IntegerArgumentType.getInteger(context, "z");
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        ChunkFactorState state = ChunkFactorManager.getOrCreateState(source.getWorld(), chunkPos);
        
        source.sendFeedback(() -> Text.literal("§6=== 区块 Factor 信息 ==="), false);
        source.sendFeedback(() -> Text.literal(String.format("§e坐标: §f%d, %d", chunkX, chunkZ)), false);
        source.sendFeedback(() -> Text.literal(String.format("§e当前浓度: §f%.1f", state.getCurrentConcentration())), false);
        source.sendFeedback(() -> Text.literal(String.format("§e初始浓度: §f%.1f", state.getInitialConcentration())), false);
        source.sendFeedback(() -> Text.literal("§e等级: §f" + getConcentrationTier(state.getCurrentConcentration())), false);
        source.sendFeedback(() -> Text.literal(String.format("§e锚定: §f%s", 
            state.isAnchored() ? "是 (半径: " + state.getAnchorRadius() + ")" : "否")), false);
        
        return 1;
    }
    
    // ==================== 机器管理命令 ====================
    
    private static int machineInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BlockPos pos = BlockPos.ofFloored(source.getPosition());
        
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (blockEntity == null) {
            source.sendError(Text.literal("§c当前位置没有方块实体"));
            return 0;
        }
        
        source.sendFeedback(() -> Text.literal("§6=== 机器信息 ==="), false);
        source.sendFeedback(() -> Text.literal(String.format("§e类型: §f%s", 
            blockEntity.getType().toString())), false);
        source.sendFeedback(() -> Text.literal(String.format("§e位置: §f%d, %d, %d", 
            pos.getX(), pos.getY(), pos.getZ())), false);
        
        if (blockEntity instanceof IFactorContainer container) {
            source.sendFeedback(() -> Text.literal(String.format("§eFactor 存储: §f%.1f / %.1f", 
                container.getFactorStorage(), container.getMaxFactorStorage())), false);
            source.sendFeedback(() -> Text.literal(String.format("§e可接收: §f%s", 
                container.canReceiveFactor() ? "是" : "否")), false);
            source.sendFeedback(() -> Text.literal(String.format("§e可提取: §f%s", 
                container.canExtractFactor() ? "是" : "否")), false);
        } else {
            source.sendFeedback(() -> Text.literal("§7该方块不是 Factor 容器"), false);
        }
        
        return 1;
    }
    
    private static int machineReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BlockPos pos = BlockPos.ofFloored(source.getPosition());
        
        BlockEntity blockEntity = source.getWorld().getBlockEntity(pos);
        if (blockEntity == null) {
            source.sendError(Text.literal("§c当前位置没有方块实体"));
            return 0;
        }
        
        if (blockEntity instanceof IFactorContainer container) {
            // 重置 Factor 存储
            double extracted = container.extractFactor(container.getFactorStorage());
            source.sendFeedback(() -> Text.literal(String.format("§a已重置机器 Factor: 抽取 %.1f", extracted)), true);
        } else {
            source.sendError(Text.literal("§c该方块不是 Factor 容器"));
            return 0;
        }
        
        // 标记需要保存
        source.getWorld().markDirty(pos);
        source.sendFeedback(() -> Text.literal("§a机器状态已重置"), true);
        
        return 1;
    }
    
    // ==================== 任务管理命令 ====================
    
    private static int questListSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        return listPlayerQuests(source, player);
    }
    
    private static int questList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String playerName = StringArgumentType.getString(context, "player");
        
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendError(Text.literal("§c玩家未找到: " + playerName));
            return 0;
        }
        
        return listPlayerQuests(source, player);
    }
    
    private static int listPlayerQuests(ServerCommandSource source, ServerPlayerEntity player) {
        QuestManager manager = QuestModule.getInstance().getQuestManager();
        List<QuestInstance> activeQuests = manager.getActiveQuests(player.getUuid());
        Set<Identifier> completedQuests = manager.getCompletedQuests(player.getUuid());
        
        source.sendFeedback(() -> Text.literal(String.format("§6=== %s 的任务 ===", 
            player.getName().getString())), false);
        source.sendFeedback(() -> Text.literal(String.format("§e活跃任务: §f%d", activeQuests.size())), false);
        
        for (QuestInstance quest : activeQuests) {
            QuestTemplate template = quest.getTemplate();
            String name = template != null ? template.getTitle() : "Unknown";
            String questIdStr = template != null ? template.getId().toString() : "unknown:unknown";
            source.sendFeedback(() -> Text.literal(String.format("  §7- §f%s §7(%s)", 
                name, questIdStr)), false);
        }
        
        source.sendFeedback(() -> Text.literal(String.format("§e已完成任务: §f%d", completedQuests.size())), false);
        
        return 1;
    }
    
    private static int questComplete(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String playerName = StringArgumentType.getString(context, "player");
        String questIdStr = StringArgumentType.getString(context, "quest");
        
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendError(Text.literal("§c玩家未找到: " + playerName));
            return 0;
        }
        
        Identifier questId = Identifier.tryParse(questIdStr);
        if (questId == null) {
            source.sendError(Text.literal("§c无效的任务 ID: " + questIdStr));
            return 0;
        }
        
        QuestManager manager = QuestModule.getInstance().getQuestManager();
        
        // 检查任务是否存在
        QuestTemplate template = manager.getTemplate(questId);
        if (template == null) {
            source.sendError(Text.literal("§c任务不存在: " + questIdStr));
            return 0;
        }
        
        // 检查玩家是否有此任务
        List<QuestInstance> activeQuests = manager.getActiveQuests(player.getUuid());
        boolean hasQuest = activeQuests.stream()
            .anyMatch(q -> q.getTemplate() != null && q.getTemplate().getId().equals(questId));
        
        if (!hasQuest) {
            // 先开始任务
            manager.startQuest(player, questId);
        }
        
        // 完成任务
        manager.completeQuest(player, questId);
        
        source.sendFeedback(() -> Text.literal(String.format("§a已为玩家 %s 完成任务 %s", 
            playerName, questIdStr)), true);
        
        return 1;
    }
    
    private static int questResetSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        QuestManager manager = QuestModule.getInstance().getQuestManager();
        int resetCount = manager.resetPlayerQuests(player.getUuid());
        
        source.sendFeedback(() -> Text.literal(String.format("§a已重置你的 %d 个任务数据", resetCount)), true);
        
        return 1;
    }
    
    private static int questReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String playerName = StringArgumentType.getString(context, "player");
        
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendError(Text.literal("§c玩家未找到: " + playerName));
            return 0;
        }
        
        QuestManager manager = QuestModule.getInstance().getQuestManager();
        int resetCount = manager.resetPlayerQuests(player.getUuid());
        
        source.sendFeedback(() -> Text.literal(String.format("§a已重置玩家 %s 的 %d 个任务数据", 
            playerName, resetCount)), true);
        
        return 1;
    }
}