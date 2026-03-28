package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 探索触发器数据
 */
public class ExplorationData {
    private final String dimension;
    private final String structure;
    private final double x;
    private final double z;
    
    public ExplorationData(String dimension, String structure, double x, double z) {
        this.dimension = dimension;
        this.structure = structure;
        this.x = x;
        this.z = z;
    }
    
    public String getDimension() {
        return dimension;
    }
    
    public String getStructure() {
        return structure;
    }
    
    public double getX() {
        return x;
    }
    
    public double getZ() {
        return z;
    }
}
