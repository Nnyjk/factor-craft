package com.factorcraft.module.technology;

import com.factorcraft.module.technology.block.ModBlocks;
import com.factorcraft.module.technology.machine.ModMachines;
import com.factorcraft.module.technology.multiblock.AltarStructureLoader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TechnologyModule {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger("TechnologyModule");
    
    private static TechnologyModule instance;
    
    public TechnologyModule() {
        instance = this;
    }
    
    public void initialize() {
        LOGGER.info("[TechnologyModule] 初始化 TechnologyModule...");
        
        // 注册方块
        ModBlocks.register();
        LOGGER.info("[TechnologyModule] 方块注册完成");
        
        // 注册机器
        ModMachines.register();
        LOGGER.info("[TechnologyModule] 机器注册完成");
        
        // 加载祭坛结构配置
        AltarStructureLoader.load();
        LOGGER.info("[TechnologyModule] 祭坛结构加载完成");
        
        LOGGER.info("[TechnologyModule] TechnologyModule 初始化完成");
    }
    
    public static TechnologyModule getInstance() {
        return instance;
    }
}
