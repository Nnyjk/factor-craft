package com.factorcraft.module.cycle.automation.endgame;

import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlocks;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 终局自动化模块
 * 包含：
 * - 自动提取器 MK-II
 * - Factor 泵 MK-II
 * - 高级合成器
 * - 量子仓储单元
 */
public class EndgameAutomationModule implements ModInitializer {
    
    public static final String MOD_ID = "factorcraft_endgame_automation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Factor Craft Endgame Automation Module");
        
        // 初始化注册表
        EndgameAutomationBlocks.init();
        EndgameAutomationBlockEntities.init();
        EndgameAutomationScreenHandlers.init();
        
        LOGGER.info("Endgame Automation Module initialized successfully");
    }
}
