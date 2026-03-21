package com.factorcraft.factor;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * Factor 标签系统
 * 
 * 提供 Factor 的分类标签，支持按属性分类查询
 */
public class FactorTags {
    
    // ========== 标签注册表 Key ==========
    
    public static final RegistryKey<Registry<Factor>> FACTOR_REGISTRY_KEY = 
        RegistryKey.ofRegistry(Identifier.of("factorcraft", "factor"));
    
    // ========== 类型标签 ==========
    
    /** 元素类型 Factor */
    public static final TagKey<Factor> ELEMENTAL = create("elemental");
    /** 火属性 Factor */
    public static final TagKey<Factor> FIRE = create("fire");
    /** 水属性 Factor */
    public static final TagKey<Factor> WATER = create("water");
    /** 土属性 Factor */
    public static final TagKey<Factor> EARTH = create("earth");
    /** 风属性 Factor */
    public static final TagKey<Factor> AIR = create("air");
    
    // ========== 能量标签 ==========
    
    /** 能量类型 Factor */
    public static final TagKey<Factor> ENERGY = create("energy");
    /** 生命类型 Factor */
    public static final TagKey<Factor> LIFE = create("life");
    /** 死亡类型 Factor */
    public static final TagKey<Factor> DEATH = create("death");
    /** 混沌类型 Factor */
    public static final TagKey<Factor> CHAOS = create("chaos");
    /** 秩序类型 Factor */
    public static final TagKey<Factor> ORDER = create("order");
    
    // ========== 稀有度标签 ==========
    
    /** 普通 Factor */
    public static final TagKey<Factor> COMMON = create("rarity/common");
    /** 罕见 Factor */
    public static final TagKey<Factor> UNCOMMON = create("rarity/uncommon");
    /** 稀有 Factor */
    public static final TagKey<Factor> RARE = create("rarity/rare");
    /** 史诗 Factor */
    public static final TagKey<Factor> EPIC = create("rarity/epic");
    /** 传说 Factor */
    public static final TagKey<Factor> LEGENDARY = create("rarity/legendary");
    
    // ========== 功能标签 ==========
    
    /** 可用于合成 */
    public static final TagKey<Factor> CRAFTABLE = create("functional/craftable");
    /** 可用于附魔 */
    public static final TagKey<Factor> ENCHANTABLE = create("functional/enchantable");
    /** 可用于强化装备 */
    public static final TagKey<Factor> EQUIPPABLE = create("functional/equippable");
    /** 可交易 */
    public static final TagKey<Factor> TRADABLE = create("functional/tradable");
    
    // ========== 来源标签 ==========
    
    /** 从方块掉落 */
    public static final TagKey<Factor> FROM_BLOCK = create("source/block");
    /** 从实体掉落 */
    public static final TagKey<Factor> FROM_ENTITY = create("source/entity");
    /** 从宝箱获取 */
    public static final TagKey<Factor> FROM_CHEST = create("source/chest");
    /** 通过合成获取 */
    public static final TagKey<Factor> FROM_CRAFT = create("source/craft");
    /** 通过任务获取 */
    public static final TagKey<Factor> FROM_QUEST = create("source/quest");
    
    // ========== 工具方法 ==========
    
    /**
     * 创建 Factor 标签
     */
    private static TagKey<Factor> create(String path) {
        return TagKey.of(FACTOR_REGISTRY_KEY, Identifier.of("factorcraft", path));
    }
    
    /**
     * 创建自定义命名空间的 Factor 标签
     */
    public static TagKey<Factor> create(Identifier id) {
        return TagKey.of(FACTOR_REGISTRY_KEY, id);
    }
    
    /**
     * 根据类型获取标签
     */
    public static TagKey<Factor> getTagForType(FactorType type) {
        return switch (type) {
            case FIRE -> FIRE;
            case WATER -> WATER;
            case EARTH -> EARTH;
            case AIR -> AIR;
            case ENERGY -> ENERGY;
            case LIFE -> LIFE;
            case DEATH -> DEATH;
            case CHAOS -> CHAOS;
            case ORDER -> ORDER;
            default -> ELEMENTAL;
        };
    }
    
    /**
     * 根据稀有度获取标签
     */
    public static TagKey<Factor> getTagForRarity(FactorRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON;
            case UNCOMMON -> UNCOMMON;
            case RARE -> RARE;
            case EPIC -> EPIC;
            case LEGENDARY -> LEGENDARY;
        };
    }
}