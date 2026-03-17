package com.factorcraft.module.network;

import com.factorcraft.module.network.item.FactorScannerItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 网络扫描仪注册
 */
public class ModScanners {
    
    public static final Item BASIC_SCANNER = register(
        "basic_scanner",
        new FactorScannerItem(FactorScannerItem.ScannerTier.BASIC, new Item.Settings())
    );
    
    public static final Item ADVANCED_SCANNER = register(
        "advanced_scanner",
        new FactorScannerItem(FactorScannerItem.ScannerTier.ADVANCED, new Item.Settings())
    );
    
    public static final Item PROFESSIONAL_SCANNER = register(
        "professional_scanner",
        new FactorScannerItem(FactorScannerItem.ScannerTier.PROFESSIONAL, new Item.Settings())
    );
    
    public static final Item MASTER_SCANNER = register(
        "master_scanner",
        new FactorScannerItem(FactorScannerItem.ScannerTier.MASTER, new Item.Settings())
    );
    
    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, 
            Identifier.of("factorcraft", name), item);
    }
    
    public static void register() {
        // 静态初始化时已注册
    }
}
