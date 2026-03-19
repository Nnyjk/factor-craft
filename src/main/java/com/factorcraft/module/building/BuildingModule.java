package com.factorcraft.module.building;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.building.block.*;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

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
        FACTOR_LAMP = registerBlock("factor_lamp", new FactorLampBlock());
        FACTOR_TORCH = registerBlock("factor_torch", new FactorTorchBlock());
        
        // Factor 建材
        FACTOR_GLASS = registerBlock("factor_glass", new FactorGlassBlock());
        FACTOR_CRYSTAL = registerBlock("factor_crystal_block", new FactorCrystalBlock());
        FACTOR_METAL = registerBlock("factor_metal", new FactorMetalBlock());
        FactorMetalBlock.FACTOR_METAL = FACTOR_METAL;
    }
    
    private void registerDecorativeBlocks() {
        // 科技装饰方块
        FACTOR_CONDUIT = registerBlock("factor_conduit", createDecorBlock(3.0f, 15));
        FACTOR_PANEL = registerBlock("factor_panel", createDecorBlock(4.0f, 0));
        FACTOR_PLATING = registerBlock("factor_plating", createDecorBlock(5.0f, 0));
        FACTOR_STRUT = registerBlock("factor_strut", createDecorBlock(3.0f, 0));
        FACTOR_GRATING = registerBlock("factor_grating", createDecorBlock(3.0f, 0));
        FACTOR_SCREEN = registerBlock("factor_screen", createDecorBlock(2.0f, 8));
        FACTOR_CONSOLE = registerBlock("factor_console", createDecorBlock(4.0f, 10));
        FACTOR_VENT = registerBlock("factor_vent", createDecorBlock(3.0f, 0));
        FACTOR_CABLE_TRAY = registerBlock("factor_cable_tray", createDecorBlock(2.0f, 0));
        FACTOR_PILLAR = registerBlock("factor_pillar", createDecorBlock(4.0f, 0));
        FACTOR_COLUMN = registerBlock("factor_column", createDecorBlock(5.0f, 0));
        FACTOR_PEDESTAL = registerBlock("factor_pedestal", createDecorBlock(4.0f, 0));
        FACTOR_PLATFORM = registerBlock("factor_platform", createDecorBlock(3.0f, 0));
        FACTOR_RAILING = registerBlock("factor_railing", createDecorBlock(2.0f, 0));
        RESONANCE_CLUSTER = registerBlock("resonance_cluster", createDecorBlock(2.0f, 12));
    }
    
    private Block createDecorBlock(float hardness, int luminance) {
        return new Block(Block.Settings.create()
            .strength(hardness, 6.0f)
            .luminance(luminance > 0 ? state -> luminance : state -> 0));
    }
    
    private <T extends Block> T registerBlock(String name, T block) {
        Identifier id = Identifier.of("factorcraft", name);
        Registry.register(Registries.BLOCK, id, block);
        
        // 同时注册 BlockItem
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册方块: {}", name);
        return block;
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 重新加载建筑配置...");
    }
}