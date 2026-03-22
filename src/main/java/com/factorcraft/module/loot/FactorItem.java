package com.factorcraft.module.loot;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorData;
import com.factorcraft.factor.Factor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Factor 物品 - 存储完整 Factor 数据的物品
 * 
 * 用于 Factor 合成器的输入输出，支持物流系统传输
 */
public class FactorItem extends Item {
    
    private static FactorItem INSTANCE;
    
    public FactorItem() {
        super(new Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", "factor_item")))
            .maxCount(1));
        INSTANCE = this;
    }
    
    /**
     * 获取实例
     */
    public static FactorItem getInstance() {
        return INSTANCE;
    }
    
    /**
     * 创建包含 Factor 的 ItemStack
     */
    public static ItemStack createFactorStack(Factor factor) {
        ItemStack stack = new ItemStack(INSTANCE);
        stack.set(FactorCraftDataComponents.FACTOR_DATA, FactorData.of(factor));
        return stack;
    }
    
    /**
     * 从 ItemStack 中获取 Factor
     */
    public static Optional<Factor> getFactor(ItemStack stack) {
        if (stack.getItem() instanceof FactorItem) {
            FactorData data = stack.get(FactorCraftDataComponents.FACTOR_DATA);
            if (data != null) {
                return Optional.of(data.factor());
            }
        }
        return Optional.empty();
    }
    
    /**
     * 检查 ItemStack 是否包含 Factor
     */
    public static boolean hasFactor(ItemStack stack) {
        return stack.getItem() instanceof FactorItem && 
               stack.contains(FactorCraftDataComponents.FACTOR_DATA);
    }
    
    /**
     * 注册 Factor 物品
     */
    public static void register() {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", "factor_item"), new FactorItem());
    }
}