package com.factorcraft.module.research;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 研究系统组件类型注册
 */
public class ModComponents {
    
    private static final String MOD_ID = "factorcraft";
    
    // 玩家研究点组件
    public static final ComponentType<ResearchPointStorage> RESEARCH_POINT = 
        Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MOD_ID, "research_point"),
            ComponentType.<ResearchPointStorage>builder()
                .codec(ResearchPointStorage.CODEC)
                .build()
        );
    
    public static void register() {
        // 组件在注册时自动初始化
    }
}
