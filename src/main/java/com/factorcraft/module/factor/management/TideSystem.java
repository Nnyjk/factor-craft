package com.factorcraft.module.factor.management;

import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TideSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(TideSystem.class);
    private static final long PERIOD_A = 96000;
    private static final long PERIOD_B = 192000;
    private static final double AMPLITUDE_A = 12.0;
    private static final double AMPLITUDE_B = 6.0;
    
    public static double calculateTideModifier(long worldTime) {
        double waveA = Math.sin(2 * Math.PI * worldTime / PERIOD_A) * AMPLITUDE_A;
        double waveB = Math.sin(2 * Math.PI * worldTime / PERIOD_B) * AMPLITUDE_B;
        return waveA + waveB;
    }
    
    public static boolean isOutbreakTime(World world) {
        long time = world.getTime();
        double modifier = calculateTideModifier(time);
        return modifier > 15.0;
    }
    
    public static void applyTideEffects(World world) {
        double tideModifier = calculateTideModifier(world.getTime());
        // 潮汐效果将在后续 Phase 中完整实现
    }
}