package com.factorcraft.module.loot.modifier;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * Factor 浓度战利品修改器
 * 
 * 根据区域 Factor 浓度调整战利品掉落
 */
public class FactorConcentrationLootModifier {
    
    private FactorConcentrationLootModifier() {}
    
    /**
     * 注册战利品修改器
     */
    public static void register() {
        LootTableEvents.MODIFY.register((key, builder, source) -> {
            Identifier id = key.getValue();
            if (isVanillaChest(id)) {
                injectFactorLoot(builder);
                FactorCraftMod.LOGGER.debug("[FactorCraft:Loot] 注入 Factor 战利品到: {}", id);
            }
        });
        FactorCraftMod.LOGGER.info("[FactorCraft:Loot] 已注册战利品修改器");
    }
    
    private static boolean isVanillaChest(Identifier id) {
        String path = id.getPath();
        return path.startsWith("chests/") && id.getNamespace().equals("minecraft");
    }
    
    private static void injectFactorLoot(LootTable.Builder tableBuilder) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1))
            .with(ItemEntry.builder(com.factorcraft.module.loot.FactorShardItem.getShardItem(1)))
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)));
        
        tableBuilder.pool(pool);
    }
}