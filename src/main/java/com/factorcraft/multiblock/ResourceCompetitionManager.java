package com.factorcraft.multiblock;

import com.factorcraft.config.MultiplayerBalanceConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * R3.4 资源竞争管理器
 * 
 * 管理多玩家同时采集资源时的竞争
 * 实现资源声明和保护机制
 */
public class ResourceCompetitionManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceCompetitionManager.class);
    private static ResourceCompetitionManager instance;
    
    private final MultiplayerBalanceConfig config;
    
    /** 资源声明 (chunkKey -> 声明信息) */
    private final Long2ObjectMap<ResourceClaim> claims;
    
    /** 玩家声明的资源 (player UUID -> 资源位置列表) */
    private final Map<UUID, List<BlockPos>> playerClaims;
    
    private ResourceCompetitionManager() {
        this.config = MultiplayerBalanceConfig.getInstance();
        this.claims = new Long2ObjectOpenHashMap<>();
        this.playerClaims = new ConcurrentHashMap<>();
    }
    
    /**
     * 获取实例
     */
    public static ResourceCompetitionManager getInstance() {
        if (instance == null) {
            instance = new ResourceCompetitionManager();
        }
        return instance;
    }
    
    /**
     * 玩家声明资源
     * 
     * @param player 玩家
     * @param pos 资源位置
     * @return 是否声明成功
     */
    public boolean claimResource(PlayerEntity player, BlockPos pos) {
        if (!config.isEnabled()) {
            return true;
        }
        
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        
        // 检查是否已有声明
        ResourceClaim existing = claims.get(chunkKey);
        if (existing != null && !existing.ownerUuid.equals(player.getUuid())) {
            // 检查声明是否过期
            long currentTime = System.currentTimeMillis();
            if (currentTime - existing.timestamp < config.getResourceClaimDuration() * 1000L) {
                LOGGER.debug("资源已被 {} 声明，{} 无法采集", existing.ownerName, player.getName().getString());
                return false;
            }
            
            // 声明过期，释放旧声明
            releaseResourceInternal(existing.ownerUuid, chunkKey);
        }
        
        // 创建新声明
        ResourceClaim claim = new ResourceClaim(
            player.getUuid(),
            player.getName().getString(),
            pos,
            System.currentTimeMillis()
        );
        
        claims.put(chunkKey, claim);
        
        // 记录玩家声明
        playerClaims.computeIfAbsent(player.getUuid(), k -> new ArrayList<>()).add(pos);
        
        LOGGER.debug("{} 声明资源：{}", player.getName().getString(), pos);
        
        return true;
    }
    
    /**
     * 玩家释放资源
     * 
     * @param player 玩家
     * @param pos 资源位置
     */
    public void releaseResource(PlayerEntity player, BlockPos pos) {
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        releaseResourceInternal(player.getUuid(), chunkKey);
    }
    
    /**
     * 内部释放方法
     */
    private void releaseResourceInternal(UUID ownerUuid, long chunkKey) {
        ResourceClaim claim = claims.get(chunkKey);
        if (claim != null && claim.ownerUuid.equals(ownerUuid)) {
            claims.remove(chunkKey);
            
            List<BlockPos> positions = playerClaims.get(ownerUuid);
            if (positions != null) {
                positions.removeIf(pos -> ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4) == chunkKey);
            }
            
            LOGGER.debug("释放资源声明：chunk={}", chunkKey);
        }
    }
    
    /**
     * 检查资源是否可采集
     * 
     * @param player 玩家
     * @param pos 资源位置
     * @return 是否可采集
     */
    public boolean canHarvest(PlayerEntity player, BlockPos pos) {
        if (!config.isEnabled()) {
            return true;
        }
        
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        ResourceClaim claim = claims.get(chunkKey);
        
        if (claim == null) {
            return true;
        }
        
        // 检查是否是声明者
        if (claim.ownerUuid.equals(player.getUuid())) {
            return true;
        }
        
        // 检查是否过期
        long currentTime = System.currentTimeMillis();
        if (currentTime - claim.timestamp >= config.getResourceClaimDuration() * 1000L) {
            // 过期，自动释放
            releaseResourceInternal(claim.ownerUuid, chunkKey);
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取附近的竞争者
     * 
     * @param player 玩家
     * @param radius 半径
     * @return 竞争者列表
     */
    public List<String> getNearbyCompetitors(PlayerEntity player, double radius) {
        List<String> competitors = new ArrayList<>();
        Set<UUID> added = new HashSet<>();
        
        BlockPos playerPos = player.getBlockPos();
        
        for (ResourceClaim claim : claims.values()) {
            if (claim.ownerUuid.equals(player.getUuid())) {
                continue;
            }
            
            if (added.contains(claim.ownerUuid)) {
                continue;
            }
            
            // 计算距离
            double distance = Math.sqrt(claim.pos.getSquaredDistance(playerPos));
            if (distance <= radius) {
                competitors.add(claim.ownerName);
                added.add(claim.ownerUuid);
            }
        }
        
        return competitors;
    }
    
    /**
     * 自动清理过期声明
     */
    public void cleanupExpiredClaims() {
        long currentTime = System.currentTimeMillis();
        long expiryTime = config.getResourceClaimDuration() * 1000L;
        
        List<Long> toRemove = new ArrayList<>();
        
        for (Map.Entry<Long, ResourceClaim> entry : claims.entrySet()) {
            if (currentTime - entry.getValue().timestamp >= expiryTime) {
                toRemove.add(entry.getKey());
            }
        }
        
        for (Long chunkKey : toRemove) {
            ResourceClaim claim = claims.get(chunkKey);
            if (claim != null) {
                releaseResourceInternal(claim.ownerUuid, chunkKey);
            }
        }
        
        if (!toRemove.isEmpty()) {
            LOGGER.debug("清理过期声明：{} 个", toRemove.size());
        }
    }
    
    /**
     * 获取玩家的声明数量
     * 
     * @param player 玩家
     * @return 声明数量
     */
    public int getPlayerClaimCount(PlayerEntity player) {
        List<BlockPos> positions = playerClaims.get(player.getUuid());
        return positions != null ? positions.size() : 0;
    }
    
    /**
     * 清除所有声明
     */
    public void clear() {
        claims.clear();
        playerClaims.clear();
        LOGGER.info("资源竞争管理器已重置");
    }
    
    /**
     * 获取统计信息
     */
    public String getStats() {
        return String.format("资源竞争统计：活跃声明=%d, 声明玩家=%d",
            claims.size(), playerClaims.size());
    }
    
    /**
     * 资源声明信息
     */
    private static class ResourceClaim {
        final UUID ownerUuid;
        final String ownerName;
        final BlockPos pos;
        final long timestamp;
        
        ResourceClaim(UUID ownerUuid, String ownerName, BlockPos pos, long timestamp) {
            this.ownerUuid = ownerUuid;
            this.ownerName = ownerName;
            this.pos = pos;
            this.timestamp = timestamp;
        }
    }
}
