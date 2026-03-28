package com.factorcraft.module.combat.defense;

import com.factorcraft.module.combat.CombatModule;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 防御塔注册表
 */
public class DefenseTowers {
    
    public static final ArrowTowerBlock ARROW_TOWER;
    public static final FrostTowerBlock FROST_TOWER;
    public static final LightningTowerBlock LIGHTNING_TOWER;
    public static final FactorTowerBlock FACTOR_TOWER;
    
    private static final Identifier ARROW_ID = Identifier.of(CombatModule.MOD_ID, "arrow_tower");
    private static final Identifier FROST_ID = Identifier.of(CombatModule.MOD_ID, "frost_tower");
    private static final Identifier LIGHTNING_ID = Identifier.of(CombatModule.MOD_ID, "lightning_tower");
    private static final Identifier FACTOR_ID = Identifier.of(CombatModule.MOD_ID, "factor_tower");
    
    static {
        RegistryKey<Block> arrowKey = RegistryKey.of(RegistryKeys.BLOCK, ARROW_ID);
        ARROW_TOWER = new ArrowTowerBlock(AbstractBlock.Settings.create()
            .registryKey(arrowKey)
            .hardness(3.0F)
            .requiresTool());
        
        RegistryKey<Block> frostKey = RegistryKey.of(RegistryKeys.BLOCK, FROST_ID);
        FROST_TOWER = new FrostTowerBlock(AbstractBlock.Settings.create()
            .registryKey(frostKey)
            .hardness(3.0F)
            .requiresTool());
        
        RegistryKey<Block> lightningKey = RegistryKey.of(RegistryKeys.BLOCK, LIGHTNING_ID);
        LIGHTNING_TOWER = new LightningTowerBlock(AbstractBlock.Settings.create()
            .registryKey(lightningKey)
            .hardness(3.0F)
            .requiresTool());
        
        RegistryKey<Block> factorKey = RegistryKey.of(RegistryKeys.BLOCK, FACTOR_ID);
        FACTOR_TOWER = new FactorTowerBlock(AbstractBlock.Settings.create()
            .registryKey(factorKey)
            .hardness(5.0F)
            .requiresTool());
    }
    
    public static void register() {
        Registry.register(Registries.BLOCK, ARROW_ID, ARROW_TOWER);
        Registry.register(Registries.BLOCK, FROST_ID, FROST_TOWER);
        Registry.register(Registries.BLOCK, LIGHTNING_ID, LIGHTNING_TOWER);
        Registry.register(Registries.BLOCK, FACTOR_ID, FACTOR_TOWER);
        
        // 注册 BlockItem
        RegistryKey<Item> arrowItemKey = RegistryKey.of(RegistryKeys.ITEM, ARROW_ID);
        Registry.register(Registries.ITEM, ARROW_ID, new BlockItem(ARROW_TOWER, new Item.Settings().registryKey(arrowItemKey)));
        
        RegistryKey<Item> frostItemKey = RegistryKey.of(RegistryKeys.ITEM, FROST_ID);
        Registry.register(Registries.ITEM, FROST_ID, new BlockItem(FROST_TOWER, new Item.Settings().registryKey(frostItemKey)));
        
        RegistryKey<Item> lightningItemKey = RegistryKey.of(RegistryKeys.ITEM, LIGHTNING_ID);
        Registry.register(Registries.ITEM, LIGHTNING_ID, new BlockItem(LIGHTNING_TOWER, new Item.Settings().registryKey(lightningItemKey)));
        
        RegistryKey<Item> factorItemKey = RegistryKey.of(RegistryKeys.ITEM, FACTOR_ID);
        Registry.register(Registries.ITEM, FACTOR_ID, new BlockItem(FACTOR_TOWER, new Item.Settings().registryKey(factorItemKey)));
    }
}
