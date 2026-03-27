package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

/**
 * 潮汐探索者技能
 * 
 * 核心定位：冒险战斗、遗迹探索
 * 技能主题：Factor能量战斗、遗迹探测
 */
public class ExplorerSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new FactorCharge(),
        new SpatialShift(),
        new TidalShield(),
        new DimensionBreak(),
        new FactorStorm()
    );
    
    /**
     * 获取所有潮汐探索者技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
    /**
     * Factor充能 - 为装备注入Factor能量，提升属性
     * 基础技能 | CD: 20秒 | Factor消耗: 150
     */
    public static class FactorCharge extends ProfessionSkill {
        
        public static final String ID = "factor_charge";
        public static final int FACTOR_COST = 150;
        public static final int COOLDOWN = 400; // 20秒
        public static final int DURATION = 600; // 30秒
        
        public FactorCharge() {
            super(ID, "Factor充能", "30秒内武器伤害+50%，护甲抗性+30%",
                  ProfessionType.EXPLORER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 1, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            SkillEffectManager.activateEffect(player, "factor_charge", DURATION);
            player.sendMessage(Text.literal("§c[Factor充能] §b你的装备充满能量！"), true);
            spawnParticles(player, ParticleTypes.ENCHANT, 25);
            playSound(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE);
            return true;
        }
    }
    
    /**
     * 空间跃迁 - 瞬间传送到Factor标记点
     * 基础技能 | CD: 15秒 | Factor消耗: 200
     */
    public static class SpatialShift extends ProfessionSkill {
        
        public static final String ID = "spatial_shift";
        public static final int FACTOR_COST = 200;
        public static final int COOLDOWN = 300; // 15秒
        public static final int MAX_RANGE = 64;
        
        public SpatialShift() {
            super(ID, "空间跃迁", "瞬移到视线落点或Factor标记点，最大64格",
                  ProfessionType.EXPLORER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 5, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // 使用射线检测获取视线落点
            Vec3d eyePos = player.getEyePos();
            Vec3d lookVec = player.getRotationVec(1.0F);
            Vec3d endPos = eyePos.add(lookVec.multiply(MAX_RANGE));
            
            RaycastContext context = new RaycastContext(
                eyePos, endPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                player
            );
            
            HitResult hit = player.getWorld().raycast(context);
            Vec3d targetPos = hit.getPos();
            
            // 安全传送
            if (player.teleport(targetPos.x, targetPos.y, targetPos.z, true)) {
                player.sendMessage(Text.literal("§c[空间跃迁] §d空间折叠！"), true);
                spawnParticles(player, ParticleTypes.REVERSE_PORTAL, 30);
                playSound(player, SoundEvents.ENTITY_ENDERMAN_TELEPORT);
                return true;
            } else {
                player.sendMessage(Text.literal("§c[空间跃迁] §7传送位置不安全"), true);
                return false;
            }
        }
    }
    
    /**
     * 潮汐护盾 - 抵挡潮汐伤害和环境负面效果
     * 基础技能 | CD: 30秒 | Factor消耗: 250
     */
    public static class TidalShield extends ProfessionSkill {
        
        public static final String ID = "tidal_shield";
        public static final int FACTOR_COST = 250;
        public static final int COOLDOWN = 600; // 30秒
        public static final int DURATION = 1200; // 1分钟
        
        public TidalShield() {
            super(ID, "潮汐护盾", "1分钟内免疫潮汐伤害和环境负面效果",
                  ProfessionType.EXPLORER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 10, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            SkillEffectManager.activateEffect(player, "tidal_shield", DURATION);
            player.sendMessage(Text.literal("§c[潮汐护盾] §9潮汐之力为你提供庇护！"), true);
            spawnParticles(player, ParticleTypes.BUBBLE, 35);
            playSound(player, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE);
            return true;
        }
    }
    
    /**
     * 维度破碎 - 大范围高伤害Factor能量攻击
     * 终极技能 | CD: 8分钟 | Factor消耗: 2000
     */
    public static class DimensionBreak extends ProfessionSkill {
        
        public static final String ID = "dimension_break";
        public static final int FACTOR_COST = 2000;
        public static final int COOLDOWN = 9600; // 8分钟
        public static final int RANGE = 16;
        public static final double DAMAGE_MULTIPLIER = 5.0;
        
        public DimensionBreak() {
            super(ID, "维度破碎", "对周围16格敌人造成5倍伤害",
                  ProfessionType.EXPLORER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 15, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现维度破碎效果
            // 需要战斗系统支持
            player.sendMessage(Text.literal("§d[终极：维度破碎] §c空间崩裂！"), true);
            spawnParticles(player, ParticleTypes.EXPLOSION_EMITTER, 20);
            playSound(player, SoundEvents.ENTITY_GENERIC_EXPLODE.value());
            return true;
        }
    }
    
    /**
     * 因子风暴 - 召唤Factor能量风暴持续攻击周围敌人
     * 终极技能 | CD: 12分钟 | Factor消耗: 2500
     */
    public static class FactorStorm extends ProfessionSkill {
        
        public static final String ID = "factor_storm";
        public static final int FACTOR_COST = 2500;
        public static final int COOLDOWN = 14400; // 12分钟
        public static final int DURATION = 600; // 30秒
        public static final int RANGE = 24;
        
        public FactorStorm() {
            super(ID, "因子风暴", "召唤30秒Factor风暴，持续攻击24格内敌人",
                  ProfessionType.EXPLORER, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 18, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 实现因子风暴效果
            // 需要战斗系统支持
            player.sendMessage(Text.literal("§d[终极：因子风暴] §bFactor能量风暴降临！"), true);
            spawnParticles(player, ParticleTypes.DRAGON_BREATH, 50);
            playSound(player, SoundEvents.ENTITY_ENDER_DRAGON_FLAP);
            return true;
        }
    }
}