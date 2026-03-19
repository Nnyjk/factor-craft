package com.factorcraft.module.social.manager;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Factor Craft 权限管理系统
 */
public class PermissionManager {
    private static final Map<String, Set<Permission>> PLAYER_PERMISSIONS = new HashMap<>();
    private static final Map<String, PermissionGroup> PERMISSION_GROUPS = new HashMap<>();
    private static final Map<String, String> PLAYER_GROUPS = new HashMap<>();
    
    // 默认权限组
    private static String defaultGroup = "player";
    
    // 预定义权限
    public static final Permission FACTOR_INFO = new Permission("factorcraft.factor.info", "查看 Factor 信息");
    public static final Permission FACTOR_SET = new Permission("factorcraft.factor.set", "设置 Factor 浓度", 2);
    public static final Permission FACTOR_RESET = new Permission("factorcraft.factor.reset", "重置 Factor", 2);
    
    public static final Permission TRAIT_INFO = new Permission("factorcraft.trait.info", "查看特性信息");
    public static final Permission TRAIT_ADD = new Permission("factorcraft.trait.add", "添加特性", 2);
    public static final Permission TRAIT_REMOVE = new Permission("factorcraft.trait.remove", "移除特性", 2);
    public static final Permission TRAIT_CLEAR = new Permission("factorcraft.trait.clear", "清除特性", 2);
    
    public static final Permission MACHINE_CREATE = new Permission("factorcraft.machine.create", "创建机器", 0);
    public static final Permission MACHINE_DESTROY = new Permission("factorcraft.machine.destroy", "破坏机器", 0);
    
    public static final Permission RELOAD_CONFIG = new Permission("factorcraft.admin.reload", "重载配置", 3);
    public static final Permission VIEW_STATS = new Permission("factorcraft.admin.stats", "查看统计", 2);
    
    // 预定义权限组
    public static final PermissionGroup GROUP_PLAYER = new PermissionGroup("player", "玩家")
        .addPermission(FACTOR_INFO)
        .addPermission(TRAIT_INFO)
        .addPermission(MACHINE_CREATE)
        .addPermission(MACHINE_DESTROY);
    
    public static final PermissionGroup GROUP_MODERATOR = new PermissionGroup("moderator", "管理员")
        .addPermission(FACTOR_INFO)
        .addPermission(FACTOR_SET)
        .addPermission(FACTOR_RESET)
        .addPermission(TRAIT_INFO)
        .addPermission(TRAIT_ADD)
        .addPermission(TRAIT_REMOVE)
        .addPermission(VIEW_STATS)
        .addPermission(MACHINE_CREATE)
        .addPermission(MACHINE_DESTROY);
    
    public static final PermissionGroup GROUP_ADMIN = new PermissionGroup("admin", "超级管理员")
        .addAllPermissions();
    
    static {
        registerGroup(GROUP_PLAYER);
        registerGroup(GROUP_MODERATOR);
        registerGroup(GROUP_ADMIN);
    }
    
    /**
     * 检查玩家是否有权限
     */
    public static boolean hasPermission(ServerPlayerEntity player, Permission permission) {
        String uuid = player.getUuidAsString();
        
        // 优先检查玩家特定权限
        Set<Permission> playerPerms = PLAYER_PERMISSIONS.get(uuid);
        if (playerPerms != null && playerPerms.contains(permission)) {
            return true;
        }
        
        // 检查玩家权限组
        String groupId = PLAYER_GROUPS.get(uuid);
        if (groupId != null) {
            PermissionGroup group = PERMISSION_GROUPS.get(groupId);
            if (group != null && group.hasPermission(permission)) {
                return true;
            }
        }
        
        // 检查 OP 等级
        if (player.hasPermissionLevel(permission.opLevel())) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 检查玩家是否有权限（通过权限 ID）
     */
    public static boolean hasPermission(ServerPlayerEntity player, String permissionId) {
        String uuid = player.getUuidAsString();
        
        // 检查玩家权限组
        String groupId = PLAYER_GROUPS.get(uuid);
        if (groupId != null) {
            PermissionGroup group = PERMISSION_GROUPS.get(groupId);
            if (group != null && group.hasPermission(permissionId)) {
                return true;
            }
        }
        
        // 通配符检查
        if (permissionId.contains(".")) {
            String wildcard = permissionId.substring(0, permissionId.lastIndexOf('.')) + ".*";
            String groupWildcard = "*";
            
            String gid = PLAYER_GROUPS.get(uuid);
            if (gid != null) {
                PermissionGroup group = PERMISSION_GROUPS.get(gid);
                if (group != null) {
                    for (Permission perm : group.getPermissions()) {
                        if (perm.id().equals(wildcard) || perm.id().equals(groupWildcard)) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * 给玩家添加权限
     */
    public static void grantPermission(String playerUuid, Permission permission) {
        PLAYER_PERMISSIONS.computeIfAbsent(playerUuid, k -> new HashSet<>()).add(permission);
    }
    
    /**
     * 移除玩家权限
     */
    public static void revokePermission(String playerUuid, Permission permission) {
        Set<Permission> perms = PLAYER_PERMISSIONS.get(playerUuid);
        if (perms != null) {
            perms.remove(permission);
        }
    }
    
    /**
     * 设置玩家权限组
     */
    public static void setPlayerGroup(String playerUuid, String groupId) {
        PermissionGroup group = PERMISSION_GROUPS.get(groupId);
        if (group != null) {
            PLAYER_GROUPS.put(playerUuid, groupId);
            PLAYER_PERMISSIONS.put(playerUuid, new HashSet<>(group.getPermissions()));
        }
    }
    
    /**
     * 获取玩家权限组
     */
    public static String getPlayerGroup(String playerUuid) {
        return PLAYER_GROUPS.getOrDefault(playerUuid, defaultGroup);
    }
    
    /**
     * 获取玩家个人权限列表
     */
    public static Set<Permission> getPlayerPermissions(String playerUuid) {
        return PLAYER_PERMISSIONS.getOrDefault(playerUuid, Collections.emptySet());
    }
    
    /**
     * 初始化玩家权限（使用默认权限组）
     */
    public static void initPlayerPermissions(String playerUuid) {
        setPlayerGroup(playerUuid, defaultGroup);
    }
    
    /**
     * 注册权限组
     */
    public static void registerGroup(PermissionGroup group) {
        PERMISSION_GROUPS.put(group.getId(), group);
    }
    
    /**
     * 获取权限组
     */
    public static PermissionGroup getGroup(String groupId) {
        return PERMISSION_GROUPS.get(groupId);
    }
    
    /**
     * 获取所有权限组
     */
    public static Collection<PermissionGroup> getAllGroups() {
        return PERMISSION_GROUPS.values();
    }
    
    /**
     * 设置默认权限组
     */
    public static void setDefaultGroup(String groupId) {
        if (PERMISSION_GROUPS.containsKey(groupId)) {
            defaultGroup = groupId;
        }
    }
    
    /**
     * 获取默认权限组
     */
    public static String getDefaultGroup() {
        return defaultGroup;
    }
    
    /**
     * 清除权限组（用于重载配置）
     */
    public static void clearGroups() {
        PERMISSION_GROUPS.clear();
    }
    
    /**
     * 清除玩家所有权限
     */
    public static void clearPlayerPermissions(String playerUuid) {
        PLAYER_PERMISSIONS.remove(playerUuid);
        PLAYER_GROUPS.remove(playerUuid);
    }
    
    /**
     * 清除所有权限数据
     */
    public static void clear() {
        PLAYER_PERMISSIONS.clear();
        PLAYER_GROUPS.clear();
    }
}
