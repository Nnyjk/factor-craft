package com.factorcraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Factor 存储数据
 * 
 * 用于 Factor 电池存储 Factor 量和类型信息
 * 
 * @param amount 当前存储的 Factor 量
 * @param factorType Factor 类型 ID（预留，当前固定为 0）
 */
public record FactorStorage(
    double amount,
    int factorType
) {
    
    /**
     * 默认 Factor 类型
     */
    public static final int DEFAULT_TYPE = 0;
    
    /**
     * Codec 用于序列化/反序列化
     */
    public static final Codec<FactorStorage> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.DOUBLE.fieldOf("amount").forGetter(FactorStorage::amount),
            Codec.INT.fieldOf("factorType").forGetter(FactorStorage::factorType)
        ).apply(instance, FactorStorage::new)
    );
    
    /**
     * PacketCodec 用于网络同步
     */
    public static final PacketCodec<RegistryByteBuf, FactorStorage> STREAM_CODEC =
        PacketCodec.tuple(
            PacketCodecs.DOUBLE,
            FactorStorage::amount,
            PacketCodecs.INTEGER,
            FactorStorage::factorType,
            FactorStorage::new
        );
    
    /**
     * 创建空的 Factor 存储
     */
    public static FactorStorage empty() {
        return new FactorStorage(0.0, DEFAULT_TYPE);
    }
    
    /**
     * 创建指定量的 Factor 存储
     * 
     * @param amount Factor 量
     * @return FactorStorage 实例
     */
    public static FactorStorage of(double amount) {
        return new FactorStorage(amount, DEFAULT_TYPE);
    }
    
    /**
     * 添加 Factor
     * 
     * @param amount 添加量
     * @param maxCapacity 最大容量
     * @return 实际添加的量
     */
    public FactorStorage add(double amount, double maxCapacity) {
        double newAmount = Math.min(this.amount + amount, maxCapacity);
        return new FactorStorage(newAmount, this.factorType);
    }
    
    /**
     * 移除 Factor
     * 
     * @param amount 移除量
     * @return 实际移除的量
     */
    public FactorStorage remove(double amount) {
        double newAmount = Math.max(this.amount - amount, 0.0);
        return new FactorStorage(newAmount, this.factorType);
    }
    
    /**
     * 获取电量百分比
     * 
     * @param maxCapacity 最大容量
     * @return 百分比 (0.0-100.0)
     */
    public double getPercentage(double maxCapacity) {
        if (maxCapacity <= 0) {
            return 0.0;
        }
        return (this.amount / maxCapacity) * 100.0;
    }
    
    /**
     * 检查是否为空
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return this.amount <= 0.0;
    }
    
    /**
     * 检查是否已满
     * 
     * @param maxCapacity 最大容量
     * @return 是否已满
     */
    public boolean isFull(double maxCapacity) {
        return this.amount >= maxCapacity;
    }
}
