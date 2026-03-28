package com.factorcraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Factor 注射数据组件
 * 
 * 存储在已注射的工具/装备上，包含：
 * - 注射时间 (tick)
 * - 持续时间 (tick)
 * - 增益类型 (0=工具，1=武器，2=盔甲)
 */
public record FactorInjection(long injectionTime, int durationTicks, int boostType) {
    
    public static final Codec<FactorInjection> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.LONG.fieldOf("injection_time").forGetter(FactorInjection::injectionTime),
            Codec.INT.fieldOf("duration").forGetter(FactorInjection::durationTicks),
            Codec.INT.fieldOf("boost_type").forGetter(FactorInjection::boostType)
        ).apply(instance, FactorInjection::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, FactorInjection> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.LONG, FactorInjection::injectionTime,
        PacketCodecs.INTEGER, FactorInjection::durationTicks,
        PacketCodecs.INTEGER, FactorInjection::boostType,
        FactorInjection::new
    );
    
    /**
     * 检查注射是否仍然有效
     */
    public boolean isActive(long currentTime) {
        return currentTime < injectionTime + durationTicks;
    }
    
    /**
     * 获取剩余时间 (ticks)
     */
    public int getRemainingTime(long currentTime) {
        return Math.max(0, (int)(injectionTime + durationTicks - currentTime));
    }
}
