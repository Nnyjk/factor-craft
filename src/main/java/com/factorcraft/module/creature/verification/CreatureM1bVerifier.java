package com.factorcraft.module.creature.verification;

import com.factorcraft.module.creature.model.CreatureAbilitySpec;
import com.factorcraft.module.creature.model.DropPoolSpec;
import com.factorcraft.module.creature.model.LootEntrySpec;
import com.factorcraft.module.creature.model.SpawnRuleSpec;

import java.util.List;
import java.util.Set;

/**
 * 纯 JVM 校验：验证 M1b 规则模型基础约束。
 */
public final class CreatureM1bVerifier {
    private static final String ENTITY_ZOMBIE = "minecraft:zombie";

    private CreatureM1bVerifier() {}

    public static void main(String[] args) {
        new SpawnRuleSpec("rule", ENTITY_ZOMBIE, Set.of("minecraft:overworld"), 1, 4, 80, 1, 4);
        new DropPoolSpec("pool", ENTITY_ZOMBIE, 2, 4, List.of(new LootEntrySpec("minecraft:iron_ingot", 0.1, 1, 1, "bonus")));
        new CreatureAbilitySpec("ability", ENTITY_ZOMBIE, 3, 4, Set.of("shockwave"), 100);

        assertThrows(() -> new SpawnRuleSpec("rule_bad", ENTITY_ZOMBIE, Set.of(), 2, 1, 80, 1, 2));
        assertThrows(() -> new DropPoolSpec("pool_bad", ENTITY_ZOMBIE, 2, 4, List.of()));

        System.out.println("M1b verifier passed");
    }

    private static void assertThrows(Runnable runnable) {
        try {
            runnable.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new IllegalStateException("expected IllegalArgumentException");
    }
}
