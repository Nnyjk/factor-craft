package com.factorcraft.module.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * UIModule - 通用 UI 系统
 */
public class UIModule {
    
    private static UIModule instance;
    
    public UIModule() {
        instance = this;
    }
    
    public void initialize() {
        // 注册通用 UI 组件
        // - 能量条
        // - 进度条
        // - 物品提示
        // - 多方块预览
    }
    
    public static UIModule getInstance() {
        return instance;
    }
}
