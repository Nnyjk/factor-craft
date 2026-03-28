package com.factorcraft.module.cycle.automation.component;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * 合成任务 - 表示一个正在进行的合成任务
 */
public class CraftingJob {
    private final RecipePattern pattern;
    private int progress;
    private final int totalProgress;
    private boolean completed;
    
    public CraftingJob(RecipePattern pattern, int totalProgress) {
        this.pattern = pattern;
        this.totalProgress = totalProgress;
        this.progress = 0;
        this.completed = false;
    }
    
    public RecipePattern getPattern() {
        return pattern;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public int getTotalProgress() {
        return totalProgress;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * 更新进度
     */
    public void tick() {
        if (!completed) {
            progress++;
            if (progress >= totalProgress) {
                completed = true;
            }
        }
    }
    
    /**
     * 获取输出物品
     */
    public ItemStack getOutput() {
        return pattern.getOutput().copy();
    }
    
    /**
     * 将任务写入 NBT
     */
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup lookup) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("pattern", pattern.toNbt(lookup));
        nbt.putInt("progress", progress);
        nbt.putInt("totalProgress", totalProgress);
        nbt.putBoolean("completed", completed);
        return nbt;
    }
    
    /**
     * 从 NBT 读取任务
     */
    public static CraftingJob fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        RecipePattern pattern = RecipePattern.fromNbt(nbt.getCompound("pattern"), lookup);
        CraftingJob job = new CraftingJob(pattern, nbt.getInt("totalProgress"));
        job.progress = nbt.getInt("progress");
        job.completed = nbt.getBoolean("completed");
        return job;
    }
}
