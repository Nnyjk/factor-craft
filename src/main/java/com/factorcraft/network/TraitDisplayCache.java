package com.factorcraft.network;

import com.factorcraft.module.material.trait.TraitInstance;
import java.util.*;

public class TraitDisplayCache {
    private static final Map<Integer, List<TraitInstance>> cache = new HashMap<>();
    
    public static void update(int slot, List<TraitInstance> traits) {
        cache.put(slot, new ArrayList<>(traits));
    }
    
    public static Optional<List<TraitInstance>> get(int slot) {
        return Optional.ofNullable(cache.get(slot)).map(ArrayList::new);
    }
    
    public static void clear(int slot) {
        cache.remove(slot);
    }
    
    public static void clearAll() {
        cache.clear();
    }
}