package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * 创生师技能
 */
public class GenesisSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new GenesisPulse(),
        new DomainExpansion(),
        new ResonanceLink(),
        new NaturalBlessing()
    );
    
    /**
     * 获取所有创生师技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
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
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现作物成熟逻辑
            // TODO: 实现机器效率提升逻辑
            return true;
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
            super(ID, "领域展开", "展开32x32保护领域，阻止敌对生物进入",
                  ProfessionType.GENESIS, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现领域展开逻辑
            return true;
        }
    }
    
    /**
     * 共鸣链接 - 与队友共享Factor产出
     */
    public static class ResonanceLink extends ProfessionSkill {
        
        public static final String ID = "resonance_link";
        public static final int FACTOR_COST = 300;
        public static final int COOLDOWN = 6000; // 5分钟
        
        public ResonanceLink() {
            super(ID, "共鸣链接", "与队友共享Factor产出，效率提升50%",
                  ProfessionType.GENESIS, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现共鸣链接逻辑
            return true;
        }
    }
    
    /**
     * 自然恩赐 - 被动增加农业产量
     */
    public static class NaturalBlessing extends ProfessionSkill {
        
        public static final String ID = "natural_blessing";
        public static final int FACTOR_COST = 0;
        public static final int COOLDOWN = 0; // 被动技能
        
        public NaturalBlessing() {
            super(ID, "自然恩赐", "被动增加农业产量20%",
                  ProfessionType.GENESIS, SkillType.PASSIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // 被动技能，无需主动执行
            return false;
        }
    }
}