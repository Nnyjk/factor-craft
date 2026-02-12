package com.factorcraft.module.command.api;

import com.factorcraft.module.command.model.CommandSpec;

public interface CommandRegistrar {
    void registerCommand(CommandSpec spec);

    void registerHandler(CommandHandler handler);
}
