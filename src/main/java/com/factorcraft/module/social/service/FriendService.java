package com.factorcraft.module.social.service;

import com.factorcraft.module.social.config.SocialConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FriendService {
    private final SocialConfig config;
    private final Map<UUID, Set<UUID>> friends = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> pending = new ConcurrentHashMap<>();
    
    public FriendService(SocialConfig config) { this.config = config; }
    
    public boolean addFriend(UUID player, UUID friend) {
        Set<UUID> list = friends.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
        if (list.size() >= config.maxFriends()) return false;
        return list.add(friend);
    }
    
    public void removeFriend(UUID player, UUID friend) {
        friends.getOrDefault(player, Set.of()).remove(friend);
    }
    
    public Set<UUID> getFriends(UUID player) {
        return Collections.unmodifiableSet(friends.getOrDefault(player, Set.of()));
    }
    
    public void sendRequest(UUID from, UUID to) {
        pending.computeIfAbsent(to, k -> ConcurrentHashMap.newKeySet()).add(from);
    }
    
    public Set<UUID> getPending(UUID player) {
        return Collections.unmodifiableSet(pending.getOrDefault(player, Set.of()));
    }
}