package com.factorcraft.module.logistics;

import com.factorcraft.module.logistics.pipe.LogisticsPipes;
import com.factorcraft.module.logistics.storage.LogisticsStorage;
import com.factorcraft.module.logistics.network.LogisticsNetwork;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor 物流系统模块
 * 
 * 功能：
 * - 智能管道系统（路由、优先级、过滤）
 * - 仓储系统（大型存储、监控）
 * - 物流网络（频道分离、自动请求）
 */
public class LogisticsModule implements ModInitializer {
    
    public static final String MODULE_ID = "logistics";
    public static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft | Logistics");
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing FactorCraft Logistics Module");
        
        // 注册方块
        LogisticsPipes.register();
        LogisticsStorage.register();
        
        // 初始化物流网络
        LogisticsNetwork.initialize();
        
        LOGGER.info("FactorCraft Logistics Module initialized");
    }
}
