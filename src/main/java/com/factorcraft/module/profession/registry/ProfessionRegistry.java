package com.factorcraft.module.profession.registry;

import com.factorcraft.module.profession.config.ProfessionConfig;
import com.factorcraft.module.profession.config.ProfessionConfigLoader;
import com.factorcraft.module.profession.model.ProfessionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 职业注册器
 * 管理职业类型的注册与查询
 */
public class ProfessionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionRegistry");
    
    private static final ProfessionRegistry INSTANCE = new ProfessionRegistry();
    
    private final Map<String, ProfessionType> professions = new HashMap<>();
    private final Map<String, ProfessionConfig> configs = new HashMap<>();
    
    private ProfessionRegistry() {
        // 注册默认职业
        for (ProfessionType type : ProfessionType.values()) {
            professions.put(type.getId(), type);
        }
    }
    
    public static ProfessionRegistry getInstance() {
        return INSTANCE;
    }
    
    public void register(ProfessionType type) {
        if (professions.containsKey(type.getId())) {
            LOGGER.warn("Profession already registered: {}", type.getId());
            return;
        }
        professions.put(type.getId(), type);
        LOGGER.info("Registered profession: {}", type.getDisplayName());
    }
    
    public Optional<ProfessionType> get(String id) {
        return Optional.ofNullable(professions.get(id));
    }
    
    public Collection<ProfessionType> getAll() {
        return professions.values();
    }
    
    public void loadConfigs() {
        configs.clear();
        configs.putAll(ProfessionConfigLoader.getLoadedConfigs());
        
        // 可以在这里根据配置创建新的职业类型
        // 但由于ProfessionType是枚举，暂时只存储配置供查询
        LOGGER.info("Loaded {} profession configs", configs.size());
    }
    
    public Optional<ProfessionConfig> getConfig(String id) {
        return Optional.ofNullable(configs.get(id));
    }
    
    public Map<String, ProfessionConfig> getAllConfigs() {
        return Collections.unmodifiableMap(configs);
    }
}