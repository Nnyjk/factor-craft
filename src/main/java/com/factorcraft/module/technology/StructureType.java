package com.factorcraft.module.technology;

/**
 * 多方块结构类型
 * 
 * 四大核心结构类型，每种类型有 T1-T5 五个等级
 */
public enum StructureType {
    
    /**
     * 提取结构 - 从环境中提取 Factor
     */
    EXTRACTOR("extractor", "提取结构"),
    
    /**
     * 消耗结构 - 消耗物品获得 Factor
     */
    CONSUMER("consumer", "消耗结构"),
    
    /**
     * 合成结构 - 用 Factor 合成物品（材料升级）
     */
    SYNTHESIZER("synthesizer", "合成结构"),
    
    /**
     * 培育结构 - 给物品注入特性
     */
    BREEDER("breeder", "培育结构");
    
    private final String id;
    private final String displayName;
    
    StructureType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据蓝图 ID 判断结构类型
     */
    public static StructureType fromPatternId(String patternId) {
        if (patternId == null) return null;
        
        String lower = patternId.toLowerCase();
        
        // 新命名规范 - 优先检查前缀
        if (lower.startsWith("extractor_")) {
            return EXTRACTOR;
        }
        if (lower.startsWith("consumer_")) {
            return CONSUMER;
        }
        if (lower.startsWith("synthesizer_")) {
            return SYNTHESIZER;
        }
        if (lower.startsWith("breeder_")) {
            return BREEDER;
        }
        
        // 名称包含检查
        if (lower.contains("collector") || lower.contains("star_array") || 
            lower.contains("siphon") || lower.contains("resonator") || 
            lower.contains("vortex")) {
            return EXTRACTOR;
        }
        
        if (lower.contains("burner") || lower.contains("devourer") ||
            lower.contains("rift") || lower.contains("eternal_core")) {
            return CONSUMER;
        }
        
        if (lower.contains("ancient_synthesis") || lower.contains("ancient_forge") ||
            lower.contains("fate_foundry") || lower.contains("creation_furnace") ||
            lower.contains("origin_altar")) {
            return SYNTHESIZER;
        }
        
        if (lower.contains("loom") || lower.contains("weaver") || 
            lower.contains("fate_altar") || lower.contains("sanctuary") ||
            lower.contains("reincarnation")) {
            return BREEDER;
        }
        
        // 旧命名兼容
        if (lower.contains("resonance_furnace") || lower.contains("factor_converter")) {
            return EXTRACTOR;
        }
        if (lower.contains("storage")) {
            return CONSUMER; // 储存结构归类为消耗
        }
        if (lower.contains("workbench") || lower.contains("forge") || lower.contains("injector")) {
            return SYNTHESIZER;
        }
        if (lower.contains("altar") || lower.contains("gate")) {
            return BREEDER;
        }
        
        return null;
    }
}