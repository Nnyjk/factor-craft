package com.factorcraft.module.network;

public class NetworkModule {
    private static NetworkModule instance;
    private NetworkModule() {}
    public static NetworkModule getInstance() {
        if (instance == null) instance = new NetworkModule();
        return instance;
    }
    public void initialize() {
        FactorNetworkManager.getInstance().initialize();
        System.out.println("[NetworkModule] 网络系统已初始化");
    }
}
