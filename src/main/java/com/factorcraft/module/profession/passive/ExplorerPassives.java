package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * 潮汐探索者被动效果
 * 
 * 核心定位：冒险战斗、遗迹探索
 * 被动效果主题：移动速度、水下能力、探索感知
 */
public class ExplorerPassives {
    
    private static final List<PassiveEffect> ALL_PASSIVES = List.of(
        new SwiftMovement(),
        new AquaticAdaptation(),
        new ExplorationSense()
    );
    
    public static List<PassiveEffect> getAllPassives() {
        return ALL_PASSIVES;
    }
    
    /**
     * 迅捷移动 - 移动速度提升 15%
     * 解锁等级：5
     */
    public static class SwiftMovement extends AttributePassiveEffect {
        
        public static final String ID = "swift_movement";
        public static final String DISPLAY_NAME = "迅捷移动";
        public static final String DESCRIPTION = "移动速度提升15%";
        public static final int UNLOCK_LEVEL = 5;
        
        public SwiftMovement() {
            super(
                ID,
                DISPLAY_NAME,
                DESCRIPTION,
                ProfessionType.EXPLORER,
                UNLOCK_LEVEL,
                EntityAttributes.MOVEMENT_SPEED,
                0.15,
                EntityAttributeModifier.Operation.ADD_VALUE
            );
        }
    }
    
    /**
     * 水下适应 - 水下呼吸时间延长，视野清晰
     * 解锁等级：10
     */
    public static class AquaticAdaptation extends PassiveEffect {
        
        public static final String ID = "aquatic_adaptation";
        public static final String DISPLAY_NAME = "水下适应";
        public static final String DESCRIPTION = "水下呼吸时间延长50%，视野清晰";
        public static final int UNLOCK_LEVEL = 10;
        
        public AquaticAdaptation() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.EXPLORER, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:aquatic_adaptation");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:aquatic_adaptation");
        }
        
        public static boolean hasBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:aquatic_adaptation");
        }
    }
    
    /**
     * 探索感知 - 探测附近矿物和遗迹
     * 解锁等级：15
     */
    public static class ExplorationSense extends PassiveEffect {
        
        public static final String ID = "exploration_sense";
        public static final String DISPLAY_NAME = "探索感知";
        public static final String DESCRIPTION = "探测附近矿物和遗迹";
        public static final int UNLOCK_LEVEL = 15;
        
        public ExplorationSense() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.EXPLORER, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:exploration_sense");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:exploration_sense");
        }
        
        public static boolean hasBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:exploration_sense");
        }
    }
}