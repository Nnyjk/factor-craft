package com.factorcraft.module.machine.extractor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Factor 提取配方
 * 
 * 定义如何将物品/方块转化为 Factor
 * 支持能量消耗、耗时、输出概率等配置
 */
public class ExtractionRecipe {
    
    // ========== 字段 ==========
    
    private final Identifier id;
    private final ItemStack input;
    private final ExtractionOutput output;
    private final int energyCost;         // 能量消耗 (FE)
    private final int processingTime;     // 处理时间 (tick)
    private final Optional<Identifier> requiredStructure; // 可选：需要特定结构
    
    // ========== 构造器 ==========
    
    public ExtractionRecipe(Identifier id, ItemStack input, ExtractionOutput output,
                           int energyCost, int processingTime,
                           Optional<Identifier> requiredStructure) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.energyCost = energyCost;
        this.processingTime = processingTime;
        this.requiredStructure = requiredStructure;
    }
    
    // ========== Getters ==========
    
    public Identifier getId() { return id; }
    public ItemStack getInput() { return input.copy(); }
    public ExtractionOutput getOutput() { return output; }
    public int getEnergyCost() { return energyCost; }
    public int getProcessingTime() { return processingTime; }
    public Optional<Identifier> getRequiredStructure() { return requiredStructure; }
    
    // ========== 匹配检查 ==========
    
    /**
     * 检查输入物品是否匹配此配方
     */
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return ItemStack.areItemsEqual(stack, input) && stack.getCount() >= input.getCount();
    }
    
    /**
     * 消耗输入物品
     */
    public void consumeInput(ItemStack stack) {
        stack.decrement(input.getCount());
    }
    
    // ========== Codec ==========
    
    public static final Codec<ExtractionRecipe> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(ExtractionRecipe::getId),
            ItemStack.CODEC.fieldOf("input").forGetter(ExtractionRecipe::getInput),
            ExtractionOutput.CODEC.fieldOf("output").forGetter(ExtractionRecipe::getOutput),
            Codec.INT.fieldOf("energy_cost").forGetter(ExtractionRecipe::getEnergyCost),
            Codec.INT.fieldOf("processing_time").forGetter(ExtractionRecipe::getProcessingTime),
            Identifier.CODEC.optionalFieldOf("required_structure").forGetter(ExtractionRecipe::getRequiredStructure)
        ).apply(instance, ExtractionRecipe::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, ExtractionRecipe> PACKET_CODEC =
        PacketCodec.tuple(
            Identifier.PACKET_CODEC, ExtractionRecipe::getId,
            ItemStack.PACKET_CODEC, ExtractionRecipe::getInput,
            ExtractionOutput.PACKET_CODEC, ExtractionRecipe::getOutput,
            PacketCodecs.INTEGER, ExtractionRecipe::getEnergyCost,
            PacketCodecs.INTEGER, ExtractionRecipe::getProcessingTime,
            PacketCodecs.optional(Identifier.PACKET_CODEC), ExtractionRecipe::getRequiredStructure,
            ExtractionRecipe::new
        );
    
    /**
     * 提取输出定义
     */
    public static class ExtractionOutput {
        private final String factorType;      // Factor 类型名称
        private final int minLevel;           // 最小等级
        private final int maxLevel;           // 最大等级
        private final double minPower;        // 最小威力
        private final double maxPower;        // 最大威力
        private final double baseChance;      // 基础成功率
        private final int count;              // 输出数量
        
        public ExtractionOutput(String factorType, int minLevel, int maxLevel,
                               double minPower, double maxPower,
                               double baseChance, int count) {
            this.factorType = factorType;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.minPower = minPower;
            this.maxPower = maxPower;
            this.baseChance = baseChance;
            this.count = count;
        }
        
        // Getters
        public String getFactorType() { return factorType; }
        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
        public double getMinPower() { return minPower; }
        public double getMaxPower() { return maxPower; }
        public double getBaseChance() { return baseChance; }
        public int getCount() { return count; }
        
        // Codec
        public static final Codec<ExtractionOutput> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("factor_type").forGetter(ExtractionOutput::getFactorType),
                Codec.INT.fieldOf("min_level").forGetter(ExtractionOutput::getMinLevel),
                Codec.INT.fieldOf("max_level").forGetter(ExtractionOutput::getMaxLevel),
                Codec.DOUBLE.fieldOf("min_power").forGetter(ExtractionOutput::getMinPower),
                Codec.DOUBLE.fieldOf("max_power").forGetter(ExtractionOutput::getMaxPower),
                Codec.DOUBLE.fieldOf("base_chance").forGetter(ExtractionOutput::getBaseChance),
                Codec.INT.fieldOf("count").forGetter(ExtractionOutput::getCount)
            ).apply(instance, ExtractionOutput::new)
        );
        
        public static final PacketCodec<RegistryByteBuf, ExtractionOutput> PACKET_CODEC =
            PacketCodec.tuple(
                PacketCodecs.STRING, ExtractionOutput::getFactorType,
                PacketCodecs.INTEGER, ExtractionOutput::getMinLevel,
                PacketCodecs.INTEGER, ExtractionOutput::getMaxLevel,
                PacketCodecs.DOUBLE, ExtractionOutput::getMinPower,
                PacketCodecs.DOUBLE, ExtractionOutput::getMaxPower,
                PacketCodecs.DOUBLE, ExtractionOutput::getBaseChance,
                PacketCodecs.INTEGER, ExtractionOutput::getCount,
                ExtractionOutput::new
            );
    }
}