package com.factorcraft.module.material.model;

/**
 * 特性分类（扩展为 5 个类别）
 * 
 * 旧类别映射（过渡期保留）：
 * - GENERAL → EXTRACTION（通用特性归入提取类）
 * - DIMENSION → ENVIRONMENT（维度特性归入环境类）
 * - ENDGAME → PRODUCTION（终局特性归入生产类）
 */
public enum TraitCategory {
    // 新类别（主要使用）
    EXTRACTION("extraction", "提取类"),
    TRANSFER("transfer", "传输类"),
    PRODUCTION("production", "生产类"),
    ENVIRONMENT("environment", "环境类"),
    NEGATIVE("negative", "负面类"),
    
    // 旧类别（过渡期保留，映射到新类别）
    @Deprecated GENERAL("extraction", "提取类（旧）"),
    @Deprecated DIMENSION("environment", "环境类（旧）"),
    @Deprecated ENDGAME("production", "生产类（旧）");
    
    private final String id;
    private final String displayName;
    
    TraitCategory(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}