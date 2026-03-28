package com.factorcraft.module.logistics.storage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 存储总线
 * 
 * 功能：
 * - 连接存储单元到物流网络
 * - 提供 Factor 输入/输出接口
 */
public class StorageBusBlock extends Block implements BlockEntityProvider {
    
    public static Settings createSettings(RegistryKey<Block> key) {
        return Settings.create().registryKey(key)
            .strength(2.0f, 10.0f)
            .nonOpaque();
    }
    
    public StorageBusBlock(Settings settings) {
        super(settings);
    }
    
    public StorageBusBlock() {
        this(createSettings(null));
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBusBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (w, p, s, be) -> {
            if (be instanceof StorageBusBlockEntity bus) {
                bus.tick();
            }
        };
    }
}
