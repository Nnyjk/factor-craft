package com.factorcraft.module.technology.multiblock;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 多方块结构蓝图加载器
 * 
 * 从 data/factorcraft/altar_structures/ 加载 JSON 蓝图文件
 * 支持数据包扩展
 */
public class BlueprintLoader {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<Identifier, Blueprint> BLUEPRINTS = new HashMap<>();
    
    /**
     * 加载所有蓝图
     */
    public static void loadAll() {
        FactorCraftMod.LOGGER.info("[BlueprintLoader] 加载多方块结构蓝图...");
        
        // 加载内置蓝图
        loadInternalBlueprints();
        
        // 加载外部数据包蓝图
        loadExternalBlueprints();
        
        FactorCraftMod.LOGGER.info("[BlueprintLoader] 加载完成，共 {} 个蓝图", BLUEPRINTS.size());
    }
    
    /**
     * 加载内置蓝图（从 resources）
     */
    private static void loadInternalBlueprints() {
        String[] defaultBlueprints = {
            "extractor_t1", "extractor_t2",
            "emitter_t1",
            "utilizer_t1"
        };
        
        for (String name : defaultBlueprints) {
            try {
                loadInternalBlueprint(name);
            } catch (Exception e) {
                FactorCraftMod.LOGGER.warn("[BlueprintLoader] 无法加载内置蓝图 {}: {}", name, e.getMessage());
            }
        }
    }
    
    /**
     * 加载单个内置蓝图
     */
    private static void loadInternalBlueprint(String name) throws IOException {
        String path = "data/factorcraft/altar_structures/" + name + ".json";
        InputStream stream = BlueprintLoader.class.getClassLoader().getResourceAsStream(path);
        
        if (stream == null) {
            throw new IOException("Resource not found: " + path);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Blueprint blueprint = parseBlueprint(json);
            BLUEPRINTS.put(blueprint.getId(), blueprint);
            FactorCraftMod.LOGGER.debug("[BlueprintLoader] 加载蓝图: {}", blueprint.getId());
        }
    }
    
    /**
     * 加载外部数据包蓝图（从 config 目录）
     */
    private static void loadExternalBlueprints() {
        Path configDir = Path.of("config/factorcraft/altar_structures");
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
                FactorCraftMod.LOGGER.info("[BlueprintLoader] 创建配置目录: {}", configDir.toAbsolutePath());
            } catch (IOException e) {
                FactorCraftMod.LOGGER.error("[BlueprintLoader] 无法创建配置目录: {}", e.getMessage());
                return;
            }
            return; // 新创建的目录是空的
        }
        
        try (var stream = Files.list(configDir)) {
            stream.filter(p -> p.toString().endsWith(".json"))
                  .forEach(BlueprintLoader::loadExternalBlueprint);
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[BlueprintLoader] 扫描配置目录失败: {}", e.getMessage());
        }
    }
    
    /**
     * 加载单个外部蓝图
     */
    private static void loadExternalBlueprint(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            Blueprint blueprint = parseBlueprint(json);
            BLUEPRINTS.put(blueprint.getId(), blueprint);
            FactorCraftMod.LOGGER.info("[BlueprintLoader] 加载外部蓝图: {} from {}", 
                blueprint.getId(), path.getFileName());
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[BlueprintLoader] 加载外部蓝图失败 {}: {}", 
                path.getFileName(), e.getMessage());
        }
    }
    
    /**
     * 解析蓝图 JSON
     */
    private static Blueprint parseBlueprint(JsonObject json) {
        Identifier id = Identifier.tryParse(json.get("id").getAsString());
        String name = json.get("name").getAsString();
        String description = json.has("description") ? json.get("description").getAsString() : "";
        int tier = json.get("tier").getAsInt();
        String type = json.get("type").getAsString();
        
        // 解析大小
        var sizeArray = json.getAsJsonArray("size");
        int[] size = {
            sizeArray.get(0).getAsInt(),
            sizeArray.get(1).getAsInt(),
            sizeArray.get(2).getAsInt()
        };
        
        // 解析结构层
        var structureArray = json.getAsJsonArray("structure");
        String[][] structure = new String[structureArray.size()][];
        for (int i = 0; i < structureArray.size(); i++) {
            var layerArray = structureArray.get(i).getAsJsonArray();
            structure[i] = new String[layerArray.size()];
            for (int j = 0; j < layerArray.size(); j++) {
                structure[i][j] = layerArray.get(j).getAsString();
            }
        }
        
        // 解析图例
        Map<String, String> legend = new HashMap<>();
        var legendObj = json.getAsJsonObject("legend");
        for (String key : legendObj.keySet()) {
            legend.put(key, legendObj.get(key).getAsString());
        }
        
        // 解析属性
        Map<String, Object> properties = new HashMap<>();
        if (json.has("properties")) {
            var propsObj = json.getAsJsonObject("properties");
            for (String key : propsObj.keySet()) {
                properties.put(key, propsObj.get(key).getAsDouble());
            }
        }
        
        return new Blueprint(id, name, description, tier, type, size, structure, legend, properties);
    }
    
    /**
     * 获取蓝图
     */
    public static Blueprint getBlueprint(Identifier id) {
        return BLUEPRINTS.get(id);
    }
    
    /**
     * 获取所有蓝图
     */
    public static Map<Identifier, Blueprint> getAllBlueprints() {
        return BLUEPRINTS;
    }
    
    /**
     * 按类型获取蓝图
     */
    public static Map<Identifier, Blueprint> getBlueprintsByType(String type) {
        Map<Identifier, Blueprint> result = new HashMap<>();
        for (Map.Entry<Identifier, Blueprint> entry : BLUEPRINTS.entrySet()) {
            if (entry.getValue().getType().equals(type)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 按阶获取蓝图
     */
    public static Map<Identifier, Blueprint> getBlueprintsByTier(int tier) {
        Map<Identifier, Blueprint> result = new HashMap<>();
        for (Map.Entry<Identifier, Blueprint> entry : BLUEPRINTS.entrySet()) {
            if (entry.getValue().getTier() == tier) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}