package com.factorcraft.module.core.achievement.trigger;

import com.factorcraft.FactorCraftMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * 成就触发器注册表
 * 单例模式，管理所有触发器的注册和事件分发
 */
public class TriggerRegistry {
    
    private static TriggerRegistry instance;
    
    // 已注册的触发器
    private final Map<String, AchievementTrigger<?>> registeredTriggers;
    
    // 按类型组织的触发器
    private final Map<TriggerType, List<AchievementTrigger<?>>> triggersByType;
    
    @SuppressWarnings("unchecked")
    private TriggerRegistry() {
        this.registeredTriggers = new HashMap<>();
        this.triggersByType = new HashMap<>();
        
        // 初始化类型列表
        for (TriggerType type : TriggerType.values()) {
            triggersByType.put(type, new ArrayList<>());
        }
    }
    
    public static TriggerRegistry getInstance() {
        if (instance == null) {
            instance = new TriggerRegistry();
        }
        return instance;
    }
    
    /**
     * 注册触发器
     */
    @SuppressWarnings("unchecked")
    public <T> void register(AchievementTrigger<T> trigger) {
        registeredTriggers.put(trigger.getId(), trigger);
        triggersByType.get(trigger.getType()).add(trigger);
        FactorCraftMod.LOGGER.info("Registered achievement trigger: {}", trigger.getId());
    }
    
    /**
     * 批量注册触发器
     */
    @SuppressWarnings("unchecked")
    public <T> void registerAll(AchievementTrigger<T>... triggers) {
        for (AchievementTrigger<T> trigger : triggers) {
            register(trigger);
        }
    }
    
    /**
     * 根据 ID 获取触发器
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<AchievementTrigger<T>> getTrigger(String id) {
        return Optional.ofNullable((AchievementTrigger<T>) registeredTriggers.get(id));
    }
    
    /**
     * 获取所有触发器
     */
    public Collection<AchievementTrigger<?>> getAllTriggers() {
        return registeredTriggers.values();
    }
    
    /**
     * 按类型获取触发器
     */
    @SuppressWarnings("unchecked")
    public <T> List<AchievementTrigger<T>> getTriggersByType(TriggerType type) {
        return (List<AchievementTrigger<T>>) (List<?>) triggersByType.get(type);
    }
    
    /**
     * 触发事件并更新成就进度
     * @param player 玩家
     * @param type 触发器类型
     * @param data 事件数据
     */
    @SuppressWarnings("unchecked")
    public <T> void fireEvent(ServerPlayerEntity player, TriggerType type, T data) {
        List<AchievementTrigger<T>> triggers = getTriggersByType(type);
        for (AchievementTrigger<T> trigger : triggers) {
            if (trigger.matches(data)) {
                int progress = trigger.trigger(player, data);
                if (progress > 0) {
                    // TODO: 调用 AchievementManager 更新进度
                    FactorCraftMod.LOGGER.debug("Trigger {} fired for player {}: +{}", 
                        trigger.getId(), player.getName().getString(), progress);
                }
            }
        }
    }
    
    /**
     * 获取触发器总数
     */
    public int getTotalTriggers() {
        return registeredTriggers.size();
    }
    
    /**
     * 清除所有注册（用于测试）
     */
    public void clear() {
        registeredTriggers.clear();
        for (List<AchievementTrigger<?>> list : triggersByType.values()) {
            list.clear();
        }
    }
}
