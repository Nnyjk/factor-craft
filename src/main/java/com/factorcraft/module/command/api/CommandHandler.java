package com.factorcraft.module.command.api;

import com.factorcraft.module.command.context.CommandExecutionContext;
import com.factorcraft.module.command.result.CommandExecutionResult;

/**
 * 受控命令处理器接口。
 */
public interface CommandHandler {
    String handlerId();

    CommandExecutionResult execute(CommandExecutionContext context);
}
