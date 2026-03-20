package com.factorcraft.module.building;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.building.block.*;
import com.factorcraft.module.building.item.FactorFoodItem;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * BuildingModule - 建筑/家具/装饰模块
 * 
 * Factor 主题装饰方块：
 * - Factor 光源（灯、火把）
 * - Factor 建材（玻璃、金属块、晶体块）
 * - Factor 管道装饰
 * - 科技装饰方块
 * - Tier 装饰板（T1-T5）及其衍生方块
 * - 功能性建筑方块
 * 
 * Factor 主题食物（T1-T5）
 */
public final class BuildingModule implements FactorCraftModule {
    
    private static BuildingModule instance;
    private static final String MOD_ID = "factorcraft";
    
    // Tier 名称定义
    private static final String[] TIER_NAMES = {
        "dust_copper",    // T1 尘铜
        "shadow_steel",   // T2 暗影钢
        "stardust",       // T3 星尘
        "ancient_alloy",  // T4 远古合金
        "void_crystal"    // T5 虚空结晶
    };
    
    // 原有方块实例
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
    
    // Tier 装饰板（5个Tier）
    public static final List<DecorativeBlock> DECORATIVE_PLATES = new ArrayList<>();
    
    // Tier 装饰楼梯（5个Tier）
    public static final List<DecorativeStairsBlock> DECORATIVE_STAIRS = new ArrayList<>();
    
    // Tier 装饰台阶（5个Tier）
    public static final List<DecorativeSlabBlock> DECORATIVE_SLABS = new ArrayList<>();
    
    // Tier 装饰栅栏（5个Tier）
    public static final List<DecorativeFenceBlock> DECORATIVE_FENCES = new ArrayList<>();
    
    // Tier 装饰栅栏门（5个Tier）
    public static final List<DecorativeFenceGateBlock> DECORATIVE_FENCE_GATES = new ArrayList<>();
    
    // Tier 装饰按钮（5个Tier）
    public static final List<DecorativeButtonBlock> DECORATIVE_BUTTONS = new ArrayList<>();
    
    // Tier 装饰压力板（5个Tier）
    public static final List<DecorativePressurePlateBlock> DECORATIVE_PRESSURE_PLATES = new ArrayList<>();
    
    // Tier 装饰墙面（5个Tier）
    public static final List<DecorativeWallBlock> DECORATIVE_WALLS = new ArrayList<>();
    
    // Tier 装饰灯（5个Tier）
    public static final List<DecorativeLampBlock> DECORATIVE_LAMPS = new ArrayList<>();
    
    // 功能性建筑方块
    public static NetworkDisplayBlock NETWORK_DISPLAY;
    public static FactorPrismBlock FACTOR_PRISM;
    public static NetworkAnchorBlock NETWORK_ANCHOR;
    public static NoiseSuppressorBlock NOISE_SUPPRESSOR;
    
    // Factor 主题食物（5个Tier）
    public static final List<FactorFoodItem> FACTOR_FOODS = new ArrayList<>();
    
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
        
        // 注册 Tier 装饰板及衍生方块
        registerTierDecorativeBlocks();
        
        // 注册功能性建筑方块
        registerFunctionalBlocks();
        
        // 注册 Factor 主题食物
        registerFactorFoods();
        
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
     * 注册 Tier 装饰板及衍生方块
     */
    private void registerTierDecorativeBlocks() {
        for (int tier = 1; tier <= 5; tier++) {
            String tierName = TIER_NAMES[tier - 1];
            
            // 基础装饰板
            String plateName = "decorative_plate_" + tierName;
            DecorativeBlock plate = registerDecorativeBlock(plateName, tier, tierName);
            DECORATIVE_PLATES.add(plate);
            
            // 衍生方块
            // 楼梯
            String stairsName = "decorative_stairs_" + tierName;
            final int finalTier = tier;
            final String finalTierName = tierName;
            DecorativeStairsBlock stairs = registerBlock(stairsName, 
                id -> new DecorativeStairsBlock(id, () -> plate.getDefaultState()));
            DECORATIVE_STAIRS.add(stairs);
            
            // 台阶
            String slabName = "decorative_slab_" + tierName;
            DecorativeSlabBlock slab = registerBlock(slabName, DecorativeSlabBlock::new);
            DECORATIVE_SLABS.add(slab);
            
            // 栅栏
            String fenceName = "decorative_fence_" + tierName;
            DecorativeFenceBlock fence = registerBlock(fenceName, DecorativeFenceBlock::new);
            DECORATIVE_FENCES.add(fence);
            
            // 栅栏门
            String fenceGateName = "decorative_fence_gate_" + tierName;
            DecorativeFenceGateBlock fenceGate = registerBlock(fenceGateName, DecorativeFenceGateBlock::new);
            DECORATIVE_FENCE_GATES.add(fenceGate);
            
            // 按钮
            String buttonName = "decorative_button_" + tierName;
            DecorativeButtonBlock button = registerBlock(buttonName, DecorativeButtonBlock::new);
            DECORATIVE_BUTTONS.add(button);
            
            // 压力板
            String pressurePlateName = "decorative_pressure_plate_" + tierName;
            DecorativePressurePlateBlock pressurePlate = registerBlock(pressurePlateName, DecorativePressurePlateBlock::new);
            DECORATIVE_PRESSURE_PLATES.add(pressurePlate);
            
            // 墙面
            String wallName = "decorative_wall_" + tierName;
            DecorativeWallBlock wall = registerBlock(wallName, DecorativeWallBlock::new);
            DECORATIVE_WALLS.add(wall);
            
            // 发光灯
            String lampName = "decorative_lamp_" + tierName;
            DecorativeLampBlock lamp = registerBlock(lampName, id -> new DecorativeLampBlock(id, finalTier));
            DECORATIVE_LAMPS.add(lamp);
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 已注册 {} 个 Tier 装饰板系列", 5);
    }
    
    /**
     * 注册功能性建筑方块
     */
    private void registerFunctionalBlocks() {
        NETWORK_DISPLAY = registerFactorBlock("network_display", NetworkDisplayBlock::new);
        FACTOR_PRISM = registerFactorBlock("factor_prism", FactorPrismBlock::new);
        NETWORK_ANCHOR = registerFactorBlock("network_anchor", NetworkAnchorBlock::new);
        NOISE_SUPPRESSOR = registerFactorBlock("noise_suppressor", NoiseSuppressorBlock::new);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 已注册 {} 个功能性建筑方块", 4);
    }
    
    /**
     * 注册 Factor 主题食物
     */
    private void registerFactorFoods() {
        // T1 尘铜烤薯 - 饱食度2
        FactorFoodItem t1Food = registerFood("dust_copper_baked_potato", 1, "dust_copper", 2, 0.3f);
        FACTOR_FOODS.add(t1Food);
        
        // T2 暗影能量棒 - 饱食度4
        FactorFoodItem t2Food = registerFood("shadow_energy_bar", 2, "shadow_steel", 4, 0.5f);
        FACTOR_FOODS.add(t2Food);
        
        // T3 星尘果冻 - 饱食度3
        FactorFoodItem t3Food = registerFood("stardust_jelly", 3, "stardust", 3, 0.4f);
        FACTOR_FOODS.add(t3Food);
        
        // T4 远古合金能量餐 - 饱食度6
        FactorFoodItem t4Food = registerFood("ancient_alloy_energy_meal", 4, "ancient_alloy", 6, 0.8f);
        FACTOR_FOODS.add(t4Food);
        
        // T5 虚空结晶药剂 - 饱食度5
        FactorFoodItem t5Food = registerFood("void_crystal_potion", 5, "void_crystal", 5, 0.6f);
        FACTOR_FOODS.add(t5Food);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 已注册 {} 个 Factor 主题食物", 5);
    }
    
    /**
     * 注册 Factor 方块（构造函数接受 Identifier）
     */
    private <T extends Block> T registerFactorBlock(String name, Function<Identifier, T> factory) {
        Identifier id = Identifier.of(MOD_ID, name);
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
        Identifier id = Identifier.of(MOD_ID, name);
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
    
    /**
     * 注册装饰板方块
     */
    private DecorativeBlock registerDecorativeBlock(String name, int tier, String tierName) {
        Identifier id = Identifier.of(MOD_ID, name);
        DecorativeBlock block = new DecorativeBlock(id, tier, tierName);
        Registry.register(Registries.BLOCK, id, block);
        
        // 同时注册 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册装饰板: {} (T{})", name, tier);
        return block;
    }
    
    /**
     * 注册方块（通用）
     */
    private <T extends Block> T registerBlock(String name, Function<Identifier, T> factory) {
        Identifier id = Identifier.of(MOD_ID, name);
        T block = factory.apply(id);
        Registry.register(Registries.BLOCK, id, block);
        
        // 同时注册 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册方块: {}", name);
        return block;
    }
    
    /**
     * 注册食物
     */
    private FactorFoodItem registerFood(String name, int tier, String tierName,
                                         int nutrition, float saturation) {
        Identifier id = Identifier.of(MOD_ID, name);
        FactorFoodItem food = new FactorFoodItem(id, tier, tierName, nutrition, saturation);
        Registry.register(Registries.ITEM, id, food);
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Building] 注册食物: {} (T{})", name, tier);
        return food;
    }
    
    @Override
    public void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Building] 重新加载建筑配置...");
    }
}