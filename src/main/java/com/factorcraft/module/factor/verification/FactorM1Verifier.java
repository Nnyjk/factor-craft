package com.factorcraft.module.factor.verification;

import com.factorcraft.module.factor.DayTierDecider;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.FactorTier;
import com.factorcraft.module.factor.state.EventCooldownState;

import java.util.Map;

/**
 * 纯 JVM 校验：验证 M1 核心数值规则与冷却状态逻辑。
 */
public final class FactorM1Verifier {
    private static final double HYSTERESIS = 2.0;
    private static final String DISASTER_EVENT_ID = "factor_disaster";

    private FactorM1Verifier() {}

    public static void main(String[] args) {
        assertEquals(FactorTier.DEPLETED.level(), FactorTier.fromFactor(8).level(), "tier depleted");
        assertEquals(FactorTier.LOW_ENERGY.level(), FactorTier.fromFactor(20).level(), "tier low");
        assertEquals(FactorTier.STABLE.level(), FactorTier.fromFactor(50).level(), "tier stable");
        assertEquals(FactorTier.HIGH_ENERGY.level(), FactorTier.fromFactor(75).level(), "tier high");
        assertEquals(FactorTier.OVERLOAD.level(), FactorTier.fromFactor(92).level(), "tier overload");

        int keepTier = DayTierDecider.resolveTier(28.5, FactorTier.STABLE.level(), HYSTERESIS);
        int dropTier = DayTierDecider.resolveTier(27.5, FactorTier.STABLE.level(), HYSTERESIS);
        assertEquals(FactorTier.STABLE.level(), keepTier, "hysteresis keep tier");
        assertEquals(FactorTier.LOW_ENERGY.level(), dropTier, "hysteresis drop tier");

        assertEquals(80.0, FactorService.baseForDimension("minecraft:the_nether"), "nether base");
        assertEquals(20.0, FactorService.baseForDimension("minecraft:the_end"), "end base");
        assertEquals(50.0, FactorService.baseForDimension("minecraft:overworld"), "overworld base");

        EventCooldownState cooldownState = new EventCooldownState(Map.of(DISASTER_EVENT_ID, 2000L));
        assertTrue(cooldownState.isCoolingDown(DISASTER_EVENT_ID, 1999), "cooldown active");
        assertTrue(!cooldownState.isCoolingDown(DISASTER_EVENT_ID, 2000), "cooldown end");

        System.out.println("M1 verifier passed");
    }

    private static void assertEquals(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) {
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
