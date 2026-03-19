package com.factorcraft.update;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 更新检查命令
 */
public class UpdateCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("factorcraft")
            .then(CommandManager.literal("update")
                .executes(UpdateCommands::checkUpdate)
                .then(CommandManager.literal("check")
                    .executes(UpdateCommands::checkUpdate))
                .then(CommandManager.literal("config")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.literal("enable")
                        .executes(ctx -> setConfig(ctx, "enabled", true)))
                    .then(CommandManager.literal("disable")
                        .executes(ctx -> setConfig(ctx, "enabled", false)))
                    .then(CommandManager.literal("silent")
                        .executes(ctx -> setConfig(ctx, "silent", true)))
                    .then(CommandManager.literal("notify")
                        .executes(ctx -> setConfig(ctx, "silent", false))))
            )
        );
    }
    
    private static int checkUpdate(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        String currentVersion = FabricLoader.getInstance()
            .getModContainer("factorcraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");
        
        source.sendFeedback(() -> Text.literal("§e[FactorCraft] §7检查更新中..."), false);
        
        UpdateChecker.checkForUpdate(currentVersion).thenAccept(info -> {
            source.getServer().execute(() -> {
                if (info.hasUpdate()) {
                    source.sendFeedback(() -> Text.literal("§e[FactorCraft] §a发现新版本!"), false);
                    source.sendFeedback(() -> Text.literal("§7当前版本: §f" + info.getCurrentVersion()), false);
                    source.sendFeedback(() -> Text.literal("§7最新版本: §f" + info.getLatestVersion()), false);
                    
                    // 发布页面链接
                    Text linkText = Text.literal("§e[点击查看更新]")
                        .styled(style -> style
                            .withClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL, 
                                info.getReleaseUrl()))
                            .withUnderline(true));
                    source.sendFeedback(() -> linkText, false);
                    
                    // 显示简短 changelog
                    String changelog = info.getShortChangelog(3);
                    if (!changelog.isEmpty()) {
                        source.sendFeedback(() -> Text.literal("§7更新内容:"), false);
                        for (String line : changelog.split("\n")) {
                            final String l = line;
                            source.sendFeedback(() -> Text.literal("§8- §f" + l), false);
                        }
                    }
                } else {
                    source.sendFeedback(() -> Text.literal("§e[FactorCraft] §a已是最新版本: §f" + currentVersion), false);
                }
            });
        });
        
        return 1;
    }
    
    private static int setConfig(CommandContext<ServerCommandSource> context, String key, boolean value) {
        ServerCommandSource source = context.getSource();
        UpdateConfig config = UpdateConfig.getInstance();
        
        switch (key) {
            case "enabled" -> config.setEnabled(value);
            case "silent" -> config.setSilentCheck(value);
        }
        
        config.save();
        
        source.sendFeedback(() -> Text.literal("§e[FactorCraft] §a配置已更新: §f" + key + " = " + value), false);
        
        return 1;
    }
}