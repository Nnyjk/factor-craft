package com.factorcraft.module.cycle.dimension.end.block;

import com.factorcraft.component.FactorCraftDataComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 末地折跃门 Factor 激活方块
 * 使用 Factor 激活末地折跃门
 */
public class EndGatewayFactorBlock extends Block {
    public static final BooleanProperty ACTIVATED = Properties.ACTIVE;
    
    public EndGatewayFactorBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVATED, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && !state.get(ACTIVATED)) {
            // 检测玩家是否持有 Factor 物品
            if (hasFactorItem(player)) {
                world.setBlockState(pos, state.with(ACTIVATED, true));
                // 激活折跃门逻辑
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
    
    /**
     * 检测玩家是否持有 Factor 相关物品
     */
    private boolean hasFactorItem(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = player.getStackInHand(hand);
            if (isFactorStack(stack)) {
                return true;
            }
        }
        
        // 检查物品栏
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isFactorStack(stack)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 判断物品堆是否为 Factor 相关物品
     */
    private boolean isFactorStack(ItemStack stack) {
        return stack.contains(FactorCraftDataComponents.FACTOR_STORAGE);
    }
}
