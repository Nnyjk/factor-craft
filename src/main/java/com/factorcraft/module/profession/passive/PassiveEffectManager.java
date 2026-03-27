package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.ProfessionModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 被动效果管理器
 * 
 * 负责管理所有职业被动效果的注册、应用和移除
 */
public class PassiveEffectManager {
    
    private static final Map<ProfessionType, List<PassiveEffect>> PASSIVES_BY_PROFESSION = new HashMap<>();
    
    static {
        PASSIVES_BY_PROFESSION.put(ProfessionType.ENGINEER, EngineerPassives.getAllPassives());
        PASSIVES_BY_PROFESSION.put(ProfessionType.CULTIVATOR, CultivatorPassives.getAllPassives());
        PASSIVES_BY_PROFESSION.put(ProfessionType.EXPLORER, ExplorerPassives.getAllPassives());
        PASSIVES_BY_PROFESSION.put(ProfessionType.MASTER, new ArrayList<>()); // 掌控者无被动
    }
    
    /**
     * 获取职业的所有被动效果
     */
    public static List<PassiveEffect> getPassivesForProfession(ProfessionType type) {
        return PASSIVES_BY_PROFESSION.getOrDefault(type, new ArrayList<>());
    }
    
    /**
     * 获取玩家当前可用的被动效果
     */
    public static List<PassiveEffect> getAvailablePassives(ServerPlayerEntity player) {
        List<PassiveEffect> available = new ArrayList<>();
        
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        if (api == null) {
            return available;
        }
        
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) {
            return available;
        }
        
        ProfessionType professionType = data.getProfessionType();
        if (professionType == null) {
            return available;
        }
        
        int level = data.getLevel();
        List<PassiveEffect> allPassives = getPassivesForProfession(professionType);
        
        for (PassiveEffect passive : allPassives) {
            if (level >= passive.getUnlockLevel()) {
                available.add(passive);
            }
        }
        
        return available;
    }
    
    /**
     * 应用玩家当前可用的所有被动效果
     */
    public static void applyAllPassives(ServerPlayerEntity player) {
        List<PassiveEffect> passives = getAvailablePassives(player);
        for (PassiveEffect passive : passives) {
            passive.apply(player);
        }
    }
    
    /**
     * 移除玩家所有的被动效果
     */
    public static void removeAllPassives(ServerPlayerEntity player) {
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        if (api == null) {
            return;
        }
        
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) {
            return;
        }
        
        List<PassiveEffect> allPassives = getPassivesForProfession(data.getProfessionType());
        for (PassiveEffect passive : allPassives) {
            passive.remove(player);
        }
    }
    
    /**
     * 更新玩家被动效果（等级变化时调用）
     */
    public static void updatePassives(ServerPlayerEntity player) {
        // 先移除所有
        removeAllPassives(player);
        // 重新应用当前可用的
        applyAllPassives(player);
    }
    
    /**
     * 切换职业时更新被动效果
     */
    public static void onProfessionChange(ServerPlayerEntity player, ProfessionType oldType, ProfessionType newType) {
        // 移除旧职业的所有被动效果
        List<PassiveEffect> oldPassives = getPassivesForProfession(oldType);
        for (PassiveEffect passive : oldPassives) {
            passive.remove(player);
        }
        
        // 应用新职业的被动效果
        applyAllPassives(player);
    }
    
    /**
     * tick 更新（用于触发型被动效果）
     */
    public static void tick(ServerPlayerEntity player) {
        List<PassiveEffect> passives = getAvailablePassives(player);
        for (PassiveEffect passive : passives) {
            passive.tick(player);
        }
    }
}