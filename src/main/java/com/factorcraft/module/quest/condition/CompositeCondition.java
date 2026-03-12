package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 复合条件 - 组合多个条件（AND/OR 逻辑）
 */
public class CompositeCondition implements QuestCondition {
    
    public enum LogicType {
        AND,  // 所有条件必须满足
        OR    // 任一条件满足即可
    }
    
    private final LogicType logicType;
    private final List<QuestCondition> conditions;
    
    public CompositeCondition(LogicType logicType) {
        this.logicType = logicType;
        this.conditions = new ArrayList<>();
    }
    
    public void addCondition(QuestCondition condition) {
        this.conditions.add(condition);
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.COMPOSITE;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        if (conditions.isEmpty()) {
            return false;
        }
        
        if (logicType == LogicType.AND) {
            for (QuestCondition condition : conditions) {
                if (!condition.check(player, context)) {
                    return false;
                }
            }
            return true;
        } else { // OR
            for (QuestCondition condition : conditions) {
                if (condition.check(player, context)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        if (conditions.isEmpty()) {
            return 0.0f;
        }
        
        if (logicType == LogicType.AND) {
            // AND: 平均进度
            float total = 0.0f;
            for (QuestCondition condition : conditions) {
                total += condition.getProgress(player, context);
            }
            return total / conditions.size();
        } else { // OR
            // OR: 最大进度
            float max = 0.0f;
            for (QuestCondition condition : conditions) {
                float progress = condition.getProgress(player, context);
                if (progress > max) {
                    max = progress;
                }
            }
            return max;
        }
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("logic", logicType.name());
        NbtList list = new NbtList();
        for (QuestCondition condition : conditions) {
            list.add(condition.toNbt(registries));
        }
        nbt.put("conditions", list);
        return nbt;
    }
    
    /**
     * 从 NBT 反序列化
     */
    public static CompositeCondition fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        LogicType logic = LogicType.valueOf(nbt.getString("logic"));
        CompositeCondition composite = new CompositeCondition(logic);
        NbtList list = nbt.getList("conditions", NbtList.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound condNbt = list.getCompound(i);
            QuestCondition condition = QuestCondition.fromNbt(condNbt, registries);
            if (condition != null) {
                composite.addCondition(condition);
            }
        }
        return composite;
    }
    
    public LogicType getLogicType() { return logicType; }
    public List<QuestCondition> getConditions() { return conditions; }
}
