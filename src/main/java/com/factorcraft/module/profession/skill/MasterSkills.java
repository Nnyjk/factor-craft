package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/**
 * 因子掌控者技能
 * 
 * 核心定位：全能型、融合三职业技能
 * 技能主题：全属性提升、自定义天赋组合
 * 
 * 解锁条件：完成主线任务"因子融合"，3个基础职业均达到10级
 */
public class MasterSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new FactorResonance(),
        new SkillInheritance(),
        new TalentFusion(),
        new OmnipotentForm(),
        new GenesisFactor()
    );
    
    /**
     * 获取所有因子掌控者技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
    /**
     * Factor共振 - 同时激活三个基础职业的被动效果
     * 基础技能 | CD: 1分钟 | Factor消耗: 500
     */
    public static class FactorResonance extends ProfessionSkill {
        
        public static final String ID = "factor_resonance";
        public static final int FACTOR_COST = 500;
        public static final int COOLDOWN = 1200; // 1分钟
        public static final int DURATION = 1200; // 1分钟
        
        public FactorResonance() {
            super(ID, "Factor共振", "1分钟内同时激活三职业的基础被动效果",
                  ProfessionType.MASTER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 1, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现Factor共振效果
            player.sendMessage(Text.literal("§e[Factor共振] §a三职业之力汇聚！"), true);
            spawnParticles(player, ParticleTypes.END_ROD, 30);
            playSound(player, SoundEvents.BLOCK_BEACON_AMBIENT);
            return true;
        }
    }
    
    /**
     * 技能继承 - 使用任意基础职业的技能（需已解锁）
     * 基础技能 | CD: 动态 | Factor消耗: 原技能消耗*1.5
     */
    public static class SkillInheritance extends ProfessionSkill {
        
        public static final String ID = "skill_inheritance";
        public static final int FACTOR_COST = 0; // 动态计算
        public static final int COOLDOWN = 0; // 使用原技能CD
        
        public SkillInheritance() {
            super(ID, "技能继承", "使用已解锁的任意基础职业技能",
                  ProfessionType.MASTER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 5, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 打开技能选择界面
            player.sendMessage(Text.literal("§e[技能继承] §7选择要使用的技能..."), true);
            spawnParticles(player, ParticleTypes.ENCHANT, 20);
            playSound(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE);
            return true;
        }
    }
    
    /**
     * 天赋融合 - 组合不同职业的天赋分支效果
     * 基础技能 | CD: 5分钟 | Factor消耗: 1000
     */
    public static class TalentFusion extends ProfessionSkill {
        
        public static final String ID = "talent_fusion";
        public static final int FACTOR_COST = 1000;
        public static final int COOLDOWN = 6000; // 5分钟
        public static final int DURATION = 3600; // 3分钟
        
        public TalentFusion() {
            super(ID, "天赋融合", "3分钟内同时激活多个天赋分支效果",
                  ProfessionType.MASTER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 10, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现天赋融合效果
            player.sendMessage(Text.literal("§e[天赋融合] §d多分支天赋激活！"), true);
            spawnParticles(player, ParticleTypes.SOUL_FIRE_FLAME, 25);
            playSound(player, SoundEvents.ITEM_TOTEM_USE);
            return true;
        }
    }
    
    /**
     * 全能形态 - 全属性大幅提升，持续5分钟
     * 终极技能 | CD: 15分钟 | Factor消耗: 5000
     */
    public static class OmnipotentForm extends ProfessionSkill {
        
        public static final String ID = "omnipotent_form";
        public static final int FACTOR_COST = 5000;
        public static final int COOLDOWN = 18000; // 15分钟
        public static final int DURATION = 6000; // 5分钟
        
        public OmnipotentForm() {
            super(ID, "全能形态", "5分钟内全属性提升50%，技能冷却-30%",
                  ProfessionType.MASTER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 15, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现全能形态效果
            player.sendMessage(Text.literal("§d[终极：全能形态] §6因子之力觉醒！"), true);
            spawnParticles(player, ParticleTypes.TOTEM_OF_UNDYING, 50);
            playSound(player, SoundEvents.BLOCK_END_PORTAL_SPAWN);
            return true;
        }
    }
    
    /**
     * 创生因子 - 释放所有基础职业终极技能的组合效果
     * 终极技能 | CD: 30分钟 | Factor消耗: 10000
     */
    public static class GenesisFactor extends ProfessionSkill {
        
        public static final String ID = "genesis_factor";
        public static final int FACTOR_COST = 10000;
        public static final int COOLDOWN = 36000; // 30分钟
        
        public GenesisFactor() {
            super(ID, "创生因子", "释放工厂意志+丰收时刻+维度破碎的组合效果",
                  ProfessionType.MASTER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 20, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现创生因子效果 - 组合三职业终极技能
            player.sendMessage(Text.literal("§d[终极：创生因子] §c§l因子掌控者之力完全释放！"), true);
            spawnParticles(player, ParticleTypes.FLASH, 80);
            playSound(player, SoundEvents.ENTITY_ENDER_DRAGON_DEATH);
            return true;
        }
    }
}