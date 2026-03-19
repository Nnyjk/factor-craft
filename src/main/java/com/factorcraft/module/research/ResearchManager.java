package com.factorcraft.module.research;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.FactorService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 研究管理器
 * 
 * 管理所有研究定义和玩家进度
 */
public class ResearchManager {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    // 所有注册的研究
    private final Map<String, Research> allResearch = new ConcurrentHashMap<>();
    
    // 研究分类
    private final Map<String, Set<String>> researchByCategory = new ConcurrentHashMap<>();
    
    // 玩家进度（playerId -> progress）
    private final Map<UUID, ResearchProgress> playerProgress = new ConcurrentHashMap<>();
    
    // 服务端引用
    private MinecraftServer server;
    
    /**
     * 注册研究
     */
    public void registerResearch(Research research) {
        allResearch.put(research.getId(), research);
        researchByCategory
            .computeIfAbsent(research.getCategory(), k -> new HashSet<>())
            .add(research.getId());
        FactorCraftMod.LOGGER.debug("[Research] 注册研究: {} ({})", 
            research.getName(), research.getId());
    }
    
    /**
     * 获取研究
     */
    public Research getResearch(String id) {
        return allResearch.get(id);
    }
    
    /**
     * 获取所有研究
     */
    public Collection<Research> getAllResearch() {
        return allResearch.values();
    }
    
    /**
     * 获取分类下的研究
     */
    public Set<String> getResearchByCategory(String category) {
        return researchByCategory.getOrDefault(category, Collections.emptySet());
    }
    
    /**
     * 获取玩家进度
     */
    public ResearchProgress getProgress(PlayerEntity player) {
        return playerProgress.computeIfAbsent(player.getUuid(), 
            uuid -> new ResearchProgress(uuid));
    }
    
    /**
     * 检查研究是否可开始
     */
    public Research.State getResearchState(String researchId, PlayerEntity player) {
        Research research = getResearch(researchId);
        if (research == null) return Research.State.LOCKED;
        
        ResearchProgress progress = getProgress(player);
        
        // 已完成
        if (progress.isCompleted(researchId)) {
            return Research.State.COMPLETED;
        }
        
        // 进行中
        if (progress.isInProgress(researchId)) {
            return Research.State.IN_PROGRESS;
        }
        
        // 检查前置研究
        for (String prereq : research.getPrerequisites()) {
            if (!progress.isCompleted(prereq)) {
                return Research.State.LOCKED;
            }
        }
        
        // 检查 Factor 消耗（不扣减，只检查）
        // TODO: 集成 FactorService 检查玩家 Factor 数量
        
        return Research.State.AVAILABLE;
    }
    
    /**
     * 开始研究
     */
    public boolean startResearch(String researchId, ServerPlayerEntity player) {
        Research research = getResearch(researchId);
        if (research == null) return false;
        
        ResearchProgress progress = getProgress(player);
        
        // 已完成或进行中
        if (progress.isCompleted(researchId) || progress.isInProgress(researchId)) {
            return false;
        }
        
        // 检查状态
        Research.State state = getResearchState(researchId, player);
        if (state != Research.State.AVAILABLE) {
            return false;
        }
        
        // 消耗 Factor
        if (!consumeFactorCosts(research, player)) {
            return false;
        }
        
        // 消耗物品
        if (!consumeItemRequirements(research, player)) {
            return false;
        }
        
        // 开始研究
        long currentTick = player.getServerWorld().getTime();
        progress.startResearch(researchId, currentTick);
        
        FactorCraftMod.LOGGER.info("[Research] 玩家 {} 开始研究: {}", 
            player.getName().getString(), research.getName());
        
        return true;
    }
    
    /**
     * 消耗 Factor 成本
     */
    private boolean consumeFactorCosts(Research research, ServerPlayerEntity player) {
        Map<String, Integer> costs = research.getFactorCosts();
        if (costs.isEmpty()) return true;
        
        // TODO: 集成 FactorService 进行实际扣减
        // FactorService factorService = FactorService.getInstance();
        // for (var entry : costs.entrySet()) {
        //     if (!factorService.hasEnoughFactor(player, entry.getKey(), entry.getValue())) {
        //         return false;
        //     }
        // }
        // for (var entry : costs.entrySet()) {
        //     factorService.consumeFactor(player, entry.getKey(), entry.getValue());
        // }
        
        return true;
    }
    
    /**
     * 消耗物品要求
     */
    private boolean consumeItemRequirements(Research research, ServerPlayerEntity player) {
        Map<Item, Integer> requirements = research.getItemRequirements();
        if (requirements.isEmpty()) return true;
        
        // 检查并消耗物品
        for (var entry : requirements.entrySet()) {
            int needed = entry.getValue();
            int found = 0;
            
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getItem() == entry.getKey()) {
                    int take = Math.min(needed - found, stack.getCount());
                    stack.decrement(take);
                    found += take;
                    if (found >= needed) break;
                }
            }
            
            if (found < needed) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Tick 更新 - 检查研究完成
     */
    public void tick(MinecraftServer server) {
        this.server = server;
        long currentTick = server.getOverworld().getTime();
        
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ResearchProgress progress = getProgress(player);
            
            for (var entry : new HashMap<>(progress.getInProgressResearch()).entrySet()) {
                String researchId = entry.getKey();
                long startTick = entry.getValue();
                Research research = getResearch(researchId);
                
                if (research == null) continue;
                
                if (currentTick - startTick >= research.getResearchTime()) {
                    // 研究完成
                    progress.completeResearch(researchId, research);
                    
                    FactorCraftMod.LOGGER.info("[Research] 玩家 {} 完成研究: {}", 
                        player.getName().getString(), research.getName());
                    
                    // TODO: 发送完成通知
                }
            }
        }
    }
    
    /**
     * 从配置文件加载研究
     */
    public void loadFromConfig(Path configPath) {
        if (!Files.exists(configPath)) {
            FactorCraftMod.LOGGER.warn("[Research] 配置文件不存在: {}", configPath);
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            JsonObject config = GSON.fromJson(reader, JsonObject.class);
            
            if (config.has("research")) {
                JsonArray researchArray = config.getAsJsonArray("research");
                for (var elem : researchArray) {
                    try {
                        Research research = Research.fromJson(elem.getAsJsonObject());
                        registerResearch(research);
                    } catch (Exception e) {
                        FactorCraftMod.LOGGER.error("[Research] 解析研究失败: {}", 
                            elem, e);
                    }
                }
            }
            
            FactorCraftMod.LOGGER.info("[Research] 已加载 {} 个研究", allResearch.size());
            
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[Research] 加载配置失败: {}", configPath, e);
        }
    }
    
    /**
     * 保存玩家数据
     */
    public void savePlayerData(Path saveDir) {
        // TODO: 实现玩家数据持久化
    }
    
    /**
     * 加载玩家数据
     */
    public void loadPlayerData(Path saveDir) {
        // TODO: 实现玩家数据加载
    }
}