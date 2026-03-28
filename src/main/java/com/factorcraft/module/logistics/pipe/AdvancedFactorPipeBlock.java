package com.factorcraft.module.logistics.pipe;

import com.factorcraft.factor.FactorType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 智能管道方块
 * 
 * 功能：
 * - 支持路由配置
 * - 自动检测连接
 * - 可视化流向
 */
public class AdvancedFactorPipeBlock extends Block implements BlockEntityProvider {
    
    public static Settings createSettings(RegistryKey<Block> key) {
        return Settings.create().registryKey(key)
            .strength(2.0f, 10.0f)
            .nonOpaque()
            .ticksRandomly();
    }
    
    public AdvancedFactorPipeBlock(Settings settings) {
        super(settings);
    }
    
    public AdvancedFactorPipeBlock() {
        this(createSettings(null));
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedFactorPipeBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (w, p, s, be) -> {
            if (be instanceof AdvancedFactorPipeBlockEntity pipe) {
                pipe.tick();
            }
        };
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // 管道 BlockEntity 没有物品栏，无需散落物品
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player.getMainHandStack().isEmpty()) {
            // 打开配置界面（TODO: 实现屏幕处理器）
            player.sendMessage(Text.literal("智能管道配置界面 - 待实现"), false);
        }
        return ActionResult.SUCCESS;
    }
}
