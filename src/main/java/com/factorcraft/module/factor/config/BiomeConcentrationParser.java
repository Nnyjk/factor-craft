package com.factorcraft.module.factor.config;

import com.factorcraft.module.factor.model.BiomeConcentration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 群系浓度配置解析器
 */
public class BiomeConcentrationParser {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 解析 biome_concentrations.json 文件
     * 
     * @param configPath 配置文件路径
     * @return 维度 ID -> 群系浓度配置的映射
     * @throws IOException 文件读取错误
     */
    public Map<String, BiomeConcentration> parse(Path configPath) throws IOException {
        Map<String, BiomeConcentration> concentrations = new HashMap<>();
        
        try (FileReader reader = new FileReader(configPath.toFile())) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            JsonObject dimensions = root.getAsJsonObject("dimensions");
            
            for (String dimensionId : dimensions.keySet()) {
                JsonObject dimObj = dimensions.getAsJsonObject(dimensionId);
                BiomeConcentration config = GSON.fromJson(dimObj, BiomeConcentration.class);
                concentrations.put(dimensionId, config);
            }
        }
        
        return concentrations;
    }
}