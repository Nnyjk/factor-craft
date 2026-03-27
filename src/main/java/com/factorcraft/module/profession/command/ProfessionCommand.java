package com.factorcraft.module.profession.command;

import com.factorcraft.module.profession.HiddenProfessionUnlockManager;
import com.factorcraft.module.profession.ProfessionModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.screen.ProfessionSelectScreenHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * 职业系统命令
 */
public class ProfessionCommand {
    
    /**
     * 注册命令
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("profession")
            .requires(source -> source.hasPermissionLevel(0))
            .executes(ProfessionCommand::openProfessionScreen)
            .then(literal("info")
                .executes(ProfessionCommand::showProfessionInfo))
            .then(literal("reset")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ProfessionCommand::resetProfession))
            .then(literal("unlock")
                .executes(ProfessionCommand::unlockHiddenProfession))
            .then(literal("progress")
                .executes(ProfessionCommand::showUnlockProgress))
        );
    }
    
    /**
     * 打开职业选择界面
     */
    private static int openProfessionScreen(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            return 0;
        }
        
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
            (syncId, playerInventory, playerEntity) -> 
                new ProfessionSelectScreenHandler(syncId, playerInventory, (ServerPlayerEntity) playerEntity),
            Text.translatable("gui.factorcraft.profession_select.title")
        ));
        
        return 1;
    }
    
    /**
     * 显示当前职业信息
     */
    private static int showProfessionInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player == null) {
            return 0;
        }
        
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        PlayerProfessionData data = api.getPlayerData(player);
        
        if (!data.hasProfession()) {
            source.sendFeedback(() -> Text.literal("§c你还没有选择职业"), false);
            return 0;
        }
        
        ProfessionType type = data.getProfessionType();
        source.sendFeedback(() -> Text.literal(String.format(
            "§a当前职业: §f%s §7(Lv.%d)§r\n" +
            "§e经验: §f%d/%d§r\n" +
            "§b天赋点: §f%d§r",
            type.getDisplayName(),
            data.getLevel(),
            data.getExperience(),
            PlayerProfessionData.getExperienceNeededForLevel(data.getLevel() + 1),
            data.getTalentPoints()
        )), false);
        
        return 1;
    }
    
    /**
     * 重置职业（管理员命令）
     */
    private static int resetProfession(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player == null) {
            return 0;
        }
        
        // TODO: 实现职业重置
        source.sendFeedback(() -> Text.translatable("command.factorcraft.profession.reset.success"), true);
        
        return 1;
    }
    
    /**
     * 尝试解锁隐藏职业
     */
    private static int unlockHiddenProfession(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player == null) {
            return 0;
        }
        
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        
        // 检查是否已解锁
        if (api.hasUnlockedHiddenProfession(player)) {
            source.sendFeedback(() -> Text.literal("§e你已经解锁了隐藏职业「因子掌控者」"), false);
            return 0;
        }
        
        // 检查并尝试解锁
        if (api.canUnlockHiddenProfession(player)) {
            String result = api.unlockHiddenProfession(player);
            source.sendFeedback(() -> Text.literal("§d" + result), false);
            return 1;
        } else {
            // 显示未满足的条件
            HiddenProfessionUnlockManager.UnlockProgress progress = 
                HiddenProfessionUnlockManager.getUnlockProgress(player, api);
            
            source.sendFeedback(() -> Text.literal(String.format(
                "§c不满足解锁条件:\n" +
                "§7- 工程师满级: %s\n" +
                "§7- 培育师满级: %s\n" +
                "§7- 探索者满级: %s\n" +
                "§7- 稀有Factor: %d/%d",
                progress.hasEngineerMastered ? "§a✓" : "§c✗",
                progress.hasCultivatorMastered ? "§a✓" : "§c✗",
                progress.hasExplorerMastered ? "§a✓" : "§c✗",
                progress.rareFactorCount,
                HiddenProfessionUnlockManager.REQUIRED_RARE_FACTORS
            )), false);
            return 0;
        }
    }
    
    /**
     * 显示解锁进度
     */
    private static int showUnlockProgress(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        
        if (player == null) {
            return 0;
        }
        
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        HiddenProfessionUnlockManager.UnlockProgress progress = 
            HiddenProfessionUnlockManager.getUnlockProgress(player, api);
        
        if (progress.unlocked) {
            source.sendFeedback(() -> Text.literal("§a你已经解锁了隐藏职业「因子掌控者」"), false);
            return 1;
        }
        
        source.sendFeedback(() -> Text.literal(String.format(
            "§d=== 隐藏职业解锁进度 ===\n" +
            "§f职业: 因子掌控者\n" +
            "§7需要:\n" +
            "§7- Factor工程师满级: %s\n" +
            "§7- 能量培育师满级: %s\n" +
            "§7- 潮汐探索者满级: %s\n" +
            "§7- 稀有Factor收集: §e%d§7/%d\n" +
            "§7进度: §e%d§7/3 职业, §e%d§7/%d Factor",
            progress.hasEngineerMastered ? "§a✓" : "§c✗",
            progress.hasCultivatorMastered ? "§a✓" : "§c✗",
            progress.hasExplorerMastered ? "§a✓" : "§c✗",
            progress.rareFactorCount,
            HiddenProfessionUnlockManager.REQUIRED_RARE_FACTORS,
            progress.getMasteredCount(),
            progress.rareFactorCount,
            HiddenProfessionUnlockManager.REQUIRED_RARE_FACTORS
        )), false);
        
        return 1;
    }
}