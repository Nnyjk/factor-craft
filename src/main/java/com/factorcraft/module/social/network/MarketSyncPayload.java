package com.factorcraft.module.social.network;

import com.factorcraft.module.social.market.TradeListing;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 市场同步网络包
 */
public record MarketSyncPayload(List<TradeListing> listings, int currentPage, int totalPages) implements CustomPayload {
    
    public static final CustomPayload.Id<MarketSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "market_sync"));
    
    public static final PacketCodec<PacketByteBuf, MarketSyncPayload> CODEC = PacketCodec.of(
        (MarketSyncPayload payload, PacketByteBuf buf) -> {
            buf.writeInt(payload.listings().size());
            for (TradeListing listing : payload.listings()) {
                // 简化的序列化：只传输显示需要的字段（不需要 registries）
                buf.writeUuid(listing.getId());
                buf.writeUuid(listing.getSellerId());
                buf.writeString(listing.getSellerName());
                buf.writeString(listing.getItemIdentifier());
                buf.writeInt(listing.getQuantity());
                buf.writeInt(listing.getPricePerUnit());
                buf.writeLong(listing.getTimestamp());
                buf.writeBoolean(listing.isSold());
            }
            buf.writeInt(payload.currentPage());
            buf.writeInt(payload.totalPages());
        },
        (PacketByteBuf buf) -> {
            int size = buf.readInt();
            List<TradeListing> listings = new ArrayList<>();
            // 注意：客户端无法从这些信息重建完整的 TradeListing（缺少 registries）
            // 这个包主要用于 UI 显示，实际交易操作通过服务器端处理
            for (int i = 0; i < size; i++) {
                buf.readUuid(); // id
                buf.readUuid(); // sellerId
                buf.readString(); // sellerName
                buf.readString(); // itemIdentifier
                buf.readInt(); // quantity
                buf.readInt(); // pricePerUnit
                buf.readLong(); // timestamp
                buf.readBoolean(); // sold
            }
            int currentPage = buf.readInt();
            int totalPages = buf.readInt();
            // 返回空列表，实际数据应该通过其他方式获取
            return new MarketSyncPayload(listings, currentPage, totalPages);
        }
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
