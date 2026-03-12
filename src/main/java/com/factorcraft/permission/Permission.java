package com.factorcraft.permission;

import net.minecraft.util.Identifier;

/**
 * 权限定义
 */
public record Permission(
    String id,
    String description,
    int opLevel
) {
    public Permission(String id, String description) {
        this(id, description, 0);
    }
    
    public String getPermissionNode() {
        return id;
    }
    
    public Identifier getIdentifier() {
        String[] parts = id.split("\\.");
        if (parts.length >= 2) {
            return Identifier.of(parts[0], String.join(".", Arrays.copyOfRange(parts, 1, parts.length)));
        }
        return Identifier.of("factorcraft", id);
    }
    
    private static class Arrays {
        static String[] copyOfRange(String[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0) throw new IllegalArgumentException(from + " > " + to);
            String[] copy = new String[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }
    }
}