package com.factorcraft.module.factor;

import com.factorcraft.module.factor.state.FactorWorldState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维度管理器 - 管理所有维度的 Factor 状态
 * 
 * 基于 docs/16_dimensions_and_biomes.md
 */
public class DimensionManager {
    
    private static final DimensionManager instance = new DimensionManager();
    
    // 存储各维度的当前 Factor 状态
    private final Map<String, FactorWorldState> dimensionStates = new ConcurrentHashMap<>();
    
    // 维度类型映射
    private final Map<String, DimensionType> dimensionTypeMap = new HashMap<>();
    
    private DimensionManager() {
        // 初始化维度类型映射
        for (DimensionType type : DimensionType.values()) {
            dimensionTypeMap.put(type.key(), type);
        }
    }
    
    public static DimensionManager getInstance() {
        return instance;
    }
    
    /**
     * 获取或创建维度的 Factor 状态
     */
    public FactorWorldState getOrCreateState(String dimensionKey, long currentTick) {
        return dimensionStates.computeIfAbsent(dimensionKey, key -> {
            DimensionType type = dimensionTypeMap.getOrDefault(key, DimensionType.OVERWORLD);
            double currentFactor = type.calculateFactor(currentTick);
            
            return new FactorWorldState(
                key,
                currentFactor,
                type.baseValue(),
                currentFactor, // dayAverage 初始化为当前值
                0.0, // trend 初始化为 0
                FactorTier.fromFactor(currentFactor).level(),
                currentTick
            );
        });
    }
    
    /**
     * 更新维度的 Factor 状态
     */
    public void updateState(String dimensionKey, long currentTick) {
        DimensionType type = dimensionTypeMap.getOrDefault(dimensionKey, DimensionType.OVERWORLD);
        FactorWorldState oldState = dimensionStates.get(dimensionKey);
        
        if (oldState == null) {
            getOrCreateState(dimensionKey, currentTick);
            return;
        }
        
        double currentFactor = type.calculateFactor(currentTick);
        double trend = currentFactor - oldState.currentFactor();
        
        // 简化版 dayAverage 计算（实际应该基于 24000 tick 的平均）
        double dayAverage = (oldState.dayAverage() * 23 + currentFactor) / 24;
        
        FactorWorldState newState = new FactorWorldState(
            dimensionKey,
            currentFactor,
            type.baseValue(),
            dayAverage,
            trend,
            FactorTier.fromFactor(currentFactor).level(),
            currentTick
        );
        
        dimensionStates.put(dimensionKey, newState);
    }
    
    /**
     * 获取维度类型
     */
    public DimensionType getDimensionType(String dimensionKey) {
        return dimensionTypeMap.getOrDefault(dimensionKey, DimensionType.OVERWORLD);
    }
    
    /**
     * 计算跨维度传输倍率
     * 
     * @param fromKey 源维度 key
     * @param toKey 目标维度 key
     * @return 传输倍率
     */
    public double calculateTransferMultiplier(String fromKey, String toKey) {
        DimensionType from = getDimensionType(fromKey);
        DimensionType to = getDimensionType(toKey);
        return from.calculateTransferMultiplierTo(to);
    }
    
    /**
     * 获取所有维度状态
     */
    public Map<String, FactorWorldState> getAllStates() {
        return new HashMap<>(dimensionStates);
    }
    
    /**
     * 清除维度状态（用于测试）
     */
    public void clearState(String dimensionKey) {
        dimensionStates.remove(dimensionKey);
    }
    
    /**
     * 清除所有状态（用于测试）
     */
    public void clearAllStates() {
        dimensionStates.clear();
    }
    
    /**
     * 获取维度基准值
     */
    public double getDimensionBaseValue(net.minecraft.registry.RegistryKey<net.minecraft.world.World> worldKey) {
        String key = worldKey.getValue().toString();
        return getDimensionBaseValueFromString(key);
    }
    
    /**
     * 从字符串获取维度基准值
     */
    public double getDimensionBaseValueFromString(String dimensionKey) {
        DimensionType type = dimensionTypeMap.getOrDefault(dimensionKey, DimensionType.OVERWORLD);
        return type.baseValue();
    }
}
