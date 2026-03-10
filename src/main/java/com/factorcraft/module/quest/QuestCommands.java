package com.factorcraft.module.quest;

import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.ui.QuestTrackerScreen;
import com.factorcraft.module.quest.ui.QuestTrackerScreenHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * 任务系统命令
 */
public class QuestCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandSource.RegistrationEnvironment environment) {
        dispatcher.register(literal("quest")
            .requires(source -> source.hasPermissionLevel(0))
            .then(literal("list")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> Text.literal("Opening quest tracker..."), false);
                    // TODO: 打开 UI
                    return 1;
                })
            )
            .then(literal("info")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    QuestManager manager = QuestModule.getInstance().getQuestManager();
                    int activeCount = manager.getActiveQuests(source.getPlayer().getUuid()).size();
                    source.sendFeedback(() -> Text.literal("Active quests: " + activeCount), false);
                    return 1;
                })
            )
        );
    }
}
