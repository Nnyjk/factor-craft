package com.factorcraft.module.cycle.dimension.block;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.dimension.nether.block.*;
import com.factorcraft.module.cycle.dimension.end.block.*;
import com.factorcraft.module.cycle.dimension.gate.block.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 维度方块注册表
 */
public class DimensionBlocks {
    // 下界方块
    public static final Block NETHER_FACTOR_VENT = register("nether_factor_vent", 
        new NetherFactorVentBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "nether_factor_vent"))).strength(3.0f).requiresTool()));
    public static final Block NETHER_FACTOR_ORE = register("nether_factor_ore",
        new NetherFactorOreBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "nether_factor_ore"))).strength(5.0f).requiresTool()));
    
    // 末地方块
    public static final Block END_FACTOR_SHARD = register("end_factor_shard",
        new EndFactorShardBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "end_factor_shard"))).strength(3.0f).requiresTool()));
    public static final Block END_FACTOR_BEACON = register("end_factor_beacon",
        new EndFactorBeaconBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "end_factor_beacon"))).strength(3.0f).requiresTool()));
    
    // 传送门方块
    public static final Block NETHER_PORTAL_UPGRADE = register("nether_portal_upgrade",
        new NetherPortalUpgradeBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "nether_portal_upgrade"))).strength(3.0f).requiresTool()));
    public static final Block END_GATEWAY_FACTOR = register("end_gateway_factor",
        new EndGatewayFactorBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "end_gateway_factor"))).strength(3.0f).requiresTool()));
    public static final Block DIMENSIONAL_GATE = register("dimensional_gate",
        new DimensionalGateBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "dimensional_gate"))).strength(5.0f).requiresTool()));
    public static final Block GATE_CONTROLLER = register("gate_controller",
        new GateControllerBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "gate_controller"))).strength(3.0f).requiresTool()));
    public static final Block GATE_STABILIZER = register("gate_stabilizer",
        new GateStabilizerBlock(Block.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, "gate_stabilizer"))).strength(3.0f).requiresTool()));
    
    /**
     * 注册方块及其 BlockItem
     */
    private static Block register(String name, Block block) {
        Identifier id = Identifier.of(FactorCraftMod.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        // 注册 Block
        Registry.register(Registries.BLOCK, id, block);
        
        // 注册 BlockItem
        Item blockItem = new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        Registry.register(Registries.ITEM, id, blockItem);
        
        return block;
    }
    
    /**
     * 注册所有方块
     */
    public static void register() {
        // 预初始化所有方块（通过静态字段）
    }
}
