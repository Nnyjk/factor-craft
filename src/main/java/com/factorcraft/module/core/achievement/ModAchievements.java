package com.factorcraft.module.core.achievement;

import com.factorcraft.FactorCraftMod;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 预定义成就注册
 * 注册所有 60+ 成就，覆盖 5 个分类
 */
public class ModAchievements {
    
    // ========== 图标资源 ==========
    private static final Identifier ICON_BOOK = id("textures/gui/achievement/book.png");
    private static final Identifier ICON_FACTOR = id("textures/gui/achievement/factor.png");
    private static final Identifier ICON_MACHINE = id("textures/gui/achievement/machine.png");
    private static final Identifier ICON_COMPASS = id("textures/gui/achievement/compass.png");
    private static final Identifier ICON_SWORD = id("textures/gui/achievement/sword.png");
    private static final Identifier ICON_CRYSTAL = id("textures/gui/achievement/crystal.png");
    private static final Identifier ICON_REACTOR = id("textures/gui/achievement/reactor.png");
    private static final Identifier ICON_GEAR = id("textures/gui/achievement/gear.png");
    private static final Identifier ICON_SYRINGE = id("textures/gui/achievement/syringe.png");
    
    private static Identifier id(String path) {
        return Identifier.of("factorcraft", path);
    }
    
    /**
     * 注册所有预定义成就
     */
    public static void registerAll() {
        AchievementManager manager = AchievementManager.getInstance();
        
        // ========== STORY 剧情成就 (15 个) ==========
        registerStoryAchievements(manager);
        
        // ========== FACTOR 因子成就 (15 个) ==========
        registerFactorAchievements(manager);
        
        // ========== MACHINE 机器成就 (15 个) ==========
        registerMachineAchievements(manager);
        
        // ========== EXPLORATION 探索成就 (10 个) ==========
        registerExplorationAchievements(manager);
        
        // ========== COMBAT 战斗成就 (10 个) ==========
        registerCombatAchievements(manager);
        
        FactorCraftMod.LOGGER.info("Registered {} pre-defined achievements", manager.getTotalAchievements());
    }
    
    /**
     * 注册剧情成就 (STORY)
     */
    private static void registerStoryAchievements(AchievementManager manager) {
        // 第一章：觉醒
        manager.register(new Achievement(
            id("story.awakening"),
            Text.literal("觉醒"),
            Text.literal("开始你的 Factor Craft 之旅"),
            AchievementCategory.STORY,
            ICON_BOOK,
            1,
            Text.literal("解锁成就系统"),
            false
        ));
        
        manager.register(new Achievement(
            id("story.first_factor"),
            Text.literal("初次接触"),
            Text.literal("获得第一块 Factor 晶体"),
            AchievementCategory.STORY,
            ICON_CRYSTAL,
            1,
            Text.literal("解锁 Factor 提取器配方"),
            false,
            id("story.awakening")
        ));
        
        manager.register(new Achievement(
            id("story.first_machine"),
            Text.literal("工业革命"),
            Text.literal("制作第一台机器"),
            AchievementCategory.STORY,
            ICON_MACHINE,
            1,
            Text.literal("解锁基础机器配方"),
            false,
            id("story.first_factor")
        ));
        
        // 第二章：成长
        manager.register(new Achievement(
            id("story.factor_purification"),
            Text.literal("提纯艺术"),
            Text.literal("制作第一块高纯度 Factor"),
            AchievementCategory.STORY,
            ICON_FACTOR,
            1,
            Text.literal("解锁 Factor 提纯机配方"),
            false,
            id("story.first_machine")
        ));
        
        manager.register(new Achievement(
            id("story.mk2_era"),
            Text.literal("MK2 时代"),
            Text.literal("制作第一台 MK2 机器"),
            AchievementCategory.STORY,
            ICON_GEAR,
            1,
            Text.literal("解锁 MK2 升级模块"),
            false,
            id("story.factor_purification")
        ));
        
        manager.register(new Achievement(
            id("story.first_quest"),
            Text.literal("任务开始"),
            Text.literal("完成第一个任务"),
            AchievementCategory.STORY,
            ICON_BOOK,
            1,
            Text.literal("解锁任务系统"),
            false,
            id("story.awakening")
        ));
        
        manager.register(new Achievement(
            id("story.quest_master"),
            Text.literal("任务大师"),
            Text.literal("完成 10 个任务"),
            AchievementCategory.STORY,
            ICON_BOOK,
            10,
            Text.literal("解锁特殊奖励"),
            false,
            id("story.first_quest")
        ));
        
        // 第三章：深入
        manager.register(new Achievement(
            id("story.depths_enter"),
            Text.literal("深入深渊"),
            Text.literal("首次进入 The Depths 维度"),
            AchievementCategory.STORY,
            ICON_COMPASS,
            1,
            Text.literal("解锁深渊传送门配方"),
            false,
            id("story.mk2_era")
        ));
        
        manager.register(new Achievement(
            id("story.factory_discovery"),
            Text.literal("废弃工厂"),
            Text.literal("发现废弃工厂结构"),
            AchievementCategory.STORY,
            ICON_MACHINE,
            1,
            Text.literal("解锁工厂战利品表"),
            false,
            id("story.depths_enter")
        ));
        
        manager.register(new Achievement(
            id("story.factor_compressor"),
            Text.literal("压缩之力"),
            Text.literal("制作 Factor 压缩机"),
            AchievementCategory.STORY,
            ICON_REACTOR,
            1,
            Text.literal("解锁压缩 Factor 配方"),
            false,
            id("story.mk2_era")
        ));
        
        manager.register(new Achievement(
            id("story.factor_reactor"),
            Text.literal("反应堆核心"),
            Text.literal("制作 Factor 反应堆"),
            AchievementCategory.STORY,
            ICON_REACTOR,
            1,
            Text.literal("解锁反应堆配方"),
            false,
            id("story.factor_compressor")
        ));
        
        manager.register(new Achievement(
            id("story.mk3_era"),
            Text.literal("MK3 巅峰"),
            Text.literal("制作第一台 MK3 机器"),
            AchievementCategory.STORY,
            ICON_GEAR,
            1,
            Text.literal("解锁 MK3 升级模块"),
            false,
            id("story.factor_reactor")
        ));
        
        // 第四章：终局
        manager.register(new Achievement(
            id("story.synthesizer"),
            Text.literal("合成大师"),
            Text.literal("制作 Factor 合成台"),
            AchievementCategory.STORY,
            ICON_CRYSTAL,
            1,
            Text.literal("解锁终极合成配方"),
            false,
            id("story.mk3_era")
        ));
        
        manager.register(new Achievement(
            id("story.ultimate_gear"),
            Text.literal("终极装备"),
            Text.literal("制作一套量子装备"),
            AchievementCategory.STORY,
            ICON_SWORD,
            1,
            Text.literal("解锁量子装备外观"),
            false,
            id("story.synthesizer")
        ));
        
        manager.register(new Achievement(
            id("story.master"),
            Text.literal("Factor 大师"),
            Text.literal("解锁所有成就"),
            AchievementCategory.STORY,
            ICON_BOOK,
            1,
            Text.literal("获得大师称号"),
            true,
            id("story.ultimate_gear")
        ));
    }
    
    /**
     * 注册因子成就 (FACTOR)
     */
    private static void registerFactorAchievements(AchievementManager manager) {
        // 基础 Factor
        manager.register(new Achievement(
            id("factor.crystal_1"),
            Text.literal("Factor 晶体"),
            Text.literal("生产 1 个 Factor 晶体"),
            AchievementCategory.FACTOR,
            ICON_CRYSTAL,
            1,
            null,
            false
        ));
        
        manager.register(new Achievement(
            id("factor.crystal_10"),
            Text.literal("晶体收集者"),
            Text.literal("生产 10 个 Factor 晶体"),
            AchievementCategory.FACTOR,
            ICON_CRYSTAL,
            10,
            Text.literal("解锁晶体堆叠升级"),
            false,
            id("factor.crystal_1")
        ));
        
        manager.register(new Achievement(
            id("factor.crystal_100"),
            Text.literal("晶体工厂"),
            Text.literal("生产 100 个 Factor 晶体"),
            AchievementCategory.FACTOR,
            ICON_CRYSTAL,
            100,
            Text.literal("解锁自动化提取器"),
            false,
            id("factor.crystal_10")
        ));
        
        manager.register(new Achievement(
            id("factor.crystal_1000"),
            Text.literal("晶体大亨"),
            Text.literal("生产 1000 个 Factor 晶体"),
            AchievementCategory.FACTOR,
            ICON_CRYSTAL,
            1000,
            Text.literal("解锁晶体装饰块"),
            false,
            id("factor.crystal_100")
        ));
        
        // 高纯度 Factor
        manager.register(new Achievement(
            id("factor.pure_1"),
            Text.literal("高纯度 Factor"),
            Text.literal("生产 1 个高纯度 Factor"),
            AchievementCategory.FACTOR,
            ICON_FACTOR,
            1,
            null,
            false,
            id("factor.crystal_10")
        ));
        
        manager.register(new Achievement(
            id("factor.pure_10"),
            Text.literal("纯度专家"),
            Text.literal("生产 10 个高纯度 Factor"),
            AchievementCategory.FACTOR,
            ICON_FACTOR,
            10,
            Text.literal("解锁纯度检测器"),
            false,
            id("factor.pure_1")
        ));
        
        manager.register(new Achievement(
            id("factor.pure_100"),
            Text.literal("完美纯度"),
            Text.literal("生产 100 个高纯度 Factor"),
            AchievementCategory.FACTOR,
            ICON_FACTOR,
            100,
            Text.literal("解锁高纯度反应堆"),
            false,
            id("factor.pure_10")
        ));
        
        // 浓缩 Factor
        manager.register(new Achievement(
            id("factor.concentrated_1"),
            Text.literal("浓缩 Factor"),
            Text.literal("生产 1 个浓缩 Factor"),
            AchievementCategory.FACTOR,
            ICON_REACTOR,
            1,
            null,
            false,
            id("factor.pure_10")
        ));
        
        manager.register(new Achievement(
            id("factor.concentrated_10"),
            Text.literal("能量压缩"),
            Text.literal("生产 10 个浓缩 Factor"),
            AchievementCategory.FACTOR,
            ICON_REACTOR,
            10,
            Text.literal("解锁压缩注射器"),
            false,
            id("factor.concentrated_1")
        ));
        
        // 特殊 Factor
        manager.register(new Achievement(
            id("factor.stabilized"),
            Text.literal("稳定 Factor"),
            Text.literal("生产 1 个稳定 Factor"),
            AchievementCategory.FACTOR,
            ICON_FACTOR,
            1,
            Text.literal("解锁稳定器配方"),
            false,
            id("factor.concentrated_1")
        ));
        
        manager.register(new Achievement(
            id("factor.injector"),
            Text.literal("注射之力"),
            Text.literal("使用 Factor 注射器"),
            AchievementCategory.FACTOR,
            ICON_SYRINGE,
            1,
            Text.literal("解锁注射强化"),
            false,
            id("factor.stabilized")
        ));
        
        manager.register(new Achievement(
            id("factor.reactor_power"),
            Text.literal("反应堆供能"),
            Text.literal("用反应堆为 10 台机器供能"),
            AchievementCategory.FACTOR,
            ICON_REACTOR,
            10,
            Text.literal("解锁能源网络"),
            false,
            id("factor.factor_reactor")
        ));
        
        manager.register(new Achievement(
            id("factor.mass_production"),
            Text.literal("大规模生产"),
            Text.literal("同时运行 20 台 Factor 机器"),
            AchievementCategory.FACTOR,
            ICON_MACHINE,
            20,
            Text.literal("解锁批量生产升级"),
            false,
            id("factor.crystal_100")
        ));
        
        manager.register(new Achievement(
            id("factor.efficiency"),
            Text.literal("效率专家"),
            Text.literal("达到 90% 生产效率和 1000 个 Factor 晶体"),
            AchievementCategory.FACTOR,
            ICON_GEAR,
            1,
            Text.literal("解锁效率模块"),
            false,
            id("factor.mass_production")
        ));
        
        manager.register(new Achievement(
            id("factor.infinite"),
            Text.literal("无限能源"),
            Text.literal("存储 10000 个 Factor 晶体当量"),
            AchievementCategory.FACTOR,
            ICON_REACTOR,
            10000,
            Text.literal("解锁量子存储"),
            false,
            id("factor.efficiency")
        ));
    }
    
    /**
     * 注册机器成就 (MACHINE)
     */
    private static void registerMachineAchievements(AchievementManager manager) {
        // 基础机器
        manager.register(new Achievement(
            id("machine.extractor"),
            Text.literal("提取器"),
            Text.literal("制作 Factor 提取器"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            1,
            null,
            false,
            id("story.first_machine")
        ));
        
        manager.register(new Achievement(
            id("machine.purifier"),
            Text.literal("提纯机"),
            Text.literal("制作 Factor 提纯机"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            1,
            null,
            false,
            id("machine.extractor")
        ));
        
        manager.register(new Achievement(
            id("machine.compressor"),
            Text.literal("压缩机"),
            Text.literal("制作 Factor 压缩机"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            1,
            null,
            false,
            id("machine.purifier")
        ));
        
        manager.register(new Achievement(
            id("machine.reactor"),
            Text.literal("反应堆"),
            Text.literal("制作 Factor 反应堆"),
            AchievementCategory.MACHINE,
            ICON_REACTOR,
            1,
            null,
            false,
            id("machine.compressor")
        ));
        
        manager.register(new Achievement(
            id("machine.synthesizer"),
            Text.literal("合成台"),
            Text.literal("制作 Factor 合成台"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            1,
            null,
            false,
            id("machine.reactor")
        ));
        
        // MK2 机器
        manager.register(new Achievement(
            id("machine.extractor_mk2"),
            Text.literal("MK2 提取器"),
            Text.literal("制作 MK2 Factor 提取器"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.extractor")
        ));
        
        manager.register(new Achievement(
            id("machine.purifier_mk2"),
            Text.literal("MK2 提纯机"),
            Text.literal("制作 MK2 Factor 提纯机"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.purifier")
        ));
        
        manager.register(new Achievement(
            id("machine.compressor_mk2"),
            Text.literal("MK2 压缩机"),
            Text.literal("制作 MK2 Factor 压缩机"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.compressor")
        ));
        
        manager.register(new Achievement(
            id("machine.pump_mk2"),
            Text.literal("MK2 泵"),
            Text.literal("制作 MK2 Factor 泵"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.extractor_mk2")
        ));
        
        // 自动化
        manager.register(new Achievement(
            id("machine.auto_extractor"),
            Text.literal("自动提取"),
            Text.literal("制作自动化提取器"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.extractor_mk2")
        ));
        
        manager.register(new Achievement(
            id("machine.auto_crafter"),
            Text.literal("自动合成"),
            Text.literal("制作高级自动合成机"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            null,
            false,
            id("machine.auto_extractor")
        ));
        
        manager.register(new Achievement(
            id("machine.quantum_storage"),
            Text.literal("量子存储"),
            Text.literal("制作量子存储单元"),
            AchievementCategory.MACHINE,
            ICON_REACTOR,
            1,
            null,
            false,
            id("machine.synthesizer")
        ));
        
        // 物流网络
        manager.register(new Achievement(
            id("machine.logistics_pipe"),
            Text.literal("物流管道"),
            Text.literal("放置第一根物流管道"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            1,
            null,
            false,
            id("machine.extractor")
        ));
        
        manager.register(new Achievement(
            id("machine.logistics_network"),
            Text.literal("物流网络"),
            Text.literal("连接 10 台机器到物流网络"),
            AchievementCategory.MACHINE,
            ICON_MACHINE,
            10,
            Text.literal("解锁物流请求器"),
            false,
            id("machine.logistics_pipe")
        ));
        
        manager.register(new Achievement(
            id("machine.full_automation"),
            Text.literal("全自动化"),
            Text.literal("建立完整的 Factor 自动化生产线"),
            AchievementCategory.MACHINE,
            ICON_GEAR,
            1,
            Text.literal("解锁自动化核心"),
            false,
            id("machine.logistics_network")
        ));
    }
    
    /**
     * 注册探索成就 (EXPLORATION)
     */
    private static void registerExplorationAchievements(AchievementManager manager) {
        // 维度探索
        manager.register(new Achievement(
            id("exploration.depths"),
            Text.literal("深渊探索"),
            Text.literal("进入 The Depths 维度"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1,
            Text.literal("解锁深渊地图"),
            false,
            id("story.depths_enter")
        ));
        
        manager.register(new Achievement(
            id("exploration.void"),
            Text.literal("虚空之境"),
            Text.literal("进入 The Void 维度"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1,
            Text.literal("解锁虚空传送门"),
            false,
            id("exploration.depths")
        ));
        
        manager.register(new Achievement(
            id("exploration.crystal"),
            Text.literal("晶体维度"),
            Text.literal("进入 Crystal Dimension"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1,
            Text.literal("解锁晶体矿脉地图"),
            false,
            id("exploration.void")
        ));
        
        // 结构发现
        manager.register(new Achievement(
            id("exploration.abandoned_lab"),
            Text.literal("废弃实验室"),
            Text.literal("发现废弃实验室"),
            AchievementCategory.EXPLORATION,
            ICON_BOOK,
            1,
            Text.literal("解锁实验室战利品"),
            false,
            id("exploration.depths")
        ));
        
        manager.register(new Achievement(
            id("exploration.abandoned_factory"),
            Text.literal("废弃工厂"),
            Text.literal("发现废弃工厂"),
            AchievementCategory.EXPLORATION,
            ICON_MACHINE,
            1,
            Text.literal("解锁工厂蓝图"),
            false,
            id("exploration.abandoned_lab")
        ));
        
        manager.register(new Achievement(
            id("exploration.reactor_ruins"),
            Text.literal("反应堆废墟"),
            Text.literal("发现反应堆废墟"),
            AchievementCategory.EXPLORATION,
            ICON_REACTOR,
            1,
            Text.literal("解锁废墟战利品"),
            false,
            id("exploration.abandoned_factory")
        ));
        
        // 探索里程碑
        manager.register(new Achievement(
            id("exploration.1000_blocks"),
            Text.literal("千里之行"),
            Text.literal("探索 1000 个区块"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1000,
            Text.literal("解锁快速旅行"),
            false
        ));
        
        manager.register(new Achievement(
            id("exploration.all_structures"),
            Text.literal("结构大师"),
            Text.literal("发现所有结构类型"),
            AchievementCategory.EXPLORATION,
            ICON_BOOK,
            1,
            Text.literal("解锁结构地图"),
            false,
            id("exploration.reactor_ruins")
        ));
        
        manager.register(new Achievement(
            id("exploration.all_dimensions"),
            Text.literal("维度行者"),
            Text.literal("访问所有维度"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1,
            Text.literal("解锁维度传送"),
            false,
            id("exploration.crystal")
        ));
        
        manager.register(new Achievement(
            id("exploration.cartographer"),
            Text.literal("制图师"),
            Text.literal("绘制完整主世界地图"),
            AchievementCategory.EXPLORATION,
            ICON_COMPASS,
            1,
            Text.literal("解锁地图标记系统"),
            false,
            id("exploration.1000_blocks")
        ));
    }
    
    /**
     * 注册战斗成就 (COMBAT)
     */
    private static void registerCombatAchievements(AchievementManager manager) {
        // 基础战斗
        manager.register(new Achievement(
            id("combat.first_blood"),
            Text.literal("第一滴血"),
            Text.literal("击杀第一个敌对生物"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            null,
            false
        ));
        
        manager.register(new Achievement(
            id("combat.infected"),
            Text.literal("感染体猎人"),
            Text.literal("击杀 50 个感染体"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            50,
            Text.literal("解锁感染抗性"),
            false,
            id("combat.first_blood")
        ));
        
        manager.register(new Achievement(
            id("combat.corrupted"),
            Text.literal("腐化清除者"),
            Text.literal("击杀 50 个腐化生物"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            50,
            Text.literal("解锁净化药水"),
            false,
            id("combat.first_blood")
        ));
        
        // Boss 战斗
        manager.register(new Achievement(
            id("combat.boss_first"),
            Text.literal("Boss 杀手"),
            Text.literal("击杀第一个 Boss"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            Text.literal("解锁 Boss 战利品"),
            false,
            id("combat.infected")
        ));
        
        manager.register(new Achievement(
            id("combat.infected_boss"),
            Text.literal("感染领主"),
            Text.literal("击杀感染 Boss"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            Text.literal("解锁感染核心"),
            false,
            id("combat.boss_first")
        ));
        
        manager.register(new Achievement(
            id("combat.corrupted_boss"),
            Text.literal("腐化之心"),
            Text.literal("击杀腐化 Boss"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            Text.literal("解锁腐化精华"),
            false,
            id("combat.boss_first")
        ));
        
        manager.register(new Achievement(
            id("combat.all_bosses"),
            Text.literal("传奇猎手"),
            Text.literal("击杀所有 Boss"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            Text.literal("解锁传奇称号"),
            true,
            id("combat.corrupted_boss")
        ));
        
        // 战斗里程碑
        manager.register(new Achievement(
            id("combat.100_kills"),
            Text.literal("百人斩"),
            Text.literal("击杀 100 个敌对生物"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            100,
            Text.literal("解锁战斗统计"),
            false,
            id("combat.first_blood")
        ));
        
        manager.register(new Achievement(
            id("combat.1000_kills"),
            Text.literal("千人斩"),
            Text.literal("击杀 1000 个敌对生物"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1000,
            Text.literal("解锁战斗大师称号"),
            false,
            id("combat.100_kills")
        ));
        
        manager.register(new Achievement(
            id("combat.survivor"),
            Text.literal("幸存者"),
            Text.literal("在 Boss 战斗中存活并获胜"),
            AchievementCategory.COMBAT,
            ICON_SWORD,
            1,
            Text.literal("解锁生存专家称号"),
            false,
            id("combat.all_bosses")
        ));
    }
}
