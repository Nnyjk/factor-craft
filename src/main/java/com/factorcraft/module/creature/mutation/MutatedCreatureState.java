package com.factorcraft.module.creature.mutation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 生物变异状态
 * 
 * 跟踪单个生物的变异状态
 */
public class MutatedCreatureState {
    
    /** 生物 UUID */
    private final UUID creatureId;
    
    /** 应用的变异 ID 列表 */
    private final List<Identifier> activeMutations;
    
    /** 变异应用时间（tick） */
    private final long applyTime;
    
    /** 过期时间（临时变异，0 = 永久） */
    private long expireTime;
    
    /** 是否为永久变异 */
    private boolean isPermanent;
    
    public MutatedCreatureState(UUID creatureId) {
        this.creatureId = creatureId;
        this.activeMutations = new ArrayList<>();
        this.applyTime = 0;
        this.expireTime = 0;
        this.isPermanent = false;
    }
    
    public MutatedCreatureState(UUID creatureId, long worldTime) {
        this.creatureId = creatureId;
        this.activeMutations = new ArrayList<>();
        this.applyTime = worldTime;
        this.expireTime = 0;
        this.isPermanent = false;
    }
    
    /**
     * 添加变异
     */
    public void addMutation(Identifier mutationId, long duration, boolean permanent) {
        if (!activeMutations.contains(mutationId)) {
            activeMutations.add(mutationId);
        }
        
        if (permanent) {
            this.isPermanent = true;
            this.expireTime = 0;
        } else {
            this.expireTime = applyTime + duration;
        }
    }
    
    /**
     * 移除变异
     */
    public void removeMutation(Identifier mutationId) {
        activeMutations.remove(mutationId);
    }
    
    /**
     * 清除所有变异
     */
    public void clearMutations() {
        activeMutations.clear();
        isPermanent = false;
        expireTime = 0;
    }
    
    /**
     * 检查是否已过期
     */
    public boolean isExpired(long currentTime) {
        if (isPermanent) {
            return false;
        }
        return expireTime > 0 && currentTime > expireTime;
    }
    
    /**
     * 获取激活的变异
     */
    public List<Identifier> getActiveMutations() {
        return Collections.unmodifiableList(activeMutations);
    }
    
    /**
     * 是否有变异
     */
    public boolean hasMutations() {
        return !activeMutations.isEmpty();
    }
    
    /**
     * 是否为永久变异
     */
    public boolean isPermanent() {
        return isPermanent;
    }
    
    /**
     * 获取生物 UUID
     */
    public UUID getCreatureId() {
        return creatureId;
    }
    
    /**
     * 保存到 NBT
     */
    public NbtCompound writeToNbt(NbtCompound nbt) {
        nbt.putUuid("CreatureId", creatureId);
        nbt.putLong("ApplyTime", applyTime);
        nbt.putLong("ExpireTime", expireTime);
        nbt.putBoolean("IsPermanent", isPermanent);
        
        // 保存变异列表
        int i = 0;
        for (Identifier mutation : activeMutations) {
            nbt.putString("Mutation_" + i, mutation.toString());
            i++;
        }
        nbt.putInt("MutationCount", activeMutations.size());
        
        return nbt;
    }
    
    /**
     * 从 NBT 加载
     */
    public static MutatedCreatureState readFromNbt(NbtCompound nbt) {
        UUID creatureId = nbt.getUuid("CreatureId");
        MutatedCreatureState state = new MutatedCreatureState(creatureId, nbt.getLong("ApplyTime"));
        state.expireTime = nbt.getLong("ExpireTime");
        state.isPermanent = nbt.getBoolean("IsPermanent");
        
        int count = nbt.getInt("MutationCount");
        for (int i = 0; i < count; i++) {
            String mutationStr = nbt.getString("Mutation_" + i);
            if (!mutationStr.isEmpty()) {
                try {
                    Identifier mutation = Identifier.of(mutationStr);
                    state.activeMutations.add(mutation);
                } catch (Exception e) {
                    // 忽略无效的变异 ID
                }
            }
        }
        
        return state;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutatedCreatureState that = (MutatedCreatureState) o;
        return Objects.equals(creatureId, that.creatureId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(creatureId);
    }
}
