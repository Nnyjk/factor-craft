package com.factorcraft.factor;

import com.factorcraft.FactorCraftMod;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factor 注册器
 * 
 * 支持自定义 Factor 的注册与管理
 * 提供查询、遍历、标签过滤等功能
 */
public class FactorRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/FactorRegistry");
    
    // ========== 单例 ==========
    
    private static final FactorRegistry INSTANCE = new FactorRegistry();
    
    public static FactorRegistry getInstance() {
        return INSTANCE;
    }
    
    // ========== 字段 ==========
    
    private final BiMap<Identifier, Factor> factors = HashBiMap.create();
    private final Map<String, Set<Factor>> tagsToFactors = new HashMap<>();
    private final Map<FactorType, Set<Factor>> typeToFactors = new EnumMap<>(FactorType.class);
    private final Map<FactorRarity, Set<Factor>> rarityToFactors = new EnumMap<>(FactorRarity.class);
    
    private boolean frozen = false;
    
    // ========== 构造器 ==========
    
    private FactorRegistry() {
        // 初始化类型映射
        for (FactorType type : FactorType.values()) {
            typeToFactors.put(type, new HashSet<>());
        }
        // 初始化稀有度映射
        for (FactorRarity rarity : FactorRarity.values()) {
            rarityToFactors.put(rarity, new HashSet<>());
        }
    }
    
    // ========== 注册方法 ==========
    
    /**
     * 注册 Factor
     * 
     * @param factor 要注册的 Factor
     * @return 注册成功返回 true，如果 ID 已存在则返回 false
     * @throws IllegalStateException 如果注册表已冻结
     */
    public boolean register(Factor factor) {
        if (frozen) {
            throw new IllegalStateException("Cannot register Factor after registry is frozen");
        }
        
        Identifier id = factor.getId();
        if (factors.containsKey(id)) {
            LOGGER.warn("Factor with ID {} already registered, skipping", id);
            return false;
        }
        
        factors.put(id, factor);
        
        // 更新类型索引
        typeToFactors.get(factor.getType()).add(factor);
        
        // 更新稀有度索引
        rarityToFactors.get(factor.getRarity()).add(factor);
        
        // 更新标签索引
        for (String tag : factor.getTags()) {
            tagsToFactors.computeIfAbsent(tag, k -> new HashSet<>()).add(factor);
        }
        
        LOGGER.debug("Registered Factor: {}", id);
        return true;
    }
    
    /**
     * 通过 Builder 注册 Factor
     */
    public boolean register(Identifier id, String name, Factor.Builder builder) {
        return register(builder.build());
    }
    
    /**
     * 冻结注册表，禁止后续注册
     */
    public void freeze() {
        this.frozen = true;
        LOGGER.info("FactorRegistry frozen with {} factors registered", factors.size());
    }
    
    /**
     * 检查注册表是否已冻结
     */
    public boolean isFrozen() {
        return frozen;
    }
    
    // ========== 查询方法 ==========
    
    /**
     * 通过 ID 获取 Factor
     */
    public Optional<Factor> get(Identifier id) {
        return Optional.ofNullable(factors.get(id));
    }
    
    /**
     * 通过字符串 ID 获取 Factor
     */
    public Optional<Factor> get(String id) {
        return get(Identifier.tryParse(id));
    }
    
    /**
     * 检查 ID 是否已注册
     */
    public boolean contains(Identifier id) {
        return factors.containsKey(id);
    }
    
    /**
     * 获取所有注册的 Factor ID
     */
    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(factors.keySet());
    }
    
    /**
     * 获取所有注册的 Factor
     */
    public Collection<Factor> getAll() {
        return Collections.unmodifiableCollection(factors.values());
    }
    
    /**
     * 获取注册的 Factor 数量
     */
    public int size() {
        return factors.size();
    }
    
    // ========== 类型查询 ==========
    
    /**
     * 按类型获取 Factor 集合
     */
    public Set<Factor> getByType(FactorType type) {
        return Collections.unmodifiableSet(typeToFactors.getOrDefault(type, Collections.emptySet()));
    }
    
    /**
     * 获取所有元素类型的 Factor
     */
    public Set<Factor> getElementalFactors() {
        Set<Factor> result = new HashSet<>();
        for (FactorType type : FactorType.values()) {
            if (type.isElemental()) {
                result.addAll(typeToFactors.get(type));
            }
        }
        return result;
    }
    
    /**
     * 获取所有能量类型的 Factor
     */
    public Set<Factor> getEnergyFactors() {
        Set<Factor> result = new HashSet<>();
        for (FactorType type : FactorType.values()) {
            if (type.isEnergy()) {
                result.addAll(typeToFactors.get(type));
            }
        }
        return result;
    }
    
    // ========== 稀有度查询 ==========
    
    /**
     * 按稀有度获取 Factor 集合
     */
    public Set<Factor> getByRarity(FactorRarity rarity) {
        return Collections.unmodifiableSet(rarityToFactors.getOrDefault(rarity, Collections.emptySet()));
    }
    
    /**
     * 获取指定稀有度及以上的 Factor
     */
    public Set<Factor> getByRarityOrHigher(FactorRarity minRarity) {
        return factors.values().stream()
            .filter(f -> f.getRarity().getTier() >= minRarity.getTier())
            .collect(Collectors.toSet());
    }
    
    // ========== 标签查询 ==========
    
    /**
     * 按标签获取 Factor 集合
     */
    public Set<Factor> getByTag(String tag) {
        return Collections.unmodifiableSet(
            tagsToFactors.getOrDefault(tag, Collections.emptySet())
        );
    }
    
    /**
     * 按多个标签获取 Factor 集合（满足任意一个）
     */
    public Set<Factor> getByAnyTag(Set<String> tags) {
        Set<Factor> result = new HashSet<>();
        for (String tag : tags) {
            result.addAll(tagsToFactors.getOrDefault(tag, Collections.emptySet()));
        }
        return result;
    }
    
    /**
     * 按多个标签获取 Factor 集合（满足所有）
     */
    public Set<Factor> getByAllTags(Set<String> tags) {
        if (tags.isEmpty()) {
            return Collections.emptySet();
        }
        
        Set<Factor> result = null;
        for (String tag : tags) {
            Set<Factor> factorsWithTag = tagsToFactors.getOrDefault(tag, Collections.emptySet());
            if (result == null) {
                result = new HashSet<>(factorsWithTag);
            } else {
                result.retainAll(factorsWithTag);
            }
            if (result.isEmpty()) {
                break;
            }
        }
        
        return result != null ? result : Collections.emptySet();
    }
    
    // ========== 遍历方法 ==========
    
    /**
     * 遍历所有 Factor
     */
    public void forEach(java.util.function.Consumer<Factor> consumer) {
        factors.values().forEach(consumer);
    }
    
    /**
     * 流式访问所有 Factor
     */
    public java.util.stream.Stream<Factor> stream() {
        return factors.values().stream();
    }
    
    // ========== 随机获取 ==========
    
    /**
     * 随机获取一个 Factor
     */
    public Optional<Factor> getRandom(Random random) {
        if (factors.isEmpty()) {
            return Optional.empty();
        }
        int index = random.nextInt(factors.size());
        return Optional.of(factors.values().toArray(new Factor[0])[index]);
    }
    
    /**
     * 按稀有度权重随机获取 Factor
     */
    public Optional<Factor> getRandomWeighted(Random random) {
        if (factors.isEmpty()) {
            return Optional.empty();
        }
        
        // 计算总权重（稀有度越高权重越低）
        double totalWeight = factors.values().stream()
            .mapToDouble(f -> f.getRarity().getDropChance())
            .sum();
        
        double randomValue = random.nextDouble() * totalWeight;
        double cumulative = 0;
        
        for (Factor factor : factors.values()) {
            cumulative += factor.getRarity().getDropChance();
            if (randomValue <= cumulative) {
                return Optional.of(factor);
            }
        }
        
        return getRandom(random);
    }
    
    // ========== 重置方法（仅测试用） ==========
    
    /**
     * 重置注册表（仅测试用）
     */
    public void reset() {
        factors.clear();
        tagsToFactors.clear();
        for (Set<Factor> set : typeToFactors.values()) {
            set.clear();
        }
        for (Set<Factor> set : rarityToFactors.values()) {
            set.clear();
        }
        frozen = false;
        LOGGER.info("FactorRegistry reset");
    }
}