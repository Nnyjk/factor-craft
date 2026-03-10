package com.factorcraft.module.ui;

public class UIModule {
    private static UIModule instance;
    private UIModule() {}
    public static UIModule getInstance() {
        if (instance == null) instance = new UIModule();
        return instance;
    }
    public void initialize() {
        System.out.println("[UIModule] UI 系统已初始化 (占位)");
    }
}
