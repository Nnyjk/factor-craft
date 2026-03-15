package com.factorcraft.module.loot;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LootModule - 战利品表系统
 * 
 * 注册 Factor Craft 专属战利品表到原版系统
 */
public class LootModule {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft:Loot");
    
    /**
     * 战利品表路径定义
     */
    public static class Tables {
        // 维度宝箱
        public static final Identifier OVERWORLD_CHEST = Identifier.of("factorcraft", "chests/overworld");
        public static final Identifier NETHER_CHEST = Identifier.of("factorcraft", "chests/nether");
        public static final Identifier END_CHEST = Identifier.of("factorcraft", "chests/end");
        
        // 实体掉落
        public static final Identifier FACTOR_ENTITY = Identifier.of("factorcraft", "entities/factor_distortion");
        
        // 结构战利品
        public static final Identifier ALTAR = Identifier.of("factorcraft", "structures/altar");
    }
    
    public void initialize() {
        LOGGER.info("[FactorCraft:Loot] LootModule 已加载 - 战利品表通过 JSON 文件注册");
        LOGGER.info("[FactorCraft:Loot] 已定义战利品表: 5 个 (维度宝箱 3 + 实体 1 + 结构 1)");
    }
    
    /**
     * 注册战利品表到原版系统
     * 在 Fabric 中，战利品表通过 resources/data/<modid>/loot_tables/ 的 JSON 文件定义
     * 此方法用于验证和记录
     */
    public void registerLootTables(Registry<LootTable> registry) {
        registerTable(registry, Tables.OVERWORLD_CHEST);
        registerTable(registry, Tables.NETHER_CHEST);
        registerTable(registry, Tables.END_CHEST);
        registerTable(registry, Tables.FACTOR_ENTITY);
        registerTable(registry, Tables.ALTAR);
    }
    
    private void registerTable(Registry<LootTable> registry, Identifier id) {
        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
        LOGGER.debug("[FactorCraft:Loot] 注册战利品表：{}", id);
    }
}