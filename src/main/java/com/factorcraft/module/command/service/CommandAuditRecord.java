package com.factorcraft.module.command.service;

import com.factorcraft.module.command.result.CommandExecutionResult;

public record CommandAuditRecord(
        String commandInput,
        String commandId,
        String executorName,
        long executedAtMillis,
        CommandExecutionResult result
) {}
