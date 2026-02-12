package com.factorcraft.module.command.model;

/**
 * 命令参数标准，采用约束型定义，不开放脚本执行能力。
 */
public record CommandArgSpec(
        String name,
        CommandArgType type,
        boolean required,
        String description,
        String defaultValue
) {}
