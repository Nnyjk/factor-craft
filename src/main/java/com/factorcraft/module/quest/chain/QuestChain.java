package com.factorcraft.module.quest.chain;

import com.factorcraft.module.quest.template.QuestTemplate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务链 - 一组有序的任务
 */
public class QuestChain {
    
    private final String id;
    private final String name;
    private final List<String> questIds;
    private int currentStep;
    
    public QuestChain(String id, String name) {
        this.id = id;
        this.name = name;
        this.questIds = new ArrayList<>();
        this.currentStep = 0;
    }
    
    public void addQuest(String questId) {
        this.questIds.add(questId);
    }
    
    public void advance() {
        if (currentStep < questIds.size()) {
            currentStep++;
        }
    }
    
    public String getCurrentQuestId() {
        if (currentStep < questIds.size()) {
            return questIds.get(currentStep);
        }
        return null;
    }
    
    public boolean isCompleted() {
        return currentStep >= questIds.size();
    }
    
    public float getProgress() {
        if (questIds.isEmpty()) {
            return 1.0f;
        }
        return (float) currentStep / questIds.size();
    }
    
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id);
        nbt.putString("name", name);
        nbt.putInt("currentStep", currentStep);
        
        NbtList list = new NbtList();
        for (String questId : questIds) {
            NbtCompound questNbt = new NbtCompound();
            questNbt.putString("id", questId);
            list.add(questNbt);
        }
        nbt.put("quests", list);
        
        return nbt;
    }
    
    public static QuestChain fromNbt(NbtCompound nbt) {
        QuestChain chain = new QuestChain(
            nbt.getString("id"),
            nbt.getString("name")
        );
        chain.currentStep = nbt.getInt("currentStep");
        
        NbtList list = nbt.getList("quests", 10);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound questNbt = list.getCompound(i);
            chain.questIds.add(questNbt.getString("id"));
        }
        
        return chain;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getQuestIds() { return new ArrayList<>(questIds); }
    public int getCurrentStep() { return currentStep; }
}
