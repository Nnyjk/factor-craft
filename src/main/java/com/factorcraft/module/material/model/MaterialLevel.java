package com.factorcraft.module.material.model;

/**
 * M2 材料等级定义（Lv1~Lv5）。
 */
public enum MaterialLevel {
    LV1_CRUDE(1, 1),
    LV2_INDUSTRIAL(2, 2),
    LV3_DIMENSIONAL(3, 3),
    LV4_ANCIENT(4, 4),
    LV5_ARBITER(5, 5);

    private final int level;
    private final int minTraitSlots;

    MaterialLevel(int level, int minTraitSlots) {
        this.level = level;
        this.minTraitSlots = minTraitSlots;
    }

    public int level() {
        return level;
    }

    public int minTraitSlots() {
        return minTraitSlots;
    }
}
