package com.factorcraft.permission;

import java.util.*;

/**
 * 权限组定义
 */
public class PermissionGroup {
    private final String id;
    private final String displayName;
    private final Set<Permission> permissions;
    
    public PermissionGroup(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.permissions = new HashSet<>();
    }
    
    public PermissionGroup addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }
    
    public PermissionGroup addAllPermissions() {
        // 添加所有预定义权限
        this.permissions.add(PermissionManager.FACTOR_INFO);
        this.permissions.add(PermissionManager.FACTOR_SET);
        this.permissions.add(PermissionManager.FACTOR_RESET);
        this.permissions.add(PermissionManager.TRAIT_INFO);
        this.permissions.add(PermissionManager.TRAIT_ADD);
        this.permissions.add(PermissionManager.TRAIT_REMOVE);
        this.permissions.add(PermissionManager.TRAIT_CLEAR);
        this.permissions.add(PermissionManager.RELOAD_CONFIG);
        this.permissions.add(PermissionManager.VIEW_STATS);
        return this;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionGroup that = (PermissionGroup) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "PermissionGroup{" +
            "id='" + id + '\'' +
            ", displayName='" + displayName + '\'' +
            ", permissions=" + permissions.size() +
            '}';
    }
}