package com.factorcraft.module.factor.management;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块 Factor 事件处理器
 * 
 * 监听区块加载/卸载事件，管理 Factor 状态的持久化
 */
public class ChunkFactorEventHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft:ChunkFactor");
    
    /**
     * 注册事件处理器
     */
    public static void register() {
        // 服务器启动时初始化存储
        ServerWorldEvents.LOAD.register(ChunkFactorEventHandler::onWorldLoad);
        
        // 区块加载时恢复 Factor 状态
        ServerChunkEvents.CHUNK_LOAD.register(ChunkFactorEventHandler::onChunkLoad);
        
        // 区块卸载时保存 Factor 状态
        ServerChunkEvents.CHUNK_UNLOAD.register(ChunkFactorEventHandler::onChunkUnload);
        
        LOGGER.info("[ChunkFactor] 事件处理器已注册");
    }
    
    /**
     * 世界加载时初始化存储
     */
    private static void onWorldLoad(MinecraftServer server, ServerWorld world) {
        String dimension = world.getRegistryKey().getValue().toString();
        
        // 确保持久化存储被初始化
        ChunkFactorStorage storage = ChunkFactorStorage.get(world);
        
        LOGGER.info("[ChunkFactor] 世界 {} 加载，已恢复 {} 个区块的 Factor 数据", 
            dimension, storage.getLoadedChunkCount());
    }
    
    /**
     * 区块加载时恢复 Factor 状态
     */
    private static void onChunkLoad(ServerWorld world, WorldChunk chunk) {
        ChunkPos pos = chunk.getPos();
        ChunkFactorStorage storage = ChunkFactorStorage.get(world);
        
        // 获取或创建区块状态
        ChunkFactorState state = storage.getOrCreateState(world, pos);
        
        // 更新最后更新时间
        state.setLastUpdatedTick(world.getTime());
        
        // 同步到内存缓存（用于快速访问）
        ChunkFactorManager.setState(pos, state);
        
        LOGGER.debug("[ChunkFactor] 区块 {}/{} 加载，Factor 浓度: {}", 
            pos.x, pos.z, state.getCurrentConcentration());
    }
    
    /**
     * 区块卸载时保存 Factor 状态
     */
    private static void onChunkUnload(ServerWorld world, WorldChunk chunk) {
        ChunkPos pos = chunk.getPos();
        
        // 从内存缓存获取最新状态
        ChunkFactorManager.getState(pos).ifPresent(state -> {
            // 保存到持久化存储
            ChunkFactorStorage storage = ChunkFactorStorage.get(world);
            storage.updateState(pos, state);
            
            LOGGER.debug("[ChunkFactor] 区块 {}/{} 卸载，Factor 浓度: {}", 
                pos.x, pos.z, state.getCurrentConcentration());
        });
    }
}