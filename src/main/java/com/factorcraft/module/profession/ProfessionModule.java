package com.factorcraft.module.profession;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.api.ProfessionServiceImpl;
import com.factorcraft.module.profession.command.ProfessionCommand;
import com.factorcraft.module.profession.config.ProfessionConfigLoader;
import com.factorcraft.module.profession.event.ProfessionEventBus;
import com.factorcraft.module.profession.event.ProfessionLevelUpNotifier;
import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.registry.ProfessionRegistry;
import com.factorcraft.module.profession.screen.ProfessionScreens;
import com.factorcraft.module.profession.skill.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * ProfessionModule - 职业系统模块
 * 
 * 四大职业体系：
 * 
 * 基础职业（开局可选）：
 * - Factor工程师（ENGINEER）：生产建造核心、自动化工厂
 * - 能量培育师（CULTIVATOR）：生物养成核心、变异培育
 * - 潮汐探索者（EXPLORER）：冒险战斗核心、遗迹探索
 * 
 * 隐藏职业（特殊条件解锁）：
 * - 因子掌控者（MASTER）：全能型、融合三职业技能
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
        
        // 注册 ScreenHandlerType
        ProfessionScreens.init();
        
        // 命令注册已移至 FactorCraftMod.java 统一处理
        
        // 注册职业配置加载器
        ProfessionConfigLoader.init();
        
        // 注册事件总线
        ProfessionEventBus.init();
        
        // 注册升级通知处理器
        ProfessionLevelUpNotifier.getInstance().register();
        
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
        // Factor工程师技能
        for (ProfessionSkill skill : EngineerSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
        
        // 能量培育师技能
        for (ProfessionSkill skill : CultivatorSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
        
        // 潮汐探索者技能
        for (ProfessionSkill skill : ExplorerSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册技能: {}", skill.getId());
        }
        
        // 因子掌控者技能（隐藏职业）
        for (ProfessionSkill skill : MasterSkills.getAllSkills()) {
            skillRegistry.put(skill.getId(), skill);
            FactorCraftMod.LOGGER.debug("[FactorCraft:Profession] 注册隐藏职业技能: {}", skill.getId());
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
        
        // 检查职业是否匹配
        ProfessionType playerProfession = api.getPlayerProfession(player).orElse(null);
        if (playerProfession != skill.getProfessionType()) {
            // 因子掌控者可以使用所有基础职业技能
            if (playerProfession != ProfessionType.MASTER || skill.getProfessionType().isHidden()) {
                return false;
            }
        }
        
        // 检查等级是否足够
        int playerLevel = api.getLevel(player);
        if (playerLevel < skill.getUnlockLevel()) {
            return false;
        }
        
        // 检查冷却
        if (!api.isSkillReady(player, skillId)) {
            return false;
        }
        
        // 检查Factor能量是否足够
        // TODO: 实现Factor能量检查
        
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
    
    /**
     * 检查玩家是否可以解锁隐藏职业
     * 条件：完成主线任务"因子融合"，3个基础职业均达到10级
     */
    public boolean canUnlockMaster(ServerPlayerEntity player) {
        // TODO: 实现隐藏职业解锁条件检查
        // 1. 检查是否完成主线任务"因子融合"
        // 2. 检查3个基础职业是否都达到10级
        return false;
    }
}