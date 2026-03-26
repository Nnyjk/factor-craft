package com.factorcraft.module.profession.skill;

import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.api.ProfessionAPI;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.server.world.ServerWorld;

/**
 * 职业技能基类
 * 
 * 技能分为主动技能和被动技能两种类型
 * 主动技能需要玩家主动释放，消耗Factor能量，有冷却时间
 * 被动技能自动生效，提供常驻属性加成或触发型效果
 */
public abstract class ProfessionSkill {
    
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final ProfessionType professionType;
    protected final SkillType skillType;
    protected final int factorCost;
    protected final int cooldownTicks; // 冷却时间（tick）
    protected final int unlockLevel; // 解锁等级
    protected final boolean isUltimate; // 是否为终极技能
    
    public ProfessionSkill(String id, String displayName, String description,
                          ProfessionType professionType, SkillType skillType,
                          int factorCost, int cooldownTicks) {
        this(id, displayName, description, professionType, skillType, 
             factorCost, cooldownTicks, 1, false);
    }
    
    public ProfessionSkill(String id, String displayName, String description,
                          ProfessionType professionType, SkillType skillType,
                          int factorCost, int cooldownTicks, int unlockLevel, boolean isUltimate) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.professionType = professionType;
        this.skillType = skillType;
        this.factorCost = factorCost;
        this.cooldownTicks = cooldownTicks;
        this.unlockLevel = unlockLevel;
        this.isUltimate = isUltimate;
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
    
    public SkillType getSkillType() {
        return skillType;
    }
    
    public int getFactorCost() {
        return factorCost;
    }
    
    public int getCooldownTicks() {
        return cooldownTicks;
    }
    
    public int getUnlockLevel() {
        return unlockLevel;
    }
    
    public boolean isUltimate() {
        return isUltimate;
    }
    
    /**
     * 获取冷却时间（毫秒）
     */
    public long getCooldownMs() {
        return cooldownTicks * 50L; // 1 tick = 50ms
    }
    
    /**
     * 获取冷却时间（秒）
     */
    public int getCooldownSeconds() {
        return cooldownTicks / 20;
    }
    
    /**
     * 检查是否可以使用技能
     * @param player 玩家
     * @param professionAPI 职业API
     * @return 是否可以使用
     */
    public boolean canUse(ServerPlayerEntity player, ProfessionAPI professionAPI) {
        // 检查职业
        if (!professionAPI.getPlayerProfession(player).orElse(null).equals(professionType)) {
            return false;
        }
        
        // 检查等级
        PlayerProfessionData data = professionAPI.getPlayerData(player);
        if (data.getLevel() < unlockLevel) {
            return false;
        }
        
        // 检查冷却时间
        if (isOnCooldown(player, data)) {
            return false;
        }
        
        // 检查Factor能量
        if (!hasEnoughFactor(player, factorCost)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查技能是否在冷却中
     */
    protected boolean isOnCooldown(ServerPlayerEntity player, PlayerProfessionData data) {
        Long lastUseTime = data.getSkillCooldowns().get(id);
        if (lastUseTime == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastUseTime) < getCooldownMs();
    }
    
    /**
     * 检查玩家是否有足够的Factor能量
     */
    protected boolean hasEnoughFactor(ServerPlayerEntity player, int cost) {
        // TODO: 实现Factor能量检查
        // 暂时返回true，等待Factor系统集成
        return true;
    }
    
    /**
     * 执行技能
     * @param player 执行技能的玩家
     * @return 是否执行成功
     */
    public abstract boolean execute(ServerPlayerEntity player);
    
    /**
     * 使用技能（包含前置检查和后置处理）
     * @param player 玩家
     * @param professionAPI 职业API
     * @return 是否成功
     */
    public boolean use(ServerPlayerEntity player, ProfessionAPI professionAPI) {
        if (!canUse(player, professionAPI)) {
            return false;
        }
        
        // 执行技能
        boolean success = execute(player);
        
        if (success) {
            // 扣除Factor消耗
            consumeFactor(player);
            
            // 记录冷却时间
            PlayerProfessionData data = professionAPI.getPlayerData(player);
            data.setSkillCooldown(id, System.currentTimeMillis());
        }
        
        return success;
    }
    
    /**
     * 扣除Factor消耗
     */
    protected void consumeFactor(ServerPlayerEntity player) {
        // TODO: 实现Factor消耗
        // 等待Factor系统集成
    }
    
    /**
     * 发送消息给玩家
     */
    protected void sendMessage(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.literal(message), false);
    }
    
    /**
     * 发送技能消息
     */
    protected void sendSkillMessage(ServerPlayerEntity player, String action) {
        player.sendMessage(Text.literal("§b[" + displayName + "] §f" + action), false);
    }
    
    /**
     * 生成粒子效果
     * @param player 玩家
     * @param particle 粒子类型
     * @param count 粒子数量
     */
    protected void spawnParticles(ServerPlayerEntity player, ParticleEffect particle, int count) {
        ServerWorld world = (ServerWorld) player.getWorld();
        world.spawnParticles(particle, 
            player.getX(), player.getY() + player.getHeight() / 2, player.getZ(),
            count, 0.5, 0.5, 0.5, 0.1);
    }
    
    /**
     * 播放声音
     * @param player 玩家
     * @param sound 声音事件
     */
    protected void playSound(ServerPlayerEntity player, SoundEvent sound) {
        player.playSound(sound, 1.0f, 1.0f);
    }
}