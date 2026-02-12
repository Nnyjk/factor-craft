package com.factorcraft.module.command.service;

import com.factorcraft.module.shared.ModuleLoggers;
import com.factorcraft.module.command.api.CommandApi;
import com.factorcraft.module.command.api.CommandHandler;
import com.factorcraft.module.command.context.CommandExecutionContext;
import com.factorcraft.module.command.model.CommandSpec;
import com.factorcraft.module.command.registry.CommandRegistry;
import com.factorcraft.module.command.result.CommandExecutionResult;
import com.factorcraft.module.event.CommandExecutedEvent;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * 命令执行入口：负责命令查找、handler 调用、限流/冷却校验与审计。
 */
public final class CommandExecutionService {
    private static final org.slf4j.Logger LOG = ModuleLoggers.forModule("command_execution");
    private static final int MAX_AUDIT_RECORDS = 500;
    private static final CommandExecutionService INSTANCE = new CommandExecutionService(CommandRegistry.getInstance());

    private final CommandApi commandApi;
    private final CommandRateLimiter rateLimiter = new CommandRateLimiter();
    private final Deque<CommandAuditRecord> auditLog = new ArrayDeque<>();

    private CommandExecutionService(CommandApi commandApi) {
        this.commandApi = commandApi;
    }

    public static CommandExecutionService getInstance() {
        return INSTANCE;
    }

    public static CommandExecutionService forTests(CommandApi commandApi) {
        return new CommandExecutionService(commandApi);
    }

    public synchronized int currentAuditSize() {
        return auditLog.size();
    }

    public CommandExecutionResult execute(String rawCommandInput, CommandExecutionContext context) {
        long now = System.currentTimeMillis();
        String normalizedInput = rawCommandInput == null ? "" : rawCommandInput.trim().toLowerCase();
        String executorName = context == null || context.source() == null ? "unknown" : context.source().getName();

        if (normalizedInput.isBlank()) {
            return audit(rawCommandInput, "", executorName, now,
                    CommandExecutionResult.failure("命令为空", "EMPTY_COMMAND"));
        }

        Optional<CommandSpec> commandOptional = commandApi.findByCommandId(normalizedInput);
        if (commandOptional.isEmpty()) {
            commandOptional = commandApi.findByAlias(normalizedInput);
        }

        if (commandOptional.isEmpty()) {
            return audit(rawCommandInput, normalizedInput, executorName, now,
                    CommandExecutionResult.failure("命令不存在: " + rawCommandInput, "COMMAND_NOT_FOUND"));
        }

        CommandSpec commandSpec = commandOptional.get();
        if (!commandSpec.enabled()) {
            return audit(rawCommandInput, commandSpec.commandId(), executorName, now,
                    CommandExecutionResult.failure("命令已禁用", "COMMAND_DISABLED"));
        }

        if (!rateLimiter.allow(executorName + "#" + commandSpec.commandId(), commandSpec.rateLimitPerMinute(), now)) {
            return audit(rawCommandInput, commandSpec.commandId(), executorName, now,
                    CommandExecutionResult.failure("命令触发限流", "RATE_LIMITED"));
        }

        Optional<CommandHandler> handlerOptional = commandApi.findHandler(commandSpec.handlerId());
        if (handlerOptional.isEmpty()) {
            return audit(rawCommandInput, commandSpec.commandId(), executorName, now,
                    CommandExecutionResult.failure("handler 未注册: " + commandSpec.handlerId(), "HANDLER_NOT_FOUND"));
        }

        CommandExecutionResult result;
        try {
            result = handlerOptional.get().execute(context);
        } catch (Exception ex) {
            result = CommandExecutionResult.failure("命令执行异常: " + ex.getMessage(), "EXECUTION_EXCEPTION");
        }

        return audit(rawCommandInput, commandSpec.commandId(), executorName, now, result);
    }

    private synchronized CommandExecutionResult audit(String rawInput, String commandId, String executorName, long now,
                                                      CommandExecutionResult result) {
        auditLog.addLast(new CommandAuditRecord(rawInput, commandId, executorName, now, result));
        while (auditLog.size() > MAX_AUDIT_RECORDS) {
            auditLog.removeFirst();
        }

        LOG.info("command_execute command={} executor={} success={} code={} message={}",
                commandId,
                executorName,
                result.success(),
                result.code(),
                result.message());

        SimpleFactorEventBus.getInstance().publish(new CommandExecutedEvent(commandId, executorName, result));
        return result;
    }
}
