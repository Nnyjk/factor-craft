package com.factorcraft.module.profession.api;

import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 职业系统统一API接口
 * 
 * 所有需要持久化的操作都需要传入 ServerPlayerEntity 以获取 ServerWorld
 */
public interface ProfessionAPI {
    
    // ==================== 职业查询 ====================
    
    /**
     * 获取玩家职业类型
     */
    Optional<ProfessionType> getPlayerProfession(ServerPlayerEntity player);
    
    /**
     * 获取玩家职业数据
     */
    PlayerProfessionData getPlayerData(ServerPlayerEntity player);
    
    /**
     * 检查玩家是否已选择职业
     */
    boolean hasProfession(ServerPlayerEntity player);
    
    // ==================== 职业选择 ====================
    
    /**
     * 玩家选择职业
     * @return 是否选择成功
     */
    boolean selectProfession(ServerPlayerEntity player, ProfessionType type);
    
    /**
     * 玩家切换职业
     * @return 是否切换成功
     */
    boolean changeProfession(ServerPlayerEntity player, ProfessionType newType);
    
    // ==================== 经验与等级 ====================
    
    /**
     * 获取玩家等级
     */
    int getLevel(ServerPlayerEntity player);
    
    /**
     * 获取玩家经验值
     */
    int getExperience(ServerPlayerEntity player);
    
    /**
     * 添加经验值
     */
    void addExperience(ServerPlayerEntity player, int amount, String source);
    
    /**
     * 获取升级所需经验
     */
    int getExperienceForLevel(int level);
    
    // ==================== 属性系统 ====================
    
    /**
     * 获取职业属性加成
     */
    Map<String, Double> getProfessionAttributes(ProfessionType type, int level);
    
    /**
     * 获取指定属性的加成值
     */
    double getAttributeBonus(ServerPlayerEntity player, String attributeKey);
    
    // ==================== 天赋点 ====================
    
    /**
     * 获取天赋点
     */
    int getTalentPoints(ServerPlayerEntity player);
    
    /**
     * 消耗天赋点
     */
    boolean spendTalentPoints(ServerPlayerEntity player, int points);
    
    // ==================== 技能冷却 ====================
    
    /**
     * 获取技能冷却时间（毫秒）
     */
    long getSkillCooldown(ServerPlayerEntity player, String skillId);
    
    /**
     * 设置技能冷却
     */
    void setSkillCooldown(ServerPlayerEntity player, String skillId, long cooldownMs);
    
    /**
     * 检查技能是否可用
     */
    boolean isSkillReady(ServerPlayerEntity player, String skillId);
    
    // ==================== 注册 ====================
    
    /**
     * 注册职业类型
     */
    void registerProfession(ProfessionType type);
    
    /**
     * 获取所有已注册的职业
     */
    Map<String, ProfessionType> getRegisteredProfessions();
    
    /**
     * 根据ID获取职业
     */
    Optional<ProfessionType> getProfessionById(String id);
}