package com.factorcraft.module.profession.api;

import com.factorcraft.module.profession.HiddenProfessionUnlockManager;
import com.factorcraft.module.profession.config.ProfessionConfig;
import com.factorcraft.module.profession.config.ProfessionConfigLoader;
import com.factorcraft.module.profession.data.ProfessionDataStorage;
import com.factorcraft.module.profession.event.*;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 职业系统API实现
 */
public class ProfessionServiceImpl implements ProfessionAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionAPI");
    
    private final Map<String, ProfessionType> registeredProfessions = new HashMap<>();
    private final ProfessionEventBus eventBus = ProfessionEventBus.getInstance();
    
    // ==================== 职业查询 ====================
    
    @Override
    public Optional<ProfessionType> getPlayerProfession(ServerPlayerEntity player) {
        PlayerProfessionData data = getPlayerData(player);
        return Optional.ofNullable(data.getProfessionType());
    }
    
    @Override
    public PlayerProfessionData getPlayerData(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        ProfessionDataStorage storage = ProfessionDataStorage.get(world);
        return storage.getPlayerData(player.getUuid());
    }
    
    @Override
    public boolean hasProfession(ServerPlayerEntity player) {
        return getPlayerData(player).hasProfession();
    }
    
    // ==================== 职业选择 ====================
    
    @Override
    public boolean selectProfession(ServerPlayerEntity player, ProfessionType type) {
        PlayerProfessionData data = getPlayerData(player);
        
        if (data.hasProfession()) {
            LOGGER.debug("玩家 {} 已有职业，无法选择新职业", player.getName().getString());
            return false;
        }
        
        // 触发选择前事件
        ProfessionSelectEvent.Pre preEvent = new ProfessionSelectEvent.Pre(player, type);
        if (!eventBus.post(preEvent)) {
            return false;
        }
        
        data.setProfessionType(type);
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        
        // 触发选择后事件
        ProfessionSelectEvent.Post postEvent = new ProfessionSelectEvent.Post(player, type);
        eventBus.post(postEvent);
        
        LOGGER.info("玩家 {} 选择了职业: {}", player.getName().getString(), type.getDisplayName());
        return true;
    }
    
    @Override
    public boolean changeProfession(ServerPlayerEntity player, ProfessionType newType) {
        PlayerProfessionData data = getPlayerData(player);
        ProfessionType oldType = data.getProfessionType();
        
        if (oldType == null) {
            return selectProfession(player, newType);
        }
        
        // 触发切换前事件
        ProfessionSelectEvent.Pre preEvent = new ProfessionSelectEvent.Pre(player, newType);
        preEvent.setChanging(true);
        if (!eventBus.post(preEvent)) {
            return false;
        }
        
        data.setProfessionType(newType);
        // 切换职业时重置等级和经验
        data.setLevel(1);
        data.setExperience(0);
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        
        // 触发切换后事件
        ProfessionSelectEvent.Post postEvent = new ProfessionSelectEvent.Post(player, newType);
        postEvent.setChanging(true);
        eventBus.post(postEvent);
        
        LOGGER.info("玩家 {} 从 {} 切换到职业: {}", 
            player.getName().getString(), 
            oldType.getDisplayName(), 
            newType.getDisplayName());
        return true;
    }
    
    // ==================== 经验与等级 ====================
    
    @Override
    public int getLevel(ServerPlayerEntity player) {
        return getPlayerData(player).getLevel();
    }
    
    @Override
    public int getExperience(ServerPlayerEntity player) {
        return getPlayerData(player).getExperience();
    }
    
    @Override
    public void addExperience(ServerPlayerEntity player, int amount, String source) {
        PlayerProfessionData data = getPlayerData(player);
        int oldLevel = data.getLevel();
        
        // 触发经验获取前事件
        ProfessionExperienceGainEvent.Pre preEvent = new ProfessionExperienceGainEvent.Pre(player, amount, source);
        if (!eventBus.post(preEvent)) {
            return;
        }
        amount = preEvent.getAmount();
        
        data.addExperience(amount);
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        
        // 触发经验获取后事件
        ProfessionExperienceGainEvent.Post postEvent = new ProfessionExperienceGainEvent.Post(player, amount, source);
        eventBus.post(postEvent);
        
        // 检查升级
        int newLevel = data.getLevel();
        if (newLevel > oldLevel) {
            ProfessionLevelUpEvent levelUpEvent = new ProfessionLevelUpEvent(player, oldLevel, newLevel);
            eventBus.post(levelUpEvent);
            LOGGER.info("玩家 {} 职业升级: {} -> {}", player.getName().getString(), oldLevel, newLevel);
        }
    }
    
    @Override
    public int getExperienceForLevel(int level) {
        // 指数增长经验需求: 100 * (level^1.5)
        return (int) (100 * Math.pow(level, 1.5));
    }
    
    // ==================== 属性系统 ====================
    
    @Override
    public Map<String, Double> getProfessionAttributes(ProfessionType type, int level) {
        Map<String, Double> attributes = new HashMap<>();
        
        // 从配置加载基础属性
        ProfessionConfig config = ProfessionConfigLoader.getConfig(type.getId());
        if (config != null && config.getBaseAttributes() != null) {
            attributes.putAll(config.getBaseAttributes());
        }
        
        // 根据等级增强属性（每级增加1%）
        double levelBonus = 1.0 + (level - 1) * 0.01;
        attributes.replaceAll((k, v) -> v * levelBonus);
        
        return attributes;
    }
    
    @Override
    public double getAttributeBonus(ServerPlayerEntity player, String attributeKey) {
        PlayerProfessionData data = getPlayerData(player);
        if (!data.hasProfession()) {
            return 1.0;
        }
        
        Map<String, Double> attributes = getProfessionAttributes(data.getProfessionType(), data.getLevel());
        return attributes.getOrDefault(attributeKey, 1.0);
    }
    
    // ==================== 天赋点 ====================
    
    @Override
    public int getTalentPoints(ServerPlayerEntity player) {
        return getPlayerData(player).getTalentPoints();
    }
    
    @Override
    public boolean spendTalentPoints(ServerPlayerEntity player, int points) {
        PlayerProfessionData data = getPlayerData(player);
        if (data.getTalentPoints() < points) {
            return false;
        }
        
        data.setTalentPoints(data.getTalentPoints() - points);
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        return true;
    }
    
    // ==================== 技能冷却 ====================
    
    @Override
    public long getSkillCooldown(ServerPlayerEntity player, String skillId) {
        PlayerProfessionData data = getPlayerData(player);
        long lastUse = data.getSkillLastUseTime(skillId);
        if (lastUse == 0) {
            return 0;
        }
        
        // 从配置获取技能冷却时间，默认60秒
        long cooldownMs = getSkillCooldownFromConfig(skillId);
        long elapsed = System.currentTimeMillis() - lastUse;
        return Math.max(0, cooldownMs - elapsed);
    }
    
    @Override
    public void setSkillCooldown(ServerPlayerEntity player, String skillId, long cooldownMs) {
        PlayerProfessionData data = getPlayerData(player);
        data.setSkillLastUseTime(skillId, System.currentTimeMillis());
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
    }
    
    @Override
    public boolean isSkillReady(ServerPlayerEntity player, String skillId) {
        return getSkillCooldown(player, skillId) <= 0;
    }
    
    // ==================== 注册 ====================
    
    @Override
    public void registerProfession(ProfessionType type) {
        if (registeredProfessions.containsKey(type.getId())) {
            LOGGER.warn("职业 {} 已注册，跳过", type.getId());
            return;
        }
        registeredProfessions.put(type.getId(), type);
        LOGGER.info("注册职业: {} ({})", type.getDisplayName(), type.getId());
    }
    
    @Override
    public Map<String, ProfessionType> getRegisteredProfessions() {
        return Collections.unmodifiableMap(registeredProfessions);
    }
    
    @Override
    public Optional<ProfessionType> getProfessionById(String id) {
        return Optional.ofNullable(registeredProfessions.get(id));
    }
    
    // ==================== 隐藏职业系统 ====================
    
    @Override
    public boolean canUnlockHiddenProfession(ServerPlayerEntity player) {
        return HiddenProfessionUnlockManager.canUnlock(player, this);
    }
    
    @Override
    public boolean hasUnlockedHiddenProfession(ServerPlayerEntity player) {
        return HiddenProfessionUnlockManager.isUnlocked(player, this);
    }
    
    @Override
    public String unlockHiddenProfession(ServerPlayerEntity player) {
        HiddenProfessionUnlockManager.UnlockResult result = HiddenProfessionUnlockManager.tryUnlock(player, this);
        if (result.success) {
            ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        }
        return result.message;
    }
    
    @Override
    public int getMasteredProfessionCount(ServerPlayerEntity player) {
        PlayerProfessionData data = getPlayerData(player);
        return data.getMasteredProfessionCount();
    }
    
    @Override
    public void recordRareFactorCollected(ServerPlayerEntity player, String factorId) {
        HiddenProfessionUnlockManager.recordRareFactor(player, this, factorId);
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
    }
    
    @Override
    public int getCollectedRareFactorCount(ServerPlayerEntity player) {
        PlayerProfessionData data = getPlayerData(player);
        return data.getCollectedRareFactorCount();
    }
    
    // ==================== 私有辅助方法 ====================
    
    private long getSkillCooldownFromConfig(String skillId) {
        // 默认冷却时间60秒
        return 60_000L;
    }
}