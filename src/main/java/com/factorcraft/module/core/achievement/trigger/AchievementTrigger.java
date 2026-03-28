package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * 成就触发器接口
 * 定义成就触发的通用行为
 * 
 * @param <T> 触发事件数据类型
 */
public interface AchievementTrigger<T> {
    
    /**
     * 获取触发器唯一标识
     */
    String getId();
    
    /**
     * 检查事件是否匹配此触发器
     * @param data 事件数据
     * @return 是否匹配
     */
    boolean matches(T data);
    
    /**
     * 触发成就进度更新
     * @param player 玩家
     * @param data 事件数据
     * @return 进度增量
     */
    int trigger(ServerPlayerEntity player, T data);
    
    /**
     * 获取触发器类型
     */
    TriggerType getType();
}
