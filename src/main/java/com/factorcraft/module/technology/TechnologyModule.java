package com.factorcraft.module.technology;

public class TechnologyModule {
    private static TechnologyModule instance;
    private TechnologyModule() {}
    public static TechnologyModule getInstance() {
        if (instance == null) instance = new TechnologyModule();
        return instance;
    }
    public void initialize() {
        System.out.println("[TechnologyModule] 科技系统已初始化");
    }
}
