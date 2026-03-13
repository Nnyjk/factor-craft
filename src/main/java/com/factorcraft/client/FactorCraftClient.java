package com.factorcraft.client;

import com.factorcraft.network.ClientNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

public class FactorCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册按键绑定
        KeyBindings.register();
        
        // 注册客户端网络处理器
        ClientNetworkHandler.register();
    }
}