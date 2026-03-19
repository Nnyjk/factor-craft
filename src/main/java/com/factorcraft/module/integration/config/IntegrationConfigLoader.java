package com.factorcraft.module.integration.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * 集成配置加载器。
 * 从默认资源文件和外部配置文件加载配置。
 */
public final class IntegrationConfigLoader {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .setLenient()
        .create();
    
    private static final String DEFAULT_CONFIG_PATH = "data/factorcraft/integration/default_config.json";
    private static final String EXTERNAL_CONFIG_NAME = "factorcraft_integration.json";
    
    private IntegrationConfigLoader() {}
    
    /**
     * 加载集成配置。
     * 优先级：外部配置 > 默认配置
     */
    public static IntegrationConfig load(Path configDir) {
        // 尝试加载外部配置
        Optional<IntegrationConfig> externalConfig = loadExternal(configDir);
        if (externalConfig.isPresent()) {
            FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 已加载外部集成配置");
            return externalConfig.get();
        }
        
        // 加载默认配置
        Optional<IntegrationConfig> defaultConfig = loadDefault();
        if (defaultConfig.isPresent()) {
            FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 使用默认集成配置");
            return defaultConfig.get();
        }
        
        // 返回内置默认值
        FactorCraftMod.LOGGER.warn("[FactorCraft:Integration] 未找到配置文件，使用内置默认值");
        return IntegrationConfig.DEFAULT;
    }
    
    /**
     * 加载外部配置文件。
     */
    private static Optional<IntegrationConfig> loadExternal(Path configDir) {
        Path configPath = configDir.resolve(EXTERNAL_CONFIG_NAME);
        if (!Files.exists(configPath)) {
            return Optional.empty();
        }
        
        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            IntegrationConfig config = GSON.fromJson(reader, IntegrationConfig.class);
            return Optional.ofNullable(config);
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Integration] 无法读取外部配置: {}", e.getMessage());
            return Optional.empty();
        } catch (JsonSyntaxException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Integration] 配置文件格式错误: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 加载默认资源配置。
     */
    private static Optional<IntegrationConfig> loadDefault() {
        try (InputStream is = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(DEFAULT_CONFIG_PATH)) {
            if (is == null) {
                return Optional.empty();
            }
            try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                IntegrationConfig config = GSON.fromJson(reader, IntegrationConfig.class);
                return Optional.ofNullable(config);
            }
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Integration] 无法读取默认配置: {}", e.getMessage());
            return Optional.empty();
        } catch (JsonSyntaxException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Integration] 默认配置格式错误: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 生成默认配置文件到指定目录。
     */
    public static void generateDefault(Path configDir) throws IOException {
        Path configPath = configDir.resolve(EXTERNAL_CONFIG_NAME);
        if (Files.exists(configPath)) {
            FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 配置文件已存在，跳过生成");
            return;
        }
        
        String json = GSON.toJson(IntegrationConfig.DEFAULT);
        Files.writeString(configPath, json, StandardCharsets.UTF_8);
        FactorCraftMod.LOGGER.info("[FactorCraft:Integration] 已生成默认配置文件: {}", configPath);
    }
}