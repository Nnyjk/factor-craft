package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 创生师技能
 */
public class GenesisSkills {
    
    /**
     * 创生脉冲 - 半径16格内作物瞬间成熟，所有机器生产效率提升100%持续30秒
     */
    public static class GenesisPulse extends ProfessionSkill {
        
        public static final String ID = "genesis_pulse";
        public static final int FACTOR_COST = 500;
        public static final int COOLDOWN = 6000; // 5分钟 = 6000 ticks
        
        public GenesisPulse() {
            super(ID, "创生脉冲", "半径16格内作物瞬间成熟，机器效率提升100%持续30秒",
                  ProfessionType.GENESIS, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现作物成熟逻辑
            // TODO: 实现机器效率提升逻辑
        }
    }
    
    /**
     * 领域展开 - 展开32x32保护领域
     */
    public static class DomainExpansion extends ProfessionSkill {
        
        public static final String ID = "domain_expansion";
        public static final int FACTOR_COST = 1000;
        public static final int COOLDOWN = 12000; // 10分钟
        
        public DomainExpansion() {
            super(ID, "领域展开", "展开32x32保护领域，敌方无法生成，友方伤害减免30%",
                  ProfessionType.GENESIS, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现领域展开逻辑
        }
    }
    
    /**
     * 物质重构 - 消耗低等级Factor合成高等级Factor
     */
    public static class MatterReconstruction extends ProfessionSkill {
        
        public static final String ID = "matter_reconstruction";
        public static final int FACTOR_COST = 0; // 根据实际转化量
        public static final int COOLDOWN = 1200; // 1分钟
        
        public MatterReconstruction() {
            super(ID, "物质重构", "消耗低等级Factor合成高等级Factor，转化率70%",
                  ProfessionType.GENESIS, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现Factor转化逻辑
        }
    }
}