package com.factorcraft.module.loot;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.loot.handler.EntityDropHandler;
import com.factorcraft.module.loot.handler.BlockDropHandler;
import com.factorcraft.module.loot.modifier.FactorConcentrationLootModifier;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * LootModule - 战利品表系统
 * 
 * 功能:
 * - Factor 碎片掉落（T1-T5）
 * - 共振核心掉落
 * - 掉落率与 Factor 浓度关联
 * - 战利品表 JSON 配置
 */
public final class LootModule implements FactorCraftModule {
    
    private static LootModule instance;
    
    public static final String MODULE_ID = "loot";
    
    // 战利品表 ID
    public static class Tables {
        public static final Identifier FACTOR_ENTITY = Identifier.of("factorcraft", "entities/factor_distortion");
        public static final Identifier OVERWORLD_CHEST = Identifier.of("factorcraft", "chests/overworld_factor_cache");
        public static final Identifier NETHER_CHEST = Identifier.of("factorcraft", "chests/nether_factor_cache");
        public static final Identifier END_CHEST = Identifier.of("factorcraft", "chests/end_factor_cache");
        public static final Identifier ALTAR = Identifier.of("factorcraft", "chests/altar_bonus");
        public static final Identifier FACTOR_NODE = Identifier.of("factorcraft", "blocks/factor_node");
        public static final Identifier RESONANCE_CLUSTER = Identifier.of("factorcraft", "blocks/resonance_cluster");
    }
    
    private LootModule() {}
    
    public static LootModule getInstance() {
        if (instance == null) {
            instance = new LootModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return MODULE_ID;
    }
    
    @Override
    public List<String> dependencies() {
        return List.of("factor");
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 正在初始化战利品系统...");
        
        // 注册物品
        FactorShardItem.registerAll();
        ResonanceCoreItem.register();
        
        // 注册掉落处理器
        EntityDropHandler.register();
        BlockDropHandler.register();
        
        // 注册战利品修改器
        FactorConcentrationLootModifier.register();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 战利品系统已初始化");
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 重新加载战利品配置...");
        // 配置热重载逻辑
    }
    
    /**
     * 注册战利品表到原版注册表
     */
    public void registerLootTables(Registry<LootTable> registry) {
        registerTable(registry, Tables.FACTOR_ENTITY);
        registerTable(registry, Tables.OVERWORLD_CHEST);
        registerTable(registry, Tables.NETHER_CHEST);
        registerTable(registry, Tables.END_CHEST);
        registerTable(registry, Tables.ALTAR);
        registerTable(registry, Tables.FACTOR_NODE);
        registerTable(registry, Tables.RESONANCE_CLUSTER);
    }
    
    private void registerTable(Registry<LootTable> registry, Identifier id) {
        RegistryKey<LootTable> key = RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
        FactorCraftMod.LOGGER.debug("[FactorCraft:Loot] 注册战利品表：{}", id);
    }
}