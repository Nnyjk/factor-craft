package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * Factor 吸收条件 - 检测玩家是否吸收指定量的 Factor
 */
public class FactorAbsorbCondition implements QuestCondition {
    
    private final String factorType;
    private final double requiredAmount;
    private double absorbedAmount;
    
    public FactorAbsorbCondition(double requiredAmount) {
        this("any", requiredAmount);
    }
    
    public FactorAbsorbCondition(String factorType, double requiredAmount) {
        this.factorType = factorType;
        this.requiredAmount = requiredAmount;
        this.absorbedAmount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.FACTOR_ABSORB;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return absorbedAmount >= requiredAmount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return (float) Math.min(1.0, absorbedAmount / requiredAmount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("factor_type", factorType);
        nbt.putDouble("required", requiredAmount);
        nbt.putDouble("current", absorbedAmount);
        return nbt;
    }
    
    public void onAbsorb(double amount) {
        this.absorbedAmount += amount;
    }
    
    public void onAbsorb(String type, double amount) {
        if ("any".equals(factorType) || factorType.equals(type)) {
            this.absorbedAmount += amount;
        }
    }
    
    public String getFactorType() { return factorType; }
    public double getRequiredAmount() { return requiredAmount; }
    public double getAbsorbedAmount() { return absorbedAmount; }
}