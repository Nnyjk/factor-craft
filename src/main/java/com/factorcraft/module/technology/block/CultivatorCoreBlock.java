package com.factorcraft.module.technology.block;

import com.factorcraft.module.technology.machine.CultivatorCoreBlockEntity;
import com.factorcraft.module.technology.machine.ModMachines;
import com.factorcraft.module.technology.screen.CultivatorCoreScreenHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 培育核心方块 - 支持 GUI 交互
 */
public class CultivatorCoreBlock extends BlockWithEntity {
    
    public static final MapCodec<CultivatorCoreBlock> CODEC = createCodec(CultivatorCoreBlock::new);
    
    public CultivatorCoreBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CultivatorCoreBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, 
                                  PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CultivatorCoreBlockEntity) {
                player.openHandledScreen(createScreenHandlerFactory(state, world, pos));
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CultivatorCoreBlockEntity cultivator) {
            return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, p) -> new CultivatorCoreScreenHandler(
                    syncId, inventory, cultivator, 
                    net.minecraft.screen.ScreenHandlerContext.create(world, pos),
                    pos
                ),
                Text.translatable("block.factorcraft.factor_machine_cultivator_core_t1")
            );
        }
        return null;
    }
    
    @Override
    public <T extends BlockEntity> net.minecraft.block.entity.BlockEntityTicker<T> getTicker(
            World world, BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
        return validateTicker(type, ModMachines.CULTIVATOR_CORE, 
            (w, p, s, be) -> be.tick(w, p, s));
    }
}