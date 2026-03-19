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
 * 
 * 测试覆盖：
 * - 方块/物品注册
 * - 提取系统 (ExtractorGameTests)
 * - 合成系统 (SynthesizerGameTests)
 * - 传递系统 (TransmitterGameTests)
 * - 消耗系统 (ConsumerGameTests)
 */
public class FactorCraftGameTests {
    
    /**
     * 测试核心机器方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void machineBlockRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing machine block registration...");
        
        // 提取器核心 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_extractor_core_t" + tier);
        }
        
        // 消费者核心 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_consumer_core_t" + tier);
        }
        
        // 合成器核心 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_synthesizer_core_t" + tier);
        }
        
        // 培养器核心 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_cultivator_core_t" + tier);
        }
        
        // 传递器 T1-T4
        for (int tier = 1; tier <= 4; tier++) {
            assertBlockRegistered(context, "factor_machine_transmitter_t" + tier);
        }
        
        // 繁育器核心 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_breeder_core_t" + tier);
        }
        
        context.complete();
    }
    
    /**
     * 测试建筑方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void buildingBlockRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing building block registration...");
        
        // 建筑方块
        String[] buildingBlocks = {
            "factor_conduit",
            "factor_panel", 
            "factor_plating",
            "factor_pillar",
            "factor_platform",
            "factor_railing",
            "resonance_cluster"
        };
        
        for (String blockId : buildingBlocks) {
            assertBlockRegistered(context, blockId);
        }
        
        // Factor 建筑方块 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_block_building_t" + tier);
        }
        
        // Trait 方块
        String[] traitBlocks = {
            "factor_block_trait_sharp",
            "factor_block_trait_sturdy",
            "factor_block_trait_protective"
        };
        
        for (String blockId : traitBlocks) {
            assertBlockRegistered(context, blockId);
        }
        
        context.complete();
    }
    
    /**
     * 测试导管方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void conduitBlockRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing conduit block registration...");
        
        // 导管 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertBlockRegistered(context, "factor_machine_conduit_t" + tier);
        }
        
        context.complete();
    }
    
    /**
     * 测试核心物品注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void itemRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing item registration...");
        
        // Factor 碎片 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertItemRegistered(context, "factor_shard_t" + tier);
        }
        
        // 特性水晶
        String[] crystals = {
            "factor_item_crystal_sharp",
            "factor_item_crystal_sturdy",
            "factor_item_crystal_protective",
            "factor_item_crystal_energetic",
            "factor_item_crystal_catalytic"
        };
        for (String crystal : crystals) {
            assertItemRegistered(context, crystal);
        }
        
        // 线圈 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertItemRegistered(context, "factor_item_coil_t" + tier);
        }
        
        // 武器 T1-T5
        for (int tier = 1; tier <= 5; tier++) {
            assertItemRegistered(context, "factor_sword_t" + tier);
            assertItemRegistered(context, "factor_pickaxe_t" + tier);
            assertItemRegistered(context, "factor_axe_t" + tier);
        }
        
        context.complete();
    }
    
    /**
     * 测试潮汐系统
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void tideSystemBasics(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing tide system...");
        
        // 验证潮汐状态枚举
        var statuses = com.factorcraft.module.factor.TideStatus.values();
        if (statuses.length < 5) {
            context.throwGameTestException("Should have at least 5 tide statuses");
        }
        
        // 验证从浓度计算状态
        var lowStatus = com.factorcraft.module.factor.TideStatus.fromConcentration(0.1);
        if (lowStatus != com.factorcraft.module.factor.TideStatus.DEPLETED) {
            context.throwGameTestException("Low concentration should give DEPLETED status");
        }
        
        var highStatus = com.factorcraft.module.factor.TideStatus.fromConcentration(0.9);
        if (highStatus != com.factorcraft.module.factor.TideStatus.OVERLOAD) {
            context.throwGameTestException("High concentration should give OVERLOAD status");
        }
        
        context.complete();
    }
    
    private static void assertBlockRegistered(TestContext context, String blockId) {
        var block = net.minecraft.registry.Registries.BLOCK.get(Identifier.of("factorcraft", blockId));
        if (block == null || block == net.minecraft.block.Blocks.AIR) {
            context.throwGameTestException("Block not registered: " + blockId);
        }
    }
    
    private static void assertItemRegistered(TestContext context, String itemId) {
        var item = net.minecraft.registry.Registries.ITEM.get(Identifier.of("factorcraft", itemId));
        if (item == null) {
            context.throwGameTestException("Item not registered: " + itemId);
        }
    }
}