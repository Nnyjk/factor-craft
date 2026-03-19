package com.factorcraft;

import com.factorcraft.command.FactorCraftCommands;
import com.factorcraft.module.quest.QuestCommands;
import com.factorcraft.module.research.ResearchCommands;
import com.factorcraft.update.UpdateCommands;
import com.factorcraft.module.social.manager.PermissionCommands;
import com.factorcraft.config.ConfigManager;
import com.factorcraft.datapack.DataPackManager;
import com.factorcraft.module.ModuleBootstrap;
import com.factorcraft.module.network.NetworkPackets;
import com.factorcraft.module.vfx.particle.FactorParticleTypes;
import com.factorcraft.network.ConfigSyncHandler;
import com.factorcraft.registry.ModInitialization;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 主 Mod 类
 */
public class FactorCraftMod implements ModInitializer {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("[FactorCraft] Initializing Factor Craft Mod...");
        
        // 初始化配置系统
        ConfigManager.initialize();
        LOGGER.info("[FactorCraft] 配置系统初始化完成");
        
        // 注册配置同步网络包
        ConfigSyncHandler.register();
        
        // 注册 Data Components
        com.factorcraft.component.FactorCraftDataComponents.register();
        LOGGER.info("[FactorCraft] Data Components 注册完成");
        
        // 使用 ModuleBootstrap 初始化所有模块
        ModuleBootstrap.initializeDefaults();
        
        // 注册游戏内容
        ModInitialization.initialize();
        
        // 注册培育系统
        com.factorcraft.module.cultivation.ModCultivation.register();
        
        // 粒子类型注册已移至 VfxModule 中，避免重复注册
        
        // 注册网络包
        NetworkPackets.register();
        LOGGER.info("[FactorCraft] 网络包注册完成");
        
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FactorCraftCommands.register(dispatcher);
            QuestCommands.register(dispatcher, registryAccess);
            ResearchCommands.register(dispatcher);
            UpdateCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });
        LOGGER.info("[FactorCraft] 命令系统注册完成");
        
        // 初始化数据包系统
        DataPackManager.initialize();
        LOGGER.info("[FactorCraft] 数据包系统初始化完成");
        
        // 注册玩家加入事件（用于配置同步）
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("[FactorCraft] 服务器已启动，配置同步已就绪");
        });
        
        LOGGER.info("[FactorCraft] Factor Craft Mod initialized successfully!");
    }
}