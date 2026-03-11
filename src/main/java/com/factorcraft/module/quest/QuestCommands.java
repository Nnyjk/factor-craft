package com.factorcraft.module.quest;

import com.factorcraft.module.quest.manager.QuestManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * 任务系统命令
 */
public class QuestCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("quest")
            .requires(source -> source.hasPermissionLevel(0))
            .then(literal("list")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> Text.literal("Opening quest tracker..."), false);
                    // 打开任务追踪 UI (服务端发送数据包)
                    try {
                        var player = source.getPlayer();
                        if (player != null) {
                            QuestManager manager = QuestModule.getInstance().getQuestManager();
                            // 发送任务列表给客户端
                            source.sendFeedback(() -> Text.literal("Active quests: " + 
                                manager.getActiveQuests(player.getUuid()).size()), false);
                        }
                    } catch (Exception e) {
                        source.sendFeedback(() -> Text.literal("Error loading quests"), false);
                    }
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
