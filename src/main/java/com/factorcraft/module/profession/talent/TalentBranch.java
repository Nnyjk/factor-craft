package com.factorcraft.module.profession.talent;

import com.factorcraft.module.profession.model.ProfessionType;

/**
 * 天赋分支类型
 * 
 * 每个职业有3个天赋分支，每个分支5个天赋节点
 */
public enum TalentBranch {
    
    // ==================== Factor工程师天赋分支 ====================
    
    /**
     * 效率分支 - 提升机器工作速度、降低Factor消耗、提升产量
     */
    ENGINEER_EFFICIENCY("engineer_efficiency", "效率", 
            "提升机器工作速度、降低Factor消耗、提升产量", ProfessionType.ENGINEER),
    
    /**
     * 自动化分支 - 解锁高级物流系统、自动合成、远程控制功能
     */
    ENGINEER_AUTOMATION("engineer_automation", "自动化", 
            "解锁高级物流系统、自动合成、远程控制功能", ProfessionType.ENGINEER),
    
    /**
     * 能量分支 - 提升Factor存储上限、传输速度、电池容量
     */
    ENGINEER_ENERGY("engineer_energy", "能量", 
            "提升Factor存储上限、传输速度、电池容量", ProfessionType.ENGINEER),
    
    // ==================== 能量培育师天赋分支 ====================
    
    /**
     * 培育分支 - 提升生物生长速度、变异概率、稀有掉落率
     */
    CULTIVATOR_BREEDING("cultivator_breeding", "培育", 
            "提升生物生长速度、变异概率、稀有掉落率", ProfessionType.CULTIVATOR),
    
    /**
     * 养殖分支 - 解锁自动化养殖、批量培育、生物状态监控
     */
    CULTIVATOR_FARMING("cultivator_farming", "养殖", 
            "解锁自动化养殖、批量培育、生物状态监控", ProfessionType.CULTIVATOR),
    
    /**
     * 能量分支 - 提升生物Factor产出效率、转化效率
     */
    CULTIVATOR_ENERGY("cultivator_energy", "能量", 
            "提升生物Factor产出效率、转化效率", ProfessionType.CULTIVATOR),
    
    // ==================== 潮汐探索者天赋分支 ====================
    
    /**
     * 战斗分支 - 提升Factor赋能装备效果、战斗属性、技能伤害
     */
    EXPLORER_COMBAT("explorer_combat", "战斗", 
            "提升Factor赋能装备效果、战斗属性、技能伤害", ProfessionType.EXPLORER),
    
    /**
     * 探索分支 - 提升Factor感知能力、遗迹探测、稀有资源发现概率
     */
    EXPLORER_EXPLORATION("explorer_exploration", "探索", 
            "提升Factor感知能力、遗迹探测、稀有资源发现概率", ProfessionType.EXPLORER),
    
    /**
     * 生存分支 - 提升潮汐负面效果抗性、生命恢复、环境适应能力
     */
    EXPLORER_SURVIVAL("explorer_survival", "生存", 
            "提升潮汐负面效果抗性、生命恢复、环境适应能力", ProfessionType.EXPLORER),
    
    // ==================== 因子掌控者天赋分支（隐藏职业） ====================
    
    /**
     * 融合分支 - 可融合三个基础职业的能力，自定义天赋组合
     */
    MASTER_FUSION("master_fusion", "融合", 
            "融合三个基础职业的能力，自定义天赋组合", ProfessionType.MASTER),
    
    /**
     * 掌控分支 - 全属性提升，全技能解锁
     */
    MASTER_CONTROL("master_control", "掌控", 
            "全属性提升，全技能解锁", ProfessionType.MASTER),
    
    /**
     * 超越分支 - 突破等级上限，专属终极技能
     */
    MASTER_TRANSCEND("master_transcend", "超越", 
            "突破等级上限，专属终极技能", ProfessionType.MASTER);
    
    private final String id;
    private final String displayName;
    private final String description;
    private final ProfessionType professionType;
    
    TalentBranch(String id, String displayName, String description, ProfessionType professionType) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.professionType = professionType;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ProfessionType getProfessionType() {
        return professionType;
    }
    
    /**
     * 根据ID获取天赋分支
     */
    public static TalentBranch fromId(String id) {
        for (TalentBranch branch : values()) {
            if (branch.id.equals(id)) {
                return branch;
            }
        }
        return null;
    }
    
    /**
     * 获取指定职业的所有天赋分支
     */
    public static TalentBranch[] getBranchesForProfession(ProfessionType type) {
        return java.util.Arrays.stream(values())
                .filter(branch -> branch.professionType == type)
                .toArray(TalentBranch[]::new);
    }
}