package com.factorcraft.module.error;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

/**
 * 错误处理模块
 * 
 * 提供统一的错误处理、日志记录和玩家反馈机制
 */
public class ErrorModule implements FactorCraftModule {
    
    private static ErrorModule instance;
    
    public ErrorModule() {
        instance = this;
    }
    
    public static ErrorModule getInstance() {
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "error";
    }
    
    @Override
    public void initialize() {
        // 初始化调试配置
        DebugConfig.load();
        
        // 注册全局异常处理器
        registerGlobalExceptionHandler();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Error] 错误处理模块已加载");
    }
    
    private void registerGlobalExceptionHandler() {
        // 设置未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            ErrorHandler.handleCriticalError("Uncaught exception in thread: " + thread.getName(), throwable);
        });
    }
}