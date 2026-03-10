package com.factorcraft.module.technology.multiblock;

import com.factorcraft.FactorCraftMod;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 祭坛结构加载器
 * 
 * 统一管理所有祭坛结构定义
 * 使用 BlueprintLoader 加载 JSON 蓝图
 */
public class AltarStructureLoader {
    
    private static final Map<Identifier, AltarStructure> STRUCTURES = new HashMap<>();
    
    /**
     * 加载所有祭坛结构
     */
    public static void load() {
        FactorCraftMod.LOGGER.info("[AltarStructureLoader] 加载祭坛结构配置...");
        
        // 从 BlueprintLoader 加载蓝图并转换为 AltarStructure
        BlueprintLoader.loadAll();
        
        for (Map.Entry<Identifier, Blueprint> entry : BlueprintLoader.getAllBlueprints().entrySet()) {
            Blueprint blueprint = entry.getValue();
            AltarStructure structure = convertBlueprint(blueprint);
            STRUCTURES.put(structure.getId(), structure);
        }
        
        FactorCraftMod.LOGGER.info("[AltarStructureLoader] 加载完成，共 {} 个结构", STRUCTURES.size());
    }
    
    /**
     * 将蓝图转换为祭坛结构
     */
    private static AltarStructure convertBlueprint(Blueprint blueprint) {
        return new AltarStructure(
            blueprint.getId(),
            blueprint.getType(),
            blueprint.getTier(),
            blueprint.parseBlockPositions(),
            blueprint.getProperties()
        );
    }
    
    /**
     * 获取所有结构
     */
    public static Map<Identifier, AltarStructure> getStructures() {
        return STRUCTURES;
    }
    
    /**
     * 获取指定结构
     */
    public static AltarStructure getStructure(Identifier id) {
        return STRUCTURES.get(id);
    }
    
    /**
     * 按类型获取结构
     */
    public static Map<Identifier, AltarStructure> getStructuresByType(String type) {
        Map<Identifier, AltarStructure> result = new HashMap<>();
        for (Map.Entry<Identifier, AltarStructure> entry : STRUCTURES.entrySet()) {
            if (entry.getValue().getType().equals(type)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 按阶获取结构
     */
    public static Map<Identifier, AltarStructure> getStructuresByTier(int tier) {
        Map<Identifier, AltarStructure> result = new HashMap<>();
        for (Map.Entry<Identifier, AltarStructure> entry : STRUCTURES.entrySet()) {
            if (entry.getValue().getTier() == tier) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}