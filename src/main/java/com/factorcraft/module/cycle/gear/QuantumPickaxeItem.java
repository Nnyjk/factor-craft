package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;

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
    
    public QuantumPickaxeItem() {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED, 
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    /**
     * 获取需要挖掘的方块列表
     */
    private List<BlockPos> getBlocksToMine(World world, BlockPos centerPos, Direction face, int range) {
        List<BlockPos> blocks = new ArrayList<>();
        blocks.add(centerPos);
        
        // 确定挖掘平面 (基于点击的面)
        Direction.Axis axis = face.getAxis();
        int halfRange = range / 2;
        
        for (int x = -halfRange; x <= halfRange; x++) {
            for (int y = -halfRange; y <= halfRange; y++) {
                for (int z = -halfRange; z <= halfRange; z++) {
                    // 根据点击面确定挖掘平面
                    boolean inPlane = switch (axis) {
                        case X -> Math.abs(x) <= halfRange && Math.abs(y) <= halfRange && z == 0;
                        case Y -> Math.abs(x) <= halfRange && y == 0 && Math.abs(z) <= halfRange;
                        case Z -> x == 0 && Math.abs(y) <= halfRange && Math.abs(z) <= halfRange;
                    };
                    
                    if (inPlane) {
                        BlockPos pos = centerPos.add(x, y, z);
                        if (!pos.equals(centerPos)) {
                            blocks.add(pos);
                        }
                    }
                }
            }
        }
        
        return blocks;
    }
    
    /**
     * 检查方块是否可以被挖掘
     */
    private boolean canMine(ItemStack stack, PlayerEntity player, BlockPos pos) {
        return !player.getWorld().getBlockState(pos).isAir();
    }
    
    /**
     * 切换范围模式
     */
    private void toggleRangeMode(ItemStack stack, PlayerEntity player) {
        int currentRange = getRangeMode(stack);
        int newRange = (currentRange == 3) ? 5 : 3;
        setRangeMode(stack, newRange);
    }
    
    /**
     * 获取当前范围模式
     */
    private int getRangeMode(ItemStack stack) {
        Integer range = stack.get(FactorGearComponents.RANGE_MODE);
        return range != null ? range : 3; // 默认 3x3
    }
    
    /**
     * 设置范围模式
     */
    private void setRangeMode(ItemStack stack, int range) {
        stack.set(FactorGearComponents.RANGE_MODE, range);
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (state.isIn(BlockTags.PICKAXE_MINEABLE)) {
            return 12.0f; // 超越下界合金的挖掘速度
        }
        return super.getMiningSpeed(stack, state);
    }
}
