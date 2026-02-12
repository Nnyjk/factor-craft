package com.factorcraft.module.command.registry;

import com.factorcraft.module.command.api.CommandApi;
import com.factorcraft.module.command.api.CommandHandler;
import com.factorcraft.module.command.api.CommandRegistrar;
import com.factorcraft.module.command.model.CommandSpec;
import com.factorcraft.module.command.validation.CommandSpecValidator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 命令标准注册中心：集中维护命令定义、别名索引、处理器索引。
 */
public final class CommandRegistry implements CommandRegistrar, CommandApi {
    private static final CommandRegistry INSTANCE = new CommandRegistry();

    private final Map<String, CommandSpec> byCommandId = new LinkedHashMap<>();
    private final Map<String, String> aliasToCommandId = new LinkedHashMap<>();
    private final Map<String, CommandHandler> handlers = new LinkedHashMap<>();

    private CommandRegistry() {}

    public static CommandRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void registerCommand(CommandSpec spec) {
        CommandSpecValidator.validate(spec);
        String normalizedCommandId = normalize(spec.commandId());
        if (byCommandId.containsKey(normalizedCommandId)) {
            throw new IllegalArgumentException("duplicate commandId: " + spec.commandId());
        }
        byCommandId.put(normalizedCommandId, spec);

        if (spec.aliases() != null) {
            for (String alias : spec.aliases()) {
                String normalizedAlias = normalize(alias);
                if (aliasToCommandId.containsKey(normalizedAlias)) {
                    throw new IllegalArgumentException("duplicate alias: " + alias);
                }
                aliasToCommandId.put(normalizedAlias, normalizedCommandId);
            }
        }
    }

    @Override
    public synchronized void registerHandler(CommandHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler must not be null");
        }
        String handlerId = normalize(handler.handlerId());
        if (handlerId.isBlank()) {
            throw new IllegalArgumentException("handlerId must not be blank");
        }
        if (handlers.containsKey(handlerId)) {
            throw new IllegalArgumentException("duplicate handlerId: " + handler.handlerId());
        }
        handlers.put(handlerId, handler);
    }

    @Override
    public synchronized Optional<CommandSpec> findByCommandId(String commandId) {
        return Optional.ofNullable(byCommandId.get(normalize(commandId)));
    }

    @Override
    public synchronized Optional<CommandSpec> findByAlias(String alias) {
        String commandId = aliasToCommandId.get(normalize(alias));
        if (commandId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byCommandId.get(commandId));
    }

    @Override
    public synchronized Optional<CommandHandler> findHandler(String handlerId) {
        return Optional.ofNullable(handlers.get(normalize(handlerId)));
    }

    public synchronized Map<String, CommandSpec> commandsView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(byCommandId));
    }

    public synchronized Map<String, String> aliasesView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(aliasToCommandId));
    }

    public synchronized Map<String, CommandHandler> handlersView() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(handlers));
    }

    public synchronized void clearCommands() {
        byCommandId.clear();
        aliasToCommandId.clear();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
