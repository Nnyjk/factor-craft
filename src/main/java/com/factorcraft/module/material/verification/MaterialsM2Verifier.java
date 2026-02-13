package com.factorcraft.module.material.verification;

import com.factorcraft.module.material.config.MaterialsM2ConfigParser;
import com.factorcraft.module.material.config.MaterialsM2Defaults;
import com.factorcraft.module.material.service.MaterialRollResult;
import com.factorcraft.module.material.service.MaterialsM2Service;
import com.factorcraft.module.material.validation.MaterialsM2SpecValidator;

import java.util.Map;

/**
 * 纯 JVM 校验：验证 M2 设计闭环与可复现性。
 */
public final class MaterialsM2Verifier {
    private MaterialsM2Verifier() {}

    public static void main(String[] args) {
        var config = MaterialsM2Defaults.create();
        MaterialsM2SpecValidator.validate(config);
        MaterialsM2Service service = new MaterialsM2Service(config);

        MaterialRollResult first = service.roll(424242L, "minecraft:the_nether", 3);

        var fallback = MaterialsM2ConfigParser.parseOrDefault(Map.of(), config);
        assertEquals(config.materials().getFirst().materialId(), fallback.materials().getFirst().materialId(), "empty dynamic fallback");
        var malformedFallback = MaterialsM2ConfigParser.parseOrDefault(Map.of("materials", Map.of("bad", true)), config);
        assertEquals(config.traits().size(), malformedFallback.traits().size(), "malformed dynamic fallback");
        MaterialRollResult second = service.roll(424242L, "minecraft:the_nether", 3);
        assertEquals(first.materialId(), second.materialId(), "deterministic material");
        assertEquals(first.traitIds().toString(), second.traitIds().toString(), "deterministic traits");
        assertEquals(first.enchantIds().toString(), second.enchantIds().toString(), "deterministic enchants");

        assertTrue(!first.traitIds().isEmpty(), "trait chain present");
        assertTrue(!first.enchantIds().isEmpty(), "enchant chain present");
        assertTrue(!first.statuses().isEmpty(), "status chain present");

        var dailyStatuses = service.settleDailyStatusPool(2, 3);
        assertTrue(!dailyStatuses.isEmpty(), "daily settlement statuses present");

        System.out.println("M2 verifier passed: " + first);
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(message + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new IllegalStateException(message + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
