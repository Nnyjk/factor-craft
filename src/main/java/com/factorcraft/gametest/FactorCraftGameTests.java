package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

/**
 * Factor Craft Game Test 入口
 * 
 * 运行方式:
 * 1. ./gradlew runGameTest - 自动运行所有测试
 * 2. 游戏内执行 /test run <test_name>
 */
public class FactorCraftGameTests {
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void blockRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing block registration...");
        
        // 四大核心机器 (T1-T5)
        String[] machineTypes = {"extractor", "consumer", "synthesizer", "cultivator"};
        for (String type : machineTypes) {
            for (int tier = 1; tier <= 5; tier++) {
                assertRegistered(context, "factor_machine_" + type + "_core_t" + tier);
            }
        }
        
        // 导管 (T1-T5)
        for (int i = 1; i <= 5; i++) {
            assertRegistered(context, "factor_machine_conduit_t" + i);
        }
        
        // 其他机器
        assertRegistered(context, "factor_machine_tank");
        assertRegistered(context, "factor_machine_pump");
        
        // 特性方块
        assertRegistered(context, "factor_block_trait_sharp");
        assertRegistered(context, "factor_block_trait_sturdy");
        assertRegistered(context, "factor_block_trait_protective");
        assertRegistered(context, "factor_block_trait_energetic");
        assertRegistered(context, "factor_block_trait_catalytic");
        assertRegistered(context, "factor_block_trait_stabilizing");
        
        // 建筑方块 (T1-T5)
        for (int i = 1; i <= 5; i++) {
            assertRegistered(context, "factor_block_building_t" + i);
        }
        
        // 其他方块
        assertRegistered(context, "factor_block_anchor");
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void itemRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing item registration...");
        
        // 特性水晶
        assertItemRegistered(context, "factor_item_crystal_sharp");
        assertItemRegistered(context, "factor_item_crystal_sturdy");
        assertItemRegistered(context, "factor_item_crystal_protective");
        assertItemRegistered(context, "factor_item_crystal_energetic");
        assertItemRegistered(context, "factor_item_crystal_catalytic");
        
        // 线圈 (T1-T5)
        for (int i = 1; i <= 5; i++) {
            assertItemRegistered(context, "factor_item_coil_t" + i);
        }
        
        // 电路
        assertItemRegistered(context, "factor_item_circuit_basic");
        assertItemRegistered(context, "factor_item_circuit_advanced");
        assertItemRegistered(context, "factor_item_circuit_elite");
        
        context.complete();
    }
    
    private static void assertRegistered(TestContext context, String blockId) {
        var block = net.minecraft.registry.Registries.BLOCK.get(Identifier.of("factorcraft", blockId));
        if (block == null || block == net.minecraft.block.Blocks.AIR) {
            throw new AssertionError("Block not registered: " + blockId);
        }
    }
    
    private static void assertItemRegistered(TestContext context, String itemId) {
        var item = net.minecraft.registry.Registries.ITEM.get(Identifier.of("factorcraft", itemId));
        if (item == null) {
            throw new AssertionError("Item not registered: " + itemId);
        }
    }
}