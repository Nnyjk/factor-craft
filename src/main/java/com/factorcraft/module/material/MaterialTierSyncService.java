package com.factorcraft.module.material;

import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.material.service.MaterialsM2Service;
import com.factorcraft.module.material.service.StatusRoll;
import com.factorcraft.module.material.state.MaterialDayStatusSnapshot;
import com.factorcraft.module.material.state.MaterialStatusState;

import java.util.List;

/**
 * M2：日切后按 Tier 结算状态池，并记录可观测快照。
 */
public final class MaterialTierSyncService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger("factor_craft.materials_traits_enchants_buffs");
    private static final String SOURCE_DAY_SETTLEMENT = "day_settlement";

    private volatile MaterialsM2Service materialsService;
    private volatile MaterialStatusState statusState;

    public void bind(MaterialsM2Service materialsService, MaterialStatusState statusState) {
        this.materialsService = materialsService;
        this.statusState = statusState;
    }

    public void onTierChanged(FactorTierChangeEvent event) {
        String worldKey = event.world().getRegistryKey().getValue().toString();
        onTierSettled(worldKey, event.dayIndex(), event.previousTier(), event.currentTier());
    }

    public void onTierSettled(String worldKey, long dayIndex, int previousTier, int currentTier) {
        if (materialsService == null || statusState == null) {
            return;
        }

        List<StatusRoll> statuses = materialsService.settleDailyStatusPool(previousTier, currentTier).stream()
                .map(status -> new StatusRoll(status.statusId(), status.type(), status.durationSeconds(), status.stacks(), SOURCE_DAY_SETTLEMENT))
                .toList();

        MaterialDayStatusSnapshot snapshot = new MaterialDayStatusSnapshot(worldKey, dayIndex, previousTier, currentTier, statuses);
        statusState.update(snapshot);

        LOG.info(
                "[M2] 日切状态池结算: world={}, day={}, prevTier={}, currTier={}, statuses={}",
                worldKey,
                dayIndex,
                previousTier,
                currentTier,
                statuses.size()
        );
    }
}
