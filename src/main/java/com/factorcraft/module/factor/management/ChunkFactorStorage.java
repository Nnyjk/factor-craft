package com.factorcraft.module.factor.management;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.state.ChunkFactorState;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 区块 Factor 状态持久化管理器
 * 
 * 使用 PersistentState 在世界加载时恢复区块 Factor 数据
 */
public class ChunkFactorStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_chunk_factor";
    
    private final Long2ObjectMap<ChunkFactorState> chunkStates = new Long2ObjectOpenHashMap<>();
    
    public ChunkFactorStorage() {}
    
    /**
     * 从 NBT 加载
     */
    public ChunkFactorStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = nbt.getList("chunks", NbtCompound.COMPOUND_TYPE);
        
        for (int i = 0; i < list.size(); i++) {
            NbtCompound chunkNbt = list.getCompound(i);
            long chunkKey = chunkNbt.getLong("key");
            
            double initialConc = chunkNbt.getDouble("initial");
            double currentConc = chunkNbt.getDouble("current");
            long lastUpdated = chunkNbt.getLong("lastUpdated");
            boolean anchored = chunkNbt.getBoolean("anchored");
            int anchorRadius = chunkNbt.getInt("anchorRadius");
            
            ChunkFactorState state = new ChunkFactorState(initialConc);
            state.setCurrentConcentration(currentConc);
            state.setLastUpdatedTick(lastUpdated);
            state.setAnchored(anchored);
            state.setAnchorRadius(anchorRadius);
            
            chunkStates.put(chunkKey, state);
        }
        
        FactorCraftMod.LOGGER.info("[ChunkFactorStorage] 已加载 {} 个区块的 Factor 数据", chunkStates.size());
    }
    
    /**
     * 保存到 NBT
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        
        for (Long2ObjectMap.Entry<ChunkFactorState> entry : chunkStates.long2ObjectEntrySet()) {
            NbtCompound chunkNbt = new NbtCompound();
            chunkNbt.putLong("key", entry.getLongKey());
            
            ChunkFactorState state = entry.getValue();
            chunkNbt.putDouble("initial", state.getInitialConcentration());
            chunkNbt.putDouble("current", state.getCurrentConcentration());
            chunkNbt.putLong("lastUpdated", state.getLastUpdatedTick());
            chunkNbt.putBoolean("anchored", state.isAnchored());
            chunkNbt.putInt("anchorRadius", state.getAnchorRadius());
            
            list.add(chunkNbt);
        }
        
        nbt.put("chunks", list);
        return nbt;
    }
    
    /**
     * 获取或创建区块状态
     */
    public ChunkFactorState getOrCreateState(World world, ChunkPos pos) {
        long key = pos.toLong();
        return chunkStates.computeIfAbsent(key, k -> {
            double initialConcentration = calculateInitialConcentration(world, pos);
            return new ChunkFactorState(initialConcentration);
        });
    }
    
    /**
     * 获取区块状态（可能为空）
     */
    public Optional<ChunkFactorState> getState(ChunkPos pos) {
        return Optional.ofNullable(chunkStates.get(pos.toLong()));
    }
    
    /**
     * 设置区块状态
     */
    public void setState(ChunkPos pos, ChunkFactorState state) {
        chunkStates.put(pos.toLong(), state);
        this.markDirty();
    }
    
    /**
     * 移除区块状态（区块卸载时调用）
     */
    public void removeState(ChunkPos pos) {
        chunkStates.remove(pos.toLong());
        this.markDirty();
    }
    
    /**
     * 更新区块状态并标记脏
     */
    public void updateState(ChunkPos pos, ChunkFactorState state) {
        chunkStates.put(pos.toLong(), state);
        this.markDirty();
    }
    
    /**
     * 获取已加载区块数量
     */
    public int getLoadedChunkCount() {
        return chunkStates.size();
    }
    
    /**
     * 获取所有已加载区块
     */
    public Set<ChunkPos> getAllLoadedChunks() {
        return chunkStates.keySet().stream()
            .mapToLong(Long::longValue)
            .mapToObj(ChunkPos::new)
            .collect(Collectors.toSet());
    }
    
    /**
     * 计算初始浓度
     */
    private double calculateInitialConcentration(World world, ChunkPos pos) {
        String dimension = world.getRegistryKey().getValue().toString();
        double baseline = switch (dimension) {
            case "minecraft:the_nether" -> 70.0;
            case "minecraft:the_end" -> 100.0;
            default -> 40.0;
        };
        
        // 基于位置的确定性噪声（相同位置总是相同的初始值）
        long seed = pos.x * 341873128712L + pos.z * 132897987541L;
        double noise = ((seed % 41) - 20) / 2.0; // -10 到 10
        
        return Math.max(0, baseline + noise);
    }
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 从世界获取存储实例
     */
    public static ChunkFactorStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                ChunkFactorStorage::new,
                ChunkFactorStorage::new,
                null
            ),
            KEY
        );
    }
    
    /**
     * 便捷方法：获取或创建区块状态
     */
    public static ChunkFactorState getChunkState(ServerWorld world, ChunkPos pos) {
        return get(world).getOrCreateState(world, pos);
    }
    
    /**
     * 便捷方法：获取区块状态
     */
    public static Optional<ChunkFactorState> getChunkState(ChunkPos pos, ServerWorld world) {
        return get(world).getState(pos);
    }
}