package com.factorcraft.module.quest;

public class QuestModule {
    private static QuestModule instance;
    private QuestModule() {}
    public static QuestModule getInstance() {
        if (instance == null) instance = new QuestModule();
        return instance;
    }
    public void initialize() {
        System.out.println("[QuestModule] 任务系统已初始化 (占位)");
    }
}
