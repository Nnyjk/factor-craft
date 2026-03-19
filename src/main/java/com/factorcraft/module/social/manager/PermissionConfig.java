package com.factorcraft.module.social.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限配置管理
 */
public class PermissionConfig {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "factorcraft", "permissions.json");
    
    private static Map<String, GroupConfig> groupConfigs = new HashMap<>();
    
    public record GroupConfig(
        String displayName,
        String[] permissions,
        boolean isDefault,
        int priority
    ) {}
    
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            createDefaultConfig();
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            groupConfigs.clear();
            
            if (json.has("groups")) {
                JsonObject groups = json.getAsJsonObject("groups");
                for (String groupId : groups.keySet()) {
                    JsonObject groupJson = groups.getAsJsonObject(groupId);
                    GroupConfig config = new GroupConfig(
                        groupJson.has("displayName") ? groupJson.get("displayName").getAsString() : groupId,
                        groupJson.has("permissions") ? parsePermissions(groupJson.getAsJsonArray("permissions")) : new String[0],
                        groupJson.has("isDefault") && groupJson.get("isDefault").getAsBoolean(),
                        groupJson.has("priority") ? groupJson.get("priority").getAsInt() : 0
                    );
                    groupConfigs.put(groupId, config);
                }
            }
            applyToManager();
        } catch (Exception e) {
            createDefaultConfig();
        }
    }
    
    private static String[] parsePermissions(JsonArray array) {
        String[] permissions = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            permissions[i] = array.get(i).getAsString();
        }
        return permissions;
    }
    
    private static void applyToManager() {
        PermissionManager.clearGroups();
        
        for (Map.Entry<String, GroupConfig> entry : groupConfigs.entrySet()) {
            String groupId = entry.getKey();
            GroupConfig config = entry.getValue();
            
            PermissionGroup group = new PermissionGroup(groupId, config.displayName());
            for (String permId : config.permissions()) {
                group.addPermission(new Permission(permId, "Configured permission"));
            }
            
            PermissionManager.registerGroup(group);
            
            if (config.isDefault()) {
                PermissionManager.setDefaultGroup(groupId);
            }
        }
    }
    
    private static void createDefaultConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            
            JsonObject json = new JsonObject();
            json.addProperty("_comment", "Factor Craft 权限配置文件");
            
            JsonObject groups = new JsonObject();
            
            JsonObject admin = new JsonObject();
            admin.addProperty("displayName", "管理员");
            admin.addProperty("isDefault", false);
            admin.addProperty("priority", 100);
            JsonArray adminPerms = new JsonArray();
            adminPerms.add("factorcraft.*");
            admin.add("permissions", adminPerms);
            groups.add("admin", admin);
            
            JsonObject vip = new JsonObject();
            vip.addProperty("displayName", "VIP");
            vip.addProperty("isDefault", false);
            vip.addProperty("priority", 50);
            JsonArray vipPerms = new JsonArray();
            vipPerms.add("factorcraft.factor.info");
            vipPerms.add("factorcraft.factor.set");
            vipPerms.add("factorcraft.machine.create");
            vipPerms.add("factorcraft.machine.destroy");
            vip.add("permissions", vipPerms);
            groups.add("vip", vip);
            
            JsonObject player = new JsonObject();
            player.addProperty("displayName", "玩家");
            player.addProperty("isDefault", true);
            player.addProperty("priority", 0);
            JsonArray playerPerms = new JsonArray();
            playerPerms.add("factorcraft.factor.info");
            playerPerms.add("factorcraft.machine.create");
            playerPerms.add("factorcraft.machine.destroy");
            player.add("permissions", playerPerms);
            groups.add("player", player);
            
            json.add("groups", groups);
            
            Files.writeString(CONFIG_PATH, GSON.toJson(json));
            
            groupConfigs.clear();
            groupConfigs.put("admin", new GroupConfig("管理员", new String[]{"factorcraft.*"}, false, 100));
            groupConfigs.put("vip", new GroupConfig("VIP", new String[]{"factorcraft.factor.info", "factorcraft.factor.set", "factorcraft.machine.create", "factorcraft.machine.destroy"}, false, 50));
            groupConfigs.put("player", new GroupConfig("玩家", new String[]{"factorcraft.factor.info", "factorcraft.machine.create", "factorcraft.machine.destroy"}, true, 0));
            
            applyToManager();
        } catch (Exception e) {
            // 忽略
        }
    }
    
    public static void reload() {
        load();
    }
    
    public static GroupConfig getGroupConfig(String groupId) {
        return groupConfigs.get(groupId);
    }
    
    public static Iterable<String> getGroupIds() {
        return groupConfigs.keySet();
    }
}
