package com.factorcraft.module.profession.command;

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
        
        // TODO: 实现职业信息显示
        source.sendFeedback(() -> Text.translatable("command.factorcraft.profession.info"), false);
        
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
}