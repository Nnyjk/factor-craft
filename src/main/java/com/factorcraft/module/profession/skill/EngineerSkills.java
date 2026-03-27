package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/**
 * Factor工程师技能
 * 
 * 核心定位：生产建造、自动化工厂
 * 技能主题：机器效率、Factor产量优化
 */
public class EngineerSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new Overclock(),
        new RemoteBuild(),
        new EnergyBurst(),
        new FactoryWill(),
        new StructureReconstruct()
    );
    
    /**
     * 获取所有Factor工程师技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
    /**
     * 超频 - 10秒内所有附近机器工作速度提升100%，Factor消耗提升50%
     * 基础技能 | CD: 30秒 | Factor消耗: 200
     */
    public static class Overclock extends ProfessionSkill {
        
        public static final String ID = "overclock";
        public static final int FACTOR_COST = 200;
        public static final int COOLDOWN = 600; // 30秒
        public static final int DURATION = 200; // 10秒
        public static final int RANGE = 32;
        
        public Overclock() {
            super(ID, "超频", "10秒内周围32格机器速度+100%，Factor消耗+50%",
                  ProfessionType.ENGINEER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 1, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            int affectedMachines = SkillEffectManager.executeOverclock(player, RANGE, DURATION);
            
            if (affectedMachines > 0) {
                player.sendMessage(Text.literal("§b[超频] §a" + affectedMachines + " 台机器效率提升100%！"), true);
                spawnParticles(player, ParticleTypes.ELECTRIC_SPARK, 20);
                playSound(player, SoundEvents.BLOCK_BEACON_POWER_SELECT);
                return true;
            } else {
                player.sendMessage(Text.literal("§c[超频] 周围没有可影响的机器"), true);
                return false;
            }
        }
    }
    
    /**
     * 远程构建 - 允许玩家在10格距离外放置方块
     * 基础技能 | CD: 20秒 | Factor消耗: 100
     */
    public static class RemoteBuild extends ProfessionSkill {
        
        public static final String ID = "remote_build";
        public static final int FACTOR_COST = 100;
        public static final int COOLDOWN = 400; // 20秒
        public static final int DURATION = 200; // 10秒
        public static final int RANGE = 10;
        
        public RemoteBuild() {
            super(ID, "远程构建", "激活后10秒内可远程放置方块",
                  ProfessionType.ENGINEER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 5, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            SkillEffectManager.activateRemoteBuild(player, DURATION);
            player.sendMessage(Text.literal("§b[远程构建] §a已激活远程放置能力！持续10秒"), true);
            spawnParticles(player, ParticleTypes.END_ROD, 15);
            playSound(player, SoundEvents.ENTITY_ENDERMAN_TELEPORT);
            return true;
        }
    }
    
    /**
     * 能量爆发 - 瞬间释放存储的Factor，为周围所有机器充满能量
     * 基础技能 | CD: 1分钟 | Factor消耗: 500
     */
    public static class EnergyBurst extends ProfessionSkill {
        
        public static final String ID = "energy_burst";
        public static final int FACTOR_COST = 500;
        public static final int COOLDOWN = 1200; // 1分钟
        public static final int RANGE = 48;
        
        public EnergyBurst() {
            super(ID, "能量爆发", "为周围48格内所有机器充满Factor能量",
                  ProfessionType.ENGINEER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 10, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            int chargedMachines = SkillEffectManager.executeEnergyBurst(player, RANGE);
            
            if (chargedMachines > 0) {
                player.sendMessage(Text.literal("§b[能量爆发] §a已为 " + chargedMachines + " 台机器充满能量！"), true);
                spawnParticles(player, ParticleTypes.TOTEM_OF_UNDYING, 30);
                playSound(player, SoundEvents.BLOCK_BEACON_ACTIVATE);
                return true;
            } else {
                player.sendMessage(Text.literal("§c[能量爆发] 周围没有需要充能的机器"), true);
                return false;
            }
        }
    }
    
    /**
     * 工厂意志 - 5分钟内所有已放置的机器无需消耗Factor工作
     * 终极技能 | CD: 10分钟 | Factor消耗: 2000
     */
    public static class FactoryWill extends ProfessionSkill {
        
        public static final String ID = "factory_will";
        public static final int FACTOR_COST = 2000;
        public static final int COOLDOWN = 12000; // 10分钟
        public static final int DURATION = 6000; // 5分钟
        
        public FactoryWill() {
            super(ID, "工厂意志", "5分钟内所有机器无需消耗Factor工作",
                  ProfessionType.ENGINEER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 15, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            SkillEffectManager.activateFactoryWill(player, DURATION);
            player.sendMessage(Text.literal("§d[终极：工厂意志] §a工厂进入自由运转模式！持续5分钟"), true);
            spawnParticles(player, ParticleTypes.DRAGON_BREATH, 50);
            playSound(player, SoundEvents.BLOCK_END_PORTAL_SPAWN);
            return true;
        }
    }
    
    /**
     * 结构重构 - 瞬间修复所有损坏的多方块结构，恢复至满状态
     * 终极技能 | CD: 8分钟 | Factor消耗: 1500
     */
    public static class StructureReconstruct extends ProfessionSkill {
        
        public static final String ID = "structure_reconstruct";
        public static final int FACTOR_COST = 1500;
        public static final int COOLDOWN = 9600; // 8分钟
        public static final int RANGE = 64;
        
        public StructureReconstruct() {
            super(ID, "结构重构", "瞬间修复64格内所有损坏的多方块结构",
                  ProfessionType.ENGINEER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 18, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现结构重构效果
            // 需要多方块结构系统支持
            player.sendMessage(Text.literal("§d[终极：结构重构] §a所有结构已修复！"), true);
            spawnParticles(player, ParticleTypes.REVERSE_PORTAL, 40);
            playSound(player, SoundEvents.ENTITY_WITHER_SPAWN);
            return true;
        }
    }
}