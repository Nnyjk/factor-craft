package com.factorcraft.module.social.manager;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * 权限管理命令
 */
public class PermissionCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("factor")
            .then(literal("permission")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("list")
                    .executes(PermissionCommands::listGroups))
                .then(literal("groups")
                    .executes(PermissionCommands::listGroups))
                .then(literal("player")
                    .then(argument("player", StringArgumentType.word())
                        .then(literal("info")
                            .executes(PermissionCommands::playerInfo))
                        .then(literal("setgroup")
                            .then(argument("group", StringArgumentType.word())
                                .executes(PermissionCommands::setPlayerGroup)))))
                .then(literal("reload")
                    .requires(source -> source.hasPermissionLevel(3))
                    .executes(PermissionCommands::reloadConfig)))
        );
    }
    
    private static int listGroups(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("§e=== 权限组列表 ==="), false);
        
        for (PermissionGroup group : PermissionManager.getAllGroups()) {
            String defaultMarker = group.getId().equals(PermissionManager.getDefaultGroup()) ? " §7(默认)" : "";
            source.sendFeedback(() -> Text.literal(String.format("§f- §b%s §7(%s)%s", 
                group.getDisplayName(), group.getId(), defaultMarker)), false);
            source.sendFeedback(() -> Text.literal(String.format("  §7权限数: §f%d", 
                group.getPermissions().size())), false);
        }
        
        return 1;
    }
    
    private static int playerInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String playerName = StringArgumentType.getString(context, "player");
        
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendError(Text.literal("§c玩家不存在: " + playerName));
            return 0;
        }
        
        String uuid = player.getUuidAsString();
        String groupId = PermissionManager.getPlayerGroup(uuid);
        PermissionGroup group = PermissionManager.getGroup(groupId);
        
        source.sendFeedback(() -> Text.literal(String.format("§e=== 玩家权限信息: %s ===", playerName)), false);
        source.sendFeedback(() -> Text.literal(String.format("§f权限组: §b%s", 
            group != null ? group.getDisplayName() : groupId)), false);
        
        if (group != null) {
            source.sendFeedback(() -> Text.literal("§f权限列表:"), false);
            for (Permission perm : group.getPermissions()) {
                source.sendFeedback(() -> Text.literal(String.format("  §7- §f%s", perm.id())), false);
            }
        }
        
        return 1;
    }
    
    private static int setPlayerGroup(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String playerName = StringArgumentType.getString(context, "player");
        String groupId = StringArgumentType.getString(context, "group");
        
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(playerName);
        if (player == null) {
            source.sendError(Text.literal("§c玩家不存在: " + playerName));
            return 0;
        }
        
        PermissionGroup group = PermissionManager.getGroup(groupId);
        if (group == null) {
            source.sendError(Text.literal("§c权限组不存在: " + groupId));
            return 0;
        }
        
        String uuid = player.getUuidAsString();
        PermissionManager.setPlayerGroup(uuid, groupId);
        
        source.sendFeedback(() -> Text.literal(String.format("§a已将玩家 §f%s §a的权限组设置为 §b%s", 
            playerName, group.getDisplayName())), true);
        
        return 1;
    }
    
    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        PermissionConfig.reload();
        
        source.sendFeedback(() -> Text.literal("§a权限配置已重载"), true);
        
        return 1;
    }
}
