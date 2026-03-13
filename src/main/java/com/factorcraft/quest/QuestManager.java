package com.factorcraft.quest;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Factor Craft 任务系统
 */
public class QuestManager {
    private static final Map<String, Quest> QUESTS = new HashMap<>();
    private static final Map<UUID, PlayerQuestData> PLAYER_DATA = new HashMap<>();
    
    // 预定义任务
    public static final Quest FIRST_EXTRACTION = new Quest(
        "first_extraction",
        "首次提取",
        "成功提取第一个 Factor 晶体",
        new QuestReward(
            new ItemStack(net.minecraft.item.Items.DIAMOND, 1),
            100,
            10
        ),
        QuestType.EXTRACTION,
        1
    );
    
    public static final Quest TRAIT_COLLECTOR = new Quest(
        "trait_collector",
        "特性收藏家",
        "为一个物品添加 5 个特性",
        new QuestReward(
            new ItemStack(net.minecraft.item.Items.ENCHANTED_BOOK, 1),
            500,
            50
        ),
        QuestType.TRAIT,
        5
    );
    
    public static final Quest RESONANCE_MASTER = new Quest(
        "resonance_master",
        "共振大师",
        "触发一次三重共振（×2.5）",
        new QuestReward(
            new ItemStack(net.minecraft.item.Items.NETHERITE_INGOT, 1),
            1000,
            100
        ),
        QuestType.RESONANCE,
        1
    );
    
    public static final Quest HIGH_ENERGY_HUNTER = new Quest(
        "high_energy_hunter",
        "高能猎人",
        "找到 3 个高能区块（100+ 浓度）",
        new QuestReward(
            new ItemStack(net.minecraft.item.Items.BEACON, 1),
            2000,
            200
        ),
        QuestType.DISCOVERY,
        3
    );
    
    static {
        registerQuest(FIRST_EXTRACTION);
        registerQuest(TRAIT_COLLECTOR);
        registerQuest(RESONANCE_MASTER);
        registerQuest(HIGH_ENERGY_HUNTER);
    }
    
    /**
     * 注册任务
     */
    public static void registerQuest(Quest quest) {
        QUESTS.put(quest.id(), quest);
    }
    
    /**
     * 获取任务
     */
    public static Optional<Quest> getQuest(String id) {
        return Optional.ofNullable(QUESTS.get(id));
    }
    
    /**
     * 获取所有任务
     */
    public static Collection<Quest> getAllQuests() {
        return QUESTS.values();
    }
    
    /**
     * 玩家完成任务
     */
    public static void completeQuest(ServerPlayerEntity player, String questId) {
        PlayerQuestData data = PLAYER_DATA.computeIfAbsent(player.getUuid(), k -> new PlayerQuestData());
        
        if (data.completeQuest(questId)) {
            Quest quest = QUESTS.get(questId);
            if (quest != null) {
                quest.reward().giveTo(player);
                player.sendMessage(Text.literal("§a任务完成: §f" + quest.name()), false);
                player.sendMessage(Text.literal("§e获得奖励!"), false);
            }
        }
    }
    
    /**
     * 更新任务进度
     */
    public static void updateProgress(ServerPlayerEntity player, QuestType type, int amount) {
        PlayerQuestData data = PLAYER_DATA.computeIfAbsent(player.getUuid(), k -> new PlayerQuestData());
        
        for (Quest quest : QUESTS.values()) {
            if (quest.type() == type && !data.isCompleted(quest.id())) {
                data.updateProgress(quest.id(), amount);
                
                if (data.getProgress(quest.id()) >= quest.required()) {
                    completeQuest(player, quest.id());
                }
            }
        }
    }
    
    /**
     * 获取玩家任务数据
     */
    public static PlayerQuestData getPlayerData(UUID playerId) {
        return PLAYER_DATA.getOrDefault(playerId, new PlayerQuestData());
    }
    
    /**
     * 检查任务是否完成
     */
    public static boolean isCompleted(UUID playerId, String questId) {
        PlayerQuestData data = PLAYER_DATA.get(playerId);
        return data != null && data.isCompleted(questId);
    }
}