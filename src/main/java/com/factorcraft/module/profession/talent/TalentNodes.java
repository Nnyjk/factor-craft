package com.factorcraft.module.profession.talent;

import com.factorcraft.module.profession.model.ProfessionType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天赋节点工厂
 * 
 * 提供所有天赋节点的创建和管理
 * 每个职业 3 个分支，每个分支 5 个天赋节点，共 45 个天赋
 */
public class TalentNodes {
    
    private static final Map<TalentBranch, List<TalentNode>> TALENTS_BY_BRANCH = new HashMap<>();
    private static final Map<ProfessionType, List<TalentNode>> TALENTS_BY_PROFESSION = new HashMap<>();
    
    static {
        initEngineerTalents();
        initCultivatorTalents();
        initExplorerTalents();
        initMasterTalents();
    }
    
    /**
     * 辅助方法：创建效果列表
     */
    private static List<TalentEffect> effects(TalentEffect... effects) {
        return List.of(effects);
    }
    
    /**
     * 辅助方法：创建简单数值效果
     */
    private static TalentEffect effect(TalentEffectType type, float baseValue, float perLevel) {
        return new TalentEffect(type, baseValue, perLevel);
    }
    
    /**
     * 辅助方法：创建百分比效果
     */
    private static TalentEffect percentEffect(TalentEffectType type, float basePercent, float perLevelPercent) {
        return new TalentEffect(type, basePercent / 100f, perLevelPercent / 100f, true);
    }
    
    // ==================== Factor工程师天赋 ====================
    
    private static void initEngineerTalents() {
        List<TalentNode> engineerTalents = new ArrayList<>();
        
        // 效率分支 (Tier 1-5)
        engineerTalents.add(new TalentNode(
            "engineer_efficiency_1", "机器精通 I", 
            "机器工作速度提升 5%", 
            TalentBranch.ENGINEER_EFFICIENCY, 1, 5,
            effects(percentEffect(TalentEffectType.MACHINE_SPEED, 5, 1))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_efficiency_2", "Factor节约 I", 
            "机器 Factor 消耗降低 3%", 
            TalentBranch.ENGINEER_EFFICIENCY, 2, 5,
            effects(percentEffect(TalentEffectType.FACTOR_COST_REDUCTION, 3, 0.5f))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_efficiency_3", "产量优化 I", 
            "机器产出效率提升 5%", 
            TalentBranch.ENGINEER_EFFICIENCY, 3, 5,
            effects(percentEffect(TalentEffectType.OUTPUT_EFFICIENCY, 5, 1))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_efficiency_4", "机器精通 II", 
            "机器工作速度提升 10%", 
            TalentBranch.ENGINEER_EFFICIENCY, 4, 5,
            effects(percentEffect(TalentEffectType.MACHINE_SPEED, 10, 2))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_efficiency_5", "效率大师", 
            "机器工作速度提升 15%，Factor 消耗降低 10%", 
            TalentBranch.ENGINEER_EFFICIENCY, 5, 3,
            effects(
                percentEffect(TalentEffectType.MACHINE_SPEED, 15, 2),
                percentEffect(TalentEffectType.FACTOR_COST_REDUCTION, 10, 1)
            )
        ));
        
        // 自动化分支 (Tier 1-5)
        engineerTalents.add(new TalentNode(
            "engineer_automation_1", "物流精通 I", 
            "管道传输速度提升 10%", 
            TalentBranch.ENGINEER_AUTOMATION, 1, 5,
            effects(percentEffect(TalentEffectType.PIPE_SPEED, 10, 2))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_automation_2", "自动合成 I", 
            "解锁基础自动合成配方", 
            TalentBranch.ENGINEER_AUTOMATION, 2, 3,
            effects(percentEffect(TalentEffectType.AUTO_CRAFT_EFFICIENCY, 5, 1))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_automation_3", "远程控制 I", 
            "远程控制范围提升至 16 格", 
            TalentBranch.ENGINEER_AUTOMATION, 3, 3,
            effects(effect(TalentEffectType.REMOTE_CONTROL_RANGE, 16, 2))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_automation_4", "自动合成 II", 
            "解锁高级自动合成配方", 
            TalentBranch.ENGINEER_AUTOMATION, 4, 3,
            effects(percentEffect(TalentEffectType.AUTO_CRAFT_EFFICIENCY, 10, 2))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_automation_5", "自动化大师", 
            "管道传输速度提升 25%，远程控制范围提升至 32 格", 
            TalentBranch.ENGINEER_AUTOMATION, 5, 3,
            effects(
                percentEffect(TalentEffectType.PIPE_SPEED, 25, 3),
                effect(TalentEffectType.REMOTE_CONTROL_RANGE, 32, 4)
            )
        ));
        
        // 能量分支 (Tier 1-5)
        engineerTalents.add(new TalentNode(
            "engineer_energy_1", "Factor存储 I", 
            "Factor 存储上限提升 1000", 
            TalentBranch.ENGINEER_ENERGY, 1, 5,
            effects(effect(TalentEffectType.FACTOR_STORAGE, 1000, 200))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_energy_2", "能量传输 I", 
            "Factor 传输速度提升 10%", 
            TalentBranch.ENGINEER_ENERGY, 2, 5,
            effects(percentEffect(TalentEffectType.FACTOR_TRANSFER_RATE, 10, 2))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_energy_3", "电池扩容 I", 
            "电池容量提升 20%", 
            TalentBranch.ENGINEER_ENERGY, 3, 5,
            effects(percentEffect(TalentEffectType.BATTERY_CAPACITY, 20, 4))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_energy_4", "Factor存储 II", 
            "Factor 存储上限提升 2000", 
            TalentBranch.ENGINEER_ENERGY, 4, 5,
            effects(effect(TalentEffectType.FACTOR_STORAGE, 2000, 400))
        ));
        engineerTalents.add(new TalentNode(
            "engineer_energy_5", "能量大师", 
            "Factor 存储上限提升 5000，传输速度提升 25%", 
            TalentBranch.ENGINEER_ENERGY, 5, 3,
            effects(
                effect(TalentEffectType.FACTOR_STORAGE, 5000, 1000),
                percentEffect(TalentEffectType.FACTOR_TRANSFER_RATE, 25, 3)
            )
        ));
        
        TALENTS_BY_PROFESSION.put(ProfessionType.ENGINEER, engineerTalents);
    }
    
    // ==================== 能量培育师天赋 ====================
    
    private static void initCultivatorTalents() {
        List<TalentNode> cultivatorTalents = new ArrayList<>();
        
        // 培育分支 (Tier 1-5)
        cultivatorTalents.add(new TalentNode(
            "cultivator_breeding_1", "快速生长 I", 
            "生物生长速度提升 5%", 
            TalentBranch.CULTIVATOR_BREEDING, 1, 5,
            effects(percentEffect(TalentEffectType.GROWTH_SPEED, 5, 1))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_breeding_2", "变异诱导 I", 
            "生物变异概率提升 3%", 
            TalentBranch.CULTIVATOR_BREEDING, 2, 5,
            effects(percentEffect(TalentEffectType.MUTATION_CHANCE, 3, 0.5f))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_breeding_3", "稀有收获 I", 
            "稀有掉落率提升 5%", 
            TalentBranch.CULTIVATOR_BREEDING, 3, 5,
            effects(percentEffect(TalentEffectType.RARE_DROP_CHANCE, 5, 1))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_breeding_4", "快速生长 II", 
            "生物生长速度提升 10%", 
            TalentBranch.CULTIVATOR_BREEDING, 4, 5,
            effects(percentEffect(TalentEffectType.GROWTH_SPEED, 10, 2))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_breeding_5", "培育大师", 
            "生长速度 +15%，变异概率 +8%，稀有掉落率 +10%", 
            TalentBranch.CULTIVATOR_BREEDING, 5, 3,
            effects(
                percentEffect(TalentEffectType.GROWTH_SPEED, 15, 2),
                percentEffect(TalentEffectType.MUTATION_CHANCE, 8, 1),
                percentEffect(TalentEffectType.RARE_DROP_CHANCE, 10, 2)
            )
        ));
        
        // 养殖分支 (Tier 1-5)
        cultivatorTalents.add(new TalentNode(
            "cultivator_ranching_1", "自动化养殖 I", 
            "解锁基础自动化养殖设备", 
            TalentBranch.CULTIVATOR_FARMING, 1, 3,
            effects(percentEffect(TalentEffectType.AUTO_FARM_EFFICIENCY, 5, 1))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_ranching_2", "批量培育 I", 
            "批量培育数量提升至 8", 
            TalentBranch.CULTIVATOR_FARMING, 2, 3,
            effects(effect(TalentEffectType.BATCH_BREED_COUNT, 8, 1))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_ranching_3", "状态监控 I", 
            "解锁生物状态监控界面", 
            TalentBranch.CULTIVATOR_FARMING, 3, 1,
            effects(percentEffect(TalentEffectType.MONITOR_RANGE, 10, 0))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_ranching_4", "自动化养殖 II", 
            "解锁高级自动化养殖设备", 
            TalentBranch.CULTIVATOR_FARMING, 4, 3,
            effects(percentEffect(TalentEffectType.AUTO_FARM_EFFICIENCY, 10, 2))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_ranching_5", "养殖大师", 
            "批量培育数量提升至 16，养殖效率 +20%", 
            TalentBranch.CULTIVATOR_FARMING, 5, 3,
            effects(
                effect(TalentEffectType.BATCH_BREED_COUNT, 16, 2),
                percentEffect(TalentEffectType.AUTO_FARM_EFFICIENCY, 20, 3)
            )
        ));
        
        // 能量分支 (Tier 1-5)
        cultivatorTalents.add(new TalentNode(
            "cultivator_energy_1", "Factor产出 I", 
            "生物 Factor 产出效率提升 5%", 
            TalentBranch.CULTIVATOR_ENERGY, 1, 5,
            effects(percentEffect(TalentEffectType.FACTOR_PRODUCTION, 5, 1))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_energy_2", "能量转化 I", 
            "Factor 转化效率提升 3%", 
            TalentBranch.CULTIVATOR_ENERGY, 2, 5,
            effects(percentEffect(TalentEffectType.FACTOR_CONVERT_EFFICIENCY, 3, 0.5f))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_energy_3", "生物共鸣 I", 
            "与生物共鸣时 Factor 恢复速度 +10%", 
            TalentBranch.CULTIVATOR_ENERGY, 3, 3,
            effects(percentEffect(TalentEffectType.FACTOR_REGEN_RATE, 10, 2))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_energy_4", "Factor产出 II", 
            "生物 Factor 产出效率提升 10%", 
            TalentBranch.CULTIVATOR_ENERGY, 4, 5,
            effects(percentEffect(TalentEffectType.FACTOR_PRODUCTION, 10, 2))
        ));
        cultivatorTalents.add(new TalentNode(
            "cultivator_energy_5", "能量大师", 
            "Factor 产出效率 +20%，转化效率 +15%", 
            TalentBranch.CULTIVATOR_ENERGY, 5, 3,
            effects(
                percentEffect(TalentEffectType.FACTOR_PRODUCTION, 20, 3),
                percentEffect(TalentEffectType.FACTOR_CONVERT_EFFICIENCY, 15, 2)
            )
        ));
        
        TALENTS_BY_PROFESSION.put(ProfessionType.CULTIVATOR, cultivatorTalents);
    }
    
    // ==================== 潮汐探索者天赋 ====================
    
    private static void initExplorerTalents() {
        List<TalentNode> explorerTalents = new ArrayList<>();
        
        // 战斗分支 (Tier 1-5)
        explorerTalents.add(new TalentNode(
            "explorer_combat_1", "Factor赋能 I", 
            "Factor 赋能装备效果提升 5%", 
            TalentBranch.EXPLORER_COMBAT, 1, 5,
            effects(percentEffect(TalentEffectType.FACTOR_EMPOWERMENT, 5, 1))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_combat_2", "战斗本能 I", 
            "攻击力提升 3%", 
            TalentBranch.EXPLORER_COMBAT, 2, 5,
            effects(percentEffect(TalentEffectType.ATTACK_DAMAGE, 3, 0.5f))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_combat_3", "技能强化 I", 
            "技能伤害提升 5%", 
            TalentBranch.EXPLORER_COMBAT, 3, 5,
            effects(percentEffect(TalentEffectType.SKILL_DAMAGE, 5, 1))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_combat_4", "Factor赋能 II", 
            "Factor 赋能装备效果提升 10%", 
            TalentBranch.EXPLORER_COMBAT, 4, 5,
            effects(percentEffect(TalentEffectType.FACTOR_EMPOWERMENT, 10, 2))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_combat_5", "战斗大师", 
            "攻击力 +10%，技能伤害 +15%，暴击率 +5%", 
            TalentBranch.EXPLORER_COMBAT, 5, 3,
            effects(
                percentEffect(TalentEffectType.ATTACK_DAMAGE, 10, 2),
                percentEffect(TalentEffectType.SKILL_DAMAGE, 15, 2),
                percentEffect(TalentEffectType.CRITICAL_CHANCE, 5, 1)
            )
        ));
        
        // 探索分支 (Tier 1-5)
        explorerTalents.add(new TalentNode(
            "explorer_explore_1", "Factor感知 I", 
            "Factor 感知范围提升至 32 格", 
            TalentBranch.EXPLORER_EXPLORATION, 1, 3,
            effects(effect(TalentEffectType.FACTOR_SENSE_RANGE, 32, 4))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_explore_2", "遗迹探测 I", 
            "解锁遗迹探测功能", 
            TalentBranch.EXPLORER_EXPLORATION, 2, 1,
            effects(effect(TalentEffectType.RUIN_DETECTION_RANGE, 64, 0))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_explore_3", "幸运发现 I", 
            "稀有资源发现概率提升 5%", 
            TalentBranch.EXPLORER_EXPLORATION, 3, 5,
            effects(percentEffect(TalentEffectType.RARE_RESOURCE_CHANCE, 5, 1))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_explore_4", "Factor感知 II", 
            "Factor 感知范围提升至 64 格", 
            TalentBranch.EXPLORER_EXPLORATION, 4, 3,
            effects(effect(TalentEffectType.FACTOR_SENSE_RANGE, 64, 8))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_explore_5", "探索大师", 
            "感知范围 +96 格，稀有资源发现概率 +15%", 
            TalentBranch.EXPLORER_EXPLORATION, 5, 3,
            effects(
                effect(TalentEffectType.FACTOR_SENSE_RANGE, 96, 12),
                percentEffect(TalentEffectType.RARE_RESOURCE_CHANCE, 15, 2)
            )
        ));
        
        // 生存分支 (Tier 1-5)
        explorerTalents.add(new TalentNode(
            "explorer_survival_1", "潮汐抗性 I", 
            "潮汐负面效果抗性提升 10%", 
            TalentBranch.EXPLORER_SURVIVAL, 1, 5,
            effects(percentEffect(TalentEffectType.TIDE_RESISTANCE, 10, 2))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_survival_2", "生命恢复 I", 
            "生命恢复速度提升 5%", 
            TalentBranch.EXPLORER_SURVIVAL, 2, 5,
            effects(percentEffect(TalentEffectType.HEALTH_REGEN, 5, 1))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_survival_3", "环境适应 I", 
            "环境伤害降低 10%", 
            TalentBranch.EXPLORER_SURVIVAL, 3, 5,
            effects(percentEffect(TalentEffectType.ENVIRONMENT_DAMAGE_REDUCTION, 10, 2))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_survival_4", "潮汐抗性 II", 
            "潮汐负面效果抗性提升 20%", 
            TalentBranch.EXPLORER_SURVIVAL, 4, 5,
            effects(percentEffect(TalentEffectType.TIDE_RESISTANCE, 20, 3))
        ));
        explorerTalents.add(new TalentNode(
            "explorer_survival_5", "生存大师", 
            "潮汐抗性 +30%，生命恢复 +15%，环境伤害 -25%", 
            TalentBranch.EXPLORER_SURVIVAL, 5, 3,
            effects(
                percentEffect(TalentEffectType.TIDE_RESISTANCE, 30, 4),
                percentEffect(TalentEffectType.HEALTH_REGEN, 15, 2),
                percentEffect(TalentEffectType.ENVIRONMENT_DAMAGE_REDUCTION, 25, 3)
            )
        ));
        
        TALENTS_BY_PROFESSION.put(ProfessionType.EXPLORER, explorerTalents);
    }
    
    // ==================== 因子掌控者天赋 ====================
    
    private static void initMasterTalents() {
        List<TalentNode> masterTalents = new ArrayList<>();
        
        // 全能融合分支 - 可以融合三个基础职业的能力
        masterTalents.add(new TalentNode(
            "master_fusion_1", "工程师融合 I", 
            "解锁 Factor工程师 效率分支天赋", 
            TalentBranch.MASTER_FUSION, 1, 1,
            effects(percentEffect(TalentEffectType.CROSS_CLASS_EFFICIENCY, 10, 0))
        ));
        masterTalents.add(new TalentNode(
            "master_fusion_2", "培育师融合 I", 
            "解锁 能量培育师 培育分支天赋", 
            TalentBranch.MASTER_FUSION, 2, 1,
            effects(percentEffect(TalentEffectType.CROSS_CLASS_BREEDING, 10, 0))
        ));
        masterTalents.add(new TalentNode(
            "master_fusion_3", "探索者融合 I", 
            "解锁 潮汐探索者 战斗分支天赋", 
            TalentBranch.MASTER_FUSION, 3, 1,
            effects(percentEffect(TalentEffectType.CROSS_CLASS_COMBAT, 10, 0))
        ));
        masterTalents.add(new TalentNode(
            "master_fusion_4", "多重融合 I", 
            "可同时激活两个职业的天赋效果", 
            TalentBranch.MASTER_FUSION, 4, 1,
            effects(percentEffect(TalentEffectType.MULTI_CLASS_BONUS, 20, 0))
        ));
        masterTalents.add(new TalentNode(
            "master_fusion_5", "因子掌控", 
            "可同时激活三个职业的天赋效果，所有属性 +10%", 
            TalentBranch.MASTER_FUSION, 5, 1,
            effects(
                percentEffect(TalentEffectType.ALL_STATS_BONUS, 10, 0),
                percentEffect(TalentEffectType.MULTI_CLASS_BONUS, 30, 0)
            )
        ));
        
        // 能量掌控分支
        masterTalents.add(new TalentNode(
            "master_energy_1", "Factor精通 I", 
            "Factor 存储上限提升 2000", 
            TalentBranch.MASTER_CONTROL, 1, 5,
            effects(effect(TalentEffectType.FACTOR_STORAGE, 2000, 400))
        ));
        masterTalents.add(new TalentNode(
            "master_energy_2", "能量循环 I", 
            "Factor 自然恢复速度提升 10%", 
            TalentBranch.MASTER_CONTROL, 2, 5,
            effects(percentEffect(TalentEffectType.FACTOR_REGEN_RATE, 10, 2))
        ));
        masterTalents.add(new TalentNode(
            "master_energy_3", "技能增效 I", 
            "所有技能效果提升 5%", 
            TalentBranch.MASTER_CONTROL, 3, 5,
            effects(percentEffect(TalentEffectType.SKILL_EFFECTIVENESS, 5, 1))
        ));
        masterTalents.add(new TalentNode(
            "master_energy_4", "Factor精通 II", 
            "Factor 存储上限提升 4000", 
            TalentBranch.MASTER_CONTROL, 4, 5,
            effects(effect(TalentEffectType.FACTOR_STORAGE, 4000, 800))
        ));
        masterTalents.add(new TalentNode(
            "master_energy_5", "能量掌控大师", 
            "Factor 存储 +10000，恢复速度 +25%，技能效果 +15%", 
            TalentBranch.MASTER_CONTROL, 5, 3,
            effects(
                effect(TalentEffectType.FACTOR_STORAGE, 10000, 2000),
                percentEffect(TalentEffectType.FACTOR_REGEN_RATE, 25, 3),
                percentEffect(TalentEffectType.SKILL_EFFECTIVENESS, 15, 2)
            )
        ));
        
        // 终极能力分支
        masterTalents.add(new TalentNode(
            "master_ultimate_1", "技能冷却 I", 
            "所有技能冷却时间降低 5%", 
            TalentBranch.MASTER_TRANSCEND, 1, 5,
            effects(percentEffect(TalentEffectType.COOLDOWN_REDUCTION, 5, 1))
        ));
        masterTalents.add(new TalentNode(
            "master_ultimate_2", "Factor消耗 I", 
            "所有技能 Factor 消耗降低 5%", 
            TalentBranch.MASTER_TRANSCEND, 2, 5,
            effects(percentEffect(TalentEffectType.FACTOR_COST_REDUCTION, 5, 1))
        ));
        masterTalents.add(new TalentNode(
            "master_ultimate_3", "终极强化 I", 
            "终极技能效果提升 10%", 
            TalentBranch.MASTER_TRANSCEND, 3, 5,
            effects(percentEffect(TalentEffectType.ULTIMATE_SKILL_POWER, 10, 2))
        ));
        masterTalents.add(new TalentNode(
            "master_ultimate_4", "技能冷却 II", 
            "所有技能冷却时间降低 10%", 
            TalentBranch.MASTER_TRANSCEND, 4, 5,
            effects(percentEffect(TalentEffectType.COOLDOWN_REDUCTION, 10, 2))
        ));
        masterTalents.add(new TalentNode(
            "master_ultimate_5", "终极大师", 
            "技能冷却 -20%，Factor消耗 -15%，终极技能效果 +25%", 
            TalentBranch.MASTER_TRANSCEND, 5, 3,
            effects(
                percentEffect(TalentEffectType.COOLDOWN_REDUCTION, 20, 3),
                percentEffect(TalentEffectType.FACTOR_COST_REDUCTION, 15, 2),
                percentEffect(TalentEffectType.ULTIMATE_SKILL_POWER, 25, 3)
            )
        ));
        
        TALENTS_BY_PROFESSION.put(ProfessionType.MASTER, masterTalents);
    }
    
    /**
     * 获取指定职业的所有天赋
     */
    public static List<TalentNode> getTalentsForProfession(ProfessionType profession) {
        return TALENTS_BY_PROFESSION.getOrDefault(profession, List.of());
    }
    
    /**
     * 获取指定分支的所有天赋
     */
    public static List<TalentNode> getTalentsForBranch(TalentBranch branch) {
        return TALENTS_BY_BRANCH.getOrDefault(branch, List.of());
    }
    
    /**
     * 根据 ID 获取天赋
     */
    public static TalentNode getTalentById(String id) {
        for (List<TalentNode> talents : TALENTS_BY_PROFESSION.values()) {
            for (TalentNode talent : talents) {
                if (talent.getId().equals(id)) {
                    return talent;
                }
            }
        }
        return null;
    }
    
    /**
     * 获取所有天赋（共 45 个）
     */
    public static List<TalentNode> getAllTalents() {
        List<TalentNode> all = new ArrayList<>();
        for (List<TalentNode> talents : TALENTS_BY_PROFESSION.values()) {
            all.addAll(talents);
        }
        return all;
    }
}