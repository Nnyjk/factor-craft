package com.factorcraft;

import com.factorcraft.client.KeyBindings;
import com.factorcraft.dynamic.DynamicBundle;
import com.factorcraft.dynamic.DynamicContentManager;
import com.factorcraft.module.network.ClientNetworkHandler;
import com.factorcraft.network.ConfigSyncHandler;
import com.factorcraft.module.technology.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 客户端初始化
 */
public class FactorCraftClient implements ClientModInitializer {
    
    public static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft:Client");
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("[FactorCraft:Client] 客户端初始化开始...");
        
        // 注册按键绑定
        KeyBindings.register();
        LOGGER.info("[FactorCraft:Client] 按键绑定注册完成");
        
        // 注册配置同步接收器
        ConfigSyncHandler.registerClientReceiver();
        
        // 注册客户端网络处理器
        ClientNetworkHandler.register();
        LOGGER.info("[FactorCraft:Client] 网络处理器注册完成");
        
        // 注册 Screen 客户端渲染
        ModScreens.initClient();
        
        // 注册培育系统客户端
        com.factorcraft.module.cultivation.ModCultivation.initClient();
        
        LOGGER.info("[FactorCraft:Client] Screen 客户端注册完成");
        
        // 动态内容信息
        DynamicBundle bundle = DynamicContentManager.getInstance().current();
        LOGGER.info("[FactorCraft:Client] 动态资源：textures={}, models={}, languages={}, commands={}",
                bundle.textures().size(),
                bundle.models().size(),
                bundle.languages().size(),
                bundle.commands().size());
        
        LOGGER.info("[FactorCraft:Client] 客户端初始化完成");
    }
}
