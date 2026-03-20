package com.factorcraft.module.profession;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.skill.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * ProfessionModule - 职业系统模块
 * 
 * 三大核心职业体系：
 * - 创生师（GENESIS）：生产建造核心、团队辅助
 * - 湮灭使（ANNIHILATION）：战斗探索核心、资源采集
 * - 锻铸匠（FORGE）：加工制造核心、装备强化
 */
public final class ProfessionModule implements FactorCraftModule {
    
    private static ProfessionModule instance;
    
    // 玩家职业数据缓存
    private final Map<UUID, PlayerProfessionData> playerDataMap = new HashMap<>();
    
    // 技能注册表
    private final Map<String, ProfessionSkill> skillRegistry = new HashMap<>();
    
    public static ProfessionModule getInstance() {
        if (instance == null) {
            instance = new ProfessionModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "profession";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of("factor_system"); // 依赖 Factor 系统
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 正在初始化职业系统...");
        
        // 注册技能
        registerSkills();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 职业系统已初始化");
    }
    
    private void registerSkills() {
        // 注册创生师技能
        registerSkill(new GenesisSkills.GenesisPulse());
        registerSkill(new GenesisSkills.DomainExpansion());
        registerSkill(new GenesisSkills.MatterReconstruction());
        
        // 注册湮灭使技能
        registerSkill(new AnnihilationSkills.AnnihilationSlash());
        registerSkill(new AnnihilationSkills.FactorDevour());
        registerSkill(new AnnihilationSkills.VoidStep());
        
        // 注册锻铸匠技能
        registerSkill(new ForgeSkills.InstantProcess());
        registerSkill(new ForgeSkills.PerfectForge());
        registerSkill(new ForgeSkills.EquipmentMaster());
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 已注册 {} 个技能", skillRegistry.size());
    }
    
    private void registerSkill(ProfessionSkill skill) {
        skillRegistry.put(skill.getId(), skill);
    }
    
    /**
     * 获取玩家职业数据
     */
    public PlayerProfessionData getPlayerData(ServerPlayerEntity player) {
        return playerDataMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerProfessionData());
    }
    
    /**
     * 玩家选择职业
     */
    public boolean selectProfession(ServerPlayerEntity player, ProfessionType type) {
        PlayerProfessionData data = getPlayerData(player);
        
        // 检查是否已有职业
        if (data.hasProfession()) {
            return false; // 已选择职业，不可更改
        }
        
        data.setProfessionType(type);
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 玩家 {} 选择了职业: {}", 
            player.getName().getString(), type.getDisplayName());
        
        return true;
    }
    
    /**
     * 使用技能
     */
    public boolean useSkill(ServerPlayerEntity player, String skillId) {
        PlayerProfessionData data = getPlayerData(player);
        
        // 检查职业
        if (!data.hasProfession()) {
            return false;
        }
        
        // 获取技能
        ProfessionSkill skill = skillRegistry.get(skillId);
        if (skill == null) {
            return false;
        }
        
        // 检查职业匹配
        if (skill.getProfessionType() != data.getProfessionType()) {
            return false;
        }
        
        // 检查是否可用
        if (!skill.canUse(player)) {
            return false;
        }
        
        // 执行技能
        skill.execute(player);
        
        return true;
    }
    
    /**
     * 获取技能
     */
    public ProfessionSkill getSkill(String skillId) {
        return skillRegistry.get(skillId);
    }
    
    /**
     * 获取职业所有技能
     */
    public List<ProfessionSkill> getProfessionSkills(ProfessionType type) {
        List<ProfessionSkill> skills = new ArrayList<>();
        for (ProfessionSkill skill : skillRegistry.values()) {
            if (skill.getProfessionType() == type) {
                skills.add(skill);
            }
        }
        return skills;
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 重新加载职业配置...");
    }
}