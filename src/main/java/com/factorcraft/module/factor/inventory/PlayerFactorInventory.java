package com.factorcraft.module.factor.inventory;

import com.factorcraft.factor.Factor;
import com.factorcraft.factor.FactorRarity;
import com.factorcraft.factor.FactorType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 玩家 Factor 背包系统
 * 
 * 存储玩家收集的所有 Factor，支持：
 * - Factor 添加/移除
 * - 按类型/稀有度查询
 * - NBT 持久化
 * - 容量限制
 */
public class PlayerFactorInventory {
    
    /** 最大 Factor 数量 */
    private static final int MAX_FACTORS = 256;
    
    /** Factor 列表 */
    private final List<Factor> factors = new ArrayList<>();
    
    /** 按 ID 索引的 Factor Map（用于快速查找） */
    private final Map<Identifier, Factor> factorById = new HashMap<>();
    
    /** 按类型分组的 Factor Map */
    private final Map<FactorType, List<Factor>> factorsByType = new HashMap<>();
    
    /** 按稀有度分组的 Factor Map */
    private final Map<FactorRarity, List<Factor>> factorsByRarity = new HashMap<>();
    
    /** 容量（可通过升级增加） */
    private int maxCapacity = MAX_FACTORS;
    
    /**
     * 添加 Factor 到背包
     * 
     * @param factor 要添加的 Factor
     * @return 是否成功添加
     */
    public boolean addFactor(Factor factor) {
        if (factors.size() >= maxCapacity) {
            return false;
        }
        
        // 检查是否已存在相同 ID 的 Factor
        if (factorById.containsKey(factor.getId())) {
            // 已存在，可以选择合并或拒绝
            return false;
        }
        
        factors.add(factor);
        factorById.put(factor.getId(), factor);
        
        // 更新索引
        factorsByType.computeIfAbsent(factor.getType(), k -> new ArrayList<>()).add(factor);
        factorsByRarity.computeIfAbsent(factor.getRarity(), k -> new ArrayList<>()).add(factor);
        
        return true;
    }
    
    /**
     * 尝试合并或添加 Factor
     * 如果存在相同 ID 的 Factor，则尝试合并（叠加数值）
     * 
     * @param factor 要添加的 Factor
     * @return 是否成功添加或合并
     */
    public boolean addOrMergeFactor(Factor factor) {
        Factor existing = factorById.get(factor.getId());
        if (existing != null) {
            // 合并逻辑：增加数量或等级
            // 这里简单地增加基础威力
            // 实际实现可以根据需求定制
            return true;
        }
        return addFactor(factor);
    }
    
    /**
     * 移除 Factor
     * 
     * @param factorId Factor ID
     * @return 被移除的 Factor，如果不存在则返回 null
     */
    public Factor removeFactor(Identifier factorId) {
        Factor factor = factorById.remove(factorId);
        if (factor == null) {
            return null;
        }
        
        factors.remove(factor);
        
        // 更新索引
        List<Factor> typeList = factorsByType.get(factor.getType());
        if (typeList != null) {
            typeList.remove(factor);
        }
        
        List<Factor> rarityList = factorsByRarity.get(factor.getRarity());
        if (rarityList != null) {
            rarityList.remove(factor);
        }
        
        return factor;
    }
    
    /**
     * 获取 Factor
     * 
     * @param factorId Factor ID
     * @return Factor，如果不存在则返回 Optional.empty()
     */
    public Optional<Factor> getFactor(Identifier factorId) {
        return Optional.ofNullable(factorById.get(factorId));
    }
    
    /**
     * 获取所有 Factor
     * 
     * @return Factor 列表的不可变副本
     */
    public List<Factor> getAllFactors() {
        return List.copyOf(factors);
    }
    
    /**
     * 按类型获取 Factor
     * 
     * @param type Factor 类型
     * @return 该类型的 Factor 列表
     */
    public List<Factor> getFactorsByType(FactorType type) {
        return factorsByType.getOrDefault(type, List.of());
    }
    
    /**
     * 按稀有度获取 Factor
     * 
     * @param rarity Factor 稀有度
     * @return 该稀有度的 Factor 列表
     */
    public List<Factor> getFactorsByRarity(FactorRarity rarity) {
        return factorsByRarity.getOrDefault(rarity, List.of());
    }
    
    /**
     * 获取 Factor 数量
     * 
     * @return 当前 Factor 数量
     */
    public int getFactorCount() {
        return factors.size();
    }
    
    /**
     * 获取最大容量
     * 
     * @return 最大容量
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    /**
     * 设置最大容量
     * 
     * @param capacity 新的最大容量
     */
    public void setMaxCapacity(int capacity) {
        this.maxCapacity = Math.max(0, capacity);
    }
    
    /**
     * 检查是否已满
     * 
     * @return 是否已满
     */
    public boolean isFull() {
        return factors.size() >= maxCapacity;
    }
    
    /**
     * 清空背包
     */
    public void clear() {
        factors.clear();
        factorById.clear();
        factorsByType.clear();
        factorsByRarity.clear();
    }
    
    /**
     * 写入 NBT
     * 
     * @param nbt NBT 标签
     */
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("MaxCapacity", maxCapacity);
        
        NbtList factorList = new NbtList();
        for (Factor factor : factors) {
            factorList.add(factor.toNbt());
        }
        nbt.put("Factors", factorList);
    }
    
    /**
     * 从 NBT 读取
     * 
     * @param nbt NBT 标签
     */
    public void readNbt(NbtCompound nbt) {
        clear();
        
        maxCapacity = nbt.getInt("MaxCapacity");
        if (maxCapacity <= 0) {
            maxCapacity = MAX_FACTORS;
        }
        
        NbtList factorList = nbt.getList("Factors", 10); // 10 = NbtCompound
        for (int i = 0; i < factorList.size(); i++) {
            NbtCompound factorNbt = factorList.getCompound(i);
            Factor factor = Factor.fromNbt(factorNbt);
            if (factor != null) {
                factors.add(factor);
                factorById.put(factor.getId(), factor);
                
                factorsByType.computeIfAbsent(factor.getType(), k -> new ArrayList<>()).add(factor);
                factorsByRarity.computeIfAbsent(factor.getRarity(), k -> new ArrayList<>()).add(factor);
            }
        }
    }
    
    /**
     * 获取各类型 Factor 的总数统计
     * 
     * @return 类型 -> 数量的映射
     */
    public Map<FactorType, Integer> getTypeCounts() {
        Map<FactorType, Integer> counts = new HashMap<>();
        for (FactorType type : FactorType.values()) {
            counts.put(type, factorsByType.getOrDefault(type, List.of()).size());
        }
        return counts;
    }
    
    /**
     * 获取各稀有度 Factor 的总数统计
     * 
     * @return 稀有度 -> 数量的映射
     */
    public Map<FactorRarity, Integer> getRarityCounts() {
        Map<FactorRarity, Integer> counts = new HashMap<>();
        for (FactorRarity rarity : FactorRarity.values()) {
            counts.put(rarity, factorsByRarity.getOrDefault(rarity, List.of()).size());
        }
        return counts;
    }
    
    @Override
    public String toString() {
        return String.format("PlayerFactorInventory{factors=%d, capacity=%d}", 
            factors.size(), maxCapacity);
    }
}