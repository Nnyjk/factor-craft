package com.factorcraft.module.cycle.dimension.gate.item;

import com.factorcraft.component.FactorCraftDataComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 传送链接器物品
 * 用于绑定维度传送门的坐标
 */
public class GateLinkerItem extends Item {
    public GateLinkerItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        BlockPos linkedPos = stack.get(FactorCraftDataComponents.POSITION_DATA);
        
        if (!world.isClient) {
            if (linkedPos != null) {
                // 已绑定坐标，显示信息
                user.sendMessage(Text.literal("已绑定坐标：X=" + linkedPos.getX() + 
                    " Y=" + linkedPos.getY() + " Z=" + linkedPos.getZ()), true);
            } else {
                user.sendMessage(Text.literal("未绑定坐标，右击方块进行绑定"), true);
            }
        }
        
        return ActionResult.SUCCESS;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        BlockPos clickedPos = context.getBlockPos();
        
        if (!world.isClient && player != null) {
            // 保存点击的方块坐标
            stack.set(FactorCraftDataComponents.POSITION_DATA, clickedPos);
            player.sendMessage(Text.literal("已绑定坐标：X=" + clickedPos.getX() + 
                " Y=" + clickedPos.getY() + " Z=" + clickedPos.getZ()), true);
        }
        
        return ActionResult.SUCCESS;
    }
}
