package com.factorcraft.module.logistics.storage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 存储单元
 * 
 * 功能：
 * - 大容量 Factor 存储（每类型 100 万+）
 * - 支持多类型 Factor 同时存储
 * - 可与物流网络集成
 */
public class FactorStorageUnitBlock extends Block implements BlockEntityProvider {
    
    public static final Settings SETTINGS = Settings.create()
        .strength(3.0f, 15.0f)
        .nonOpaque();
    
    public FactorStorageUnitBlock() {
        super(SETTINGS);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorStorageUnitBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != LogisticsStorage.STORAGE_UNIT_ENTITY) return null;
        return world.isClient ? null : (w, p, s, be) -> FactorStorageUnitBlockEntity.tick(w, p, s, (FactorStorageUnitBlockEntity)be);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            // 打开存储界面（TODO: 实现屏幕处理器）
            player.sendMessage(net.minecraft.text.Text.literal("存储单元界面 - 待实现"), true);
        }
        return ActionResult.SUCCESS;
    }
}
