package com.factorcraft.module.profession.network;

import com.factorcraft.module.network.ProfessionSyncPayload;
import com.factorcraft.module.profession.ProfessionModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.event.ProfessionEventBus;
import com.factorcraft.module.profession.event.ProfessionEventType;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 职业系统网络处理
 * 
 * 负责职业数据的客户端-服务器同步
 */
public class ProfessionNetworkHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionNetwork");
    
    /** 职业数据缓存（用于增量更新比较） */
    private static final Map<String, CachedProfessionData> dataCache = new ConcurrentHashMap<>();
    
    /** 同步频率限制（毫秒） */
    private static final long SYNC_COOLDOWN_MS = 500;
    
    /** 上次同步时间 */
    private static final Map<String, Long> lastSyncTime = new ConcurrentHashMap<>();
    
    /**
     * 注册网络包处理器
     */
    public static void register() {
        LOGGER.info("注册职业网络包处理器");
        
        // 注册 S2C Payload 类型（在 NetworkPackets 中已注册）
        // ServerPlayNetworking.registerGlobalReceiver 用于接收 C2S 包
        // 职业数据同步是单向的 S2C，所以这里只需要确保 Payload 类型已注册
    }
    
    /**
     * 清理缓存
     */
    public static void cleanup() {
        dataCache.clear();
        lastSyncTime.clear();
        LOGGER.debug("职业数据缓存已清理");
    }
    
    /**
     * 同步玩家职业数据到客户端
     * 
     * @param player 目标玩家
     * @param fullSync 是否全量同步（登录、维度切换时为 true）
     */
    public static void syncProfessionData(ServerPlayerEntity player, boolean fullSync) {
        String playerName = player.getName().getString();
        
        // 频率限制
        if (!fullSync) {
            Long lastSync = lastSyncTime.get(playerName);
            if (lastSync != null && System.currentTimeMillis() - lastSync < SYNC_COOLDOWN_MS) {
                LOGGER.debug("玩家 {} 同步频率限制，跳过", playerName);
                return;
            }
        }
        
        // 获取玩家职业数据
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) {
            LOGGER.warn("玩家 {} 无职业数据，跳过同步", playerName);
            return;
        }
        
        // 构建当前数据快照
        CachedProfessionData currentData = new CachedProfessionData(
            data.getProfessionType(),
            data.getLevel(),
            data.getExperience(),
            data.getTalentPoints(),
            Set.of(), // 暂时使用空集合，天赋系统待实现
            data.getSkillCooldowns()
        );
        
        // 检查是否需要同步
        CachedProfessionData cachedData = dataCache.get(playerName);
        if (!fullSync && cachedData != null && cachedData.equals(currentData)) {
            LOGGER.debug("玩家 {} 职业数据无变化，跳过同步", playerName);
            return;
        }
        
        // 更新缓存
        dataCache.put(playerName, currentData);
        lastSyncTime.put(playerName, System.currentTimeMillis());
        
        // 创建并发送同步包
        ProfessionSyncPayload payload = new ProfessionSyncPayload(
            currentData.professionType(),
            currentData.level(),
            currentData.experience(),
            currentData.talentPoints(),
            currentData.activeTalents(),
            currentData.skillCooldowns(),
            fullSync
        );
        
        ServerPlayNetworking.send(player, payload);
        
        // 触发同步事件
        ProfessionEventBus.getInstance().post(
            new com.factorcraft.module.profession.event.ProfessionEventImpl(ProfessionEventType.DATA_SYNC, player));
        
        LOGGER.debug("已同步玩家 {} 职业数据 (fullSync={})", playerName, fullSync);
    }
    
    /**
     * 增量同步职业数据
     */
    public static void syncIncremental(ServerPlayerEntity player) {
        syncProfessionData(player, false);
    }
    
    /**
     * 全量同步职业数据
     */
    public static void syncFull(ServerPlayerEntity player) {
        syncProfessionData(player, true);
    }
    
    /**
     * 同步技能冷却更新
     */
    public static void syncSkillCooldown(ServerPlayerEntity player, String skillId, long remainingSeconds) {
        ProfessionAPI api = ProfessionModule.getInstance().getAPI();
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) return;
        
        // 只同步冷却数据
        Map<String, Long> cooldowns = new java.util.HashMap<>();
        cooldowns.put(skillId, remainingSeconds);
        
        ProfessionSyncPayload payload = new ProfessionSyncPayload(
            data.getProfessionType(),
            data.getLevel(),
            data.getExperience(),
            data.getTalentPoints(),
            Set.of(), // 暂时使用空集合
            cooldowns,
            false
        );
        
        ServerPlayNetworking.send(player, payload);
        LOGGER.debug("已同步玩家 {} 技能 {} 冷却: {}s", player.getName().getString(), skillId, remainingSeconds);
    }
    
    /**
     * 缓存的职业数据（用于增量更新比较）
     */
    private record CachedProfessionData(
        ProfessionType professionType,
        int level,
        int experience,
        int talentPoints,
        Set<String> activeTalents,
        Map<String, Long> skillCooldowns
    ) {}
}