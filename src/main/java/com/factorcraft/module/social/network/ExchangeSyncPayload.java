package com.factorcraft.module.social.network;

import com.factorcraft.module.social.exchange.TradeOrder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易所数据同步包
 */
public record ExchangeSyncPayload(List<OrderData> orders, String factorType, int price) implements CustomPayload {
    
    public static final Id<ExchangeSyncPayload> ID = new Id<>(Identifier.of("factorcraft", "exchange_sync"));
    
    public static final PacketCodec<PacketByteBuf, ExchangeSyncPayload> CODEC = PacketCodec.of(
        (ExchangeSyncPayload payload, PacketByteBuf buf) -> {
            buf.writeInt(payload.orders().size());
            for (OrderData data : payload.orders()) {
                data.write(buf);
            }
            buf.writeString(payload.factorType() != null ? payload.factorType() : "");
            buf.writeInt(payload.price());
        },
        (PacketByteBuf buf) -> {
            int count = buf.readInt();
            List<OrderData> orders = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                orders.add(OrderData.read(buf));
            }
            String factorType = buf.readString();
            int price = buf.readInt();
            return new ExchangeSyncPayload(orders, factorType.isEmpty() ? null : factorType, price);
        }
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 订单数据（简化版，用于网络传输）
     */
    public static class OrderData {
        public final String id;
        public final String playerId;
        public final String playerName;
        public final String factorType;
        public final int quantity;
        public final int pricePerUnit;
        public final OrderType type;
        public final long timestamp;
        public final boolean completed;
        
        public OrderData(String id, String playerId, String playerName, String factorType,
                        int quantity, int pricePerUnit, OrderType type, long timestamp, boolean completed) {
            this.id = id;
            this.playerId = playerId;
            this.playerName = playerName;
            this.factorType = factorType;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.type = type;
            this.timestamp = timestamp;
            this.completed = completed;
        }
        
        public static OrderData fromOrder(TradeOrder order) {
            return new OrderData(
                order.getId().toString(),
                order.getPlayerId().toString(),
                order.getPlayerName(),
                order.getFactorType(),
                order.getQuantity(),
                order.getPricePerUnit(),
                order.getType() == TradeOrder.OrderType.BUY ? OrderType.BUY : OrderType.SELL,
                order.getTimestamp(),
                order.isComplete()
            );
        }
        
        public void write(PacketByteBuf buf) {
            buf.writeString(id);
            buf.writeString(playerId);
            buf.writeString(playerName);
            buf.writeString(factorType);
            buf.writeInt(quantity);
            buf.writeInt(pricePerUnit);
            buf.writeInt(type.ordinal());
            buf.writeLong(timestamp);
            buf.writeBoolean(completed);
        }
        
        public static OrderData read(PacketByteBuf buf) {
            String id = buf.readString();
            String playerId = buf.readString();
            String playerName = buf.readString();
            String factorType = buf.readString();
            int quantity = buf.readInt();
            int pricePerUnit = buf.readInt();
            OrderType type = OrderType.values()[buf.readInt()];
            long timestamp = buf.readLong();
            boolean completed = buf.readBoolean();
            return new OrderData(id, playerId, playerName, factorType, quantity, pricePerUnit, type, timestamp, completed);
        }
    }
    
    public enum OrderType {
        BUY,
        SELL
    }
}
