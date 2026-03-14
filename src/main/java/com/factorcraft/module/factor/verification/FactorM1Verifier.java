package com.factorcraft.module.factor.verification;

import com.factorcraft.module.factor.DayTierDecider;
import com.factorcraft.module.factor.DimensionType;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.FactorTier;
import com.factorcraft.module.factor.state.EventCooldownState;

import java.util.Map;

/**
 * 纯 JVM 校验：验证 M1 核心数值规则与冷却状态逻辑。
 * 
 * 使用偏离度体系：
 * - 基准值：主世界 0.5 / 下界 1.5 / 末地 3.0
 * - Tier 由偏离度决定
 */
public final class FactorM1Verifier {
    private static final double HYSTERESIS = 0.1; // 迟滞阈值（偏离度）
    private static final String DISASTER_EVENT_ID = "factor_disaster";

    private FactorM1Verifier() {}

    public static void main(String[] args) {
        // 使用主世界基准值 0.5 测试 Tier
        double baseValue = DimensionType.OVERWORLD.baseValue();
        
        // 测试各 Tier 的偏离度边界
        // DEPLETED: 偏离 < -50% → factor < 0.25
        assertEquals(FactorTier.DEPLETED.level(), 
            FactorTier.fromFactor(0.2, baseValue).level(), "tier depleted");
        
        // LOW_ENERGY: 偏离 -50% ~ -20% → factor 0.25 ~ 0.4
        assertEquals(FactorTier.LOW_ENERGY.level(), 
            FactorTier.fromFactor(0.35, baseValue).level(), "tier low");
        
        // STABLE: 偏离 -20% ~ +20% → factor 0.4 ~ 0.6
        assertEquals(FactorTier.STABLE.level(), 
            FactorTier.fromFactor(0.5, baseValue).level(), "tier stable");
        
        // HIGH_ENERGY: 偏离 +20% ~ +50% → factor 0.6 ~ 0.75
        assertEquals(FactorTier.HIGH_ENERGY.level(), 
            FactorTier.fromFactor(0.65, baseValue).level(), "tier high");
        
        // OVERLOAD: 偏离 > +50% → factor > 0.75
        assertEquals(FactorTier.OVERLOAD.level(), 
            FactorTier.fromFactor(0.8, baseValue).level(), "tier overload");

        // 测试迟滞
        // 偏离度 -23% (factor = 0.385)，在迟滞范围内，保持 STABLE
        int keepTier = DayTierDecider.resolveTier(0.385, baseValue, FactorTier.STABLE.level(), HYSTERESIS);
        // 偏离度 -30% (factor = 0.35)，超出迟滞范围，降为 LOW_ENERGY
        int dropTier = DayTierDecider.resolveTier(0.35, baseValue, FactorTier.STABLE.level(), HYSTERESIS);
        assertEquals(FactorTier.STABLE.level(), keepTier, "hysteresis keep tier");
        assertEquals(FactorTier.LOW_ENERGY.level(), dropTier, "hysteresis drop tier");

        // 测试维度基准值
        assertEquals(1.5, FactorService.baseForDimension("minecraft:the_nether"), "nether base");
        assertEquals(3.0, FactorService.baseForDimension("minecraft:the_end"), "end base");
        assertEquals(0.5, FactorService.baseForDimension("minecraft:overworld"), "overworld base");

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