package com.factorcraft.module.creature.api;

import com.factorcraft.module.creature.model.CreatureAbilitySpec;
import com.factorcraft.module.creature.model.DropPoolSpec;
import com.factorcraft.module.creature.model.SpawnRuleSpec;

public interface CreatureRegistrar {
    void registerSpawnRule(SpawnRuleSpec spec);

    void registerDropPool(DropPoolSpec spec);

    void registerAbility(CreatureAbilitySpec spec);
}
