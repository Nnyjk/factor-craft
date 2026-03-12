package com.factorcraft.command;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.module.material.trait.TraitService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

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
    
    private static String getConcentrationTier(double concentration) {
        if (concentration < 20) return "DEPLETED";
        if (concentration < 50) return "LOW_ENERGY";
        if (concentration < 80) return "STABLE";
        if (concentration < 100) return "HIGH_ENERGY";
        return "OVERLOAD";
    }
}