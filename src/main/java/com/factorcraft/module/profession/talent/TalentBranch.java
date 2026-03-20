package com.factorcraft.module.profession.talent;

/**
 * 天赋分支类型
 * 
 * 每个职业有3个天赋分支
 */
public enum TalentBranch {
    
    // 创生师天赋分支
    EARTH_BLESSING("earth_blessing", "大地恩泽", "作物/资源产出加成，Factor生产速度提升"),
    HAND_OF_CREATION("hand_of_creation", "造物之手", "方块放置速度提升，多方块结构搭建容错率提升"),
    LIFE_LINK("life_link", "生命链接", "团队回血、抗性提升，基地范围增益buff"),
    
    // 湮灭使天赋分支
    BLADE_OF_DESTRUCTION("blade_of_destruction", "毁灭之刃", "近战伤害加成，破甲效果提升"),
    SHADOW_WALKER("shadow_walker", "暗影行者", "移动速度、夜视、隐身能力，适合探索"),
    ENERGY_OVERFLOW("energy_overflow", "能量倾泻", "范围Factor伤害技能，AOE清怪能力"),
    
    // 锻铸匠天赋分支
    FORGE_MASTER("forge_master", "锻铸大师", "基础加工效率提升，配方解锁加速"),
    RESOURCE_LINK("resource_link", "资源链接", "材料消耗减少，库存管理优化"),
    EQUIPMENT_ENHANCE("equipment_enhance", "装备强化", "装备属性提升，特殊词条概率增加");
    
    private final String id;
    private final String displayName;
    private final String description;
    
    TalentBranch(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
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
}