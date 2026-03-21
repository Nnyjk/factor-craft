package com.factorcraft.factor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Factor 数据类
 * 
 * 定义 Factor 的完整数据结构，包含 ID、名称、等级、属性、稀有度等
 * 支持 NBT 和网络序列化
 */
public class Factor {
    
    // ========== 静态常量 ==========
    
    public static final int MAX_LEVEL = 100;
    public static final int MAX_TIER = 5;
    
    // ========== 字段 ==========
    
    private final Identifier id;
    private final String name;
    private final FactorType type;
    private final FactorRarity rarity;
    private final int level;
    private final int tier;
    private final double basePower;
    private final Set<String> tags;
    private final Optional<String> description;
    
    // ========== 构造器 ==========
    
    private Factor(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.rarity = builder.rarity;
        this.level = Math.min(MAX_LEVEL, Math.max(1, builder.level));
        this.tier = Math.min(MAX_TIER, Math.max(1, builder.tier));
        this.basePower = builder.basePower;
        this.tags = new HashSet<>(builder.tags);
        this.description = Optional.ofNullable(builder.description);
    }
    
    // ========== Getters ==========
    
    public Identifier getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public FactorType getType() {
        return type;
    }
    
    public FactorRarity getRarity() {
        return rarity;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getTier() {
        return tier;
    }
    
    public double getBasePower() {
        return basePower;
    }
    
    /**
     * 获取实际功率，考虑等级和稀有度加成
     */
    public double getActualPower() {
        double levelMultiplier = 1.0 + (level - 1) * 0.1;
        double rarityMultiplier = 1.0 + rarity.getTier() * 0.25;
        return basePower * levelMultiplier * rarityMultiplier;
    }
    
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }
    
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    public Optional<String> getDescription() {
        return description;
    }
    
    // ========== 序列化 ==========
    
    /**
     * Codec 用于序列化/反序列化
     */
    public static final Codec<Factor> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(Factor::getId),
            Codec.STRING.fieldOf("name").forGetter(Factor::getName),
            Codec.STRING.xmap(FactorType::fromName, FactorType::asString)
                .fieldOf("type").forGetter(Factor::getType),
            Codec.STRING.xmap(FactorRarity::fromName, FactorRarity::asString)
                .fieldOf("rarity").forGetter(Factor::getRarity),
            Codec.INT.fieldOf("level").forGetter(Factor::getLevel),
            Codec.INT.fieldOf("tier").forGetter(Factor::getTier),
            Codec.DOUBLE.fieldOf("basePower").forGetter(Factor::getBasePower),
            Codec.STRING.listOf().xmap(
                list -> (Set<String>) new HashSet<>(list),
                set -> set.stream().toList()
            ).optionalFieldOf("tags", new HashSet<>()).forGetter(Factor::getTags),
            Codec.STRING.optionalFieldOf("description").forGetter(Factor::getDescription)
        ).apply(instance, (id, name, type, rarity, level, tier, power, tags, desc) -> 
            new Builder(id, name)
                .type(type)
                .rarity(rarity)
                .level(level)
                .tier(tier)
                .basePower(power)
                .tags(tags)
                .description(desc.orElse(null))
                .build()
        )
    );
    
    /**
     * PacketCodec 用于网络同步
     * 由于字段数量超过 tuple 的最大参数限制，使用 NBT 序列化
     */
    public static final PacketCodec<RegistryByteBuf, Factor> PACKET_CODEC = 
        new PacketCodec<RegistryByteBuf, Factor>() {
            @Override
            public Factor decode(RegistryByteBuf buf) {
                NbtCompound nbt = buf.readNbt();
                return Factor.fromNbt(nbt);
            }

            @Override
            public void encode(RegistryByteBuf buf, Factor factor) {
                buf.writeNbt(factor.toNbt());
            }
        };
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id.toString());
        nbt.putString("name", name);
        nbt.putString("type", type.asString());
        nbt.putString("rarity", rarity.asString());
        nbt.putInt("level", level);
        nbt.putInt("tier", tier);
        nbt.putDouble("basePower", basePower);
        
        // 写入标签
        int i = 0;
        for (String tag : tags) {
            nbt.putString("tag_" + i++, tag);
        }
        nbt.putInt("tagCount", tags.size());
        
        description.ifPresent(d -> nbt.putString("description", d));
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static Factor fromNbt(NbtCompound nbt) {
        Builder builder = new Builder(
            Identifier.tryParse(nbt.getString("id")),
            nbt.getString("name")
        );
        
        if (nbt.contains("type")) {
            builder.type(FactorType.fromName(nbt.getString("type")));
        }
        if (nbt.contains("rarity")) {
            builder.rarity(FactorRarity.fromName(nbt.getString("rarity")));
        }
        if (nbt.contains("level")) {
            builder.level(nbt.getInt("level"));
        }
        if (nbt.contains("tier")) {
            builder.tier(nbt.getInt("tier"));
        }
        if (nbt.contains("basePower")) {
            builder.basePower(nbt.getDouble("basePower"));
        }
        
        // 读取标签
        int tagCount = nbt.getInt("tagCount");
        for (int i = 0; i < tagCount; i++) {
            String tag = nbt.getString("tag_" + i);
            if (!tag.isEmpty()) {
                builder.addTag(tag);
            }
        }
        
        if (nbt.contains("description")) {
            builder.description(nbt.getString("description"));
        }
        
        return builder.build();
    }
    
    // ========== Builder ==========
    
    public static class Builder {
        private final Identifier id;
        private final String name;
        private FactorType type = FactorType.ELEMENTAL;
        private FactorRarity rarity = FactorRarity.COMMON;
        private int level = 1;
        private int tier = 1;
        private double basePower = 1.0;
        private final Set<String> tags = new HashSet<>();
        private String description = null;
        
        public Builder(Identifier id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Builder type(FactorType type) {
            this.type = type;
            return this;
        }
        
        public Builder rarity(FactorRarity rarity) {
            this.rarity = rarity;
            return this;
        }
        
        public Builder level(int level) {
            this.level = level;
            return this;
        }
        
        public Builder tier(int tier) {
            this.tier = tier;
            return this;
        }
        
        public Builder basePower(double basePower) {
            this.basePower = basePower;
            return this;
        }
        
        public Builder tags(Set<String> tags) {
            this.tags.addAll(tags);
            return this;
        }
        
        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Factor build() {
            return new Factor(this);
        }
    }
    
    // ========== Object 方法 ==========
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Factor factor = (Factor) o;
        return id.equals(factor.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Factor{" + id + ", type=" + type + ", rarity=" + rarity + ", level=" + level + "}";
    }
}