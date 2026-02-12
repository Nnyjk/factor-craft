package com.factorcraft.module.command;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

/**
 * 命令系统 MVP 模块（低自由度、强约束）。
 */
public final class CommandModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "command_mvp";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 命令MVP模块骨架加载完成（配置化/校验/注册中心/审计预留）", ModuleMilestone.M0_COMMAND_MVP);
    }
}
