package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ChunkFactorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkFactorManager.class);
    private static final Long2ObjectMap<ChunkFactorState> CHUNK_STATES = new Long2ObjectOpenHashMap<>();
    
    public static ChunkFactorState getOrCreateState(World world, ChunkPos pos) {
        long key = pos.toLong();
        return CHUNK_STATES.computeIfAbsent(key, k -> {
            double initialConcentration = calculateInitialConcentration(world, pos);
            return new ChunkFactorState(initialConcentration);
        });
    }
    
    public static Optional<ChunkFactorState> getState(ChunkPos pos) {
        return Optional.ofNullable(CHUNK_STATES.get(pos.toLong()));
    }
    
    public static void setState(ChunkPos pos, ChunkFactorState state) {
        CHUNK_STATES.put(pos.toLong(), state);
    }
    
    private static double calculateInitialConcentration(World world, ChunkPos pos) {
        // 基础浓度（根据维度）
        String dimension = world.getRegistryKey().getValue().toString();
        double baseline = switch (dimension) {
            case "minecraft:the_nether" -> 70.0;
            case "minecraft:the_end" -> 100.0;
            default -> 40.0;
        };
        
        // 添加噪声变化
        double noise = (world.random.nextDouble() - 0.5) * 20.0;
        
        return Math.max(0, baseline + noise);
    }
    
    public static void updateChunk(World world, ChunkPos pos) {
        ChunkFactorState state = getOrCreateState(world, pos);
        long currentTick = world.getTime();
        
        if (currentTick - state.getLastUpdatedTick() > 20) {
            // 自然衰减
            double decay = 0.001;
            state.setCurrentConcentration(state.getCurrentConcentration() - decay);
            state.setLastUpdatedTick(currentTick);
        }
    }
    
    public static void extractFactor(World world, ChunkPos pos, double amount) {
        ChunkFactorState state = getOrCreateState(world, pos);
        state.setCurrentConcentration(state.getCurrentConcentration() - amount);
    }
    
    public static void injectFactor(World world, ChunkPos pos, double amount) {
        ChunkFactorState state = getOrCreateState(world, pos);
        state.setCurrentConcentration(state.getCurrentConcentration() + amount);
    }
    
    public static void clear() {
        CHUNK_STATES.clear();
    }
    
    public static int getLoadedChunkCount() {
        return CHUNK_STATES.size();
    }
    
    public static java.util.Set<ChunkPos> getAllLoadedChunks() {
        return CHUNK_STATES.keySet().stream()
            .mapToLong(Long::longValue)
            .mapToObj(ChunkPos::new)
            .collect(java.util.stream.Collectors.toSet());
    }
}