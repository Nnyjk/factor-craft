package com.factorcraft.module.quest.template;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.quest.condition.*;
import com.factorcraft.module.quest.reward.*;
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
 * 
 * 支持从 data/factorcraft/quests/ 目录加载 JSON 格式的任务定义
 * 支持 datapack 覆盖和热重载
 */
public class QuestTemplateLoader {
    
    private final Gson gson = new Gson();
    private int loadedCount;
    private QuestManagerRef managerRef;
    
    /**
     * QuestManager 引用接口（避免循环依赖）
     */
    public interface QuestManagerRef {
        void registerTemplate(QuestTemplate template);
    }
    
    public QuestTemplateLoader() {
        this.loadedCount = 0;
    }
    
    /**
     * 设置 QuestManager 引用
     */
    public void setManagerRef(QuestManagerRef ref) {
        this.managerRef = ref;
    }
    
    /**
     * 加载所有任务模板
     * 从 data/factorcraft/quests/*.json 加载
     */
    public void loadAll() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 开始加载任务模板...");
        
        this.loadedCount = 0;
        
        // 占位实现：加载预设任务模板
        // 实际加载通过 loadFromResources(ResourceManager) 进行
        loadDefaultTemplates();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 加载完成，共 {} 个模板", this.loadedCount);
    }
    
    /**
     * 加载默认任务模板
     */
    private void loadDefaultTemplates() {
        // 默认模板计数（实际从 JSON 加载）
        this.loadedCount = 0;
    }
    
    /**
     * 从资源管理器加载任务（供游戏运行时使用）
     */
    public void loadFromResources(ResourceManager resourceManager) {
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 从资源加载任务模板...");
        
        this.loadedCount = 0;
        
        try {
            // 查找所有任务文件
            Map<Identifier, Resource> questFiles = resourceManager.findResources(
                "quests",
                path -> path.getPath().endsWith(".json")
            );
            
            FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 发现 {} 个任务文件", questFiles.size());
            
            for (Map.Entry<Identifier, Resource> entry : questFiles.entrySet()) {
                Identifier fileId = entry.getKey();
                Resource resource = entry.getValue();
                
                try {
                    InputStreamReader reader = new InputStreamReader(resource.getInputStream());
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    reader.close();
                    
                    QuestTemplate template = parseTemplate(json, fileId);
                    if (template != null) {
                        registerTemplate(template);
                        this.loadedCount++;
                    }
                } catch (Exception e) {
                    FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 解析任务文件失败: {} - {}", 
                        fileId, e.getMessage());
                }
            }
            
            FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 从资源加载完成，共 {} 个模板", this.loadedCount);
            
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Quest] 加载任务资源失败", e);
        }
    }
    
    /**
     * 解析 JSON 为任务模板
     */
    private QuestTemplate parseTemplate(JsonObject json, Identifier fileId) {
        try {
            // 解析 ID
            String idStr = json.has("id") ? json.get("id").getAsString() : 
                extractIdFromPath(fileId.getPath());
            Identifier id = Identifier.of("factorcraft", idStr);
            
            // 解析基础字段
            String title = json.has("title") ? json.get("title").getAsString() : "未命名任务";
            String description = json.has("description") ? json.get("description").getAsString() : "";
            String category = json.has("category") ? json.get("category").getAsString() : "general";
            boolean repeatable = json.has("repeatable") && json.get("repeatable").getAsBoolean();
            int priority = json.has("priority") ? json.get("priority").getAsInt() : 0;
            
            // 解析图标
            Identifier icon = null;
            if (json.has("icon")) {
                icon = Identifier.tryParse(json.get("icon").getAsString());
            }
            
            // 解析 objectives → conditions
            List<QuestCondition> conditions = new ArrayList<>();
            if (json.has("objectives")) {
                JsonArray objectives = json.getAsJsonArray("objectives");
                for (JsonElement elem : objectives) {
                    QuestCondition condition = parseCondition(elem.getAsJsonObject());
                    if (condition != null) {
                        conditions.add(condition);
                    }
                }
            }
            
            // 解析 rewards
            List<QuestReward> rewards = new ArrayList<>();
            if (json.has("rewards")) {
                JsonArray rewardsArray = json.getAsJsonArray("rewards");
                for (JsonElement elem : rewardsArray) {
                    QuestReward reward = parseReward(elem.getAsJsonObject());
                    if (reward != null) {
                        rewards.add(reward);
                    }
                }
            }
            
            // 解析前置任务
            List<Identifier> prerequisites = new ArrayList<>();
            if (json.has("prerequisites")) {
                JsonArray prereqArray = json.getAsJsonArray("prerequisites");
                for (JsonElement elem : prereqArray) {
                    String prereqId = elem.getAsString();
                    prerequisites.add(Identifier.of("factorcraft", prereqId));
                }
            }
            
            // 解析后续任务
            List<Identifier> nextQuests = new ArrayList<>();
            if (json.has("next_quests")) {
                JsonArray nextArray = json.getAsJsonArray("next_quests");
                for (JsonElement elem : nextArray) {
                    String nextId = elem.getAsString();
                    nextQuests.add(Identifier.tryParse(nextId));
                }
            }
            
            // 解析成就关联
            List<Identifier> advancementIds = new ArrayList<>();
            if (json.has("advancements")) {
                JsonArray advArray = json.getAsJsonArray("advancements");
                for (JsonElement elem : advArray) {
                    advancementIds.add(Identifier.tryParse(elem.getAsString()));
                }
            }
            
            return new QuestTemplate(
                id, category, repeatable, priority, title, description,
                icon, conditions, rewards, nextQuests, advancementIds
            );
            
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 解析任务模板失败: {} - {}", 
                fileId, e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析条件
     */
    private QuestCondition parseCondition(JsonObject json) {
        if (!json.has("type")) {
            return null;
        }
        
        String type = json.get("type").getAsString();
        
        try {
            return switch (type) {
                case "craft_item" -> {
                    Identifier itemId = Identifier.tryParse(json.get("item").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new ItemCraftCondition(itemId, count);
                }
                case "pickup_item", "item_pickup" -> {
                    Identifier itemId = Identifier.tryParse(json.get("item").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new ItemPickupCondition(itemId, count);
                }
                case "place_block", "block_place" -> {
                    Identifier blockId = Identifier.tryParse(json.get("block").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new BlockPlaceCondition(blockId, count);
                }
                case "kill_entity", "entity_kill" -> {
                    Identifier entityId = Identifier.tryParse(json.get("entity").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new EntityKillCondition(entityId, count);
                }
                case "submit_item", "item_submit" -> {
                    Identifier itemId = Identifier.tryParse(json.get("item").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new ItemSubmitCondition(itemId, count);
                }
                case "use_item", "item_use" -> {
                    Identifier itemId = Identifier.tryParse(json.get("item").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new ItemUseCondition(itemId, count);
                }
                case "dimension_travel" -> {
                    String dimension = json.has("dimension") ? json.get("dimension").getAsString() : "minecraft:the_nether";
                    yield new DimensionTravelCondition(Identifier.tryParse(dimension));
                }
                case "factor_absorb", "absorb_factor", "extract_factor" -> {
                    double amount = json.has("amount") ? json.get("amount").getAsDouble() : 100.0;
                    String factorType = json.has("factor_type") ? json.get("factor_type").getAsString() : "any";
                    yield new FactorAbsorbCondition(factorType, amount);
                }
                default -> {
                    FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 未知条件类型: {}", type);
                    yield null;
                }
            };
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 解析条件失败: {} - {}", type, e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析奖励
     */
    private QuestReward parseReward(JsonObject json) {
        if (!json.has("type")) {
            return null;
        }
        
        String type = json.get("type").getAsString();
        
        try {
            return switch (type) {
                case "item" -> {
                    Identifier itemId = Identifier.tryParse(json.get("item").getAsString());
                    int count = json.has("count") ? json.get("count").getAsInt() : 1;
                    yield new ItemReward(itemId, count);
                }
                case "exp", "experience", "quest_xp" -> {
                    int amount = json.has("amount") ? json.get("amount").getAsInt() : 100;
                    yield new ExperienceReward(amount);
                }
                case "factor" -> {
                    double amount = json.has("amount") ? json.get("amount").getAsDouble() : 100.0;
                    yield new FactorReward(amount);
                }
                case "tech", "technology" -> {
                    String techIdStr = json.has("tech_id") ? json.get("tech_id").getAsString() : 
                        (json.has("id") ? json.get("id").getAsString() : "unknown");
                    yield new TechnologyReward(Identifier.tryParse(techIdStr));
                }
                case "achievement" -> {
                    Identifier advId = Identifier.tryParse(json.get("id").getAsString());
                    yield new AchievementReward(advId);
                }
                default -> {
                    FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 未知奖励类型: {}", type);
                    yield null;
                }
            };
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 解析奖励失败: {} - {}", type, e.getMessage());
            return null;
        }
    }
    
    /**
     * 从路径提取 ID
     */
    private String extractIdFromPath(String path) {
        // quests/tutorial/01_welcome.json -> tutorial_01
        String normalized = path.replace("quests/", "").replace(".json", "");
        return normalized.replace("/", "_");
    }
    
    /**
     * 注册任务模板
     */
    private void registerTemplate(QuestTemplate template) {
        if (managerRef != null) {
            managerRef.registerTemplate(template);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 注册任务: {}", template.getId());
        } else {
            FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 注册任务: {} (ManagerRef 未设置)", template.getId());
        }
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