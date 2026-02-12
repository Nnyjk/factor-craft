package com.factorcraft.module.command.result;

/**
 * 命令执行结果标准：便于审计与监控。
 */
public record CommandExecutionResult(
        boolean success,
        String message,
        String errorCode
) {
    public static CommandExecutionResult success(String message) {
        return new CommandExecutionResult(true, message, "");
    }

    public static CommandExecutionResult failure(String message, String errorCode) {
        return new CommandExecutionResult(false, message, errorCode);
    }
}
