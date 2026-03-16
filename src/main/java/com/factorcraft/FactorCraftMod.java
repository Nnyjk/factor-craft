package com.factorcraft;

import com.factorcraft.command.FactorCraftCommands;
import com.factorcraft.module.factor.management.DiffusionSystem;
import com.factorcraft.module.quest.QuestCommands;
import com.factorcraft.config.ConfigManager;
import com.factorcraft.datapack.DataPackManager;
import com.factorcraft.module.ModuleBootstrap;
import com.factorcraft.module.network.NetworkPackets;
import com.factorcraft.registry.ModInitialization;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 主 Mod 类
 */
public class FactorCraftMod implements ModInitializer {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // 扩散间隔 (tick)
    private static final int DIFFUSION_INTERVAL = 100;
    
    @Override
    public void onInitialize() {
        LOGGER.info("[FactorCraft] Initializing Factor Craft Mod...");
        
        // 初始化配置系统
        ConfigManager.initialize();
        LOGGER.info("[FactorCraft] 配置系统初始化完成");
        
        // 使用 ModuleBootstrap 初始化所有模块
        ModuleBootstrap.initializeDefaults();
        
        // 注册游戏内容
        ModInitialization.initialize();
        
        // 注册网络包
        NetworkPackets.register();
        LOGGER.info("[FactorCraft] 网络包注册完成");
        
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FactorCraftCommands.register(dispatcher);
            QuestCommands.register(dispatcher, registryAccess);
        });
        LOGGER.info("[FactorCraft] 命令系统注册完成");
        
        // 初始化数据包系统
        DataPackManager.initialize();
        LOGGER.info("[FactorCraft] 数据包系统初始化完成");
        
        // 接入 Factor 扩散系统到世界 tick 循环
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        LOGGER.info("[FactorCraft] Factor 扩散系统已接入世界 tick 循环 (间隔：{} ticks)", DIFFUSION_INTERVAL);
        
        LOGGER.info("[FactorCraft] Factor Craft Mod initialized successfully!");
    }
    
    /**
     * 世界 tick 事件处理
     */
    private void onWorldTick(ServerWorld world) {
        // 每 DIFFUSION_INTERVAL tick 执行一次扩散计算
        if (world.getTime() % DIFFUSION_INTERVAL == 0) {
            DiffusionSystem.processAllDiffusion(world);
        }
    }
}
