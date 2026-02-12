package com.factorcraft.module.creature.api;

import com.factorcraft.module.creature.model.DropPoolSpec;
import com.factorcraft.module.creature.model.SpawnRuleSpec;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public interface CreatureApi {
    List<SpawnRuleSpec> getSpawnRules(ServerWorld world, int tier);

    List<DropPoolSpec> getDropPools(String entityId, int tier);
}
