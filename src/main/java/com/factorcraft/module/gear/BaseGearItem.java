package com.factorcraft.module.gear;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.ToolMaterial;

/**
 * 基础装备类
 * 
 * 所有 Factor 装备的基类，提供通用的强化和数据存储功能
 */
public class BaseGearItem extends Item implements IGear {
    private final GearType gearType;
    private final ToolMaterial material;
    
    public BaseGearItem(Settings settings, GearType gearType, ToolMaterial material) {
        super(settings);
        this.gearType = gearType;
        this.material = material;
    }
    
    @Override
    public GearType getGearType() {
        return this.gearType;
    }
    
    @Override
    public GearUpgradeLevel getUpgradeLevel(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return GearUpgradeLevel.T1;
        }
        NbtCompound nbt = nbtComponent.copyNbt();
        if (nbt == null || !nbt.contains("upgrade_level")) {
            return GearUpgradeLevel.T1;
        }
        int level = nbt.getInt("upgrade_level");
        return GearUpgradeLevel.fromLevel(level);
    }
    
    @Override
    public void setUpgradeLevel(ItemStack stack, GearUpgradeLevel level) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt;
        if (nbtComponent == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbtComponent.copyNbt();
            if (nbt == null) {
                nbt = new NbtCompound();
            }
        }
        nbt.putInt("upgrade_level", level.getLevel());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    
    @Override
    public int getFactorEnergy(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {
            return 0;
        }
        NbtCompound nbt = nbtComponent.copyNbt();
        if (nbt == null || !nbt.contains("factor_energy")) {
            return 0;
        }
        return nbt.getInt("factor_energy");
    }
    
    @Override
    public void setFactorEnergy(ItemStack stack, int energy) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt;
        if (nbtComponent == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbtComponent.copyNbt();
            if (nbt == null) {
                nbt = new NbtCompound();
            }
        }
        nbt.putInt("factor_energy", energy);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    
    @Override
    public int getMaxFactorEnergy(ItemStack stack) {
        GearUpgradeLevel level = getUpgradeLevel(stack);
        return (int)(100 * level.getMultiplier());
    }
    
    @Override
    public boolean isAbilityUnlocked(ItemStack stack) {
        GearUpgradeLevel level = getUpgradeLevel(stack);
        return level.getLevel() >= 3;
    }
    
    @Override
    public void unlockAbility(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt;
        if (nbtComponent == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbtComponent.copyNbt();
            if (nbt == null) {
                nbt = new NbtCompound();
            }
        }
        nbt.putBoolean("ability_unlocked", true);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    
    /**
     * 获取强化等级显示名称
     */
    public String getUpgradeDisplayName(ItemStack stack) {
        GearUpgradeLevel level = getUpgradeLevel(stack);
        return level.getDisplayName();
    }
}
