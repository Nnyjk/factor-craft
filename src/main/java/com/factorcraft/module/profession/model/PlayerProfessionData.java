package com.factorcraft.module.profession.model;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.HashMap;
import java.util.Map;

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
    }
}