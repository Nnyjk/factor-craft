package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * 限时挑战条件 - 在指定时间内完成其他条件
 */
public class TimedCondition implements QuestCondition {
    
    private final QuestCondition innerCondition;
    private final long timeLimitTicks;
    private long startTick;
    private boolean started;
    private boolean failed;
    
    /**
     * 创建限时条件
     * @param innerCondition 内部条件
     * @param timeLimitSeconds 时间限制（秒）
     */
    public TimedCondition(QuestCondition innerCondition, int timeLimitSeconds) {
        this.innerCondition = innerCondition;
        this.timeLimitTicks = timeLimitSeconds * 20L; // 转换为 ticks
        this.startTick = -1;
        this.started = false;
        this.failed = false;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.TIMED;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        if (failed) return false;
        if (!started) {
            startTick = player.getWorld().getTime();
            started = true;
        }
        
        // 检查时间是否用尽
        long elapsed = player.getWorld().getTime() - startTick;
        if (elapsed > timeLimitTicks) {
            failed = true;
            return false;
        }
        
        // 检查内部条件
        return innerCondition.check(player, context);
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        if (failed) return 0.0f;
        if (!started) return innerCondition.getProgress(player, context);
        
        long elapsed = player.getWorld().getTime() - startTick;
        float timeProgress = 1.0f - (float) elapsed / timeLimitTicks;
        float conditionProgress = innerCondition.getProgress(player, context);
        
        // 返回较小的进度
        return Math.min(timeProgress, conditionProgress);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("inner", innerCondition.toNbt(registries));
        nbt.putLong("time_limit", timeLimitTicks);
        nbt.putLong("start_tick", startTick);
        nbt.putBoolean("started", started);
        nbt.putBoolean("failed", failed);
        return nbt;
    }
    
    /**
     * 获取剩余时间（秒）
     */
    public int getRemainingSeconds(PlayerEntity player) {
        if (!started || failed) return 0;
        long elapsed = player.getWorld().getTime() - startTick;
        long remaining = timeLimitTicks - elapsed;
        return Math.max(0, (int) (remaining / 20));
    }
    
    /**
     * 重置计时器
     */
    public void reset() {
        startTick = -1;
        started = false;
        failed = false;
    }
    
    public QuestCondition getInnerCondition() { return innerCondition; }
    public long getTimeLimitTicks() { return timeLimitTicks; }
    public boolean isFailed() { return failed; }
}