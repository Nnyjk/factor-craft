package com.factorcraft.module.material.verification;

import com.factorcraft.module.material.MaterialTierSyncService;
import com.factorcraft.module.material.config.MaterialsM2Defaults;
import com.factorcraft.module.material.service.MaterialsM2Service;
import com.factorcraft.module.material.state.MaterialStatusState;

/**
 * 纯 JVM 校验：验证 M2 日切状态池结算与状态快照存储。
 */
public final class MaterialsM2TierSyncVerifier {
    private MaterialsM2TierSyncVerifier() {}

    public static void main(String[] args) {
        MaterialsM2Service materialsService = new MaterialsM2Service(MaterialsM2Defaults.create());
        MaterialStatusState statusState = new MaterialStatusState();
        MaterialTierSyncService syncService = new MaterialTierSyncService();
        syncService.bind(materialsService, statusState);

        syncService.onTierSettled("minecraft:overworld", 12L, 2, 3);

        var snapshot = statusState.latest("minecraft:overworld").orElseThrow();
        assertEquals(12L, snapshot.dayIndex(), "day index");
        assertEquals(2, snapshot.previousTier(), "previous tier");
        assertEquals(3, snapshot.currentTier(), "current tier");
        assertTrue(!snapshot.statuses().isEmpty(), "settled statuses");
        assertTrue(snapshot.statuses().stream().allMatch(s -> "day_settlement".equals(s.source())), "status source normalized");

        System.out.println("M2 tier-sync verifier passed");
    }

    private static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
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
