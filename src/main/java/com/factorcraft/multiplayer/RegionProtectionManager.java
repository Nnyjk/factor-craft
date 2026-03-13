package com.factorcraft.multiplayer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

/**
 * 区域保护系统
 * 允许玩家保护 Factor 区域不被他人破坏
 */
public class RegionProtectionManager {
    private static final Map<UUID, Set<ProtectedRegion>> PROTECTED_REGIONS = new HashMap<>();
    private static final int MAX_REGIONS_PER_PLAYER = 3;
    private static final int MIN_RADIUS = 3;
    private static final int MAX_RADIUS = 50;
    
    /**
     * 创建保护区
     */
    public static boolean createRegion(ServerPlayerEntity player, BlockPos center, int radius, String name) {
        UUID playerId = player.getUuid();
        
        // 检查玩家区域数量限制
        Set<ProtectedRegion> playerRegions = PROTECTED_REGIONS.computeIfAbsent(playerId, k -> new HashSet<>());
        if (playerRegions.size() >= MAX_REGIONS_PER_PLAYER) {
            player.sendMessage(net.minecraft.text.Text.literal("§c你已达到保护区数量上限 (" + MAX_REGIONS_PER_PLAYER + ")"), false);
            return false;
        }
        
        // 检查半径限制
        if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
            player.sendMessage(net.minecraft.text.Text.literal("§c半径必须在 " + MIN_RADIUS + "-" + MAX_RADIUS + " 之间"), false);
            return false;
        }
        
        // 检查是否与其他区域重叠
        ChunkPos chunkPos = new ChunkPos(center);
        for (ProtectedRegion region : playerRegions) {
            if (regionsOverlap(region.center(), center, region.radius(), radius)) {
                player.sendMessage(net.minecraft.text.Text.literal("§c该区域与现有保护区重叠"), false);
                return false;
            }
        }
        
        // 创建保护区
        ProtectedRegion region = new ProtectedRegion(center, radius, name, playerId);
        playerRegions.add(region);
        
        player.sendMessage(net.minecraft.text.Text.literal("§a已创建保护区: " + name), false);
        return true;
    }
    
    /**
     * 删除保护区
     */
    public static boolean removeRegion(ServerPlayerEntity player, String name) {
        UUID playerId = player.getUuid();
        Set<ProtectedRegion> playerRegions = PROTECTED_REGIONS.get(playerId);
        
        if (playerRegions == null) {
            return false;
        }
        
        return playerRegions.removeIf(region -> {
            if (region.name().equals(name)) {
                player.sendMessage(net.minecraft.text.Text.literal("§a已删除保护区: " + name), false);
                return true;
            }
            return false;
        });
    }
    
    /**
     * 检查位置是否在保护区内
     */
    public static boolean isProtected(BlockPos pos, UUID excludePlayer) {
        for (Map.Entry<UUID, Set<ProtectedRegion>> entry : PROTECTED_REGIONS.entrySet()) {
            if (entry.getKey().equals(excludePlayer)) continue;
            
            for (ProtectedRegion region : entry.getValue()) {
                if (isInRegion(pos, region)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 检查玩家是否可以在该位置操作
     */
    public static boolean canInteract(ServerPlayerEntity player, BlockPos pos) {
        UUID playerId = player.getUuid();
        
        // 检查是否在保护区内
        for (Map.Entry<UUID, Set<ProtectedRegion>> entry : PROTECTED_REGIONS.entrySet()) {
            if (entry.getKey().equals(playerId)) continue; // 自己的区域可以操作
            
            for (ProtectedRegion region : entry.getValue()) {
                if (isInRegion(pos, region)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 获取玩家的所有保护区
     */
    public static Set<ProtectedRegion> getPlayerRegions(UUID playerId) {
        return PROTECTED_REGIONS.getOrDefault(playerId, Collections.emptySet());
    }
    
    private static boolean isInRegion(BlockPos pos, ProtectedRegion region) {
        double dx = pos.getX() - region.center().getX();
        double dz = pos.getZ() - region.center().getZ();
        return dx * dx + dz * dz <= region.radius() * region.radius();
    }
    
    private static boolean regionsOverlap(BlockPos center1, BlockPos center2, int radius1, int radius2) {
        double dx = center1.getX() - center2.getX();
        double dz = center1.getZ() - center2.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        return distance < radius1 + radius2;
    }
}

/**
 * 保护区定义
 */
record ProtectedRegion(
    BlockPos center,
    int radius,
    String name,
    UUID ownerId
) {}