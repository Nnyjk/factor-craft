package com.factorcraft.module.social.service;

import com.factorcraft.module.social.config.SocialConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuildService {
    private final SocialConfig config;
    private final Map<String, Guild> guilds = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerGuild = new ConcurrentHashMap<>();
    
    public GuildService(SocialConfig config) { this.config = config; }
    
    public String createGuild(UUID owner, String name) {
        if (name.length() > config.maxGuildNameLength()) return null;
        String id = UUID.randomUUID().toString().substring(0, 8);
        guilds.put(id, new Guild(id, name, owner, Set.of(owner)));
        playerGuild.put(owner, id);
        return id;
    }
    
    public boolean joinGuild(UUID player, String guildId) {
        Guild g = guilds.get(guildId);
        if (g == null || g.members().size() >= config.maxGuildSize()) return false;
        Set<UUID> members = new HashSet<>(g.members());
        members.add(player);
        guilds.put(guildId, new Guild(g.id(), g.name(), g.owner(), members));
        playerGuild.put(player, guildId);
        return true;
    }
    
    public void leaveGuild(UUID player) {
        String gid = playerGuild.remove(player);
        if (gid != null) {
            Guild g = guilds.get(gid);
            if (g != null) {
                Set<UUID> members = new HashSet<>(g.members());
                members.remove(player);
                if (members.isEmpty()) guilds.remove(gid);
                else guilds.put(gid, new Guild(g.id(), g.name(), g.owner(), members));
            }
        }
    }
    
    public Optional<Guild> getGuild(String id) { return Optional.ofNullable(guilds.get(id)); }
    public Optional<String> getPlayerGuild(UUID player) { return Optional.ofNullable(playerGuild.get(player)); }
    
    public record Guild(String id, String name, UUID owner, Set<UUID> members) {}
}