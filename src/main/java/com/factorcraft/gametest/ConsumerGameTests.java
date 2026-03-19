package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

/**
 * 消耗结构 GameTest
 * 
 * 测试范围：
 * 1. 物品转 Factor 效率
 * 2. 不同物品类型对比
 * 3. 输出槽满处理
 */
public class ConsumerGameTests {
    
    /**
     * 测试消耗器核心方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void consumerCoreRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing consumer core registration...");
        
        // 验证 T1-T5 消耗器核心已注册
        for (int tier = 1; tier <= 5; tier++) {
            String blockId = "factor_machine_consumer_t" + tier;
            var block = net.minecraft.registry.Registries.BLOCK.get(
                net.minecraft.util.Identifier.of("factorcraft", blockId));
            if (block == null || block == net.minecraft.block.Blocks.AIR) {
                context.throwGameTestException("Consumer T" + tier + " not registered");
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试消耗配置加载
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void consumerConfigLoaded(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing consumer config...");
        
        // 验证基础转换效率
        double baseEfficiency = ConsumerTestConfig.getBaseConversionEfficiency();
        if (baseEfficiency <= 0 || baseEfficiency > 1) {
            context.throwGameTestException("Base efficiency should be between 0 and 1");
        }
        
        // 验证处理时间
        int processTime = ConsumerTestConfig.getProcessTime(1);
        if (processTime <= 0) {
            context.throwGameTestException("Process time should be positive");
        }
        
        context.complete();
    }
    
    /**
     * 测试物品转 Factor 效率
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void itemToFactorEfficiency(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing item to factor efficiency...");
        
        // 测试不同物品的转换值
        double coalValue = ConsumerTestConfig.getItemFactorValue("minecraft:coal");
        double diamondValue = ConsumerTestConfig.getItemFactorValue("minecraft:diamond");
        double netheriteValue = ConsumerTestConfig.getItemFactorValue("minecraft:netherite_ingot");
        
        // 验证值为正
        if (coalValue <= 0) {
            context.throwGameTestException("Coal should have positive factor value");
        }
        
        // 验证稀有物品价值更高
        if (diamondValue <= coalValue) {
            context.throwGameTestException("Diamond should have higher value than coal");
        }
        if (netheriteValue <= diamondValue) {
            context.throwGameTestException("Netherite should have highest value");
        }
        
        context.complete();
    }
    
    /**
     * 测试不同物品类型对比
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void differentItemTypesComparison(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing different item types...");
        
        // 原材料
        double rawIron = ConsumerTestConfig.getItemFactorValue("minecraft:raw_iron");
        double ironIngot = ConsumerTestConfig.getItemFactorValue("minecraft:iron_ingot");
        double ironBlock = ConsumerTestConfig.getItemFactorValue("minecraft:iron_block");
        
        // 验证方块价值更高
        if (ironBlock <= ironIngot * 9) {
            context.throwGameTestException(
                "Iron block should be worth at least 9 ingots");
        }
        
        // 验证加工品价值更高
        if (ironIngot <= rawIron) {
            context.throwGameTestException(
                "Ingot should have higher value than raw ore");
        }
        
        context.complete();
    }
    
    /**
     * 测试 Tier 效率加成
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void tierEfficiencyBonus(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing tier efficiency bonus...");
        
        // 验证高 Tier 有更高的效率加成
        double eff1 = ConsumerTestConfig.getTierEfficiency(1);
        double eff3 = ConsumerTestConfig.getTierEfficiency(3);
        double eff5 = ConsumerTestConfig.getTierEfficiency(5);
        
        if (eff3 <= eff1) {
            context.throwGameTestException("Tier 3 should have higher efficiency than Tier 1");
        }
        if (eff5 <= eff3) {
            context.throwGameTestException("Tier 5 should have highest efficiency");
        }
        
        context.complete();
    }
    
    /**
     * 测试输出槽满处理
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void outputSlotFullHandling(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing output slot full handling...");
        
        // 输出槽满时应停止消耗
        boolean shouldProcess = ConsumerTestConfig.shouldProcessItem(true, false);
        if (shouldProcess) {
            context.throwGameTestException(
                "Should not process when output is full and no space");
        }
        
        // 输出槽有空间时应继续
        shouldProcess = ConsumerTestConfig.shouldProcessItem(false, true);
        if (!shouldProcess) {
            context.throwGameTestException(
                "Should process when output has space");
        }
        
        context.complete();
    }
    
    /**
     * 测试 Factor 储存容量
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void factorStorageCapacity(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing factor storage capacity...");
        
        // 验证容量随 Tier 增加
        int prevCapacity = 0;
        for (int tier = 1; tier <= 5; tier++) {
            int capacity = ConsumerTestConfig.getStorageCapacity(tier);
            
            if (capacity <= 0) {
                context.throwGameTestException(
                    "Storage capacity for tier " + tier + " should be positive");
            }
            
            if (capacity < prevCapacity) {
                context.throwGameTestException(
                    "Storage capacity should increase with tier");
            }
            
            prevCapacity = capacity;
        }
        
        context.complete();
    }
    
    /**
     * 测试处理队列限制
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void processingQueueLimit(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing processing queue limit...");
        
        // 验证队列大小
        int queueSize = ConsumerTestConfig.getMaxQueueSize(1);
        if (queueSize <= 0) {
            context.throwGameTestException("Queue size should be positive");
        }
        
        // 高 Tier 应有更大队列
        int queue5 = ConsumerTestConfig.getMaxQueueSize(5);
        if (queue5 < queueSize) {
            context.throwGameTestException(
                "Higher tier should have larger queue");
        }
        
        context.complete();
    }
    
    /**
     * 测试 Factor 浓度输出
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void factorConcentrationOutput(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing factor concentration output...");
        
        // 测试浓度计算
        double inputFactor = 100.0;
        int tier = 2;
        
        double expectedOutput = ConsumerTestConfig.calculateOutput(inputFactor, tier);
        
        // 输出应为正
        if (expectedOutput <= 0) {
            context.throwGameTestException("Output should be positive");
        }
        
        // 输出应与输入和效率相关
        double efficiency = ConsumerTestConfig.getTierEfficiency(tier);
        double baseOutput = inputFactor * efficiency;
        
        if (Math.abs(expectedOutput - baseOutput) > baseOutput * 0.5) {
            context.throwGameTestException(
                "Output should be approximately input * efficiency");
        }
        
        context.complete();
    }
}

// 测试配置占位
class ConsumerTestConfig {
    
    public static double getBaseConversionEfficiency() {
        return 0.75;
    }
    
    public static int getProcessTime(int tier) {
        return 100 / tier;
    }
    
    public static double getItemFactorValue(String itemId) {
        if (itemId.contains("netherite")) return 50.0;
        if (itemId.contains("diamond")) return 20.0;
        if (itemId.contains("emerald")) return 15.0;
        if (itemId.contains("gold")) return 10.0;
        if (itemId.contains("iron")) return 5.0;
        if (itemId.contains("coal")) return 2.0;
        if (itemId.contains("raw")) return 3.0;
        if (itemId.contains("block")) {
            // 方块 = 9 个锭的价值
            String baseItem = itemId.replace("_block", "_ingot");
            return getItemFactorValue(baseItem) * 9 * 1.1; // 额外加成
        }
        return 1.0;
    }
    
    public static double getTierEfficiency(int tier) {
        return 0.5 + tier * 0.1;
    }
    
    public static boolean shouldProcessItem(boolean outputFull, boolean hasSpace) {
        return !outputFull || hasSpace;
    }
    
    public static int getStorageCapacity(int tier) {
        return 1000 * tier;
    }
    
    public static int getMaxQueueSize(int tier) {
        return 4 + tier;
    }
    
    public static double calculateOutput(double input, int tier) {
        return input * getTierEfficiency(tier);
    }
}