package com.factorcraft.module.quest.template;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.quest.QuestModule;

/**
 * 任务模板加载器 - 从 JSON 配置加载任务模板
 */
public class QuestTemplateLoader {
    
    private int loadedCount;
    
    public QuestTemplateLoader() {
        this.loadedCount = 0;
    }
    
    /**
     * 加载所有任务模板
     * 从 data/factorcraft/quests/*.json 加载
     */
    public void loadAll() {
        FactorCraftMod.LOGGER.info("[QuestTemplateLoader] 开始加载任务模板...");
        
        // TODO: 实现 JSON 加载逻辑
        // 1. 读取 data/factorcraft/quests/ 目录
        // 2. 解析每个 JSON 文件
        // 3. 创建 QuestTemplate 对象
        // 4. 注册到 QuestManager
        
        this.loadedCount = 0; // 占位，后续实现
        
        FactorCraftMod.LOGGER.info("[QuestTemplateLoader] 加载完成，共 {} 个模板", this.loadedCount);
    }
    
    /**
     * 重新加载任务模板 (支持热重载)
     */
    public void reload() {
        FactorCraftMod.LOGGER.info("[QuestTemplateLoader] 重新加载任务模板...");
        loadAll();
    }
    
    public int getLoadedCount() {
        return this.loadedCount;
    }
}
