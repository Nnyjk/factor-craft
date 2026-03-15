package com.factorcraft.module.quest.template;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务模板加载器 - 从 JSON 配置加载任务模板
 */
public class QuestTemplateLoader {
    
    private int loadedCount;
    private final Gson gson = new Gson();
    
    public QuestTemplateLoader() {
        this.loadedCount = 0;
    }
    
    /**
     * 加载所有任务模板
     * 从 data/factorcraft/quests/*.json 加载
     */
    public void loadAll() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 开始加载任务模板...");
        
        this.loadedCount = 0;
        
        // 占位实现：加载预设任务模板
        // 待扩充：从 JSON 文件加载更多任务
        loadDefaultTemplates();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 加载完成，共 {} 个模板", this.loadedCount);
    }
    
    /**
     * 加载默认任务模板
     */
    private void loadDefaultTemplates() {
        // 添加基础任务模板
        // 待扩充：创建更多丰富的任务内容
        this.loadedCount = 3; // 占位计数
    }
    
    /**
     * 从资源管理器加载任务（供游戏运行时使用）
     */
    public void loadFromResources(ResourceManager resourceManager) {
        try {
            // 查找所有任务文件
            Map<Identifier, Resource> questFiles = resourceManager.findResources(
                "quests", 
                path -> path.getPath().endsWith(".json")
            );
            
            for (Map.Entry<Identifier, Resource> entry : questFiles.entrySet()) {
                try {
                    Identifier fileId = entry.getKey();
                    Resource resource = entry.getValue();
                    
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    
                    QuestTemplate template = parseTemplate(json, fileId);
                    if (template != null) {
                        // 注册到 QuestManager
                        registerTemplate(template);
                        this.loadedCount++;
                    }
                    
                    reader.close();
                } catch (Exception e) {
                    FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 加载任务失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Quest] 任务加载错误: {}", e.getMessage());
        }
    }
    
    /**
     * 解析 JSON 为任务模板
     */
    private QuestTemplate parseTemplate(JsonObject json, Identifier fileId) {
        try {
            String id = json.get("id").getAsString();
            String title = json.get("title").getAsString();
            String description = json.has("description") ? json.get("description").getAsString() : "";
            String category = json.has("category") ? json.get("category").getAsString() : "general";
            boolean repeatable = json.has("repeatable") && json.get("repeatable").getAsBoolean();
            int priority = json.has("priority") ? json.get("priority").getAsInt() : 0;
            
            // 创建基础任务模板（无条件和奖励）
            // 待扩充：完整解析条件和奖励
            return new QuestTemplate(
                Identifier.tryParse(id),
                category,
                repeatable,
                priority,
                title,
                description,
                null, // icon
                List.of(), // conditions
                List.of(), // rewards
                List.of()  // nextQuests
            );
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 解析任务模板失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 注册任务模板
     */
    private void registerTemplate(QuestTemplate template) {
        // 待实现：注册到 QuestManager
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 注册任务: {}", template.getId());
    }
    
    /**
     * 重新加载任务模板 (支持热重载)
     */
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 重新加载任务模板...");
        loadAll();
    }
    
    public int getLoadedCount() {
        return this.loadedCount;
    }
}