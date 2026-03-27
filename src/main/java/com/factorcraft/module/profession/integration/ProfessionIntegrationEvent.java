package com.factorcraft.module.profession.integration;

import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * 职业系统集成事件
 * 
 * 用于在不同游戏系统与职业系统之间传递事件数据
 */
public class ProfessionIntegrationEvent {
    
    /** 触发事件的玩家 */
    private final ServerPlayerEntity player;
    
    /** 事件来源标识（方块ID、任务ID、成就ID等） */
    private final String sourceId;
    
    /** 事件相关的方块 */
    private final Block block;
    
    /** 事件发生的位置 */
    private final BlockPos pos;
    
    /** 事件数值（经验值、效率倍率等） */
    private final float value;
    
    /** 时间戳 */
    private final long timestamp;
    
    /**
     * 创建方块相关事件
     */
    public ProfessionIntegrationEvent(ServerPlayerEntity player, Block block, BlockPos pos, float value) {
        this.player = player;
        this.sourceId = block != null ? block.toString() : "unknown";
        this.block = block;
        this.pos = pos;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 创建ID相关事件（任务、成就等）
     */
    public ProfessionIntegrationEvent(ServerPlayerEntity player, String sourceId, float value) {
        this.player = player;
        this.sourceId = sourceId;
        this.block = null;
        this.pos = null;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ServerPlayerEntity getPlayer() {
        return player;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public Block getBlock() {
        return block;
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    public float getValue() {
        return value;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("ProfessionIntegrationEvent{player=%s, source=%s, value=%.2f}", 
            player.getName().getString(), sourceId, value);
    }
}