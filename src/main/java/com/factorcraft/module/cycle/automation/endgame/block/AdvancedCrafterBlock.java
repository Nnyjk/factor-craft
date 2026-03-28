package com.factorcraft.module.cycle.automation.endgame.block;

import com.factorcraft.module.cycle.automation.endgame.block.entity.AdvancedCrafterBlockEntity;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 高级合成器
 * 支持多方块合成，4 配方并行处理
 */
public class AdvancedCrafterBlock extends BlockWithEntity implements BlockEntityProvider {
    
    public static final MapCodec<AdvancedCrafterBlock> CODEC = createCodec(AdvancedCrafterBlock::new);
    
    public AdvancedCrafterBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedCrafterBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AdvancedCrafterBlockEntity crafter) {
            player.openHandledScreen((NamedScreenHandlerFactory) crafter);
        }
        
        return ActionResult.CONSUME;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, EndgameAutomationBlockEntities.ADVANCED_CRAFTER, AdvancedCrafterBlockEntity::tick);
    }
}
