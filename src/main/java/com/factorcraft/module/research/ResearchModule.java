package com.factorcraft.module.research;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

/**
 * ResearchModule - 研究系统模块
 * 
 * 实现 Factor 科技树系统
 */
public class ResearchModule implements FactorCraftModule {
    
    public static final String MODULE_ID = "research";
    
    private static ResearchModule instance;
    private final ResearchManager researchManager;
    private MinecraftServer server;
    
    public ResearchModule() {
        this.researchManager = new ResearchManager();
        instance = this;
    }
    
    public static ResearchModule getInstance() {
        return instance;
    }
    
    public ResearchManager getResearchManager() {
        return researchManager;
    }
    
    @Override
    public String moduleId() {
        return MODULE_ID;
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Research] 正在初始化研究系统...");
        
        // 加载配置
        loadResearchConfig();
        
        // 注册默认研究
        registerDefaultResearch();
        
        // 注册事件监听
        registerEventListeners();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Research] 研究系统已初始化，已注册 {} 个研究", 
            researchManager.getAllResearch().size());
    }
    
    /**
     * 从配置文件加载研究定义
     */
    private void loadResearchConfig() {
        // 配置文件路径: config/factorcraft/research.json
        Path configPath = Path.of("config", "factorcraft", "research.json");
        researchManager.loadFromConfig(configPath);
    }
    
    /**
     * 注册默认研究
     */
    private void registerDefaultResearch() {
        // ==================== 技术研究 ====================
        
        // 基础提取技术
        registerResearch(new Research.Builder()
            .id("basic_extraction")
            .name("基础提取技术")
            .description("学习如何从自然界提取 Factor")
            .type(Research.Type.TECHNOLOGY)
            .researchTime(1200)  // 1分钟
            .addEffect("unlock_extractor", true)
            .treeX(0).treeY(0)
            .category("technology")
            .build());
        
        // Factor 存储技术
        registerResearch(new Research.Builder()
            .id("factor_storage")
            .name("Factor 存储技术")
            .description("解锁 Factor 存储容器")
            .type(Research.Type.TECHNOLOGY)
            .researchTime(2400)  // 2分钟
            .addPrerequisite("basic_extraction")
            .addItemRequirement(Items.IRON_INGOT, 8)
            .addEffect("unlock_tank", true)
            .treeX(1).treeY(0)
            .category("technology")
            .build());
        
        // 基础合成技术
        registerResearch(new Research.Builder()
            .id("basic_synthesis")
            .name("基础合成技术")
            .description("学习如何合成新 Factor")
            .type(Research.Type.TECHNOLOGY)
            .researchTime(2400)
            .addPrerequisite("basic_extraction")
            .addFactorCost("basic", 100)
            .addEffect("unlock_synthesizer", true)
            .treeX(2).treeY(0)
            .category("technology")
            .build());
        
        // Factor 网络技术
        registerResearch(new Research.Builder()
            .id("factor_network")
            .name("Factor 网络技术")
            .description("解锁 Factor 远程传输")
            .type(Research.Type.TECHNOLOGY)
            .researchTime(3600)  // 3分钟
            .addPrerequisite("factor_storage")
            .addItemRequirement(Items.REDSTONE, 16)
            .addEffect("unlock_transmitter", true)
            .treeX(1).treeY(1)
            .category("technology")
            .build());
        
        // 高级合成
        registerResearch(new Research.Builder()
            .id("advanced_synthesis")
            .name("高级合成技术")
            .description("解锁高级 Factor 合成配方")
            .type(Research.Type.TECHNOLOGY)
            .researchTime(6000)  // 5分钟
            .addPrerequisite("basic_synthesis")
            .addFactorCost("basic", 500)
            .addFactorCost("rare", 50)
            .addEffect("unlock_advanced_recipes", true)
            .treeX(3).treeY(0)
            .category("technology")
            .build());
        
        // ==================== 效率研究 ====================
        
        // 提取效率 I
        registerResearch(new Research.Builder()
            .id("extraction_efficiency_1")
            .name("提取效率 I")
            .description("提取器效率提升 25%")
            .type(Research.Type.EFFICIENCY)
            .researchTime(1800)
            .addPrerequisite("basic_extraction")
            .addFactorCost("basic", 200)
            .addEffect("extractor_efficiency", 1.25f)
            .treeX(0).treeY(2)
            .category("efficiency")
            .build());
        
        // 提取效率 II
        registerResearch(new Research.Builder()
            .id("extraction_efficiency_2")
            .name("提取效率 II")
            .description("提取器效率提升 50%")
            .type(Research.Type.EFFICIENCY)
            .researchTime(3600)
            .addPrerequisite("extraction_efficiency_1")
            .addFactorCost("basic", 500)
            .addFactorCost("rare", 100)
            .addEffect("extractor_efficiency", 1.5f)
            .treeX(0).treeY(3)
            .category("efficiency")
            .build());
        
        // 合成效率 I
        registerResearch(new Research.Builder()
            .id("synthesis_efficiency_1")
            .name("合成效率 I")
            .description("合成器速度提升 25%")
            .type(Research.Type.EFFICIENCY)
            .researchTime(1800)
            .addPrerequisite("basic_synthesis")
            .addFactorCost("basic", 200)
            .addEffect("synthesizer_speed", 1.25f)
            .treeX(2).treeY(2)
            .category("efficiency")
            .build());
        
        // 网络优化
        registerResearch(new Research.Builder()
            .id("network_optimization")
            .name("网络优化")
            .description("传递器范围增加 50%")
            .type(Research.Type.EFFICIENCY)
            .researchTime(3600)
            .addPrerequisite("factor_network")
            .addFactorCost("rare", 200)
            .addEffect("transmitter_range", 1.5f)
            .treeX(1).treeY(3)
            .category("efficiency")
            .build());
        
        // ==================== 容量研究 ====================
        
        // 存储容量 I
        registerResearch(new Research.Builder()
            .id("storage_capacity_1")
            .name("存储容量 I")
            .description("Factor 容器容量提升 50%")
            .type(Research.Type.CAPACITY)
            .researchTime(2400)
            .addPrerequisite("factor_storage")
            .addItemRequirement(Items.IRON_INGOT, 16)
            .addEffect("tank_capacity", 1.5f)
            .treeX(1).treeY(4)
            .category("capacity")
            .build());
        
        // 存储容量 II
        registerResearch(new Research.Builder()
            .id("storage_capacity_2")
            .name("存储容量 II")
            .description("Factor 容器容量提升 100%")
            .type(Research.Type.CAPACITY)
            .researchTime(4800)
            .addPrerequisite("storage_capacity_1")
            .addItemRequirement(Items.DIAMOND, 4)
            .addFactorCost("rare", 300)
            .addEffect("tank_capacity", 2.0f)
            .treeX(1).treeY(5)
            .category("capacity")
            .build());
        
        // ==================== 应用研究 ====================
        
        // Factor 工具
        registerResearch(new Research.Builder()
            .id("factor_tools")
            .name("Factor 工具")
            .description("解锁 Factor 动力工具制作")
            .type(Research.Type.APPLICATION)
            .researchTime(3000)
            .addPrerequisite("factor_storage")
            .addFactorCost("basic", 300)
            .addEffect("unlock_factor_tools", true)
            .treeX(3).treeY(2)
            .category("application")
            .build());
        
        // Factor 装备
        registerResearch(new Research.Builder()
            .id("factor_armor")
            .name("Factor 装备")
            .description("解锁 Factor 动力护甲制作")
            .type(Research.Type.APPLICATION)
            .researchTime(4800)
            .addPrerequisite("factor_tools")
            .addFactorCost("basic", 500)
            .addFactorCost("rare", 100)
            .addEffect("unlock_factor_armor", true)
            .treeX(3).treeY(3)
            .category("application")
            .build());
        
        // Factor 充能
        registerResearch(new Research.Builder()
            .id("factor_charging")
            .name("Factor 充能")
            .description("解锁 Factor 电池系统")
            .type(Research.Type.APPLICATION)
            .researchTime(3600)
            .addPrerequisite("factor_tools")
            .addEffect("unlock_factor_battery", true)
            .treeX(4).treeY(2)
            .category("application")
            .build());
        
        // ==================== 终极研究 ====================
        
        // Factor 大师
        registerResearch(new Research.Builder()
            .id("factor_mastery")
            .name("Factor 大师")
            .description("掌握 Factor 的终极奥秘，所有效率+25%")
            .type(Research.Type.ULTIMATE)
            .researchTime(12000)  // 10分钟
            .addPrerequisite("extraction_efficiency_2")
            .addPrerequisite("storage_capacity_2")
            .addPrerequisite("factor_armor")
            .addFactorCost("basic", 2000)
            .addFactorCost("rare", 500)
            .addFactorCost("legendary", 100)
            .addEffect("global_efficiency", 1.25f)
            .treeX(2).treeY(6)
            .category("ultimate")
            .build());
    }
    
    private void registerResearch(Research research) {
        researchManager.registerResearch(research);
    }
    
    /**
     * 注册事件监听器
     */
    private void registerEventListeners() {
        // 服务端启动时初始化
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            FactorCraftMod.LOGGER.info("[Research] 服务端已启动");
        });
        
        // 服务端停止时保存数据
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            FactorCraftMod.LOGGER.info("[Research] 服务端停止，保存数据...");
            // TODO: 保存所有玩家研究数据
        });
        
        // Tick 更新
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            researchManager.tick(server);
        });
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Research] 重新加载研究配置...");
        loadResearchConfig();
    }
}