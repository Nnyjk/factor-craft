package com.factorcraft.registry;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.technology.block.ModBlocks;
import com.factorcraft.module.technology.item.ModItems;
import com.factorcraft.module.technology.machine.ModMachines;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 创造模式标签页 - Fabric 1.21.4
 */
public class ModItemGroups {
    
    private static final String MOD_ID = "factorcraft";
    
    public static final RegistryKey<ItemGroup> FACTOR_CRAFT_KEY = 
        RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "general"));
    
    // 延迟初始化图标，避免静态初始化顺序问题
    private static ItemStack getIcon() {
        try {
            return new ItemStack(ModMachines.EXTRACTOR_CORE_T1);
        } catch (Exception e) {
            return new ItemStack(Items.BEACON);
        }
    }
    
    public static final ItemGroup FACTOR_CRAFT = FabricItemGroup.builder()
        .icon(() -> getIcon())
        .displayName(Text.translatable("itemGroup.factorcraft.general"))
        .entries((displayContext, entries) -> {
            // ========== 核心机器 ==========
            // 提取核心
            entries.add(ModMachines.EXTRACTOR_CORE_T1);
            entries.add(ModMachines.EXTRACTOR_CORE_T2);
            entries.add(ModMachines.EXTRACTOR_CORE_T3);
            entries.add(ModMachines.EXTRACTOR_CORE_T4);
            entries.add(ModMachines.EXTRACTOR_CORE_T5);
            
            // 消耗核心
            entries.add(ModMachines.CONSUMER_CORE_T1);
            entries.add(ModMachines.CONSUMER_CORE_T2);
            entries.add(ModMachines.CONSUMER_CORE_T3);
            entries.add(ModMachines.CONSUMER_CORE_T4);
            entries.add(ModMachines.CONSUMER_CORE_T5);
            
            // 合成核心
            entries.add(ModMachines.SYNTHESIZER_CORE_T1);
            entries.add(ModMachines.SYNTHESIZER_CORE_T2);
            entries.add(ModMachines.SYNTHESIZER_CORE_T3);
            entries.add(ModMachines.SYNTHESIZER_CORE_T4);
            entries.add(ModMachines.SYNTHESIZER_CORE_T5);
            
            // 培育核心
            entries.add(ModMachines.CULTIVATOR_CORE_T1);
            entries.add(ModMachines.CULTIVATOR_CORE_T2);
            entries.add(ModMachines.CULTIVATOR_CORE_T3);
            entries.add(ModMachines.CULTIVATOR_CORE_T4);
            entries.add(ModMachines.CULTIVATOR_CORE_T5);
            
            // ========== 传输系统 ==========
            entries.add(ModBlocks.CONDUIT_T1);
            entries.add(ModBlocks.CONDUIT_T2);
            entries.add(ModBlocks.CONDUIT_T3);
            entries.add(ModBlocks.CONDUIT_T4);
            entries.add(ModBlocks.CONDUIT_T5);
            entries.add(ModBlocks.TANK);
            entries.add(ModBlocks.PUMP);
            
            // ========== 特性方块 ==========
            entries.add(ModBlocks.TRAIT_SHARP);
            entries.add(ModBlocks.TRAIT_STURDY);
            entries.add(ModBlocks.TRAIT_PROTECTIVE);
            entries.add(ModBlocks.TRAIT_ENERGETIC);
            entries.add(ModBlocks.TRAIT_CATALYTIC);
            entries.add(ModBlocks.TRAIT_STABILIZING);
            
            // ========== 建筑方块 ==========
            entries.add(ModBlocks.BUILDING_T1);
            entries.add(ModBlocks.BUILDING_T2);
            entries.add(ModBlocks.BUILDING_T3);
            entries.add(ModBlocks.BUILDING_T4);
            entries.add(ModBlocks.BUILDING_T5);
            
            // ========== 特性水晶 ==========
            entries.add(ModItems.CRYSTAL_SHARP);
            entries.add(ModItems.CRYSTAL_STURDY);
            entries.add(ModItems.CRYSTAL_PROTECTIVE);
            entries.add(ModItems.CRYSTAL_ENERGETIC);
            entries.add(ModItems.CRYSTAL_CATALYTIC);
            
            // ========== 线圈 ==========
            entries.add(ModItems.COIL_T1);
            entries.add(ModItems.COIL_T2);
            entries.add(ModItems.COIL_T3);
            entries.add(ModItems.COIL_T4);
            entries.add(ModItems.COIL_T5);
            
            // ========== 电路 ==========
            entries.add(ModItems.CIRCUIT_BASIC);
            entries.add(ModItems.CIRCUIT_ADVANCED);
            entries.add(ModItems.CIRCUIT_ELITE);
        })
        .build();
    
    public static void register() {
        Registry.register(Registries.ITEM_GROUP, FACTOR_CRAFT_KEY, FACTOR_CRAFT);
        FactorCraftMod.LOGGER.info("[FactorCraft:Registry] 创造模式标签页已注册");
    }
}