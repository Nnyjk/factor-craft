package com.factorcraft.module.material.api;

import com.factorcraft.module.material.service.MaterialRollResult;
import com.factorcraft.module.material.state.MaterialDayStatusSnapshot;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

/**
 * M2 对外能力：为后续 M3/M4 提供材料生成与日切状态查询入口。
 */
public interface MaterialApi {
    MaterialRollResult rollForCurrentTier(ServerWorld world, long seed);

    Optional<MaterialDayStatusSnapshot> latestDaySnapshot(ServerWorld world);
}
