package com.factorcraft.factor.synthesis;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Factor 合成配方
 * 
 * 定义如何将多个低级 Factor 合成为高级 Factor
 * 支持成功率配置和失败返还设置
 */
public class FactorSynthesisRecipe {
    
    // ========== 失败处理策略 ==========
    
    public enum FailureBehavior {
        DESTROY,    // 销毁所有输入
        RETURN_ALL, // 返还所有输入
        RETURN_HALF // 返还一半输入（随机选择）
    }
    
    // ========== 字段 ==========
    
    private final Identifier id;
    private final List<FactorIngredient> inputs;
    private final FactorIngredient output;
    private final double successRate;
    private final FailureBehavior failureBehavior;
    private final Optional<Identifier> requiredStructure; // 可选：需要特定结构
    private final int synthesisTime; // 合成时间（tick）
    
    // ========== Codec ==========
    
    public static final Codec<FactorSynthesisRecipe> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(FactorSynthesisRecipe::getId),
            FactorIngredient.CODEC.listOf().fieldOf("inputs").forGetter(FactorSynthesisRecipe::getInputs),
            FactorIngredient.CODEC.fieldOf("output").forGetter(FactorSynthesisRecipe::getOutput),
            Codec.DOUBLE.optionalFieldOf("success_rate", 1.0).forGetter(FactorSynthesisRecipe::getSuccessRate),
            Codec.STRING.xmap(FailureBehavior::valueOf, FailureBehavior::name)
                .optionalFieldOf("failure_behavior", FailureBehavior.DESTROY).forGetter(FactorSynthesisRecipe::getFailureBehavior),
            Identifier.CODEC.optionalFieldOf("required_structure").forGetter(FactorSynthesisRecipe::getRequiredStructure),
            Codec.INT.optionalFieldOf("synthesis_time", 200).forGetter(FactorSynthesisRecipe::getSynthesisTime)
        ).apply(instance, FactorSynthesisRecipe::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, FactorSynthesisRecipe> PACKET_CODEC =
        PacketCodec.tuple(
            Identifier.PACKET_CODEC, FactorSynthesisRecipe::getId,
            PacketCodecs.collection(java.util.ArrayList::new, FactorIngredient.PACKET_CODEC), FactorSynthesisRecipe::getInputs,
            FactorIngredient.PACKET_CODEC, FactorSynthesisRecipe::getOutput,
            PacketCodecs.DOUBLE, FactorSynthesisRecipe::getSuccessRate,
            PacketCodecs.STRING.xmap(FailureBehavior::valueOf, FailureBehavior::name), FactorSynthesisRecipe::getFailureBehavior,
            PacketCodecs.optional(Identifier.PACKET_CODEC), FactorSynthesisRecipe::getRequiredStructure,
            PacketCodecs.INTEGER, FactorSynthesisRecipe::getSynthesisTime,
            FactorSynthesisRecipe::new
        );
    
    // ========== 构造器 ==========
    
    public FactorSynthesisRecipe(Identifier id, 
                                  List<FactorIngredient> inputs,
                                  FactorIngredient output,
                                  double successRate,
                                  FailureBehavior failureBehavior,
                                  Optional<Identifier> requiredStructure,
                                  int synthesisTime) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.successRate = Math.max(0.0, Math.min(1.0, successRate));
        this.failureBehavior = failureBehavior;
        this.requiredStructure = requiredStructure;
        this.synthesisTime = Math.max(1, synthesisTime);
    }
    
    // 简化构造器 - 100% 成功率，销毁输入
    public FactorSynthesisRecipe(Identifier id, List<FactorIngredient> inputs, FactorIngredient output) {
        this(id, inputs, output, 1.0, FailureBehavior.DESTROY, Optional.empty(), 200);
    }
    
    // ========== Getters ==========
    
    public Identifier getId() {
        return id;
    }
    
    public List<FactorIngredient> getInputs() {
        return inputs;
    }
    
    public FactorIngredient getOutput() {
        return output;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public FailureBehavior getFailureBehavior() {
        return failureBehavior;
    }
    
    public Optional<Identifier> getRequiredStructure() {
        return requiredStructure;
    }
    
    public int getSynthesisTime() {
        return synthesisTime;
    }
    
    // ========== 工具方法 ==========
    
    /**
     * 检查配方是否在指定结构中可用
     */
    public boolean isAvailableInStructure(Identifier structureId) {
        if (requiredStructure.isEmpty()) {
            return true; // 没有结构限制
        }
        return requiredStructure.get().equals(structureId);
    }
    
    /**
     * 获取输出 Factor 的 ID
     */
    public Identifier getOutputFactorId() {
        return output.getFactorId();
    }
    
    /**
     * 获取输出数量
     */
    public int getOutputCount() {
        return output.getCount();
    }
    
    /**
     * 创建 Builder
     */
    public static Builder builder(Identifier id) {
        return new Builder(id);
    }
    
    // ========== Builder ==========
    
    public static class Builder {
        private final Identifier id;
        private final List<FactorIngredient> inputs = new java.util.ArrayList<>();
        private FactorIngredient output;
        private double successRate = 1.0;
        private FailureBehavior failureBehavior = FailureBehavior.DESTROY;
        private Optional<Identifier> requiredStructure = Optional.empty();
        private int synthesisTime = 200;
        
        public Builder(Identifier id) {
            this.id = id;
        }
        
        public Builder addInput(FactorIngredient input) {
            this.inputs.add(input);
            return this;
        }
        
        public Builder addInput(Identifier factorId, int count) {
            this.inputs.add(new FactorIngredient(factorId, count));
            return this;
        }
        
        public Builder output(FactorIngredient output) {
            this.output = output;
            return this;
        }
        
        public Builder output(Identifier factorId, int count) {
            this.output = new FactorIngredient(factorId, count);
            return this;
        }
        
        public Builder successRate(double rate) {
            this.successRate = rate;
            return this;
        }
        
        public Builder failureBehavior(FailureBehavior behavior) {
            this.failureBehavior = behavior;
            return this;
        }
        
        public Builder requiredStructure(Identifier structureId) {
            this.requiredStructure = Optional.of(structureId);
            return this;
        }
        
        public Builder synthesisTime(int ticks) {
            this.synthesisTime = ticks;
            return this;
        }
        
        public FactorSynthesisRecipe build() {
            if (output == null) {
                throw new IllegalStateException("Output must be specified");
            }
            if (inputs.isEmpty()) {
                throw new IllegalStateException("At least one input must be specified");
            }
            return new FactorSynthesisRecipe(id, inputs, output, successRate, failureBehavior, requiredStructure, synthesisTime);
        }
    }
}