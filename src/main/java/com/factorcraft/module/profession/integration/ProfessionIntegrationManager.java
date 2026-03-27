package com.factorcraft.module.profession.integration;

import com.factorcraft.module.profession.ProfessionModule;
import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.event.ProfessionEventBus;
import com.factorcraft.module.profession.event.ProfessionEventImpl;
import com.factorcraft.module.profession.event.ProfessionEventType;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.network.ProfessionNetworkHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 职业系统集成管理器
 * 
 * 负责将职业系统与现有游戏系统集成：
 * - Factor 合成
 * - 机器操作
 * - 任务完成
 * - 资源采集
 */
public class ProfessionIntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionIntegration");
    
    private static ProfessionIntegrationManager instance;
    
    private ProfessionAPI api;
    private MinecraftServer server;
    
    private ProfessionIntegrationManager() {}
    
    public static ProfessionIntegrationManager getInstance() {
        if (instance == null) {
            instance = new ProfessionIntegrationManager();
        }
        return instance;
    }
    
    /**
     * 初始化集成管理器
     */
    public void init() {
        // 服务器启动时获取 API 实例
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.api = ProfessionModule.getInstance().getAPI();
            LOGGER.info("职业系统集成管理器初始化完成");
        });
        
        // 注册事件监听器
        registerEventListeners();
        
        LOGGER.info("职业系统集成管理器已注册");
    }
    
    /**
     * 注册事件监听器
     */
    private void registerEventListeners() {
        // 玩家破坏方块事件 - 资源采集
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                handleResourceCollection(serverPlayer, state, pos, blockEntity);
            }
        });
    }
    
    /**
     * 处理资源采集
     */
    private void handleResourceCollection(ServerPlayerEntity player, BlockState state, BlockPos pos, BlockEntity blockEntity) {
        if (api == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        ProfessionType profession = professionOpt.get();
        
        // 发布资源采集事件
        ProfessionEventBus.getInstance().post(
            new ProfessionEventImpl(ProfessionEventType.RESOURCE_COLLECT, player));
        
        // 根据职业类型给予经验奖励
        int expReward = calculateResourceExpReward(state, profession);
        if (expReward > 0) {
            api.addExperience(player, expReward, "resource_collection");
        }
        
        // 同步数据
        ProfessionNetworkHandler.syncIncremental(player);
    }
    
    /**
     * 计算资源采集经验奖励
     */
    private int calculateResourceExpReward(BlockState state, ProfessionType profession) {
        // 基础经验值
        int baseExp = 1;
        
        // 根据方块类型和职业调整经验
        String blockId = state.getBlock().toString().toLowerCase();
        
        // 工程师采集机器相关方块额外经验
        if (profession == ProfessionType.ENGINEER && 
            (blockId.contains("machine") || blockId.contains("factor"))) {
            return baseExp + 2;
        }
        
        // 培育师采集生物相关方块额外经验
        if (profession == ProfessionType.CULTIVATOR && 
            (blockId.contains("crop") || blockId.contains("plant"))) {
            return baseExp + 2;
        }
        
        // 探索者采集稀有方块额外经验
        if (profession == ProfessionType.EXPLORER && 
            (blockId.contains("ore") || blockId.contains("rare"))) {
            return baseExp + 3;
        }
        
        return baseExp;
    }
    
    // ==================== 公共 API ====================
    
    /**
     * 处理任务完成事件
     */
    public void handleQuestComplete(ServerPlayerEntity player, String questId, int baseExp) {
        if (api == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        // 发布任务完成事件
        ProfessionEventBus.getInstance().post(
            new ProfessionEventImpl(ProfessionEventType.QUEST_COMPLETE, player));
        
        // 给予经验奖励（职业加成）
        ProfessionType profession = professionOpt.get();
        int bonusExp = (int) (baseExp * getProfessionExpMultiplier(profession));
        api.addExperience(player, bonusExp, "quest_complete");
        
        // 同步数据
        ProfessionNetworkHandler.syncIncremental(player);
        
        LOGGER.debug("玩家 {} 完成任务 {} 获得 {} 经验", player.getName().getString(), questId, bonusExp);
    }
    
    /**
     * 处理成就解锁事件
     */
    public void handleAchievementUnlock(ServerPlayerEntity player, String achievementId) {
        if (api == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        // 发布成就解锁事件
        ProfessionEventBus.getInstance().post(
            new ProfessionEventImpl(ProfessionEventType.ACHIEVEMENT_UNLOCK, player));
        
        // 成就解锁给予天赋点奖励
        PlayerProfessionData data = api.getPlayerData(player);
        if (data != null) {
            data.addTalentPoints(1);
            api.savePlayerData(player);
        }
        
        // 同步数据
        ProfessionNetworkHandler.syncIncremental(player);
        
        LOGGER.debug("玩家 {} 解锁成就 {} 获得 1 天赋点", player.getName().getString(), achievementId);
    }
    
    /**
     * 处理 Factor 合成事件
     */
    public void handleFactorCraft(ServerPlayerEntity player, String factorType, int amount) {
        if (api == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        // 给予经验奖励
        int expReward = amount * 2; // 每个 Factor 给 2 经验
        api.addExperience(player, expReward, "factor_craft");
        
        // 同步数据
        ProfessionNetworkHandler.syncIncremental(player);
    }
    
    /**
     * 处理机器操作事件
     */
    public void handleMachineOperation(ServerPlayerEntity player, String machineType) {
        if (api == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        ProfessionType profession = professionOpt.get();
        
        // 工程师操作机器获得额外经验
        if (profession == ProfessionType.ENGINEER) {
            int expReward = 5;
            api.addExperience(player, expReward, "machine_operation");
            
            // 同步数据
            ProfessionNetworkHandler.syncIncremental(player);
        }
    }
    
    /**
     * 获取职业经验加成倍率
     */
    private float getProfessionExpMultiplier(ProfessionType profession) {
        // 基础倍率
        float multiplier = 1.0f;
        
        // 根据职业类型调整（可后续通过配置实现）
        switch (profession) {
            case ENGINEER -> multiplier = 1.1f;
            case CULTIVATOR -> multiplier = 1.15f;
            case EXPLORER -> multiplier = 1.2f;
            case MASTER -> multiplier = 1.3f;
        }
        
        return multiplier;
    }
    
    /**
     * 检查并解锁隐藏职业
     */
    public void checkHiddenProfessionUnlock(ServerPlayerEntity player) {
        if (api == null) return;
        
        // 检查是否已解锁
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null || data.isHiddenProfessionUnlocked()) return;
        
        // 检查是否满足解锁条件
        // 条件：三个基础职业均达到 10 级
        int masteredCount = data.getMasteredProfessions().size();
        
        if (masteredCount >= 3) {
            data.unlockHiddenProfession();
            api.savePlayerData(player);
            
            // 同步数据
            ProfessionNetworkHandler.syncFull(player);
            
            LOGGER.info("玩家 {} 解锁隐藏职业：因子掌控者", player.getName().getString());
        }
    }
}