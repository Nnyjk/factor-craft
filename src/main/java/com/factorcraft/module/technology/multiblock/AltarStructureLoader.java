package com.factorcraft.module.technology.multiblock;

import com.factorcraft.module.technology.TechnologyModule;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 祭坛结构配置加载器
 * 
 * 从 data/factorcraft/altar_structures/ 加载 JSON 配置
 */
public class AltarStructureLoader {
    
    private static final Map<Identifier, AltarStructure> STRUCTURES = new HashMap<>();
    
    public static void load() {
        TechnologyModule.LOGGER.info("[AltarStructureLoader] 加载祭坛结构配置...");
        
        // TODO: 从 JSON 文件加载
        // 示例：注册 T1 提取器祭坛
        registerDefaultStructures();
        
        TechnologyModule.LOGGER.info("[AltarStructureLoader] 加载完成，共 {} 个结构", STRUCTURES.size());
    }
    
    private static void registerDefaultStructures() {
        // T1 提取器祭坛
        AltarStructure extractorT1 = new AltarStructure(
            Identifier.of("factorcraft:extractor_t1"),
            "extractor",
            1
        );
        STRUCTURES.put(extractorT1.getId(), extractorT1);
        
        // T2 提取器祭坛
        AltarStructure extractorT2 = new AltarStructure(
            Identifier.of("factorcraft:extractor_t2"),
            "extractor",
            2
        );
        STRUCTURES.put(extractorT2.getId(), extractorT2);
        
        // T3-T5 类似...
    }
    
    public static Map<Identifier, AltarStructure> getStructures() {
        return STRUCTURES;
    }
}
