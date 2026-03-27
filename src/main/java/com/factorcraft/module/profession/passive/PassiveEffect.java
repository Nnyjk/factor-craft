package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 被动效果基类
 * 
 * 被动效果提供常驻属性加成或触发型效果
 * 与主动技能不同，被动效果自动生效，无需玩家操作
 */
public abstract class PassiveEffect {
    
    protected final String id;
    protected final String displayName;
    protected final String description;
    protected final ProfessionType professionType;
    protected final int unlockLevel;
    
    protected PassiveEffect(String id, String displayName, String description, 
                           ProfessionType professionType, int unlockLevel) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.professionType = professionType;
        this.unlockLevel = unlockLevel;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ProfessionType getProfessionType() {
        return professionType;
    }
    
    public int getUnlockLevel() {
        return unlockLevel;
    }
    
    /**
     * 应用被动效果到玩家
     */
    public abstract void apply(ServerPlayerEntity player);
    
    /**
     * 移除被动效果
     */
    public abstract void remove(ServerPlayerEntity player);
    
    /**
     * 每tick更新（用于触发型效果）
     */
    public void tick(ServerPlayerEntity player) {
        // 默认空实现
    }
    
    /**
     * 创建属性修改器
     */
    protected EntityAttributeModifier createModifier(Identifier id, double value, 
                                                      EntityAttributeModifier.Operation operation) {
        return new EntityAttributeModifier(id, value, operation);
    }
    
    /**
     * 获取修改器标识
     */
    protected Identifier getModifierId() {
        return Identifier.of("factorcraft", "passive_" + id);
    }
    
    @Override
    public String toString() {
        return String.format("PassiveEffect[%s, profession=%s, unlock=%d]", 
                           id, professionType, unlockLevel);
    }
}