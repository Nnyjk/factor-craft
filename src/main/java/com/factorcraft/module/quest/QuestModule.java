package com.factorcraft.module.quest;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplateLoader;
import com.factorcraft.module.quest.data.PlayerQuestData;

import java.util.List;

/**
 * 任务系统模块 (配置驱动)
 * 
 * 功能:
 * - JSON 配置定义任务模板
 * - 条件系统支持动态组合
 * - 奖励系统可配置
 * - 任务追踪 UI
 * - 支持数据包扩展
 */
public final class QuestModule implements FactorCraftModule {
    
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
        
        this.templateLoader = new QuestTemplateLoader();
        this.questManager = new QuestManager();
        this.templateLoader.loadAll();
        PlayerQuestData.register();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 任务系统已初始化");
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 已加载 {} 个任务模板", 
            this.templateLoader.getLoadedCount());
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 正在重新加载任务配置...");
        this.templateLoader.reload();
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 任务配置已重新加载");
    }
    
    public QuestManager getQuestManager() {
        return this.questManager;
    }
    
    public QuestTemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }
}