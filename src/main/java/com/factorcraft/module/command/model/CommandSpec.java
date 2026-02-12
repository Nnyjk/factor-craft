package com.factorcraft.module.command.model;

import java.util.List;
import java.util.Set;

/**
 * 命令配置标准：由配置驱动，转发到受控 handler。
 */
public record CommandSpec(
        String commandId,
        String sourcePackId,
        String handlerId,
        List<String> aliases,
        String description,
        String permission,
        CommandScope scope,
        int cooldownTicks,
        int rateLimitPerMinute,
        boolean enabled,
        Set<String> tags,
        List<CommandArgSpec> args
) {}
