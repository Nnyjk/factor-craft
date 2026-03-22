package com.factorcraft.component.type;

import com.factorcraft.factor.Factor;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

/**
 * Factor 数据组件
 * 
 * 用于在 ItemStack 中存储完整的 Factor 对象
 * 支持 Factor 合成器的输入输出
 */
public record FactorData(Factor factor) {
    
    /**
     * Codec 用于序列化/反序列化
     */
    public static final Codec<FactorData> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance ->
        instance.group(
            Factor.CODEC.fieldOf("factor").forGetter(FactorData::factor)
        ).apply(instance, FactorData::new)
    );
    
    /**
     * PacketCodec 用于网络同步
     */
    public static final PacketCodec<RegistryByteBuf, FactorData> PACKET_CODEC = PacketCodec.of(
        (data, buf) -> {
            Factor.PACKET_CODEC.encode(buf, data.factor);
        },
        buf -> {
            Factor factor = Factor.PACKET_CODEC.decode(buf);
            return new FactorData(factor);
        }
    );
    
    /**
     * 创建 FactorData
     */
    public static FactorData of(Factor factor) {
        return new FactorData(factor);
    }
    
    /**
     * 获取 Factor
     */
    public Factor getFactor() {
        return factor;
    }
}