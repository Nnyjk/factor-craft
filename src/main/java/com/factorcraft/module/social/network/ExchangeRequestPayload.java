package com.factorcraft.module.social.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * 交易所数据请求包 (C2S)
 */
public record ExchangeRequestPayload(String factorType) implements CustomPayload {
    
    public static final CustomPayload.Id<ExchangeRequestPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "exchange_request"));
    
    public static final PacketCodec<PacketByteBuf, ExchangeRequestPayload> CODEC = 
        PacketCodec.of(ExchangeRequestPayload::write, ExchangeRequestPayload::read);
    
    private void write(PacketByteBuf buf) {
        buf.writeString(factorType);
    }
    
    private static ExchangeRequestPayload read(PacketByteBuf buf) {
        String factorType = buf.readString();
        return new ExchangeRequestPayload(factorType);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
