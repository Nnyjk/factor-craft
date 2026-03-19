package com.factorcraft.module.building;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.building.block.*;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;

/**
 * BuildingModule - 建筑/家具/装饰模块
 * 
 * Factor 主题装饰方块：
 * - Factor 光源（灯、火把）
 * - Factor 建材（玻璃、金属块、晶体块）
 * - Factor 管道装饰
 * - 科技装饰方块
 */
public final class BuildingModule implements FactorCraftModule {
    
    private static BuildingModule instance;
    
    // 方块实例
    public static FactorLampBlock FACTOR_LAMP;
    public static FactorTorchBlock FACTOR_TORCH;
    public static FactorGlassBlock FACTOR_GLASS;
    public static FactorCrystalBlock FACTOR_CRYSTAL;
    public static FactorMetalBlock FACTOR_METAL;
    
    // 装饰方块
    public static Block FACTOR_CONDUIT;
    public static Block FACTOR_PANEL;
    public static Block FACTOR_PLATING;
    public static Block FACTOR_STRUT;
    public static Block FACTOR_GRATING;
    public static Block FACTOR_SCREEN;
    public static Block FACTOR_CONSOLE;
    public static Block FACTOR_VENT;
    public static Block FACTOR_CABLE_TRAY;
    public static Block FACTOR_PILLAR;
    public static Block FACTOR_COLUMN;
    public static Block FACTOR_PEDESTAL;
    public static Block FACTOR_PLATFORM;
    public static Block FACTOR_RAILING;
    public static Block RESONANCE_CLUSTER;
    
    public static BuildingModule getInstance() {
        if (instance == null) {
            instance = new BuildingModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "building";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of();
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 正在初始化建筑/装饰模块...");
        
        // 注册核心方块
        registerCoreBlocks();
        
        // 注册装饰方块
        registerDecorativeBlocks();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 建筑/装饰模块已初始化");
    }
    
    private void registerCoreBlocks() {
        // Factor 光源
        FACTOR_LAMP = registerFactorBlock("factor_lamp", FactorLampBlock::new);
        FACTOR_TORCH = registerFactorBlock("factor_torch", FactorTorchBlock::new);
        
        // Factor 建材
        FACTOR_GLASS = registerFactorBlock("factor_glass", FactorGlassBlock::new);
        FACTOR_CRYSTAL = registerFactorBlock("factor_crystal_block", FactorCrystalBlock::new);
        FACTOR_METAL = registerFactorBlock("factor_metal", FactorMetalBlock::new);
        FactorMetalBlock.FACTOR_METAL = FACTOR_METAL;
    }
    
    private void registerDecorativeBlocks() {
        // 科技装饰方块
        FACTOR_CONDUIT = registerDecorBlock("factor_conduit", 3.0f, 15);
        FACTOR_PANEL = registerDecorBlock("factor_panel", 4.0f, 0);
        FACTOR_PLATING = registerDecorBlock("factor_plating", 5.0f, 0);
        FACTOR_STRUT = registerDecorBlock("factor_strut", 3.0f, 0);
        FACTOR_GRATING = registerDecorBlock("factor_grating", 3.0f, 0);
        FACTOR_SCREEN = registerDecorBlock("factor_screen", 2.0f, 8);
        FACTOR_CONSOLE = registerDecorBlock("factor_console", 4.0f, 10);
        FACTOR_VENT = registerDecorBlock("factor_vent", 3.0f, 0);
        FACTOR_CABLE_TRAY = registerDecorBlock("factor_cable_tray", 2.0f, 0);
        FACTOR_PILLAR = registerDecorBlock("factor_pillar", 4.0f, 0);
        FACTOR_COLUMN = registerDecorBlock("factor_column", 5.0f, 0);
        FACTOR_PEDESTAL = registerDecorBlock("factor_pedestal", 4.0f, 0);
        FACTOR_PLATFORM = registerDecorBlock("factor_platform", 3.0f, 0);
        FACTOR_RAILING = registerDecorBlock("factor_railing", 2.0f, 0);
        RESONANCE_CLUSTER = registerDecorBlock("resonance_cluster", 2.0f, 12);
    }
    
    /**
     * 注册 Factor 方块（构造函数接受 Identifier）
     */
    private <T extends Block> T registerFactorBlock(String name, Function<Identifier, T> factory) {
        Identifier id = Identifier.of("factorcraft", name);
        T block = factory.apply(id);
        Registry.register(Registries.BLOCK, id, block);
        
        // 同时注册 BlockItem（需要 registryKey）
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册方块: {}", name);
        return block;
    }
    
    /**
     * 注册装饰方块（简单方块）
     */
    private Block registerDecorBlock(String name, float hardness, int luminance) {
        Identifier id = Identifier.of("factorcraft", name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new Block(AbstractBlock.Settings.create()
            .registryKey(blockKey)
            .strength(hardness, 6.0f)
            .luminance(luminance > 0 ? state -> luminance : state -> 0));
        
        Registry.register(Registries.BLOCK, id, block);
        
        // 同时注册 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册装饰方块: {}", name);
        return block;
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 重新加载建筑配置...");
    }
}