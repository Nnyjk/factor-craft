package com.factorcraft.module.profession.event;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业事件接口
 */
public interface ProfessionEvent {
    
    /**
     * 获取事件类型
     */
    ProfessionEventType getType();
    
    /**
     * 获取触发事件的玩家
     */
    ServerPlayerEntity getPlayer();
    
    /**
     * 获取事件时间戳
     */
    long getTimestamp();
    
    /**
     * 是否可取消
     */
    boolean isCancellable();
    
    /**
     * 是否已取消
     */
    boolean isCancelled();
    
    /**
     * 取消事件
     */
    default void cancel() {
        if (!isCancellable()) {
            throw new UnsupportedOperationException("此事件不可取消");
        }
    }
}