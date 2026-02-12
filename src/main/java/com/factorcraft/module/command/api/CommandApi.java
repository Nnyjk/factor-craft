package com.factorcraft.module.command.api;

import com.factorcraft.module.command.model.CommandSpec;

import java.util.Optional;

public interface CommandApi {
    Optional<CommandSpec> findByCommandId(String commandId);

    Optional<CommandSpec> findByAlias(String alias);

    Optional<CommandHandler> findHandler(String handlerId);
}
