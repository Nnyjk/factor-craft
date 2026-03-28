package com.factorcraft.module.cycle.gear;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.block.Blocks;

/**
 * 量子斧 - 终极斧
 * 
 * 特性:
 * - 超越下界合金的挖掘速度
 * - 耐久度 10,000
 * - 整树砍伐 (砍一个原木，砍掉整棵树的所有原木)
 */
public class QuantumAxeItem extends AxeItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 6.0f;
    private static final float ATTACK_SPEED = -2.9f;
    
    public QuantumAxeItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        // 对原木类方块有超高挖掘速度
        if (state.isIn(net.minecraft.registry.tag.BlockTags.LOGS)) {
            return 18.0f; // 超越下界合金斧的 9.0
        }
        return super.getMiningSpeed(stack, state);
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.isIn(net.minecraft.registry.tag.BlockTags.LOGS)) {
            // 整树砍伐逻辑
            harvestTree(world, pos, stack, miner);
        }
        return true;
    }
    
    /**
     * 砍伐整棵树
     */
    private void harvestTree(World world, BlockPos startPos, ItemStack stack, LivingEntity miner) {
        // 简单的 BFS 查找所有相连的原木
        java.util.Queue<BlockPos> queue = new java.util.LinkedList<>();
        java.util.Set<BlockPos> harvested = new java.util.HashSet<>();
        
        queue.offer(startPos);
        harvested.add(startPos);
        
        while (!queue.isEmpty() && harvested.size() < 500) { // 限制最大砍伐数量
            BlockPos current = queue.poll();
            
            // 检查 6 个方向
            for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
                BlockPos neighbor = current.offset(dir);
                
                if (!harvested.contains(neighbor)) {
                    BlockState neighborState = world.getBlockState(neighbor);
                    
                    // 如果是原木
                    if (neighborState.isIn(net.minecraft.registry.tag.BlockTags.LOGS)) {
                        harvested.add(neighbor);
                        queue.offer(neighbor);
                        
                        // 破坏方块
                        if (miner instanceof PlayerEntity player) {
                            world.breakBlock(neighbor, true, player);
                        } else {
                            world.breakBlock(neighbor, true, null);
                        }
                        
                        // 消耗耐久
                        stack.damage(1, miner, EquipmentSlot.MAINHAND);
                        if (stack.isEmpty()) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
