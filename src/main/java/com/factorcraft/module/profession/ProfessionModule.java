package com.factorcraft.module.profession;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.api.ProfessionServiceImpl;
import com.factorcraft.module.profession.config.ProfessionConfigLoader;
import com.factorcraft.module.profession.event.ProfessionEventBus;
import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.registry.ProfessionRegistry;
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
    
    // API实现
    private final ProfessionAPI api;
    
    // 技能注册表
    private final Map<String, ProfessionSkill> skillRegistry = new HashMap<>();
    
    public static ProfessionModule getInstance() {
        if (instance == null) {
            synchronized (ProfessionModule.class) {
                if (instance == null) {
                    instance = new ProfessionModule();
                }
            }
        }
        return instance;
    }
    
    private ProfessionModule() {
        this.api = new ProfessionServiceImpl();
    }
    
    @Override
    public String moduleId() {
        return "profession";
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 初始化职业系统模块...");
        
        // 注册职业配置加载器
        ProfessionConfigLoader.init();
        
        // 注册事件总线
        ProfessionEventBus.init();
        
        // 注册技能
        registerSkills();
        
        // 加载配置
        ProfessionRegistry.getInstance().loadConfigs();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 职业系统模块初始化完成");
    }
    
    /**
     * 注册所有技能
     */
    private void registerSkills() {
        // 创生师技能
        for (ProfessionSkill skill : GenesisSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
        
        // 湮灭使技能
        for (ProfessionSkill skill : AnnihilationSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
        
        // 锻铸匠技能
        for (ProfessionSkill skill : ForgeSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
    }
    
    // ==================== 公开API ====================
    
    /**
     * 获取职业API
     */
    public ProfessionAPI getAPI() {
        return api;
    }
    
    /**
     * 玩家选择职业
     */
    public boolean selectProfession(ServerPlayerEntity player, ProfessionType type) {
        return api.selectProfession(player, type);
    }
    
    /**
     * 获取玩家职业数据（通过API）
     */
    public com.factorcraft.module.profession.model.PlayerProfessionData getPlayerData(ServerPlayerEntity player) {
        return api.getPlayerData(player);
    }
    
    /**
     * 使用技能
     */
    public boolean useSkill(ServerPlayerEntity player, String skillId) {
        ProfessionSkill skill = skillRegistry.get(skillId);
        if (skill == null) {
            return false;
        }
        
        // 检查冷却
        if (!api.isSkillReady(player, skillId)) {
            return false;
        }
        
        // 执行技能
        boolean success = skill.execute(player);
        if (success) {
            api.setSkillCooldown(player, skillId, skill.getCooldownMs());
        }
        
        return success;
    }
    
    /**
     * 获取技能注册表
     */
    public Map<String, ProfessionSkill> getSkillRegistry() {
        return Collections.unmodifiableMap(skillRegistry);
    }
    
    /**
     * 获取指定职业的所有技能
     */
    public List<ProfessionSkill> getSkillsForProfession(ProfessionType type) {
        List<ProfessionSkill> skills = new ArrayList<>();
        for (ProfessionSkill skill : skillRegistry.values()) {
            if (skill.getProfessionType() == type) {
                skills.add(skill);
            }
        }
        return skills;
    }
}