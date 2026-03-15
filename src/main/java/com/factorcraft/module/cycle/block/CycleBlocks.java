package com.factorcraft.module.cycle.block;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.block.entity.CycleBlockEntities;
import com.factorcraft.module.cycle.block.entity.FactorSinkBlockEntity;
import com.factorcraft.module.cycle.block.entity.FactorSourceBlockEntity;
import com.factorcraft.module.cycle.block.entity.FactorTransmitterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Cycle 模块方块注册
 * 
 * Fabric 1.21.4 最佳实践：
 * - 使用 RegistryKey 注册方块和物品
 * - 使用 FabricBlockEntityTypeBuilder 创建 BlockEntityType
 * - 延迟初始化避免循环依赖
 */
public class CycleBlocks {
    
    private static Block factorSink;
    private static Block factorSource;
    private static Block factorTransmitter;
    
    /**
     * 获取 Factor 吸收结构方块（延迟初始化）
     */
    public static Block getFactorSink() {
        if (factorSink == null) {
            factorSink = registerBlock("factor_sink", 3.0f);
        }
        return factorSink;
    }
    
    /**
     * 获取 Factor 释放结构方块（延迟初始化）
     */
    public static Block getFactorSource() {
        if (factorSource == null) {
            factorSource = registerBlock("factor_source", 3.0f);
        }
        return factorSource;
    }
    
    /**
     * 获取跨维度传递器方块（延迟初始化）
     */
    public static Block getFactorTransmitter() {
        if (factorTransmitter == null) {
            factorTransmitter = registerBlock("factor_transmitter", 3.0f);
        }
        return factorTransmitter;
    }
    
    /**
     * 注册方块（带 BlockItem）- Fabric 1.21.4 最佳实践
     */
    private static Block registerBlock(String name, float hardness) {
        Identifier id = Identifier.of(FactorCraftMod.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        
        // 创建方块
        Block block = new Block(AbstractBlock.Settings.create()
            .registryKey(blockKey)
            .strength(hardness)
            .requiresTool());
        
        // 注册方块
        Registry.register(Registries.BLOCK, id, block);
        
        // 注册 BlockItem
        Item blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, id, blockItem);
        
        return block;
    }
    
    /**
     * 注册所有方块
     */
    public static void register() {
        // 预初始化所有方块
        getFactorSink();
        getFactorSource();
        getFactorTransmitter();
    }
    
    /**
     * 获取方块的 BlockEntityTicker
     */
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Block block, BlockEntityType<T> type) {
        if (block == factorSink && type == CycleBlockEntities.FACTOR_SINK) {
            return (world, pos, state, blockEntity) -> 
                FactorSinkBlockEntity.tick(world, pos, state, (FactorSinkBlockEntity) blockEntity);
        }
        if (block == factorSource && type == CycleBlockEntities.FACTOR_SOURCE) {
            return (world, pos, state, blockEntity) -> 
                FactorSourceBlockEntity.tick(world, pos, state, (FactorSourceBlockEntity) blockEntity);
        }
        if (block == factorTransmitter && type == CycleBlockEntities.FACTOR_TRANSMITTER) {
            return (world, pos, state, blockEntity) -> 
                FactorTransmitterBlockEntity.tick(world, pos, state, (FactorTransmitterBlockEntity) blockEntity);
        }
        return null;
    }
}
