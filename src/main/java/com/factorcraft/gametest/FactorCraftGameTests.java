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
        
        // 核心方块
        assertRegistered(context, "factor_extractor_core");
        assertRegistered(context, "factor_emitter_core");
        assertRegistered(context, "factor_utilizer_core");
        
        // 导管
        for (int i = 1; i <= 5; i++) {
            assertRegistered(context, "factor_conduit_t" + i);
        }
        
        // 特性方块
        assertRegistered(context, "sharp_block");
        assertRegistered(context, "sturdy_block");
        assertRegistered(context, "protective_block");
        assertRegistered(context, "energetic_block");
        assertRegistered(context, "catalytic_block");
        assertRegistered(context, "stabilizing_block");
        
        // 建筑方块
        for (int i = 1; i <= 5; i++) {
            assertRegistered(context, "building_block_t" + i);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void itemRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing item registration...");
        
        // 水晶
        assertItemRegistered(context, "sharp_crystal");
        assertItemRegistered(context, "sturdy_crystal");
        assertItemRegistered(context, "protective_crystal");
        assertItemRegistered(context, "energetic_crystal");
        assertItemRegistered(context, "catalytic_crystal");
        
        // 线圈
        for (int i = 1; i <= 5; i++) {
            assertItemRegistered(context, "extraction_coil_t" + i);
        }
        
        // 电路
        assertItemRegistered(context, "basic_circuit");
        assertItemRegistered(context, "advanced_circuit");
        assertItemRegistered(context, "elite_circuit");
        
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