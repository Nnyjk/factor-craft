package com.factorcraft.module.command;

import com.factorcraft.module.command.model.CommandScope;
import com.factorcraft.module.command.model.CommandSpec;
import com.factorcraft.module.command.registry.CommandRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommandRegistry 测试
 */
class CommandRegistryTest {
    
    private CommandRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = CommandRegistry.getInstance();
        registry.clearCommands();
    }
    
    private CommandSpec createSpec(String commandId, String handlerId, List<String> aliases) {
        return new CommandSpec(
            commandId, "test-pack", handlerId, aliases,
            "test", "factorcraft.test", CommandScope.OPERATOR,
            0, 0, true, Set.of(), List.of()
        );
    }
    
    @Test
    void shouldAllowDottedHandlerId() {
        // Issue #5: 允许 handlerId 包含点号
        CommandSpec spec = createSpec("factorcraft:test", "factor.debug", List.of());
        assertDoesNotThrow(() -> registry.registerCommand(spec));
    }
    
    @Test
    void shouldRejectAliasCollidingWithCommandId() {
        // Issue #6: 别名不能与现有命令 ID 冲突
        registry.registerCommand(createSpec("factorcraft:cmd1", "handler1", List.of()));
        
        // 别名 "factorcraft:cmd1" 与已注册的命令 ID 冲突
        CommandSpec spec2 = createSpec("factorcraft:cmd2", "handler2", List.of("factorcraft:cmd1"));
        assertThrows(IllegalArgumentException.class, () -> registry.registerCommand(spec2));
    }
    
    @Test
    void shouldRejectCommandIdCollidingWithAlias() {
        // Issue #6: 命令 ID 不能与现有别名冲突
        registry.registerCommand(createSpec("factorcraft:cmd1", "handler1", List.of("alias1")));
        
        CommandSpec spec2 = createSpec("alias1", "handler2", List.of());
        assertThrows(IllegalArgumentException.class, () -> registry.registerCommand(spec2));
    }
    
    @Test
    void shouldRegisterAndFindCommand() {
        CommandSpec spec = createSpec("factorcraft:test", "handler1", List.of("t", "tst"));
        registry.registerCommand(spec);
        
        assertTrue(registry.findByCommandId("factorcraft:test").isPresent());
        assertTrue(registry.findByAlias("t").isPresent());
        assertTrue(registry.findByAlias("tst").isPresent());
    }
}