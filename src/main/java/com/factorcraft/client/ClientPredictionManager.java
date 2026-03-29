package com.factorcraft.client;

import com.factorcraft.factor.FactorType;
import com.factorcraft.module.network.NetworkConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * R3.3 客户端预测管理器
 * 
 * 负责在客户端预测 Factor 浓度变化，减少网络延迟带来的视觉卡顿
 * 支持预测回滚和服务器校正
 */
public class ClientPredictionManager {
    
    private static ClientPredictionManager instance;
    
    private final Map<ChunkPos, PredictedChunkData> predictedData;
    private final Map<ChunkPos, Long> lastUpdateTime;
    private long lastCleanupTime;
    
    private ClientPredictionManager() {
        this.predictedData = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        this.lastCleanupTime = System.currentTimeMillis();
    }
    
    /**
     * 获取单例实例
     */
    public static @NotNull ClientPredictionManager getInstance() {
        if (instance == null) {
            instance = new ClientPredictionManager();
        }
        return instance;
    }
    
    /**
     * 重置管理器（用于世界切换）
     */
    public void reset() {
        predictedData.clear();
        lastUpdateTime.clear();
    }
    
    /**
     * 预测 Factor 浓度变化
     * 
     * @param pos chunk 位置
     * @param type Factor 类型
     * @param delta 变化量
     */
    public void predictFactorChange(@NotNull ChunkPos pos, @NotNull Identifier type, float delta) {
        PredictedChunkData data = getOrCreateData(pos);
        data.predictFactorChange(type, delta);
        lastUpdateTime.put(pos, System.currentTimeMillis());
        
        // 定期清理过期数据
        maybeCleanup();
    }
    
    /**
     * 预测机器状态变化
     * 
     * @param pos 机器位置
     * @param energyDelta 能量变化
     * @param progressDelta 进度变化
     */
    public void predictMachineState(@NotNull ChunkPos pos, long energyDelta, int progressDelta) {
        PredictedChunkData data = getOrCreateData(pos);
        data.predictMachineState(energyDelta, progressDelta);
        lastUpdateTime.put(pos, System.currentTimeMillis());
        
        maybeCleanup();
    }
    
    /**
     * 应用服务器校正数据
     * 
     * @param pos chunk 位置
     * @param actualConcentrations 实际浓度
     */
    public void applyServerCorrection(@NotNull ChunkPos pos, @NotNull Map<Identifier, Float> actualConcentrations) {
        PredictedChunkData data = predictedData.get(pos);
        if (data != null) {
            data.applyCorrection(actualConcentrations);
        }
        lastUpdateTime.put(pos, System.currentTimeMillis());
    }
    
    /**
     * 获取预测的 Factor 浓度
     * 
     * @param pos chunk 位置
     * @param type Factor 类型
     * @return 预测浓度
     */
    public float getPredictedConcentration(@NotNull ChunkPos pos, @NotNull Identifier type) {
        PredictedChunkData data = predictedData.get(pos);
        if (data != null) {
            Float predicted = data.predictedConcentrations.get(type);
            if (predicted != null) {
                return predicted;
            }
        }
        return 0.0f;
    }
    
    /**
     * 获取预测的机器能量
     */
    public long getPredictedEnergy(@NotNull ChunkPos pos) {
        PredictedChunkData data = predictedData.get(pos);
        return data != null ? data.predictedEnergy : 0L;
    }
    
    /**
     * 获取预测的机器进度
     */
    public int getPredictedProgress(@NotNull ChunkPos pos) {
        PredictedChunkData data = predictedData.get(pos);
        return data != null ? data.predictedProgress : 0;
    }
    
    /**
     * 获取或创建预测数据
     */
    @NotNull
    private PredictedChunkData getOrCreateData(@NotNull ChunkPos pos) {
        return predictedData.computeIfAbsent(pos, k -> new PredictedChunkData());
    }
    
    /**
     * 清理过期数据
     */
    private void maybeCleanup() {
        long now = System.currentTimeMillis();
        long cleanupInterval = NetworkConfig.PREDICTION_CLEANUP_INTERVAL_MS;
        
        if (now - lastCleanupTime >= cleanupInterval) {
            cleanup();
            lastCleanupTime = now;
        }
    }
    
    /**
     * 清理超过预测有效期的数据
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        long expiryTime = NetworkConfig.PREDICTION_EXPIRY_MS;
        
        predictedData.entrySet().removeIf(entry -> {
            Long lastUpdate = lastUpdateTime.get(entry.getKey());
            return lastUpdate != null && (now - lastUpdate) > expiryTime;
        });
        
        lastUpdateTime.entrySet().removeIf(entry -> {
            return (now - entry.getValue()) > expiryTime;
        });
    }
    
    /**
     * 预测的 chunk 数据
     */
    public static class PredictedChunkData {
        public final Map<Identifier, Float> predictedConcentrations;
        public long predictedEnergy;
        public int predictedProgress;
        
        public PredictedChunkData() {
            this.predictedConcentrations = new HashMap<>();
            this.predictedEnergy = 0L;
            this.predictedProgress = 0;
        }
        
        public void predictFactorChange(@NotNull Identifier type, float delta) {
            float current = predictedConcentrations.getOrDefault(type, 0.0f);
            float predicted = Math.max(0.0f, current + delta);
            predictedConcentrations.put(type, predicted);
        }
        
        public void predictMachineState(long energyDelta, int progressDelta) {
            predictedEnergy = Math.max(0L, predictedEnergy + energyDelta);
            predictedProgress = Math.min(100, Math.max(0, predictedProgress + progressDelta));
        }
        
        public void applyCorrection(@NotNull Map<Identifier, Float> actualConcentrations) {
            // 使用服务器数据覆盖预测值
            predictedConcentrations.clear();
            predictedConcentrations.putAll(actualConcentrations);
        }
    }
    
    /**
     * 客户端接收服务器同步数据时的处理
     * 
     * @param pos chunk 位置
     * @param concentrations 服务器浓度数据
     */
    public static void onServerSync(@NotNull ChunkPos pos, @NotNull Map<Identifier, Float> concentrations) {
        ClientPredictionManager manager = getInstance();
        manager.applyServerCorrection(pos, concentrations);
    }
    
    /**
     * 客户端本地 Factor 变化时的预测
     * 
     * @param pos chunk 位置
     * @param type Factor 类型
     * @param delta 变化量
     */
    public static void onLocalFactorChange(@NotNull ChunkPos pos, @NotNull Identifier type, float delta) {
        ClientPredictionManager manager = getInstance();
        manager.predictFactorChange(pos, type, delta);
    }
    
    /**
     * 客户端本地机器状态变化时的预测
     * 
     * @param pos chunk 位置
     * @param energyDelta 能量变化
     * @param progressDelta 进度变化
     */
    public static void onLocalMachineChange(@NotNull ChunkPos pos, long energyDelta, int progressDelta) {
        ClientPredictionManager manager = getInstance();
        manager.predictMachineState(pos, energyDelta, progressDelta);
    }
}
