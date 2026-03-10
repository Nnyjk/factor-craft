package com.factorcraft.module.technology.multiblock;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

/**
 * 多方块结构蓝图
 * 
 * 表示从 JSON 加载的结构定义
 */
public class Blueprint {
    
    private final Identifier id;
    private final String name;
    private final String description;
    private final int tier;
    private final String type; // "extractor", "emitter", "utilizer"
    private final int[] size; // [width, height, depth]
    private final String[][] structure; // 每层的结构字符串
    private final Map<String, String> legend; // 字符 -> 方块 ID
    private final Map<String, Object> properties;
    
    public Blueprint(Identifier id, String name, String description, int tier, String type,
                     int[] size, String[][] structure, Map<String, String> legend,
                     Map<String, Object> properties) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.type = type;
        this.size = size;
        this.structure = structure;
        this.legend = legend;
        this.properties = properties;
    }
    
    // Getters
    public Identifier getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getTier() { return tier; }
    public String getType() { return type; }
    public int[] getSize() { return size; }
    public String[][] getStructure() { return structure; }
    public Map<String, String> getLegend() { return legend; }
    public Map<String, Object> getProperties() { return properties; }
    
    /**
     * 获取属性值
     */
    public double getProperty(String key, double defaultValue) {
        Object value = properties.get(key);
        return value != null ? ((Number) value).doubleValue() : defaultValue;
    }
    
    /**
     * 解析方块位置
     * 返回相对位置和对应方块 ID 的映射
     */
    public Map<BlockPos, String> parseBlockPositions() {
        Map<BlockPos, String> positions = new java.util.HashMap<>();
        
        int width = size[0];
        int height = size[1];
        int depth = size[2];
        
        // 遍历每层
        for (int y = 0; y < structure.length && y < height; y++) {
            String[] rows = structure[y];
            
            // 遍历每行
            for (int z = 0; z < rows.length && z < depth; z++) {
                String row = rows[z];
                
                // 遍历每个字符
                for (int x = 0; x < row.length() && x < width; x++) {
                    char symbol = row.charAt(x);
                    String symbolStr = String.valueOf(symbol);
                    
                    // 跳过空气
                    if (symbol == ' ' || symbol == '_') continue;
                    
                    // 查找对应的方块 ID
                    String blockId = legend.get(symbolStr);
                    if (blockId != null && !blockId.equals("minecraft:air")) {
                        // 相对于核心位置（中心点）
                        int relX = x - width / 2;
                        int relY = y;
                        int relZ = z - depth / 2;
                        
                        positions.put(new BlockPos(relX, relY, relZ), blockId);
                    }
                }
            }
        }
        
        return positions;
    }
    
    @Override
    public String toString() {
        return String.format("Blueprint[id=%s, type=%s, tier=%d, name=%s]", 
            id, type, tier, name);
    }
}