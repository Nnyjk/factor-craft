package com.factorcraft.module.command.verification;

import com.factorcraft.module.command.api.CommandApi;
import com.factorcraft.module.command.api.CommandHandler;
import com.factorcraft.module.command.context.CommandExecutionContext;
import com.factorcraft.module.command.model.CommandScope;
import com.factorcraft.module.command.model.CommandSpec;
import com.factorcraft.module.command.registry.CommandRegistry;
import com.factorcraft.module.command.result.CommandExecutionResult;
import com.factorcraft.module.command.service.CommandExecutionService;
import com.factorcraft.module.command.validation.CommandSpecValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * M0 回归检查（纯 JVM，无第三方测试框架）。
 */
public final class CommandM0Verifier {
    private CommandM0Verifier() {}

    public static void main(String[] args) {
        verifyValidator();
        verifyRegistry();
        verifyExecutionService();
        System.out.println("M0 verifier passed");
    }

    private static void verifyValidator() {
        CommandSpec valid = new CommandSpec(
                "factor_craft:factor_debug",
                "factor_craft",
                "factor.debug",
                List.of("fc_debug"),
                "desc",
                "permission",
                CommandScope.OPERATOR,
                0,
                0,
                true,
                Set.of("debug"),
                List.of()
        );
        CommandSpecValidator.validate(valid);

        boolean invalidRejected = false;
        try {
            CommandSpecValidator.validate(new CommandSpec(
                    "factor_debug",
                    "factor_craft",
                    "factor.debug",
                    List.of(),
                    "desc",
                    "permission",
                    CommandScope.OPERATOR,
                    0,
                    0,
                    true,
                    Set.of(),
                    List.of()
            ));
        } catch (IllegalArgumentException ignored) {
            invalidRejected = true;
        }
        assertTrue(invalidRejected, "validator should reject invalid command id");
    }

    private static void verifyRegistry() {
        CommandRegistry registry = CommandRegistry.getInstance();
        registry.clearCommands();

        CommandSpec spec = new CommandSpec(
                "factor_craft:factor_debug",
                "factor_craft",
                "factor.debug",
                List.of("fc_debug"),
                "desc",
                "permission",
                CommandScope.OPERATOR,
                0,
                0,
                true,
                Set.of("debug"),
                List.of()
        );

        registry.registerCommand(spec);
        assertTrue(registry.findByAlias("fc_debug").isPresent(), "alias should be indexed");

        boolean duplicateRejected = false;
        try {
            registry.registerCommand(spec);
        } catch (IllegalArgumentException ignored) {
            duplicateRejected = true;
        }
        assertTrue(duplicateRejected, "duplicate command should be rejected");
    }

    private static void verifyExecutionService() {
        CommandSpec spec = new CommandSpec(
                "factor_craft:factor_debug",
                "factor_craft",
                "factor.debug",
                List.of("fc_debug"),
                "desc",
                "permission",
                CommandScope.OPERATOR,
                0,
                1,
                true,
                Set.of("debug"),
                List.of()
        );

        CommandHandler handler = new CommandHandler() {
            @Override
            public String handlerId() {
                return "factor.debug";
            }

            @Override
            public CommandExecutionResult execute(CommandExecutionContext context) {
                return CommandExecutionResult.success("ok");
            }
        };

        CommandExecutionService service = CommandExecutionService.forTests(new FakeApi(spec, handler));
        CommandExecutionResult ok = service.execute("fc_debug", new CommandExecutionContext(null, Map.of()));
        assertTrue(ok.success(), "existing command should succeed");

        CommandExecutionResult notFound = service.execute("missing", new CommandExecutionContext(null, Map.of()));
        assertTrue("COMMAND_NOT_FOUND".equals(notFound.code()), "missing command should return not found code");

        CommandExecutionResult rateLimited = service.execute("fc_debug", new CommandExecutionContext(null, Map.of()));
        assertTrue("RATE_LIMITED".equals(rateLimited.code()), "second call should be rate limited");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private record FakeApi(CommandSpec spec, CommandHandler handler) implements CommandApi {
        @Override
        public Optional<CommandSpec> findByCommandId(String commandId) {
            if (spec != null && spec.commandId().equals(commandId)) {
                return Optional.of(spec);
            }
            return Optional.empty();
        }

        @Override
        public Optional<CommandSpec> findByAlias(String alias) {
            if (spec != null && spec.aliases().contains(alias)) {
                return Optional.of(spec);
            }
            return Optional.empty();
        }

        @Override
        public Optional<CommandHandler> findHandler(String handlerId) {
            if (handler != null && handler.handlerId().equals(handlerId)) {
                return Optional.of(handler);
            }
            return Optional.empty();
        }
    }
}
