package com.factorcraft.module.quest.registry;

import com.factorcraft.module.quest.config.QuestConfig;
import com.factorcraft.module.quest.config.QuestConfigLoader;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 任务注册表
 * 
 * 管理所有已注册的任务配置
 */
public class QuestRegistry {
    
    private static final QuestRegistry INSTANCE = new QuestRegistry();
    
    private final Map<Identifier, QuestConfig> registeredQuests = new HashMap<>();
    private final QuestConfigLoader configLoader;
    
    private QuestRegistry() {
        this.configLoader = new QuestConfigLoader();
        
        // 设置配置加载回调
        this.configLoader.setCallback(configs -> {
            registeredQuests.clear();
            registeredQuests.putAll(configs);
        });
    }
    
    /**
     * 获取注册表实例
     */
    public static QuestRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * 获取配置加载器
     */
    public QuestConfigLoader getConfigLoader() {
        return configLoader;
    }
    
    /**
     * 注册任务
     */
    public void register(Identifier questId, QuestConfig config) {
        registeredQuests.put(questId, config);
    }
    
    /**
     * 注销任务
     */
    public void unregister(Identifier questId) {
        registeredQuests.remove(questId);
    }
    
    /**
     * 获取任务配置
     */
    public Optional<QuestConfig> getQuestConfig(Identifier questId) {
        return Optional.ofNullable(registeredQuests.get(questId));
    }
    
    /**
     * 检查任务是否存在
     */
    public boolean exists(Identifier questId) {
        return registeredQuests.containsKey(questId);
    }
    
    /**
     * 获取所有任务配置
     */
    public Collection<QuestConfig> getAllQuests() {
        return registeredQuests.values();
    }
    
    /**
     * 获取所有任务 ID
     */
    public Collection<Identifier> getAllQuestIds() {
        return registeredQuests.keySet();
    }
    
    /**
     * 获取任务数量
     */
    public int getQuestCount() {
        return registeredQuests.size();
    }
    
    /**
     * 清空注册表
     */
    public void clear() {
        registeredQuests.clear();
    }
}