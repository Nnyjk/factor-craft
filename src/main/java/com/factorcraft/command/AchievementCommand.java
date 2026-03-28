package com.factorcraft.command;

import com.factorcraft.module.core.achievement.Achievement;
import com.factorcraft.module.core.achievement.AchievementManager;
import com.factorcraft.module.core.achievement.AchievementProgress;
import com.factorcraft.module.core.init.CoreScreenHandlers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * 成就系统命令
 * /achievement - 打开成就界面
 * /achievement list - 列出所有成就
 * /achievement progress - 查看个人成就进度
 */
public class AchievementCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
            CommandManager.literal("achievement")
                .executes(AchievementCommand::openAchievementScreen)
                .then(CommandManager.literal("open")
                    .executes(AchievementCommand::openAchievementScreen))
                .then(CommandManager.literal("list")
                    .executes(AchievementCommand::listAchievements))
                .then(CommandManager.literal("progress")
                    .executes(AchievementCommand::showProgress))
                .then(CommandManager.literal("unlock")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("achievement", IdentifierArgumentType.identifier())
                        .executes(AchievementCommand::unlockAchievement)))
        );
    }
    
    /**
     * 打开成就界面
     */
    private static int openAchievementScreen(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("此命令只能由玩家执行"));
            return 0;
        }
        
        // 打开成就界面
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.literal("成就");
            }
            
            @Override
            public net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory inv, net.minecraft.entity.player.PlayerEntity player) {
                return CoreScreenHandlers.ACHIEVEMENT_TREE.create(syncId, inv);
            }
        });
        
        context.getSource().sendFeedback(() -> Text.literal("§a已打开成就界面"), true);
        return 1;
    }
    
    /**
     * 列出所有成就
     */
    private static int listAchievements(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        AchievementManager manager = AchievementManager.getInstance();
        
        source.sendFeedback(() -> Text.literal("§6=== 成就列表 ==="), false);
        
        int count = 0;
        for (Achievement achievement : manager.getAllAchievements()) {
            final int finalCount = count + 1;
            Text status = Text.literal("§7[???]");
            
            source.sendFeedback(() -> Text.empty()
                .append(Text.literal("§e" + finalCount + ". "))
                .append(status)
                .append(Text.literal(" "))
                .append(achievement.getTitle()), false);
            count++;
        }
        
        final int finalTotal = count;
        source.sendFeedback(() -> Text.literal("§7共 " + finalTotal + " 个成就"), false);
        return 1;
    }
    
    /**
     * 显示个人成就进度
     */
    private static int showProgress(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("此命令只能由玩家执行"));
            return 0;
        }
        
        AchievementManager manager = AchievementManager.getInstance();
        AchievementProgress progress = manager.getPlayerProgress(player.getUuid());
        
        int unlocked = progress.getUnlockedAchievements().size();
        int total = manager.getTotalAchievements();
        double percent = (total > 0) ? (unlocked * 100.0 / total) : 0;
        
        context.getSource().sendFeedback(() -> Text.literal(
            String.format("§6=== 成就进度 ===\n§e已解锁：§f%d/%d §7(%.1f%%)", unlocked, total, percent)
        ), false);
        
        return 1;
    }
    
    /**
     * 解锁成就（管理员命令）
     */
    private static int unlockAchievement(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("此命令只能由玩家执行"));
            return 0;
        }
        
        net.minecraft.util.Identifier achievementId = IdentifierArgumentType.getIdentifier(context, "achievement");
        AchievementManager manager = AchievementManager.getInstance();
        
        if (manager.unlockAchievement(player.getUuid(), achievementId)) {
            context.getSource().sendFeedback(() -> Text.literal("§a已解锁成就：" + achievementId), true);
        } else {
            context.getSource().sendError(Text.literal("无法解锁成就：" + achievementId));
        }
        
        return 1;
    }
}
