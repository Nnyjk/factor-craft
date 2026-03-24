package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * 锻铸匠技能
 */
public class ForgeSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new InstantProcess(),
        new PerfectForge(),
        new EquipmentMaster()
    );
    
    /**
     * 获取所有锻铸匠技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
    /**
     * 瞬间加工 - 立即完成当前机器加工任务
     */
    public static class InstantProcess extends ProfessionSkill {
        
        public static final String ID = "instant_process";
        public static final int FACTOR_COST = 200;
        public static final int COOLDOWN = 1200; // 1分钟
        
        public InstantProcess() {
            super(ID, "瞬间加工", "立即完成当前机器加工任务",
                  ProfessionType.FORGE, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现瞬间加工逻辑
            return true;
        }
    }
    
    /**
     * 完美锻铸 - 下次合成必定成功且品质+1
     */
    public static class PerfectForge extends ProfessionSkill {
        
        public static final String ID = "perfect_forge";
        public static final int FACTOR_COST = 500;
        public static final int COOLDOWN = 2400; // 2分钟
        
        public PerfectForge() {
            super(ID, "完美锻铸", "下次合成必定成功且品质+1",
                  ProfessionType.FORGE, SkillType.ACTIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现完美锻铸逻辑
            return true;
        }
    }
    
    /**
     * 装备大师 - 永久提升装备强化上限
     */
    public static class EquipmentMaster extends ProfessionSkill {
        
        public static final String ID = "equipment_master";
        public static final int FACTOR_COST = 1000;
        public static final int COOLDOWN = 0; // 被动技能
        
        public EquipmentMaster() {
            super(ID, "装备大师", "永久提升装备强化上限",
                  ProfessionType.FORGE, SkillType.PASSIVE, FACTOR_COST, COOLDOWN);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // 被动技能，无需主动执行
            return false;
        }
    }
}