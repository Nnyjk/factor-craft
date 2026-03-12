package com.factorcraft.module.cycle;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

import java.util.List;

/**
 * CycleModule 适配器 - 实现 FactorCraftModule 接口
 * 
 * 包装 CycleModule 以集成到模块系统
 */
public class CycleModuleAdapter implements FactorCraftModule {
    
    private final CycleModule cycleModule;
    
    public CycleModuleAdapter() {
        this.cycleModule = CycleModule.getInstance();
    }
    
    @Override
    public String moduleId() {
        return "cycle";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of(); // 无依赖
    }
    
    @Override
    public void initialize() {
        cycleModule.initialize();
    }
    
    @Override
    public void reload() {
        // 从配置重载周期参数
        // 待完善：实现配置文件加载
        System.out.println("[CycleModule] 配置已重新加载");
    }
    
    @Override
    public void shutdown() {
        // 清理资源
    }
    
    /**
     * 获取内部 CycleModule 实例
     */
    public CycleModule getCycleModule() {
        return cycleModule;
    }
}