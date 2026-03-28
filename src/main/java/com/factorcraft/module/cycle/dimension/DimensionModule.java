package com.factorcraft.module.cycle.dimension;

import com.factorcraft.module.cycle.dimension.block.DimensionBlocks;
import com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities;
import com.factorcraft.module.cycle.dimension.nether.BlazingFactor;
import com.factorcraft.module.cycle.dimension.end.VoidFactor;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 维度模块 - 服务端初始化
 * 实现下界和末地的 Factor 系统
 */
public class DimensionModule implements ModInitializer {
    public static final String MOD_ID = "factorcraft_dimension";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing FactorCraft Dimension Module");
        
        // 注册方块
        DimensionBlocks.register();
        LOGGER.info("Dimension blocks registered");
        
        // 注册 BlockEntities
        DimensionBlockEntities.register();
        LOGGER.info("Dimension block entities registered");
        
        // 注册 Factor 类型
        registerFactors();
        LOGGER.info("Dimension factors registered");
    }
    
    private void registerFactors() {
        // 注册炽热 Factor (下界)
        // 注册虚空 Factor (末地)
    }
}
