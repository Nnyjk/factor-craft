package com.factorcraft.api;

import java.util.Collection;

/**
 * 科技系统公共 API
 * 
 * 提供给第三方 Mod 注册科技结构和材料
 * 
 * @since 0.1.0
 */
public interface TechnologyApi {
    
    /**
     * 注册多方块结构
     * 
     * @param spec 结构规格
     */
    void registerStructure(StructureSpec spec);
    
    /**
     * 注册材料
     * 
     * @param spec 材料规格
     */
    void registerMaterial(MaterialSpec spec);
    
    /**
     * 获取所有已注册的结构
     * 
     * @return 结构集合
     */
    Collection<StructureSpec> getStructures();
    
    /**
     * 获取所有已注册的材料
     * 
     * @return 材料集合
     */
    Collection<MaterialSpec> getMaterials();
    
    /**
     * 获取指定等级的材料
     * 
     * @param tier 科技等级 (1-5)
     * @return 材料集合
     */
    Collection<MaterialSpec> getMaterialsByTier(int tier);
    
    /**
     * 多方块结构规格
     */
    record StructureSpec(
        String id,
        String name,
        int tier,
        String pattern,
        java.util.Map<String, Object> properties
    ) {}
    
    /**
     * 材料规格
     */
    record MaterialSpec(
        String id,
        String name,
        int tier,
        String type,
        java.util.Map<String, Object> properties
    ) {}
}
