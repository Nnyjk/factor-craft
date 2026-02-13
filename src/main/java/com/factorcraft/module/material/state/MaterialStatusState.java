package com.factorcraft.module.material.state;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * M2 模块运行时状态：按世界记录最近一次日切状态池快照。
 */
public final class MaterialStatusState {
    private final Map<String, MaterialDayStatusSnapshot> byWorld = new ConcurrentHashMap<>();

    public void update(MaterialDayStatusSnapshot snapshot) {
        byWorld.put(snapshot.worldKey(), snapshot);
    }

    public Optional<MaterialDayStatusSnapshot> latest(String worldKey) {
        return Optional.ofNullable(byWorld.get(worldKey));
    }

    public int worldCount() {
        return byWorld.size();
    }

    public void clear() {
        byWorld.clear();
    }
}
