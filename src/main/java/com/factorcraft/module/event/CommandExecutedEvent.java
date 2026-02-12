package com.factorcraft.module.event;

import com.factorcraft.module.command.result.CommandExecutionResult;

public record CommandExecutedEvent(String commandId, String executorName, CommandExecutionResult result) {}
