package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;

/**
 * 无死亡挑战条件 - 在不死亡的情况下完成其他条件
 */
public class NoDeathCondition implements QuestCondition {
    
    private final QuestCondition innerCondition;
    private boolean hasDied;
    private int initialDeaths;
    private boolean initialized;
    
    /**
     * 创建无死亡条件
     * @param innerCondition 内部条件
     */
    public NoDeathCondition(QuestCondition innerCondition) {
        this.innerCondition = innerCondition;
        this.hasDied = false;
        this.initialDeaths = 0;
        this.initialized = false;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.NO_DEATH;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        // 仅服务端玩家支持统计查询
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return innerCondition.check(player, context);
        }
        
        // 首次检查时记录当前死亡次数
        if (!initialized) {
            initialDeaths = serverPlayer.getStatHandler().getStat(Stats.CUSTOM, Stats.DEATHS);
            initialized = true;
        }
        
        // 检查是否死亡
        if (serverPlayer.getStatHandler().getStat(Stats.CUSTOM, Stats.DEATHS) > initialDeaths) {
            hasDied = true;
            return false;
        }
        
        // 检查内部条件
        return innerCondition.check(player, context);
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        if (hasDied) return 0.0f;
        return innerCondition.getProgress(player, context);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("inner", innerCondition.toNbt(registries));
        nbt.putBoolean("has_died", hasDied);
        nbt.putInt("initial_deaths", initialDeaths);
        nbt.putBoolean("initialized", initialized);
        return nbt;
    }
    
    /**
     * 检查是否已死亡
     */
    public boolean hasDied() {
        return hasDied;
    }
    
    /**
     * 重置条件（允许重试）
     */
    public void reset() {
        hasDied = false;
        initialized = false;
        initialDeaths = 0;
    }
    
    public QuestCondition getInnerCondition() { return innerCondition; }
}