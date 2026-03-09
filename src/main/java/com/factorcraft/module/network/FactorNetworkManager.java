package com.factorcraft.module.network;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Factor 网络管理器
 * 
 * 负责跨维度 Factor 同步和传输
 */
public class FactorNetworkManager {
    
    private static FactorNetworkManager instance;
    
    private FactorNetworkManager() {}
    
    public static FactorNetworkManager getInstance() {
        if (instance == null) {
            instance = new FactorNetworkManager();
        }
        return instance;
    }
    
    /**
     * 跨维度传输 Factor
     */
    public double transferFactor(
            ServerWorld fromWorld, BlockPos fromPos,
            ServerWorld toWorld, BlockPos toPos,
            int amount, double efficiency) {
        
        double fromBase = getDimensionBase(fromWorld);
        double toBase = getDimensionBase(toWorld);
        double multiplier = fromBase / toBase;
        double distance = fromPos.getSquaredDistance(toPos);
        double distanceLoss = Math.min(0.5, distance / 10000.0);
        double received = amount * multiplier * efficiency * (1 - distanceLoss);
        
        return received;
    }
    
    private double getDimensionBase(ServerWorld world) {
        String dimensionKey = world.getRegistryKey().getValue().toString();
        if (dimensionKey.contains("the_nether")) return 1.5;
        if (dimensionKey.contains("the_end")) return 3.0;
        return 0.5;
    }
    
    public void initialize() {
        System.out.println("[FactorNetworkManager] 网络管理器已初始化");
    }
}
