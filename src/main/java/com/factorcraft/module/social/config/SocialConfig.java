package com.factorcraft.module.social.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;

public record SocialConfig(
    boolean enabled,
    int maxFriends,
    int maxGuildSize,
    int maxGuildNameLength,
    int chatHistorySize
) {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = Path.of("config/factorcraft/social.json");
    
    public static SocialConfig load() {
        try {
            if (Files.exists(PATH)) return GSON.fromJson(Files.readString(PATH), SocialConfig.class);
        } catch (Exception e) {}
        return new SocialConfig(false, 50, 100, 32, 100);
    }
}