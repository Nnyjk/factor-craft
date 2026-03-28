package com.factorcraft.module.cycle.automation;

import com.factorcraft.module.cycle.CycleModule;
import com.factorcraft.module.cycle.automation.block.AutomationBlocks;
import com.factorcraft.module.cycle.automation.block.entity.AutomationBlockEntities;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Q3 自动化机器系统模块
 * 
 * 实现完整的自动化生产机器，支持配方编程、批量生产和智能管理。
 * 
 * 功能模块：
 * 1. 自动合成器 - 按配方自动合成物品
 * 2. 自动采集器 - 自动收割作物和采集矿物
 * 3. 自动分配器 - 均匀分配物品到相邻容器
 * 4. 中央控制器 - 监控和管理整个自动化系统
 */
public class AutomationModule {
    
    private static AutomationModule instance;
    private MinecraftServer server;
    
    public AutomationModule() {
        instance = this;
    }
    
    /**
     * 获取模块实例
     */
    public static AutomationModule getInstance() {
        return instance;
    }
    
    /**
     * 静态初始化方法（供 CycleModule 调用）
     */
    public static void init() {
        if (instance == null) {
            instance = new AutomationModule();
        }
        instance.initialize();
    }
    
    /**
     * 初始化自动化模块
     * 注册方块、BlockEntity、物品等
     */
    public void initialize() {
        System.out.println("[AutomationModule] 初始化自动化机器系统...");
        
        // 注册方块
        AutomationBlocks.init();
        System.out.println("[AutomationModule] 方块已注册");
        
        // 注册 BlockEntity
        AutomationBlockEntities.init();
        System.out.println("[AutomationModule] BlockEntity 已注册");
        
        // 注册服务端 tick 事件（用于 BlockEntity tick）
        ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);
        
        System.out.println("[AutomationModule] 自动化机器系统初始化完成");
    }
    
    /**
     * 世界 tick 事件
     * 用于处理 BlockEntity 的逻辑更新
     */
    private void onWorldTick(ServerWorld world) {
        // BlockEntity 的 tick 由世界自动处理
        // 这里可以添加额外的全局逻辑
    }
    
    /**
     * 获取服务器实例
     */
    public MinecraftServer getServer() {
        return server;
    }
    
    /**
     * 设置服务器实例
     */
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
}
