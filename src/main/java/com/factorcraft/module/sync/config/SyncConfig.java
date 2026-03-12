package com.factorcraft.module.sync.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 跨服同步配置
 */
public record SyncConfig(
    boolean enabled,
    String redisHost,
    int redisPort,
    String redisPassword,
    int syncIntervalTicks,
    int batchSize,
    String serverId
) {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/factorcraft/sync.json");
    
    public static SyncConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                return GSON.fromJson(Files.readString(CONFIG_PATH), SyncConfig.class);
            }
        } catch (IOException e) {
            // 使用默认配置
        }
        return new SyncConfig(false, "localhost", 6379, "", 100, 50, "default");
    }
    
    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sync config", e);
        }
    }
}