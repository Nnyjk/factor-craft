package com.factorcraft.module.profession.talent;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;

/**
 * 玩家天赋数据
 * 
 * 存储玩家的职业选择和天赋等级
 */
public class PlayerTalentData {
    
    private ProfessionType selectedProfession;
    private final Map<String, Integer> talentLevels;
    private final Set<String> unlockedTalentIds;
    private int talentPoints;
    private int totalTalentPointsSpent;
    private final TalentEffectHandler effectHandler;
    
    public PlayerTalentData() {
        this.selectedProfession = null;
        this.talentLevels = new HashMap<>();
        this.unlockedTalentIds = new HashSet<>();
        this.talentPoints = 0;
        this.totalTalentPointsSpent = 0;
        this.effectHandler = new TalentEffectHandler(this);
    }
    
    // ==================== 职业相关 ====================
    
    /**
     * 获取当前选择的职业
     */
    public ProfessionType getSelectedProfession() {
        return selectedProfession;
    }
    
    /**
     * 设置当前职业
     * 
     * @param profession 职业类型
     * @param resetTalents 是否重置天赋
     */
    public void setProfession(ProfessionType profession, boolean resetTalents) {
        this.selectedProfession = profession;
        
        if (resetTalents) {
            resetAllTalents();
        }
    }
    
    /**
     * 检查是否有职业
     */
    public boolean hasProfession() {
        return selectedProfession != null;
    }
    
    // ==================== 天赋点数 ====================
    
    /**
     * 获取可用天赋点数
     */
    public int getTalentPoints() {
        return talentPoints;
    }
    
    /**
     * 获取已使用的天赋点数
     */
    public int getTotalTalentPointsSpent() {
        return totalTalentPointsSpent;
    }
    
    /**
     * 添加天赋点数
     */
    public void addTalentPoints(int points) {
        this.talentPoints += points;
    }
    
    /**
     * 使用天赋点数
     * 
     * @param points 要使用的点数
     * @return 是否成功
     */
    public boolean spendTalentPoints(int points) {
        if (talentPoints >= points) {
            talentPoints -= points;
            totalTalentPointsSpent += points;
            return true;
        }
        return false;
    }
    
    // ==================== 天赋等级 ====================
    
    /**
     * 获取天赋等级
     * 
     * @param talentId 天赋ID
     * @return 等级，未学习返回 0
     */
    public int getTalentLevel(String talentId) {
        return talentLevels.getOrDefault(talentId, 0);
    }
    
    /**
     * 设置天赋等级
     */
    public void setTalentLevel(String talentId, int level) {
        TalentNode talent = TalentNodes.getTalentById(talentId);
        if (talent == null) return;
        
        int clampedLevel = Math.max(0, Math.min(level, talent.getMaxLevel()));
        int oldLevel = talentLevels.getOrDefault(talentId, 0);
        
        if (clampedLevel > 0) {
            talentLevels.put(talentId, clampedLevel);
            unlockedTalentIds.add(talentId);
        } else {
            talentLevels.remove(talentId);
            unlockedTalentIds.remove(talentId);
        }
        
        // 更新已使用的天赋点数
        totalTalentPointsSpent += (clampedLevel - oldLevel);
        
        // 标记效果需要重新计算
        effectHandler.markDirty();
    }
    
    /**
     * 升级天赋
     * 
     * @param talentId 天赋ID
     * @return 是否成功
     */
    public boolean levelUpTalent(String talentId) {
        TalentNode talent = TalentNodes.getTalentById(talentId);
        if (talent == null) return false;
        
        int currentLevel = getTalentLevel(talentId);
        if (currentLevel >= talent.getMaxLevel()) return false;
        
        // 检查天赋点数
        if (!spendTalentPoints(1)) return false;
        
        // 升级
        int newLevel = currentLevel + 1;
        talentLevels.put(talentId, newLevel);
        unlockedTalentIds.add(talentId);
        
        // 标记效果需要重新计算
        effectHandler.markDirty();
        
        return true;
    }
    
    /**
     * 检查天赋是否已解锁
     */
    public boolean isTalentUnlocked(String talentId) {
        return unlockedTalentIds.contains(talentId);
    }
    
    /**
     * 获取所有已解锁天赋ID
     */
    public Set<String> getUnlockedTalentIds() {
        return Collections.unmodifiableSet(unlockedTalentIds);
    }
    
    /**
     * 获取所有天赋等级
     */
    public Map<String, Integer> getAllTalentLevels() {
        return Collections.unmodifiableMap(talentLevels);
    }
    
    /**
     * 重置所有天赋
     */
    public void resetAllTalents() {
        talentLevels.clear();
        unlockedTalentIds.clear();
        talentPoints += totalTalentPointsSpent;
        totalTalentPointsSpent = 0;
        effectHandler.markDirty();
    }
    
    /**
     * 重置指定分支的天赋
     * 
     * @param branch 天赋分支
     */
    public void resetBranchTalents(TalentBranch branch) {
        List<TalentNode> talents = TalentNodes.getTalentsForBranch(branch);
        for (TalentNode talent : talents) {
            String talentId = talent.getId();
            int level = talentLevels.remove(talentId);
            if (level > 0) {
                unlockedTalentIds.remove(talentId);
                talentPoints += level;
                totalTalentPointsSpent -= level;
            }
        }
        effectHandler.markDirty();
    }
    
    /**
     * 获取效果处理器
     */
    public TalentEffectHandler getEffectHandler() {
        return effectHandler;
    }
    
    // ==================== 序列化 ====================
    
    /**
     * 写入 NBT
     */
    public void writeNbt(NbtCompound nbt) {
        // 职业
        if (selectedProfession != null) {
            nbt.putString("profession", selectedProfession.name());
        }
        
        // 天赋点数
        nbt.putInt("talentPoints", talentPoints);
        nbt.putInt("totalSpent", totalTalentPointsSpent);
        
        // 天赋等级
        NbtList levelsList = new NbtList();
        for (Map.Entry<String, Integer> entry : talentLevels.entrySet()) {
            NbtCompound levelNbt = new NbtCompound();
            levelNbt.putString("id", entry.getKey());
            levelNbt.putInt("level", entry.getValue());
            levelsList.add(levelNbt);
        }
        nbt.put("talentLevels", levelsList);
    }
    
    /**
     * 读取 NBT
     */
    public void readNbt(NbtCompound nbt) {
        // 职业
        if (nbt.contains("profession")) {
            try {
                selectedProfession = ProfessionType.valueOf(nbt.getString("profession"));
            } catch (IllegalArgumentException ignored) {
                selectedProfession = null;
            }
        }
        
        // 天赋点数
        talentPoints = nbt.getInt("talentPoints");
        totalTalentPointsSpent = nbt.getInt("totalSpent");
        
        // 天赋等级
        talentLevels.clear();
        unlockedTalentIds.clear();
        
        NbtList levelsList = nbt.getList("talentLevels", 10);
        for (int i = 0; i < levelsList.size(); i++) {
            NbtCompound levelNbt = levelsList.getCompound(i);
            String id = levelNbt.getString("id");
            int level = levelNbt.getInt("level");
            
            if (level > 0) {
                talentLevels.put(id, level);
                unlockedTalentIds.add(id);
            }
        }
        
        // 标记效果需要重新计算
        effectHandler.markDirty();
    }
    
    @Override
    public String toString() {
        return String.format("PlayerTalentData[profession=%s, points=%d, talents=%d]", 
            selectedProfession, talentPoints, talentLevels.size());
    }
}