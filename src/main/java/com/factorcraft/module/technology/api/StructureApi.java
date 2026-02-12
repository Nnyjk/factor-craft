package com.factorcraft.module.technology.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface StructureApi {
    int getPowerBudget(World world, BlockPos corePos);
}
