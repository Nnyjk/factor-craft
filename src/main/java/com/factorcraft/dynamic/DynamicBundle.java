package com.factorcraft.dynamic;

import java.util.List;
import java.util.Map;

/**
 * 统一动态加载数据模型：
 * - configs: 任意业务配置键值
 * - textures/models/lang: 资源路径或国际化键值
 */
public record DynamicBundle(
        Map<String, Object> configs,
        List<TextureSpec> textures,
        List<ModelSpec> models,
        List<LangSpec> languages
) {
    public record TextureSpec(String key, String resourcePath) {}

    public record ModelSpec(String key, String resourcePath) {}

    public record LangSpec(String locale, Map<String, String> entries) {}
}
