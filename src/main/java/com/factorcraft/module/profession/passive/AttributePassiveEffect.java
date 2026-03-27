package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 属性型被动效果
 * 
 * 提供固定的属性加成，使用 Minecraft 的 AttributeModifier 系统
 */
public class AttributePassiveEffect extends PassiveEffect {
    
    private final RegistryEntry<EntityAttribute> attribute;
    private final double value;
    private final EntityAttributeModifier.Operation operation;
    private final Identifier modifierId;
    
    public AttributePassiveEffect(String id, String displayName, String description,
                                  ProfessionType professionType, int unlockLevel,
                                  RegistryEntry<EntityAttribute> attribute,
                                  double value, EntityAttributeModifier.Operation operation) {
        super(id, displayName, description, professionType, unlockLevel);
        this.attribute = attribute;
        this.value = value;
        this.operation = operation;
        this.modifierId = Identifier.of("factorcraft", "passive_" + id);
    }
    
    @Override
    public void apply(ServerPlayerEntity player) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        if (attributeInstance == null) {
            return;
        }
        
        remove(player);
        
        EntityAttributeModifier modifier = new EntityAttributeModifier(
            modifierId,
            value,
            operation
        );
        attributeInstance.addPersistentModifier(modifier);
    }
    
    @Override
    public void remove(ServerPlayerEntity player) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(modifierId);
        }
    }
    
    public RegistryEntry<EntityAttribute> getAttribute() {
        return attribute;
    }
    
    public double getValue() {
        return value;
    }
    
    public EntityAttributeModifier.Operation getOperation() {
        return operation;
    }
}