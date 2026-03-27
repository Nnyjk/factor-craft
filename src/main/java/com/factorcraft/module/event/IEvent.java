package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * 事件接口
 * 
 * 所有随机事件必须实现此接口
 */
public interface IEvent {
    /**
     * 获取事件唯一 ID
     */
    UUID getId();
    
    /**
     * 获取事件类型
     */
    EventType getType();
    
    /**
     * 获取事件持续时间（ticks）
     */
    int getDuration();
    
    /**
     * 获取事件影响的维度
     */
    Identifier getWorldKey();
    
    /**
     * 事件开始时的回调
     * @param server 服务器实例
     * @param world 影响的世界
     */
    void onStart(MinecraftServer server, ServerWorld world);
    
    /**
     * 事件结束时的回调
     * @param server 服务器实例
     * @param world 影响的世界
     */
    void onEnd(MinecraftServer server, ServerWorld world);
    
    /**
     * 每 tick 调用的更新逻辑
     * @param server 服务器实例
     * @param world 影响的世界
     * @param remainingTicks 剩余 ticks
     */
    void onTick(MinecraftServer server, ServerWorld world, int remainingTicks);
    
    /**
     * 事件是否已完成
     */
    default boolean isFinished() {
        return false;
    }
}
