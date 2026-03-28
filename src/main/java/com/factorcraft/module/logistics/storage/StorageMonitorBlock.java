package com.factorcraft.module.logistics.storage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 存储监控器
 * 
 * 功能：
 * - 显示连接的存储单元中的 Factor 信息
 * - 提供搜索和过滤功能
 */
public class StorageMonitorBlock extends Block implements BlockEntityProvider {
    
    public static final Settings SETTINGS = Settings.create()
        .strength(2.5f, 12.0f)
        .nonOpaque();
    
    public StorageMonitorBlock() {
        super(SETTINGS);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StorageMonitorBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != LogisticsStorage.STORAGE_MONITOR_ENTITY) return null;
        return world.isClient ? null : (w, p, s, be) -> StorageMonitorBlockEntity.tick(w, p, s, (StorageMonitorBlockEntity)be);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof StorageMonitorBlockEntity) {
                player.openHandledScreen((NamedScreenHandlerFactory) be);
            }
        }
        return ActionResult.SUCCESS;
    }
}
