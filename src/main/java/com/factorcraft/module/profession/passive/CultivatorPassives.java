package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * 能量培育师被动效果
 * 
 * 核心定位：生物养成、变异培育
 * 被动效果主题：能量恢复、生物亲和、培育效率
 */
public class CultivatorPassives {
    
    private static final List<PassiveEffect> ALL_PASSIVES = List.of(
        new EnergyRegeneration(),
        new BioAffinity(),
        new GrowthEfficiency()
    );
    
    public static List<PassiveEffect> getAllPassives() {
        return ALL_PASSIVES;
    }
    
    /**
     * 能量恢复 - 提升能量恢复速度 20%
     * 解锁等级：5
     */
    public static class EnergyRegeneration extends AttributePassiveEffect {
        
        public static final String ID = "energy_regeneration";
        public static final String DISPLAY_NAME = "能量恢复";
        public static final String DESCRIPTION = "能量恢复速度提升20%";
        public static final int UNLOCK_LEVEL = 5;
        
        public EnergyRegeneration() {
            super(
                ID,
                DISPLAY_NAME,
                DESCRIPTION,
                ProfessionType.CULTIVATOR,
                UNLOCK_LEVEL,
                EntityAttributes.MAX_HEALTH, // 作为能量上限代理
                0.2,
                EntityAttributeModifier.Operation.ADD_VALUE
            );
        }
    }
    
    /**
     * 生物亲和 - 与生物交互距离增加 30%
     * 解锁等级：10
     */
    public static class BioAffinity extends PassiveEffect {
        
        public static final String ID = "bio_affinity";
        public static final String DISPLAY_NAME = "生物亲和";
        public static final String DESCRIPTION = "与生物交互距离增加30%";
        public static final int UNLOCK_LEVEL = 10;
        
        public BioAffinity() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.CULTIVATOR, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:bio_affinity");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:bio_affinity");
        }
        
        public static boolean hasBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:bio_affinity");
        }
    }
    
    /**
     * 培育效率 - 培育速度提升 25%
     * 解锁等级：15
     */
    public static class GrowthEfficiency extends PassiveEffect {
        
        public static final String ID = "growth_efficiency";
        public static final String DISPLAY_NAME = "培育效率";
        public static final String DESCRIPTION = "培育速度提升25%";
        public static final int UNLOCK_LEVEL = 15;
        
        public GrowthEfficiency() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.CULTIVATOR, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:growth_efficiency");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:growth_efficiency");
        }
        
        public static boolean hasBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:growth_efficiency");
        }
    }
}