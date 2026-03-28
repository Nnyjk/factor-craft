package com.factorcraft.module.social.exchange;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

/**
 * 交易订单 - Factor 交易所的买单/卖单
 */
public class TradeOrder {
    public enum OrderType {
        BUY,      // 买单
        SELL      // 卖单
    }
    
    public enum OrderMode {
        LIMIT,    // 限价单
        MARKET    // 市价单
    }
    
    private final UUID id;
    private final UUID playerId;
    private final String playerName;
    private final String factorType;  // Factor 类型
    private final OrderType type;
    private final OrderMode mode;
    private final int quantity;       // Factor 数量
    private final int pricePerUnit;   // 单价（限价单有效，市价单为 0）
    private final long timestamp;
    private int filledQuantity;       // 已成交数量
    private boolean cancelled;
    
    public TradeOrder(UUID id, UUID playerId, String playerName, String factorType, OrderType type, OrderMode mode, 
                      int quantity, int pricePerUnit) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.factorType = factorType;
        this.type = type;
        this.mode = mode;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.timestamp = System.currentTimeMillis();
        this.filledQuantity = 0;
        this.cancelled = false;
    }
    
    public UUID getId() {
        return id;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public String getFactorType() {
        return factorType;
    }
    
    public String getCreatorName() {
        return playerName;
    }
    
    public OrderType getType() {
        return type;
    }
    
    public OrderMode getMode() {
        return mode;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getPricePerUnit() {
        return pricePerUnit;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int getFilledQuantity() {
        return filledQuantity;
    }
    
    public int getRemainingQuantity() {
        return quantity - filledQuantity;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean isComplete() {
        return filledQuantity >= quantity || cancelled;
    }
    
    public void fill(int amount) {
        this.filledQuantity += amount;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("id", id);
        nbt.putUuid("player_id", playerId);
        nbt.putString("player_name", playerName);
        nbt.putString("factor_type", factorType);
        nbt.putString("type", type.name());
        nbt.putString("mode", mode.name());
        nbt.putInt("quantity", quantity);
        nbt.putInt("price_per_unit", pricePerUnit);
        nbt.putLong("timestamp", timestamp);
        nbt.putInt("filled_quantity", filledQuantity);
        nbt.putBoolean("cancelled", cancelled);
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static TradeOrder fromNbt(NbtCompound nbt) {
        UUID id = nbt.getUuid("id");
        UUID playerId = nbt.getUuid("player_id");
        String playerName = nbt.getString("player_name");
        String factorType = nbt.getString("factor_type");
        OrderType type = OrderType.valueOf(nbt.getString("type"));
        OrderMode mode = OrderMode.valueOf(nbt.getString("mode"));
        int quantity = nbt.getInt("quantity");
        int pricePerUnit = nbt.getInt("price_per_unit");
        
        TradeOrder order = new TradeOrder(id, playerId, playerName, factorType, type, mode, quantity, pricePerUnit);
        order.filledQuantity = nbt.getInt("filled_quantity");
        order.cancelled = nbt.getBoolean("cancelled");
        return order;
    }
    
    /**
     * 写入网络包
     */
    public void write(PacketByteBuf buf) {
        buf.writeUuid(id);
        buf.writeUuid(playerId);
        buf.writeString(playerName);
        buf.writeString(factorType);
        buf.writeEnumConstant(type);
        buf.writeEnumConstant(mode);
        buf.writeInt(quantity);
        buf.writeInt(pricePerUnit);
        buf.writeLong(timestamp);
        buf.writeInt(filledQuantity);
        buf.writeBoolean(cancelled);
    }
    
    /**
     * 从网络包读取
     */
    public static TradeOrder read(PacketByteBuf buf) {
        UUID id = buf.readUuid();
        UUID playerId = buf.readUuid();
        String playerName = buf.readString();
        String factorType = buf.readString();
        OrderType type = buf.readEnumConstant(OrderType.class);
        OrderMode mode = buf.readEnumConstant(OrderMode.class);
        int quantity = buf.readInt();
        int pricePerUnit = buf.readInt();
        long timestamp = buf.readLong();
        int filledQuantity = buf.readInt();
        boolean cancelled = buf.readBoolean();
        
        TradeOrder order = new TradeOrder(id, playerId, playerName, factorType, type, mode, quantity, pricePerUnit);
        order.filledQuantity = filledQuantity;
        order.cancelled = cancelled;
        return order;
    }
}
