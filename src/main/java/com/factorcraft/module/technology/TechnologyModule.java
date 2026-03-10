package com.factorcraft.module.technology;

import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.technology.block.ModBlocks;
import com.factorcraft.module.technology.item.ModItems;
import com.factorcraft.module.technology.machine.ModMachines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TechnologyModule - Factor 核心科技系统
 * 
 * 核心功能:
 * - 3 个核心机器：Extractor, Emitter, Utilizer
 * - 多方块祭坛系统 (配置驱动)
 * - 传输系统：导管、储罐、泵
 * - 特性方块系统
 * - 建筑方块系统
 */
public class TechnologyModule implements FactorCraftModule {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("TechnologyModule");
    
    private static TechnologyModule instance;
    
    public TechnologyModule() {
        instance = this;
    }
    
    @Override
    public String moduleId() {
        return "technology";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of(); // 无依赖
    }
    
    @Override
    public void initialize() {
        LOGGER.info("[TechnologyModule] 初始化 TechnologyModule...");
        
        // 注册方块（静态初始化已完成）
        ModBlocks.register();
        LOGGER.info("[TechnologyModule] 方块注册完成");
        
        // 注册物品（静态初始化已完成）
        ModItems.register();
        LOGGER.info("[TechnologyModule] 物品注册完成");
        
        // 注册 BlockEntity
        ModMachines.register();
        LOGGER.info("[TechnologyModule] BlockEntity 注册完成");
        
        LOGGER.info("[TechnologyModule] TechnologyModule 初始化完成");
    }
    
    public static TechnologyModule getInstance() {
        return instance;
    }
}