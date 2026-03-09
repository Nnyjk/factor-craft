package com.factorcraft.module.cycle.block;

import com.factorcraft.FactorCraftMod;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Cycle 模块方块注册
 * 
 * ⚠️ BlockEntity 功能暂时禁用
 * 原因：Minecraft 1.21.4 BlockEntityFactory 是私有接口
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
            factorSink = registerBlock("factor_sink",
                new Block(AbstractBlock.Settings.create().strength(3.0f).requiresTool()));
        }
        return factorSink;
    }
    
    /**
     * 获取 Factor 释放结构方块（延迟初始化）
     */
    public static Block getFactorSource() {
        if (factorSource == null) {
            factorSource = registerBlock("factor_source",
                new Block(AbstractBlock.Settings.create().strength(3.0f).requiresTool()));
        }
        return factorSource;
    }
    
    /**
     * 获取跨维度传递器方块（延迟初始化）
     */
    public static Block getFactorTransmitter() {
        if (factorTransmitter == null) {
            factorTransmitter = registerBlock("factor_transmitter",
                new Block(AbstractBlock.Settings.create().strength(3.0f).requiresTool()));
        }
        return factorTransmitter;
    }
    
    /**
     * 注册方块（带 BlockItem）
     */
    private static Block registerBlock(String name, Block block) {
        // 注册方块
        Block registeredBlock = Registry.register(Registries.BLOCK, 
            Identifier.of(FactorCraftMod.MOD_ID, name), block);
        
        // 注册 BlockItem
        Item blockItem = new BlockItem(registeredBlock, new Item.Settings());
        Registry.register(Registries.ITEM, Identifier.of(FactorCraftMod.MOD_ID, name), blockItem);
        
        return registeredBlock;
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
     * TODO: BlockEntity 实现待恢复
     */
    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Block block, BlockEntityType<T> type) {
        // TODO: 恢复 BlockEntity 后实现
        return null;
    }
}
