package com.factorcraft.module.quest.data;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * 玩家任务数据组件 - 使用 Component 系统存储玩家任务进度
 */
public class PlayerQuestData {
    
    private static final Identifier COMPONENT_ID = Identifier.of("factorcraft", "quest_data");
    
    private final Set<Identifier> completedQuests;
    private final Set<Identifier> activeQuests;
    
    public PlayerQuestData() {
        this.completedQuests = new HashSet<>();
        this.activeQuests = new HashSet<>();
    }
    
    /**
     * 注册玩家数据组件
     */
    public static void register() {
        // 使用 Fabric API 的 Component API 注册
        // 待完善: 实现完整的玩家数据持久化
        System.out.println("[PlayerQuestData] 玩家任务数据组件已注册");
    }
    
    /**
     * 从 NBT 读取数据
     */
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        completedQuests.clear();
        activeQuests.clear();
        
        if (nbt.contains("completed", 9)) {
            NbtList completedList = nbt.getList("completed", 8);
            for (NbtElement element : completedList) {
                completedQuests.add(Identifier.tryParse(((NbtString) element).asString()));
            }
        }
        
        if (nbt.contains("active", 9)) {
            NbtList activeList = nbt.getList("active", 8);
            for (NbtElement element : activeList) {
                activeQuests.add(Identifier.tryParse(((NbtString) element).asString()));
            }
        }
    }
    
    /**
     * 写入 NBT
     */
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList completedList = new NbtList();
        for (Identifier id : completedQuests) {
            completedList.add(NbtString.of(id.toString()));
        }
        nbt.put("completed", completedList);
        
        NbtList activeList = new NbtList();
        for (Identifier id : activeQuests) {
            activeList.add(NbtString.of(id.toString()));
        }
        nbt.put("active", activeList);
    }
    
    /**
     * 获取玩家的任务数据
     */
    public static PlayerQuestData get(PlayerEntity player) {
        // 从玩家组件获取
        // 待完善: 实现完整的玩家数据组件系统
        PlayerQuestData data = new PlayerQuestData();
        // 可以从玩家的 NBT 数据中读取
        return data;
    }
    
    public Set<Identifier> getCompletedQuests() { return completedQuests; }
    public Set<Identifier> getActiveQuests() { return activeQuests; }
    
    public boolean isCompleted(Identifier questId) { return completedQuests.contains(questId); }
    public boolean isActive(Identifier questId) { return activeQuests.contains(questId); }
    
    public void markCompleted(Identifier questId) {
        activeQuests.remove(questId);
        completedQuests.add(questId);
    }
    
    public void markActive(Identifier questId) {
        activeQuests.add(questId);
    }
}
