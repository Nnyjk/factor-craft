package com.factorcraft.factor.synthesis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Factor 合成材料
 * 
 * 定义合成配方中的输入材料要求，可以是精确匹配或范围匹配
 */
public class FactorIngredient {
    
    // ========== 字段 ==========
    
    private final Identifier factorId;
    private final Optional<Integer> minLevel;
    private final Optional<Integer> maxLevel;
    private final Optional<Integer> minTier;
    private final Optional<Integer> maxTier;
    private final int count;
    
    // ========== Codec ==========
    
    public static final Codec<FactorIngredient> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("factor").forGetter(FactorIngredient::getFactorId),
            Codec.INT.optionalFieldOf("min_level").forGetter(FactorIngredient::getMinLevel),
            Codec.INT.optionalFieldOf("max_level").forGetter(FactorIngredient::getMaxLevel),
            Codec.INT.optionalFieldOf("min_tier").forGetter(FactorIngredient::getMinTier),
            Codec.INT.optionalFieldOf("max_tier").forGetter(FactorIngredient::getMaxTier),
            Codec.INT.optionalFieldOf("count", 1).forGetter(FactorIngredient::getCount)
        ).apply(instance, FactorIngredient::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, FactorIngredient> PACKET_CODEC =
        PacketCodec.tuple(
            Identifier.PACKET_CODEC, FactorIngredient::getFactorId,
            PacketCodecs.optional(PacketCodecs.INTEGER), FactorIngredient::getMinLevel,
            PacketCodecs.optional(PacketCodecs.INTEGER), FactorIngredient::getMaxLevel,
            PacketCodecs.optional(PacketCodecs.INTEGER), FactorIngredient::getMinTier,
            PacketCodecs.optional(PacketCodecs.INTEGER), FactorIngredient::getMaxTier,
            PacketCodecs.INTEGER, FactorIngredient::getCount,
            FactorIngredient::new
        );
    
    // ========== 构造器 ==========
    
    public FactorIngredient(Identifier factorId, 
                           Optional<Integer> minLevel, Optional<Integer> maxLevel,
                           Optional<Integer> minTier, Optional<Integer> maxTier,
                           int count) {
        this.factorId = factorId;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.minTier = minTier;
        this.maxTier = maxTier;
        this.count = Math.max(1, count);
    }
    
    // 简化构造器 - 只需要 Factor ID
    public FactorIngredient(Identifier factorId) {
        this(factorId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 1);
    }
    
    // 简化构造器 - ID 和数量
    public FactorIngredient(Identifier factorId, int count) {
        this(factorId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), count);
    }
    
    // ========== Getters ==========
    
    public Identifier getFactorId() {
        return factorId;
    }
    
    public Optional<Integer> getMinLevel() {
        return minLevel;
    }
    
    public Optional<Integer> getMaxLevel() {
        return maxLevel;
    }
    
    public Optional<Integer> getMinTier() {
        return minTier;
    }
    
    public Optional<Integer> getMaxTier() {
        return maxTier;
    }
    
    public int getCount() {
        return count;
    }
    
    // ========== 工具方法 ==========
    
    /**
     * 检查给定的 Factor 是否满足此材料要求
     */
    public boolean matches(com.factorcraft.factor.Factor factor) {
        // 检查 ID
        if (!factor.getId().equals(factorId)) {
            return false;
        }
        
        // 检查等级范围
        int level = factor.getLevel();
        if (minLevel.isPresent() && level < minLevel.get()) {
            return false;
        }
        if (maxLevel.isPresent() && level > maxLevel.get()) {
            return false;
        }
        
        // 检查阶数范围
        int tier = factor.getTier();
        if (minTier.isPresent() && tier < minTier.get()) {
            return false;
        }
        if (maxTier.isPresent() && tier > maxTier.get()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 创建 Builder 用于构建复杂的材料要求
     */
    public static Builder builder(Identifier factorId) {
        return new Builder(factorId);
    }
    
    // ========== Builder ==========
    
    public static class Builder {
        private final Identifier factorId;
        private Optional<Integer> minLevel = Optional.empty();
        private Optional<Integer> maxLevel = Optional.empty();
        private Optional<Integer> minTier = Optional.empty();
        private Optional<Integer> maxTier = Optional.empty();
        private int count = 1;
        
        public Builder(Identifier factorId) {
            this.factorId = factorId;
        }
        
        public Builder minLevel(int minLevel) {
            this.minLevel = Optional.of(minLevel);
            return this;
        }
        
        public Builder maxLevel(int maxLevel) {
            this.maxLevel = Optional.of(maxLevel);
            return this;
        }
        
        public Builder levelRange(int min, int max) {
            this.minLevel = Optional.of(min);
            this.maxLevel = Optional.of(max);
            return this;
        }
        
        public Builder minTier(int minTier) {
            this.minTier = Optional.of(minTier);
            return this;
        }
        
        public Builder maxTier(int maxTier) {
            this.maxTier = Optional.of(maxTier);
            return this;
        }
        
        public Builder tierRange(int min, int max) {
            this.minTier = Optional.of(min);
            this.maxTier = Optional.of(max);
            return this;
        }
        
        public Builder count(int count) {
            this.count = count;
            return this;
        }
        
        public FactorIngredient build() {
            return new FactorIngredient(factorId, minLevel, maxLevel, minTier, maxTier, count);
        }
    }
}