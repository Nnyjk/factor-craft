package com.factorcraft.module.quest;

import com.factorcraft.module.quest.condition.*;
import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务事件监听器 - 检测各种事件并更新任务进度
 */
public class QuestEventListener {
    
    private final Map<UUID, QuestManager> playerQuestManagers;
    
    public QuestEventListener(Map<UUID, QuestManager> playerQuestManagers) {
        this.playerQuestManagers = playerQuestManagers;
    }
    
    /**
     * 物品获得事件
     */
    public void onItemPickup(PlayerEntity player, ItemStack stack) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof ItemPickupCondition) {
                    ((ItemPickupCondition) condition).onPickup(stack);
                }
            });
        });
    }
    
    /**
     * 物品合成事件
     */
    public void onItemCraft(PlayerEntity player, ItemStack result, int count) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof ItemCraftCondition) {
                    Identifier itemId = ((ItemCraftCondition) condition).getItemId();
                    if (result.getItem().equals(player.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ITEM).get(itemId))) {
                        ((ItemCraftCondition) condition).onCraft(count);
                    }
                }
            });
        });
    }
    
    /**
     * 物品提交事件
     */
    public void onItemSubmit(PlayerEntity player, Identifier itemId, int count) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof ItemSubmitCondition) {
                    Identifier requiredId = ((ItemSubmitCondition) condition).getItemId();
                    if (requiredId.equals(itemId)) {
                        ((ItemSubmitCondition) condition).onSubmit(count);
                    }
                }
            });
        });
    }
    
    /**
     * 物品使用事件
     */
    public void onItemUse(PlayerEntity player, Identifier itemId, int count) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof ItemUseCondition) {
                    Identifier requiredId = ((ItemUseCondition) condition).getItemId();
                    if (requiredId.equals(itemId)) {
                        ((ItemUseCondition) condition).onUse(count);
                    }
                }
            });
        });
    }
    
    /**
     * 实体击杀事件
     */
    public void onEntityKill(PlayerEntity player, Identifier entityId, int count) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof EntityKillCondition) {
                    Identifier requiredId = ((EntityKillCondition) condition).getEntityId();
                    if (requiredId.equals(entityId)) {
                        ((EntityKillCondition) condition).onKill(count);
                    }
                }
            });
        });
    }
    
    /**
     * 方块放置事件
     */
    public void onBlockPlace(PlayerEntity player, Identifier blockId, int count) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof BlockPlaceCondition) {
                    Identifier requiredId = ((BlockPlaceCondition) condition).getBlockId();
                    if (requiredId.equals(blockId)) {
                        ((BlockPlaceCondition) condition).onPlace(count);
                    }
                }
            });
        });
    }
    
    /**
     * 维度传输事件
     */
    public void onDimensionTravel(PlayerEntity player, Identifier dimensionId) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof DimensionTravelCondition) {
                    Identifier requiredId = ((DimensionTravelCondition) condition).getDimensionId();
                    if (requiredId.equals(dimensionId)) {
                        ((DimensionTravelCondition) condition).onTravel();
                    }
                }
            });
        });
    }
    
    /**
     * Factor 吸收事件
     */
    public void onFactorAbsorb(PlayerEntity player, double amount) {
        QuestManager manager = playerQuestManagers.get(player.getUuid());
        if (manager == null) return;
        
        manager.getActiveQuests(player.getUuid()).forEach(quest -> {
            quest.getTemplate().getConditions().forEach(condition -> {
                if (condition instanceof FactorAbsorbCondition) {
                    ((FactorAbsorbCondition) condition).onAbsorb(amount);
                }
            });
        });
    }
}
