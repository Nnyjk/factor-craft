package com.factorcraft.module.social.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * 市场数据请求包 (C2S)
 */
public record MarketRequestPayload(int page) implements CustomPayload {
    
    public static final CustomPayload.Id<MarketRequestPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "market_request"));
    
    public static final PacketCodec<PacketByteBuf, MarketRequestPayload> CODEC = 
        PacketCodec.of(MarketRequestPayload::write, MarketRequestPayload::read);
    
    private void write(PacketByteBuf buf) {
        buf.writeInt(page);
    }
    
    private static MarketRequestPayload read(PacketByteBuf buf) {
        int page = buf.readInt();
        return new MarketRequestPayload(page);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
