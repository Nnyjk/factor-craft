package com.factorcraft.module.command;

import com.factorcraft.module.shared.ModuleLoggers;
import com.factorcraft.dynamic.DynamicBundle;
import com.factorcraft.dynamic.DynamicContentManager;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.command.api.CommandHandler;
import com.factorcraft.module.command.context.CommandExecutionContext;
import com.factorcraft.module.command.model.CommandScope;
import com.factorcraft.module.command.model.CommandSpec;
import com.factorcraft.module.command.registry.CommandRegistry;
import com.factorcraft.module.command.result.CommandExecutionResult;
import com.factorcraft.module.shared.ModuleMilestone;

import java.util.List;
import java.util.Set;

/**
 * 命令系统 MVP 模块（低自由度、强约束）。
 */
public final class CommandModule implements FactorCraftModule {
    private static final org.slf4j.Logger LOG = ModuleLoggers.forModule("command_mvp");
    private final CommandRegistry registry = CommandRegistry.getInstance();

    @Override
    public String moduleId() {
        return "command_mvp";
    }

    @Override
    public void initialize() {
        registerBuiltinHandlers();
        reload();
        LOG.info("[{}] 命令MVP模块初始化完成（动态配置/校验/注册/审计）", ModuleMilestone.M0_COMMAND_MVP);
    }

    @Override
    public void reload() {
        registry.clearCommands();
        registerDynamicCommands();
    }

    private void registerBuiltinHandlers() {
        if (registry.findHandler("factor.debug").isEmpty()) {
            registry.registerHandler(new FactorDebugHandler());
        }
    }

    private void registerDynamicCommands() {
        DynamicBundle bundle = DynamicContentManager.getInstance().current();
        for (DynamicBundle.CommandSpec dynamicSpec : bundle.commands()) {
            if (!dynamicSpec.enabled()) {
                continue;
            }

            CommandSpec commandSpec = new CommandSpec(
                    dynamicSpec.commandId(),
                    "factor_craft",
                    dynamicSpec.handlerId(),
                    List.of(),
                    "dynamic command: " + dynamicSpec.commandId(),
                    dynamicSpec.permission(),
                    CommandScope.OPERATOR,
                    20,
                    30,
                    true,
                    Set.of("dynamic"),
                    List.of()
            );

            try {
                registry.registerCommand(commandSpec);
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException(
                        "命令配置非法，启动已中止。commandId=" + dynamicSpec.commandId() + ", reason=" + ex.getMessage(),
                        ex
                );
            }
        }
    }

    private static final class FactorDebugHandler implements CommandHandler {
        @Override
        public String handlerId() {
            return "factor.debug";
        }

        @Override
        public CommandExecutionResult execute(CommandExecutionContext context) {
            return CommandExecutionResult.success("factor.debug handled");
        }
    }
}
