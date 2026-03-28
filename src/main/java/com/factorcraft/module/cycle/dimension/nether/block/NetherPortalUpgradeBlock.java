package com.factorcraft.module.cycle.dimension.nether.block;

import com.factorcraft.component.FactorCraftDataComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 下界传送门升级方块
 * 使用 Factor 驱动的下界传送门升级
 */
public class NetherPortalUpgradeBlock extends Block {
    public static final BooleanProperty ACTIVATED = Properties.ACTIVE;
    
    public NetherPortalUpgradeBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVATED, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        
        // 检测 Factor 物品
        if (isFactorItem(stack)) {
            if (!world.isClient && !state.get(ACTIVATED)) {
                // 激活传送门升级
                world.setBlockState(pos, state.with(ACTIVATED, true));
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }
    
    private boolean isFactorItem(ItemStack stack) {
        // 检测是否为 Factor 相关物品
        return stack.getItem() == Items.BLAZE_POWDER || 
               stack.getItem() == Items.BLAZE_ROD ||
               stack.contains(FactorCraftDataComponents.FACTOR_STORAGE);
    }
}
