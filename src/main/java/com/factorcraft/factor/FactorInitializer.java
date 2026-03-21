package com.factorcraft.factor;

import com.factorcraft.FactorCraftMod;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor 初始化器
 * 
 * 在 Mod 初始化时注册默认 Factor
 */
public class FactorInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/FactorInitializer");
    
    private static boolean initialized = false;
    
    /**
     * 初始化 Factor 系统
     */
    public static void initialize() {
        if (initialized) {
            LOGGER.warn("FactorInitializer already initialized");
            return;
        }
        
        LOGGER.info("Initializing Factor system...");
        
        // 注册基础 Factor
        registerBasicFactors();
        
        // 冻结注册表
        FactorRegistry.getInstance().freeze();
        
        initialized = true;
        LOGGER.info("Factor system initialized with {} factors", 
            FactorRegistry.getInstance().size());
    }
    
    /**
     * 注册基础 Factor
     */
    private static void registerBasicFactors() {
        FactorRegistry registry = FactorRegistry.getInstance();
        
        // ========== 元素 Factor ==========
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "fire_essence"),
            "火焰精华"
        )
            .type(FactorType.FIRE)
            .rarity(FactorRarity.COMMON)
            .level(1)
            .tier(1)
            .basePower(10.0)
            .addTag("elemental")
            .addTag("basic")
            .description("从火焰中提取的基础能量精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "water_essence"),
            "水精华"
        )
            .type(FactorType.WATER)
            .rarity(FactorRarity.COMMON)
            .level(1)
            .tier(1)
            .basePower(10.0)
            .addTag("elemental")
            .addTag("basic")
            .description("从水源中提取的基础能量精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "earth_essence"),
            "土精华"
        )
            .type(FactorType.EARTH)
            .rarity(FactorRarity.COMMON)
            .level(1)
            .tier(1)
            .basePower(10.0)
            .addTag("elemental")
            .addTag("basic")
            .description("从大地中提取的基础能量精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "air_essence"),
            "风精华"
        )
            .type(FactorType.AIR)
            .rarity(FactorRarity.COMMON)
            .level(1)
            .tier(1)
            .basePower(10.0)
            .addTag("elemental")
            .addTag("basic")
            .description("从气流中提取的基础能量精华")
            .build()
        );
        
        // ========== 能量 Factor ==========
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "life_essence"),
            "生命精华"
        )
            .type(FactorType.LIFE)
            .rarity(FactorRarity.UNCOMMON)
            .level(10)
            .tier(2)
            .basePower(50.0)
            .addTag("energy")
            .addTag("healing")
            .description("蕴含生命力量的精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "death_essence"),
            "死亡精华"
        )
            .type(FactorType.DEATH)
            .rarity(FactorRarity.UNCOMMON)
            .level(10)
            .tier(2)
            .basePower(50.0)
            .addTag("energy")
            .addTag("combat")
            .description("蕴含死亡力量的精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "chaos_essence"),
            "混沌精华"
        )
            .type(FactorType.CHAOS)
            .rarity(FactorRarity.RARE)
            .level(25)
            .tier(3)
            .basePower(100.0)
            .addTag("energy")
            .addTag("chaotic")
            .description("蕴含混沌力量的精华")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "order_essence"),
            "秩序精华"
        )
            .type(FactorType.ORDER)
            .rarity(FactorRarity.RARE)
            .level(25)
            .tier(3)
            .basePower(100.0)
            .addTag("energy")
            .addTag("stable")
            .description("蕴含秩序力量的精华")
            .build()
        );
        
        // ========== 特殊 Factor ==========
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "void_shard"),
            "虚空碎片"
        )
            .type(FactorType.VOID)
            .rarity(FactorRarity.EPIC)
            .level(50)
            .tier(4)
            .basePower(250.0)
            .addTag("special")
            .addTag("void")
            .description("来自虚空的神秘碎片")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "time_fragment"),
            "时间碎片"
        )
            .type(FactorType.TIME)
            .rarity(FactorRarity.EPIC)
            .level(50)
            .tier(4)
            .basePower(250.0)
            .addTag("special")
            .addTag("time")
            .description("凝固的时间碎片")
            .build()
        );
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "space_crystal"),
            "空间水晶"
        )
            .type(FactorType.SPACE)
            .rarity(FactorRarity.EPIC)
            .level(50)
            .tier(4)
            .basePower(250.0)
            .addTag("special")
            .addTag("space")
            .description("蕴含空间之力的水晶")
            .build()
        );
        
        // ========== 原始 Factor ==========
        
        registry.register(new Factor.Builder(
            Identifier.of("factorcraft", "primal_core"),
            "原始核心"
        )
            .type(FactorType.PRIMAL)
            .rarity(FactorRarity.LEGENDARY)
            .level(100)
            .tier(5)
            .basePower(1000.0)
            .addTag("primal")
            .addTag("legendary")
            .description("世界诞生之初的原始力量结晶")
            .build()
        );
        
        LOGGER.debug("Registered {} basic factors", registry.size());
    }
    
    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}