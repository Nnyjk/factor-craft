package com.factorcraft.module.loot;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 共振核心 - 稀有掉落物
 */
public class ResonanceCoreItem extends Item {
    
    private static ResonanceCoreItem INSTANCE;
    
    public ResonanceCoreItem() {
        super(new Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", "resonance_core")))
            .maxCount(16));
    }
    
    /**
     * 创建共振核心
     */
    public static ItemStack createCore(int count) {
        return new ItemStack(INSTANCE, count);
    }
    
    /**
     * 创建带能量的共振核心
     */
    public static ItemStack createCoreWithEnergy(int count, int energy) {
        ItemStack stack = new ItemStack(INSTANCE, count);
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("energy", energy);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        return stack;
    }
    
    /**
     * 获取共振核心的储能
     */
    public static int getEnergy(ItemStack stack) {
        if (stack.getItem() instanceof ResonanceCoreItem) {
            NbtComponent nbt = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbt != null) {
                return nbt.copyNbt().getInt("energy");
            }
        }
        return 0;
    }
    
    /**
     * 注册物品
     */
    public static void register() {
        Identifier id = Identifier.of("factorcraft", "resonance_core");
        INSTANCE = new ResonanceCoreItem();
        Registry.register(Registries.ITEM, id, INSTANCE);
    }
}