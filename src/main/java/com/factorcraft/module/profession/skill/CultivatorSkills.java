package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.world.event.GameEvent;

import java.util.List;

/**
 * 能量培育师技能
 * 
 * 核心定位：生物养成、变异培育
 * 技能主题：Factor生物培育、变异生物养殖
 */
public class CultivatorSkills {
    
    private static final List<ProfessionSkill> ALL_SKILLS = List.of(
        new GrowthCatalyst(),
        new MutationInduce(),
        new SoulLink(),
        new HarvestMoment(),
        new BioEmpire()
    );
    
    /**
     * 获取所有能量培育师技能
     */
    public static List<ProfessionSkill> getAllSkills() {
        return ALL_SKILLS;
    }
    
    /**
     * 生长催化 - 瞬间催熟范围内所有植物/生物
     * 基础技能 | CD: 10秒 | Factor消耗: 150
     */
    public static class GrowthCatalyst extends ProfessionSkill {
        
        public static final String ID = "growth_catalyst";
        public static final int FACTOR_COST = 150;
        public static final int COOLDOWN = 200; // 10秒
        public static final int RANGE = 24;
        
        public GrowthCatalyst() {
            super(ID, "生长催化", "瞬间催熟24格内所有植物和生物",
                  ProfessionType.CULTIVATOR, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 1, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            World world = player.getWorld();
            BlockPos center = player.getBlockPos();
            int affectedCount = 0;
            
            // 遍历范围内的方块
            for (int x = -RANGE; x <= RANGE; x++) {
                for (int y = -RANGE / 2; y <= RANGE / 2; y++) {
                    for (int z = -RANGE; z <= RANGE; z++) {
                        BlockPos pos = center.add(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        
                        // 如果是可以催熟的植物
                        if (state.getBlock() instanceof Fertilizable fertilizable) {
                            if (fertilizable.isFertilizable(world, pos, state)) {
                                fertilizable.grow((net.minecraft.server.world.ServerWorld) world, world.random, pos, state);
                                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player));
                                affectedCount++;
                            }
                        }
                    }
                }
            }
            
            if (affectedCount > 0) {
                player.sendMessage(Text.literal("§a[生长催化] §e" + affectedCount + " 个植物被催熟！"), true);
                spawnParticles(player, ParticleTypes.HAPPY_VILLAGER, 30);
                playSound(player, SoundEvents.BLOCK_CROP_BREAK);
                return true;
            } else {
                player.sendMessage(Text.literal("§7[生长催化] 周围没有可催熟的植物"), true);
                return false;
            }
        }
    }
    
    /**
     * 变异诱导 - 提升范围内生物变异概率100%，持续1分钟
     * 基础技能 | CD: 1分钟 | Factor消耗: 300
     */
    public static class MutationInduce extends ProfessionSkill {
        
        public static final String ID = "mutation_induce";
        public static final int FACTOR_COST = 300;
        public static final int COOLDOWN = 1200; // 1分钟
        public static final int DURATION = 1200; // 1分钟
        public static final int RANGE = 32;
        
        public MutationInduce() {
            super(ID, "变异诱导", "1分钟内周围32格变异概率翻倍",
                  ProfessionType.CULTIVATOR, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 5, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            SkillEffectManager.activateEffect(player, "mutation_induce", DURATION);
            player.sendMessage(Text.literal("§a[变异诱导] §d变异能量弥漫开来！持续1分钟"), true);
            spawnParticles(player, ParticleTypes.PORTAL, 25);
            playSound(player, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL);
            return true;
        }
    }
    
    /**
     * 灵魂链接 - 与绑定生物共享生命值和Factor能量
     * 基础技能 | CD: 5秒 | Factor消耗: 100
     */
    public static class SoulLink extends ProfessionSkill {
        
        public static final String ID = "soul_link";
        public static final int FACTOR_COST = 100;
        public static final int COOLDOWN = 100; // 5秒
        public static final int DURATION = 6000; // 5分钟
        
        public SoulLink() {
            super(ID, "灵魂链接", "与目标生物建立灵魂链接，共享生命和能量",
                  ProfessionType.CULTIVATOR, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 10, false);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 需要生物绑定系统支持
            SkillEffectManager.activateEffect(player, "soul_link", DURATION);
            player.sendMessage(Text.literal("§a[灵魂链接] §c灵魂链接已激活！请右键目标生物绑定"), true);
            spawnParticles(player, ParticleTypes.SOUL, 20);
            playSound(player, SoundEvents.PARTICLE_SOUL_ESCAPE.value());
            return true;
        }
    }
    
    /**
     * 丰收时刻 - 瞬间收获范围内所有成熟作物/生物产物
     * 终极技能 | CD: 10分钟 | Factor消耗: 1000
     */
    public static class HarvestMoment extends ProfessionSkill {
        
        public static final String ID = "harvest_moment";
        public static final int FACTOR_COST = 1000;
        public static final int COOLDOWN = 12000; // 10分钟
        public static final int RANGE = 48;
        
        public HarvestMoment() {
            super(ID, "丰收时刻", "瞬间收获48格内所有成熟产物，无损耗",
                  ProfessionType.CULTIVATOR, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 15, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 需要作物收获系统支持
            player.sendMessage(Text.literal("§d[终极：丰收时刻] §6大丰收！"), true);
            spawnParticles(player, ParticleTypes.TOTEM_OF_UNDYING, 40);
            playSound(player, SoundEvents.BLOCK_BELL_USE);
            return true;
        }
    }
    
    /**
     * 生物帝国 - 召唤所有已培育的Factor生物协助作战/生产
     * 终极技能 | CD: 15分钟 | Factor消耗: 3000
     */
    public static class BioEmpire extends ProfessionSkill {
        
        public static final String ID = "bio_empire";
        public static final int FACTOR_COST = 3000;
        public static final int COOLDOWN = 18000; // 15分钟
        public static final int SUMMON_COUNT = 10;
        
        public BioEmpire() {
            super(ID, "生物帝国", "召唤10只Factor生物协助战斗和生产",
                  ProfessionType.CULTIVATOR, SkillType.ACTIVE, FACTOR_COST, COOLDOWN, 18, true);
        }
        
        @Override
        public boolean execute(ServerPlayerEntity player) {
            // TODO: 需要生物召唤系统支持
            player.sendMessage(Text.literal("§d[终极：生物帝国] §a你的生物军团已苏醒！"), true);
            spawnParticles(player, ParticleTypes.DRAGON_BREATH, 50);
            playSound(player, SoundEvents.ENTITY_ENDER_DRAGON_GROWL);
            return true;
        }
    }
}