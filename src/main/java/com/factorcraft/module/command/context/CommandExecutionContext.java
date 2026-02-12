package com.factorcraft.module.command.context;

import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;

/**
 * 命令执行上下文：封装调用者与解析参数。
 */
public record CommandExecutionContext(
        ServerCommandSource source,
        Map<String, String> arguments
) {}
