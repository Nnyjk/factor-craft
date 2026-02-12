package com.factorcraft.dynamic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.factorcraft.FactorCraftMod;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责把配置目录 + 默认资源目录中的 JSON 聚合为统一 Bundle。
 */
public final class DynamicContentLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type STRING_OBJECT_MAP = new TypeToken<Map<String, Object>>() {}.getType();
    private static final Type STRING_STRING_MAP = new TypeToken<Map<String, String>>() {}.getType();

    private DynamicContentLoader() {}

    public static DynamicBundle load(Path configRoot) {
        Path configsFile = configRoot.resolve("configs.json");
        Path texturesFile = configRoot.resolve("textures.json");
        Path modelsFile = configRoot.resolve("models.json");
        Path langFile = configRoot.resolve("lang.json");
        Path commandsFile = configRoot.resolve("commands.json");

        Map<String, Object> configs = readConfigs(configsFile);
        List<DynamicBundle.TextureSpec> textures = readTextures(texturesFile);
        List<DynamicBundle.ModelSpec> models = readModels(modelsFile);
        List<DynamicBundle.LangSpec> languages = readLanguages(langFile);
        List<DynamicBundle.CommandSpec> commands = readCommands(commandsFile);

        return new DynamicBundle(configs, textures, models, languages, commands);
    }

    public static void ensureDefaults(Path configRoot) {
        try {
            Files.createDirectories(configRoot);
            writeDefaultIfMissing(configRoot.resolve("configs.json"), defaultConfigs());
            writeDefaultIfMissing(configRoot.resolve("textures.json"), defaultTextures());
            writeDefaultIfMissing(configRoot.resolve("models.json"), defaultModels());
            writeDefaultIfMissing(configRoot.resolve("lang.json"), defaultLang());
            writeDefaultIfMissing(configRoot.resolve("commands.json"), defaultCommands());
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("初始化动态配置目录失败: {}", configRoot, e);
        }
    }

    private static void writeDefaultIfMissing(Path target, String content) throws IOException {
        if (!Files.exists(target)) {
            Files.writeString(target, content);
        }
    }

    private static Map<String, Object> readConfigs(Path file) {
        JsonObject root = readJson(file);
        if (root == null || !root.has("configs")) {
            return Map.of();
        }
        return GSON.fromJson(root.get("configs"), STRING_OBJECT_MAP);
    }

    private static List<DynamicBundle.TextureSpec> readTextures(Path file) {
        JsonObject root = readJson(file);
        if (root == null || !root.has("textures")) {
            return List.of();
        }

        List<DynamicBundle.TextureSpec> results = new ArrayList<>();
        root.getAsJsonArray("textures").forEach(item -> {
            JsonObject obj = item.getAsJsonObject();
            results.add(new DynamicBundle.TextureSpec(
                    obj.get("key").getAsString(),
                    obj.get("resourcePath").getAsString()
            ));
        });
        return results;
    }

    private static List<DynamicBundle.ModelSpec> readModels(Path file) {
        JsonObject root = readJson(file);
        if (root == null || !root.has("models")) {
            return List.of();
        }

        List<DynamicBundle.ModelSpec> results = new ArrayList<>();
        root.getAsJsonArray("models").forEach(item -> {
            JsonObject obj = item.getAsJsonObject();
            results.add(new DynamicBundle.ModelSpec(
                    obj.get("key").getAsString(),
                    obj.get("resourcePath").getAsString()
            ));
        });
        return results;
    }

    private static List<DynamicBundle.LangSpec> readLanguages(Path file) {
        JsonObject root = readJson(file);
        if (root == null || !root.has("languages")) {
            return List.of();
        }

        List<DynamicBundle.LangSpec> results = new ArrayList<>();
        root.getAsJsonArray("languages").forEach(item -> {
            JsonObject obj = item.getAsJsonObject();
            String locale = obj.get("locale").getAsString();
            Map<String, String> entries = GSON.fromJson(obj.get("entries"), STRING_STRING_MAP);
            results.add(new DynamicBundle.LangSpec(locale, entries));
        });
        return results;
    }

    private static List<DynamicBundle.CommandSpec> readCommands(Path file) {
        JsonObject root = readJson(file);
        if (root == null || !root.has("commands")) {
            return List.of();
        }

        List<DynamicBundle.CommandSpec> results = new ArrayList<>();
        root.getAsJsonArray("commands").forEach(item -> {
            JsonObject obj = item.getAsJsonObject();
            results.add(new DynamicBundle.CommandSpec(
                    obj.get("commandId").getAsString(),
                    obj.get("handlerId").getAsString(),
                    obj.get("permission").getAsString(),
                    obj.has("enabled") && obj.get("enabled").getAsBoolean()
            ));
        });
        return results;
    }

    private static JsonObject readJson(Path file) {
        if (!Files.exists(file)) {
            FactorCraftMod.LOGGER.warn("动态配置文件不存在: {}", file);
            return null;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("读取动态配置文件失败: {}", file, e);
            return null;
        }
    }

    private static String defaultConfigs() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("feature.switch.dynamicResource", true);
        defaults.put("balance.recipe.factorEnergyCost", 120);
        defaults.put("ui.theme", "industrial");
        return GSON.toJson(Map.of("configs", defaults));
    }

    private static String defaultTextures() {
        return GSON.toJson(Map.of("textures", List.of(
                Map.of("key", "block.factor_core", "resourcePath", "factor_craft:textures/block/factor_core.png"),
                Map.of("key", "item.factor_chip", "resourcePath", "factor_craft:textures/item/factor_chip.png")
        )));
    }

    private static String defaultModels() {
        return GSON.toJson(Map.of("models", List.of(
                Map.of("key", "block.factor_core", "resourcePath", "factor_craft:models/block/factor_core.json"),
                Map.of("key", "item.factor_chip", "resourcePath", "factor_craft:models/item/factor_chip.json")
        )));
    }

    private static String defaultLang() {
        return GSON.toJson(Map.of("languages", List.of(
                Map.of(
                        "locale", "zh_cn",
                        "entries", Map.of(
                                "item.factor_craft.factor_chip", "因子芯片",
                                "block.factor_craft.factor_core", "因子核心"
                        )
                ),
                Map.of(
                        "locale", "en_us",
                        "entries", Map.of(
                                "item.factor_craft.factor_chip", "Factor Chip",
                                "block.factor_craft.factor_core", "Factor Core"
                        )
                )
        )));
    }

    private static String defaultCommands() {
        return GSON.toJson(Map.of("commands", List.of(
                Map.of(
                        "commandId", "factor_craft:factor_debug",
                        "handlerId", "factor.debug",
                        "permission", "factorcraft.command.factor.debug",
                        "enabled", true
                )
        )));
    }
}
