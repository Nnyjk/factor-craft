package com.factorcraft.module.profession.passive;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * Factor工程师被动效果
 * 
 * 核心定位：生产建造、自动化工厂
 * 被动效果主题：机器效率、挖掘速度、Factor产量
 */
public class EngineerPassives {
    
    private static final List<PassiveEffect> ALL_PASSIVES = List.of(
        new MachineEfficiency(),
        new MiningSpeed(),
        new FactorYield()
    );
    
    public static List<PassiveEffect> getAllPassives() {
        return ALL_PASSIVES;
    }
    
    /**
     * 机器效率 - 提升交互速度 10%
     * 解锁等级：5
     */
    public static class MachineEfficiency extends AttributePassiveEffect {
        
        public static final String ID = "machine_efficiency";
        public static final String DISPLAY_NAME = "机器效率";
        public static final String DESCRIPTION = "交互速度提升10%";
        public static final int UNLOCK_LEVEL = 5;
        
        public MachineEfficiency() {
            super(
                ID,
                DISPLAY_NAME,
                DESCRIPTION,
                ProfessionType.ENGINEER,
                UNLOCK_LEVEL,
                EntityAttributes.ATTACK_SPEED,
                0.1,
                EntityAttributeModifier.Operation.ADD_VALUE
            );
        }
    }
    
    /**
     * 快速挖掘 - 挖掘速度提升 15%
     * 解锁等级：10
     */
    public static class MiningSpeed extends PassiveEffect {
        
        public static final String ID = "mining_speed";
        public static final String DISPLAY_NAME = "快速挖掘";
        public static final String DESCRIPTION = "挖掘速度提升15%";
        public static final int UNLOCK_LEVEL = 10;
        
        public MiningSpeed() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.ENGINEER, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:mining_speed");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:mining_speed");
        }
        
        public static boolean hasBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:mining_speed");
        }
    }
    
    /**
     * Factor产量 - Factor产量提升 20%
     * 解锁等级：15
     */
    public static class FactorYield extends PassiveEffect {
        
        public static final String ID = "factor_yield";
        public static final String DISPLAY_NAME = "Factor产量";
        public static final String DESCRIPTION = "Factor产量提升20%";
        public static final int UNLOCK_LEVEL = 15;
        
        public FactorYield() {
            super(ID, DISPLAY_NAME, DESCRIPTION, ProfessionType.ENGINEER, UNLOCK_LEVEL);
        }
        
        @Override
        public void apply(ServerPlayerEntity player) {
            player.getCommandTags().add("factorcraft:engineer_yield_bonus");
        }
        
        @Override
        public void remove(ServerPlayerEntity player) {
            player.getCommandTags().remove("factorcraft:engineer_yield_bonus");
        }
        
        public static boolean hasYieldBonus(ServerPlayerEntity player) {
            return player.getCommandTags().contains("factorcraft:engineer_yield_bonus");
        }
    }
}