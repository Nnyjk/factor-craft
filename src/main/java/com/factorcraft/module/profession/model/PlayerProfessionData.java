package com.factorcraft.module.profession.model;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

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
    private long lastSkillUseTime;
    
    public PlayerProfessionData() {
        this.professionType = null;
        this.level = 1;
        this.experience = 0;
        this.talentPoints = 0;
        this.lastSkillUseTime = 0;
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
    
    public int getTalentPoints() {
        return talentPoints;
    }
    
    public void addTalentPoints(int points) {
        this.talentPoints += points;
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
    
    public long getLastSkillUseTime() {
        return lastSkillUseTime;
    }
    
    public void setLastSkillUseTime(long time) {
        this.lastSkillUseTime = time;
    }
    
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (professionType != null) {
            nbt.putString("profession", professionType.getId());
        }
        nbt.putInt("level", level);
        nbt.putInt("experience", experience);
        nbt.putInt("talentPoints", talentPoints);
        nbt.putLong("lastSkillUseTime", lastSkillUseTime);
        return nbt;
    }
    
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("profession")) {
            professionType = ProfessionType.fromId(nbt.getString("profession"));
        }
        level = nbt.getInt("level");
        experience = nbt.getInt("experience");
        talentPoints = nbt.getInt("talentPoints");
        lastSkillUseTime = nbt.getLong("lastSkillUseTime");
    }
}