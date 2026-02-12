package com.factorcraft.module.command.validation;

import com.factorcraft.module.command.model.CommandArgSpec;
import com.factorcraft.module.command.model.CommandSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 命令配置统一校验器。
 */
public final class CommandSpecValidator {
    private static final Pattern ID_PATTERN = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9_/.-]+$");
    private static final Pattern SIMPLE_NAME_PATTERN = Pattern.compile("^[a-z0-9_:-]+$");

    private CommandSpecValidator() {}

    public static void validate(CommandSpec spec) {
        require(spec != null, "spec must not be null");
        require(nonEmpty(spec.commandId()), "commandId must not be empty");
        require(ID_PATTERN.matcher(spec.commandId()).matches(), "commandId must be namespace:path");
        require(nonEmpty(spec.sourcePackId()), "sourcePackId must not be empty");
        require(nonEmpty(spec.handlerId()), "handlerId must not be empty");
        require(SIMPLE_NAME_PATTERN.matcher(spec.handlerId()).matches(), "handlerId format invalid");
        require(nonEmpty(spec.permission()), "permission must not be empty");
        require(spec.scope() != null, "scope must not be null");
        require(spec.cooldownTicks() >= 0, "cooldownTicks must be >= 0");
        require(spec.rateLimitPerMinute() >= 0, "rateLimitPerMinute must be >= 0");
        validateAliases(spec.aliases());
        validateArgs(spec.args());
    }

    private static void validateAliases(Iterable<String> aliases) {
        if (aliases == null) {
            return;
        }
        Set<String> seen = new HashSet<>();
        for (String alias : aliases) {
            require(nonEmpty(alias), "alias must not be empty");
            require(SIMPLE_NAME_PATTERN.matcher(alias).matches(), "alias format invalid");
            String normalized = alias.toLowerCase();
            require(seen.add(normalized), "duplicate alias: " + alias);
        }
    }

    private static void validateArgs(Iterable<CommandArgSpec> args) {
        if (args == null) {
            return;
        }
        Set<String> names = new HashSet<>();
        for (CommandArgSpec arg : args) {
            require(arg != null, "arg spec must not be null");
            require(nonEmpty(arg.name()), "arg name must not be empty");
            require(SIMPLE_NAME_PATTERN.matcher(arg.name()).matches(), "arg name format invalid");
            require(arg.type() != null, "arg type must not be null");
            require(names.add(arg.name().toLowerCase()), "duplicate arg name: " + arg.name());
        }
    }

    private static boolean nonEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
