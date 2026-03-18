package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * 结构发现条件 - 检测玩家是否发现特定结构
 */
public class StructureDiscoverCondition implements QuestCondition {
    
    @Nullable
    private final Identifier structureId;
    private final int requiredCount;
    private int discoveredCount;
    
    /**
     * 创建结构发现条件
     * @param structureId 结构 ID (null 表示任意结构)
     * @param requiredCount 需要发现的数量
     */
    public StructureDiscoverCondition(@Nullable Identifier structureId, int requiredCount) {
        this.structureId = structureId;
        this.requiredCount = requiredCount;
        this.discoveredCount = 0;
    }
    
    /**
     * 创建任意结构发现条件
     */
    public StructureDiscoverCondition(int requiredCount) {
        this(null, requiredCount);
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.STRUCTURE_DISCOVER;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return discoveredCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) discoveredCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        if (structureId != null) {
            nbt.putString("structure_id", structureId.toString());
        }
        nbt.putInt("required", requiredCount);
        nbt.putInt("discovered", discoveredCount);
        return nbt;
    }
    
    /**
     * 记录结构发现
     * @param discoveredId 发现的结构 ID
     * @return 是否匹配并计数
     */
    public boolean onDiscover(Identifier discoveredId) {
        if (structureId == null || structureId.equals(discoveredId)) {
            discoveredCount++;
            return true;
        }
        return false;
    }
    
    /**
     * 记录任意结构发现
     */
    public void onDiscoverAny() {
        discoveredCount++;
    }
    
    @Nullable
    public Identifier getStructureId() { return structureId; }
    public int getRequiredCount() { return requiredCount; }
    public int getDiscoveredCount() { return discoveredCount; }
}