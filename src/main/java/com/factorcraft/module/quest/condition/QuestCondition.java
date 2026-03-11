package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * 任务条件接口
 */
public interface QuestCondition {
    
    /**
     * 获取条件类型
     */
    QuestConditionType getType();
    
    /**
     * 检查条件是否完成
     */
    boolean check(PlayerEntity player, Object context);
    
    /**
     * 获取进度 (0.0-1.0)
     */
    float getProgress(PlayerEntity player, Object context);
    
    /**
     * 序列化为 NBT
     */
    NbtCompound toNbt(RegistryWrapper.WrapperLookup registries);
    
    /**
     * 从 NBT 反序列化
     */
    static QuestCondition fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        String typeName = nbt.getString("type");
        QuestConditionType type = QuestConditionType.fromSerializedName(typeName);
        if (type == null) {
            return null;
        }
        
        return switch (type) {
            case ITEM_PICKUP -> new ItemPickupCondition(
                Identifier.tryParse(nbt.getString("item_id")),
                nbt.getInt("required")
            );
            case ITEM_CRAFT -> new ItemCraftCondition(
                Identifier.tryParse(nbt.getString("item_id")),
                nbt.getInt("required")
            );
            case ITEM_SUBMIT -> new ItemSubmitCondition(
                Identifier.tryParse(nbt.getString("item_id")),
                nbt.getInt("required")
            );
            case ITEM_USE -> new ItemUseCondition(
                Identifier.tryParse(nbt.getString("item_id")),
                nbt.getInt("required")
            );
            case ENTITY_KILL -> {
                var cond = new EntityKillCondition(
                    Identifier.tryParse(nbt.getString("entity_id")),
                    nbt.getInt("required")
                );
                cond.onKill(nbt.getInt("current"));
                yield cond;
            }
            case BLOCK_PLACE -> new BlockPlaceCondition(
                Identifier.tryParse(nbt.getString("block_id")),
                nbt.getInt("required")
            );
            case DIMENSION_TRAVEL -> new DimensionTravelCondition(
                Identifier.tryParse(nbt.getString("dimension"))
            );
            case FACTOR_ABSORB -> new FactorAbsorbCondition(
                nbt.getDouble("required")
            );
            case COMPOSITE -> {
                // TODO: 实现 CompositeCondition 的 NBT 反序列化
                yield new CompositeCondition(CompositeCondition.LogicType.AND);
            }
        };
    }
}
