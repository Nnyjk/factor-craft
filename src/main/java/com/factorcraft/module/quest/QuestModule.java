package com.factorcraft.module.quest;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.quest.generator.QuestGenerator;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplate;
import com.factorcraft.module.quest.template.QuestTemplateLoader;
import com.factorcraft.module.quest.data.PlayerQuestData;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * 任务系统模块 (配置驱动)
 * 
 * 功能:
 * - JSON 配置定义任务模板
 * - 条件系统支持动态组合
 * - 奖励系统可配置
 * - 任务追踪 UI
 * - 每日任务生成
 * - 支持数据包扩展
 */
public final class QuestModule implements FactorCraftModule, QuestTemplateLoader.QuestManagerRef {
    
    private static QuestModule instance;
    private QuestManager questManager;
    private QuestTemplateLoader templateLoader;
    
    private QuestModule() {}
    
    public static QuestModule getInstance() {
        if (instance == null) {
            instance = new QuestModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "quest";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of("factor");
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 正在初始化任务系统...");
        
        this.questManager = new QuestManager();
        this.templateLoader = new QuestTemplateLoader();
        this.templateLoader.setManagerRef(this);
        
        // 注册服务端启动时的资源加载
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        
        PlayerQuestData.register();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 任务系统已初始化");
    }
    
    /**
     * 服务端启动时从资源加载任务模板
     */
    private void onServerStarting(MinecraftServer server) {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 从服务端资源加载任务模板...");
        templateLoader.loadFromResources(server.getResourceManager());
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 已加载 {} 个任务模板", 
            templateLoader.getLoadedCount());
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 正在重新加载任务配置...");
        this.templateLoader.reload();
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 任务配置已重新加载");
    }
    
    /**
     * 实现 QuestManagerRef 接口 - 注册模板到 QuestManager
     */
    @Override
    public void registerTemplate(QuestTemplate template) {
        if (questManager != null) {
            questManager.registerTemplate(template);
        }
    }
    
    /**
     * 为玩家生成每日任务
     */
    public void generateDailyQuests(PlayerEntity player) {
        QuestGenerator.generateDailyQuests(player, questManager, 
            List.copyOf(questManager.getAllTemplates()));
        FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 已为玩家 {} 生成每日任务", 
            player.getName().getString());
    }
    
    /**
     * 为玩家生成推荐任务
     */
    public void generateRecommendedQuests(PlayerEntity player) {
        QuestGenerator.generateRecommendedQuests(player, questManager, 
            List.copyOf(questManager.getAllTemplates()));
    }
    
    public QuestManager getQuestManager() {
        return this.questManager;
    }
    
    public QuestTemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }
}