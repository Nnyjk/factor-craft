package com.factorcraft.module.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 事件系统命令
 * 
 * 提供事件查询和手动触发功能
 */
public class EventCommands {
    
    /**
     * 注册命令
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register(EventCommands::registerCommands);
    }
    
    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, 
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment environment) {
        
        // /event 命令根节点
        dispatcher.register(CommandManager.literal("event")
            .requires(source -> source.hasPermissionLevel(2)) // 需要管理员权限
            .then(CommandManager.literal("trigger")
                .then(CommandManager.argument("type", StringArgumentType.word())
                    .suggests((context, builder) -> {
                        // 提供事件类型建议
                        for (EventType type : EventType.values()) {
                            builder.suggest(type.getId());
                        }
                        return builder.buildFuture();
                    })
                    .executes(context -> {
                        String typeId = StringArgumentType.getString(context, "type");
                        EventType type = EventType.fromId(typeId);
                        
                        if (type == null) {
                            context.getSource().sendMessage(
                                Text.literal("❌ 未知的事件类型：").append(Text.literal(typeId).formatted(Formatting.RED))
                            );
                            return 0;
                        }
                        
                        ServerWorld world = context.getSource().getWorld();
                        EventModule.getInstance().triggerEvent(
                            context.getSource().getServer(),
                            world,
                            type
                        );
                        
                        context.getSource().sendMessage(
                            Text.literal("✅ 已触发事件：").append(Text.literal(type.getDisplayName()).formatted(Formatting.GOLD))
                        );
                        return 1;
                    })
                )
            )
            .then(CommandManager.literal("list")
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();
                    List<IEvent> events = EventModule.getInstance()
                        .getRandomEventManager()
                        .getActiveEvents(world.getRegistryKey().getValue());
                    
                    if (events.isEmpty()) {
                        context.getSource().sendMessage(
                            Text.literal("📋 当前没有活跃事件").formatted(Formatting.GRAY)
                        );
                        return 1;
                    }
                    
                    context.getSource().sendMessage(
                        Text.literal("📋 活跃事件列表:").formatted(Formatting.GOLD)
                    );
                    
                    for (IEvent event : events) {
                        context.getSource().sendMessage(
                            Text.literal("  - ").append(Text.literal(event.getType().getDisplayName())
                                .formatted(Formatting.YELLOW))
                                .append(Text.literal(" (剩余：" + event.getDuration() / 20 + "秒)").formatted(Formatting.GRAY))
                        );
                    }
                    
                    return 1;
                })
            )
            .then(CommandManager.literal("status")
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();
                    List<IEvent> events = EventModule.getInstance()
                        .getRandomEventManager()
                        .getActiveEvents(world.getRegistryKey().getValue());
                    
                    context.getSource().sendMessage(
                        Text.literal("📊 事件系统状态:").formatted(Formatting.GOLD)
                    );
                    context.getSource().sendMessage(
                        Text.literal("  活跃事件数：").append(Text.literal(String.valueOf(events.size())).formatted(Formatting.YELLOW))
                    );
                    
                    return 1;
                })
            )
        );
    }
}
