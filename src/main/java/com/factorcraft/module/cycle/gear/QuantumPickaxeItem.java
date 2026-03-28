package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * 量子稿 - 终极工具
 * 
 * 特性:
 * - 超越下界合金的挖掘速度 (12.0)
 * - 耐久度 10,000
 * - Shift+ 右键切换 3x3/5x5 范围挖掘
 * - 挖掘等级 4 (下界合金级别)
 */
public class QuantumPickaxeItem extends PickaxeItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 1.0f;
    private static final float ATTACK_SPEED = -2.8f;
    
    public QuantumPickaxeItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getMiningSpeed(stack, state);
        // 对可挖掘方块提供超高挖掘速度
        if (baseSpeed > 1.0f) {
            return 12.0f; // 超越下界合金稿的 9.0
        }
        return baseSpeed;
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && miner instanceof PlayerEntity player) {
            // 挖掘 3x3 或 5x5 范围
            List<BlockPos> blocksToMine = getBlocksToMine(world, pos, player, stack);
            
            for (BlockPos blockPos : blocksToMine) {
                if (!blockPos.equals(pos)) {
                    BlockState blockState = world.getBlockState(blockPos);
                    float speed = getMiningSpeed(stack, blockState);
                    if (speed > 1.0f) {
                        world.breakBlock(blockPos, true, player);
                        stack.damage(1, miner, EquipmentSlot.MAINHAND);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 获取要挖掘的方块列表 (3x3 或 5x5)
     */
    public List<BlockPos> getBlocksToMine(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        List<BlockPos> blocks = new ArrayList<>();
        
        // 检查 RANGE_MODE component (3 = 3x3, 5 = 5x5)
        Integer rangeValue = stack.get(FactorGearComponents.RANGE_MODE);
        int range = (rangeValue != null && rangeValue == 5) ? 2 : 1; // 2 = 5x5, 1 = 3x3
        
        // 获取玩家朝向
        Direction facing = player.getHorizontalFacing();
        
        // 根据朝向确定挖掘平面
        if (facing.getAxis() == Direction.Axis.Z) {
            // 南北朝向 - 在 X-Y 平面挖掘
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    blocks.add(pos.add(dx, dy, 0));
                }
            }
        } else {
            // 东西朝向 - 在 Z-Y 平面挖掘
            for (int dz = -range; dz <= range; dz++) {
                for (int dy = -range; dy <= range; dy++) {
                    blocks.add(pos.add(0, dy, dz));
                }
            }
        }
        
        return blocks;
    }
}
