package com.factorcraft.module.integration.detection;

import net.minecraft.item.Item;

/**
 * 检测到的可集成内容。
 */
public record DetectedContent(
    String itemId,
    String sourceModId,
    ContentCategory category,
    Item item
) {
    /**
     * 内容类别。
     */
    public enum ContentCategory {
        TOOL("tool"),
        WEAPON("weapon"),
        ARMOR("armor"),
        FURNITURE("furniture"),
        DECOR("decor");
        
        private final String id;
        
        ContentCategory(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
        
        public static ContentCategory fromId(String id) {
            for (ContentCategory cat : values()) {
                if (cat.id.equalsIgnoreCase(id)) {
                    return cat;
                }
            }
            return null;
        }
    }
}