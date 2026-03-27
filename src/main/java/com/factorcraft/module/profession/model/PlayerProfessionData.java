package com.factorcraft.module.profession.model;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 玩家职业数据
 * 
 * 存储玩家的职业信息、经验、天赋点等
 */
public class PlayerProfessionData {
    
    private ProfessionType professionType;
    private int level;
    private int experience;
    private int talentPoints;
    
    // 技能冷却时间存储（技能ID -> 上次使用时间戳）
    private final Map<String, Long> skillCooldowns = new HashMap<>();
    
    // ==================== 隐藏职业系统 ====================
    
    /** 已满级的职业（用于隐藏职业解锁检测） */
    private final Set<ProfessionType> masteredProfessions = new HashSet<>();
    
    /** 隐藏职业是否已解锁 */
    private boolean hiddenProfessionUnlocked = false;
    
    /** 转职到隐藏职业时保留的历史天赋点 */
    private int historicalTalentPoints = 0;
    
    /** 已收集的稀有 Factor 类型（用于解锁检测） */
    private final Set<String> collectedRareFactors = new HashSet<>();
    
    public PlayerProfessionData() {
        this.professionType = null;
        this.level = 1;
        this.experience = 0;
        this.talentPoints = 0;
    }
    
    public boolean hasProfession() {
        return professionType != null;
    }
    
    public ProfessionType getProfessionType() {
        return professionType;
    }
    
    public void setProfessionType(ProfessionType professionType) {
        this.professionType = professionType;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(level, 100));
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void addExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }
    
    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }
    
    public int getTalentPoints() {
        return talentPoints;
    }
    
    public void addTalentPoints(int points) {
        this.talentPoints += points;
    }
    
    public void setTalentPoints(int talentPoints) {
        this.talentPoints = Math.max(0, talentPoints);
    }
    
    public boolean useTalentPoints(int points) {
        if (talentPoints >= points) {
            talentPoints -= points;
            return true;
        }
        return false;
    }
    
    private void checkLevelUp() {
        int expNeeded = getExperienceNeededForLevel(level + 1);
        while (experience >= expNeeded && level < 100) {
            experience -= expNeeded;
            level++;
            talentPoints += 1; // 每升一级获得1天赋点
            expNeeded = getExperienceNeededForLevel(level + 1);
        }
    }
    
    public static int getExperienceNeededForLevel(int level) {
        return level * level * 100; // 简单的经验公式
    }
    
    // ==================== 技能冷却 ====================
    
    /**
     * 获取技能上次使用时间
     */
    public long getSkillLastUseTime(String skillId) {
        return skillCooldowns.getOrDefault(skillId, 0L);
    }
    
    /**
     * 设置技能上次使用时间
     */
    public void setSkillLastUseTime(String skillId, long time) {
        skillCooldowns.put(skillId, time);
    }
    
    /**
     * 获取所有技能冷却数据
     */
    public Map<String, Long> getSkillCooldowns() {
        return skillCooldowns;
    }
    
    /**
     * 设置技能冷却（简化方法）
     */
    public void setSkillCooldown(String skillId, long time) {
        setSkillLastUseTime(skillId, time);
    }
    
    /**
     * 清除技能冷却
     */
    public void clearSkillCooldown(String skillId) {
        skillCooldowns.remove(skillId);
    }
    
    /**
     * 清除所有技能冷却
     */
    public void clearAllSkillCooldowns() {
        skillCooldowns.clear();
    }
    
    // ==================== 隐藏职业系统方法 ====================
    
    /**
     * 添加已满级的职业
     */
    public void addMasteredProfession(ProfessionType type) {
        if (type != null && !type.isHidden()) {
            masteredProfessions.add(type);
        }
    }
    
    /**
     * 检查职业是否已满级
     */
    public boolean hasMasteredProfession(ProfessionType type) {
        return masteredProfessions.contains(type);
    }
    
    /**
     * 获取已满级的职业数量
     */
    public int getMasteredProfessionCount() {
        return masteredProfessions.size();
    }
    
    /**
     * 获取所有已满级的职业
     */
    public Set<ProfessionType> getMasteredProfessions() {
        return masteredProfessions;
    }
    
    /**
     * 检查是否可以解锁隐藏职业
     * 需要满足：3个基础职业全部满级
     */
    public boolean canUnlockHiddenProfession() {
        return hasMasteredProfession(ProfessionType.ENGINEER) &&
               hasMasteredProfession(ProfessionType.CULTIVATOR) &&
               hasMasteredProfession(ProfessionType.EXPLORER);
    }
    
    /**
     * 检查隐藏职业是否已解锁
     */
    public boolean isHiddenProfessionUnlocked() {
        return hiddenProfessionUnlocked;
    }
    
    /**
     * 解锁隐藏职业
     */
    public void unlockHiddenProfession() {
        this.hiddenProfessionUnlocked = true;
    }
    
    /**
     * 获取历史天赋点
     */
    public int getHistoricalTalentPoints() {
        return historicalTalentPoints;
    }
    
    /**
     * 设置历史天赋点（转职时保留的天赋点）
     */
    public void setHistoricalTalentPoints(int points) {
        this.historicalTalentPoints = points;
    }
    
    /**
     * 添加已收集的稀有 Factor
     */
    public void addCollectedRareFactor(String factorId) {
        collectedRareFactors.add(factorId);
    }
    
    /**
     * 获取已收集的稀有 Factor 数量
     */
    public int getCollectedRareFactorCount() {
        return collectedRareFactors.size();
    }
    
    /**
     * 检查是否收集了指定的稀有 Factor
     */
    public boolean hasCollectedRareFactor(String factorId) {
        return collectedRareFactors.contains(factorId);
    }
    
    /**
     * 获取所有已收集的稀有 Factor
     */
    public Set<String> getCollectedRareFactors() {
        return collectedRareFactors;
    }
    
    // ==================== NBT 序列化 ====================
    
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (professionType != null) {
            nbt.putString("profession", professionType.getId());
        }
        nbt.putInt("level", level);
        nbt.putInt("experience", experience);
        nbt.putInt("talentPoints", talentPoints);
        
        // 保存技能冷却
        NbtList cooldownList = new NbtList();
        for (Map.Entry<String, Long> entry : skillCooldowns.entrySet()) {
            NbtCompound cooldownNbt = new NbtCompound();
            cooldownNbt.putString("skillId", entry.getKey());
            cooldownNbt.putLong("lastUseTime", entry.getValue());
            cooldownList.add(cooldownNbt);
        }
        nbt.put("skillCooldowns", cooldownList);
        
        // 保存隐藏职业数据
        nbt.putBoolean("hiddenProfessionUnlocked", hiddenProfessionUnlocked);
        nbt.putInt("historicalTalentPoints", historicalTalentPoints);
        
        // 保存已满级的职业
        NbtList masteredList = new NbtList();
        for (ProfessionType type : masteredProfessions) {
            masteredList.add(net.minecraft.nbt.NbtString.of(type.getId()));
        }
        nbt.put("masteredProfessions", masteredList);
        
        // 保存已收集的稀有 Factor
        NbtList factorList = new NbtList();
        for (String factorId : collectedRareFactors) {
            factorList.add(net.minecraft.nbt.NbtString.of(factorId));
        }
        nbt.put("collectedRareFactors", factorList);
        
        return nbt;
    }
    
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("profession")) {
            professionType = ProfessionType.fromId(nbt.getString("profession"));
        }
        level = nbt.getInt("level");
        if (level < 1) level = 1;
        experience = nbt.getInt("experience");
        talentPoints = nbt.getInt("talentPoints");
        
        // 读取技能冷却
        skillCooldowns.clear();
        if (nbt.contains("skillCooldowns")) {
            NbtList cooldownList = nbt.getList("skillCooldowns", NbtList.COMPOUND_TYPE);
            for (int i = 0; i < cooldownList.size(); i++) {
                NbtCompound cooldownNbt = cooldownList.getCompound(i);
                String skillId = cooldownNbt.getString("skillId");
                long lastUseTime = cooldownNbt.getLong("lastUseTime");
                skillCooldowns.put(skillId, lastUseTime);
            }
        }
        
        // 读取隐藏职业数据
        hiddenProfessionUnlocked = nbt.getBoolean("hiddenProfessionUnlocked");
        historicalTalentPoints = nbt.getInt("historicalTalentPoints");
        
        // 读取已满级的职业
        masteredProfessions.clear();
        if (nbt.contains("masteredProfessions")) {
            NbtList masteredList = nbt.getList("masteredProfessions", NbtList.STRING_TYPE);
            for (int i = 0; i < masteredList.size(); i++) {
                ProfessionType type = ProfessionType.fromId(masteredList.getString(i));
                if (type != null) {
                    masteredProfessions.add(type);
                }
            }
        }
        
        // 读取已收集的稀有 Factor
        collectedRareFactors.clear();
        if (nbt.contains("collectedRareFactors")) {
            NbtList factorList = nbt.getList("collectedRareFactors", NbtList.STRING_TYPE);
            for (int i = 0; i < factorList.size(); i++) {
                collectedRareFactors.add(factorList.getString(i));
            }
        }
    }
}