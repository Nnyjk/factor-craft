package com.factorcraft.module.cycle.dimension.nether.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * 下界 Factor 矿石
 * 稀有矿石，掉落炽热 Factor 碎片
 */
public class NetherFactorOreBlock extends Block {
    public NetherFactorOreBlock(Settings settings) {
        super(settings);
    }
}
