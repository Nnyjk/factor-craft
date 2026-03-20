package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 湮灭使技能
 */
public class AnnihilationSkills {
    
    /**
     * 湮灭斩 - 向前释放范围伤害波
     */
    public static class AnnihilationSlash extends ProfessionSkill {
        
        public static final String ID = "annihilation_slash";
        public static final int FACTOR_COST = 300;
        public static final int COOLDOWN = 600; // 30秒
        
        public AnnihilationSlash() {
            super(ID, "湮灭斩", "向前释放范围伤害波，造成10倍基础伤害",
                  ProfessionType.ANNIHILATION, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现范围伤害逻辑
        }
    }
    
    /**
     * 因子吞噬 - 吸收周围Factor能量恢复生命
     */
    public static class FactorDevour extends ProfessionSkill {
        
        public static final String ID = "factor_devour";
        public static final int FACTOR_COST = 200;
        public static final int COOLDOWN = 1200; // 1分钟
        
        public FactorDevour() {
            super(ID, "因子吞噬", "吸收周围Factor能量恢复生命值",
                  ProfessionType.ANNIHILATION, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现Factor吸收逻辑
        }
    }
    
    /**
     * 虚空步 - 短距离瞬移
     */
    public static class VoidStep extends ProfessionSkill {
        
        public static final String ID = "void_step";
        public static final int FACTOR_COST = 100;
        public static final int COOLDOWN = 100; // 5秒
        
        public VoidStep() {
            super(ID, "虚空步", "短距离瞬移，穿过障碍物",
                  ProfessionType.ANNIHILATION, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public void execute(ServerPlayerEntity player) {
            // TODO: 实现瞬移逻辑
        }
    }
}