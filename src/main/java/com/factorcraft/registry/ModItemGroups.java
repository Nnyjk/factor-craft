package com.factorcraft.registry;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.technology.block.ModBlocks;
import com.factorcraft.module.technology.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 创造模式标签页
 */
public class ModItemGroups {
    
    public static final ItemGroup FACTOR_CRAFT = FabricItemGroup.builder()
        .icon(() -> new ItemStack(ModBlocks.FACTOR_EXTRACTOR_CORE))
        .displayName(Text.translatable("itemGroup.factorcraft.general"))
        .entries((displayContext, entries) -> {
            // 核心机器
            entries.add(ModBlocks.FACTOR_EXTRACTOR_CORE);
            entries.add(ModBlocks.FACTOR_EMITTER_CORE);
            entries.add(ModBlocks.FACTOR_UTILIZER_CORE);
            
            // 传输系统
            entries.add(ModBlocks.FACTOR_CONDUIT_T1);
            entries.add(ModBlocks.FACTOR_CONDUIT_T2);
            entries.add(ModBlocks.FACTOR_CONDUIT_T3);
            entries.add(ModBlocks.FACTOR_CONDUIT_T4);
            entries.add(ModBlocks.FACTOR_CONDUIT_T5);
            entries.add(ModBlocks.FACTOR_TANK);
            entries.add(ModBlocks.FACTOR_PUMP);
            
            // 特性方块
            entries.add(ModBlocks.SHARP_BLOCK);
            entries.add(ModBlocks.STURDY_BLOCK);
            entries.add(ModBlocks.PROTECTIVE_BLOCK);
            entries.add(ModBlocks.ENERGETIC_BLOCK);
            entries.add(ModBlocks.CATALYTIC_BLOCK);
            entries.add(ModBlocks.STABILIZING_BLOCK);
            
            // 建筑方块
            entries.add(ModBlocks.BUILDING_BLOCK_T1);
            entries.add(ModBlocks.BUILDING_BLOCK_T2);
            entries.add(ModBlocks.BUILDING_BLOCK_T3);
            entries.add(ModBlocks.BUILDING_BLOCK_T4);
            entries.add(ModBlocks.BUILDING_BLOCK_T5);
            
            // 特性水晶
            entries.add(ModItems.SHARP_CRYSTAL);
            entries.add(ModItems.STURDY_CRYSTAL);
            entries.add(ModItems.PROTECTIVE_CRYSTAL);
            entries.add(ModItems.ENERGETIC_CRYSTAL);
            entries.add(ModItems.CATALYTIC_CRYSTAL);
            
            // 升级组件
            entries.add(ModItems.EXTRACTION_COIL_T1);
            entries.add(ModItems.EXTRACTION_COIL_T2);
            entries.add(ModItems.EXTRACTION_COIL_T3);
            entries.add(ModItems.EXTRACTION_COIL_T4);
            entries.add(ModItems.EXTRACTION_COIL_T5);
            
            // 电路
            entries.add(ModItems.BASIC_CIRCUIT);
            entries.add(ModItems.ADVANCED_CIRCUIT);
            entries.add(ModItems.ELITE_CIRCUIT);
        })
        .build();
    
    public static void register() {
        Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(FactorCraftMod.MOD_ID, "general"),
            FACTOR_CRAFT
        );
        
        FactorCraftMod.LOGGER.info("[ModItemGroups] 创造模式标签页已注册");
    }
}