package com.factorcraft.module.network;

import com.factorcraft.module.network.item.FactorScannerItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Factor 网络扫描仪注册
 */
public class ModScanners {
    
    private static final String MOD_ID = "factorcraft";
    
    public static final Item BASIC_SCANNER = registerScanner(
        "basic_scanner",
        FactorScannerItem.ScannerTier.BASIC
    );
    
    public static final Item ADVANCED_SCANNER = registerScanner(
        "advanced_scanner",
        FactorScannerItem.ScannerTier.ADVANCED
    );
    
    public static final Item PROFESSIONAL_SCANNER = registerScanner(
        "professional_scanner",
        FactorScannerItem.ScannerTier.PROFESSIONAL
    );
    
    public static final Item MASTER_SCANNER = registerScanner(
        "master_scanner",
        FactorScannerItem.ScannerTier.MASTER
    );
    
    /**
     * 注册扫描仪物品
     */
    private static Item registerScanner(String name, FactorScannerItem.ScannerTier tier) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = new FactorScannerItem(tier, 
            new Item.Settings().registryKey(key).maxDamage(tier.maxDurability));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    public static void register() {
        // 静态初始化时已注册
    }
}