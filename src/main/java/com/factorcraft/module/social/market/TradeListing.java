package com.factorcraft.module.social.market;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;

import java.util.UUID;

/**
 * 交易挂单 - 市场上的一个出售条目
 */
public class TradeListing {
    private final UUID id;
    private final UUID sellerId;
    private final String sellerName;
    private final ItemStack itemStack;
    private final int quantity;
    private final int pricePerUnit; // 单价（Factor）
    private final long timestamp;
    private boolean sold;
    
    public TradeListing(UUID id, UUID sellerId, String sellerName, ItemStack itemStack, int quantity, int pricePerUnit) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemStack = itemStack.copy();
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.timestamp = System.currentTimeMillis();
        this.sold = false;
    }
    
    public UUID getId() {
        return id;
    }
    
    public UUID getSellerId() {
        return sellerId;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public ItemStack getItemStack() {
        return itemStack.copy();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getPricePerUnit() {
        return pricePerUnit;
    }
    
    public int getTotalPrice() {
        return quantity * pricePerUnit;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isSold() {
        return sold;
    }
    
    public void setSold(boolean sold) {
        this.sold = sold;
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("id", id);
        nbt.putUuid("seller_id", sellerId);
        nbt.putString("seller_name", sellerName);
        nbt.put("item_stack", itemStack.toNbt(registries));
        nbt.putInt("quantity", quantity);
        nbt.putInt("price_per_unit", pricePerUnit);
        nbt.putLong("timestamp", timestamp);
        nbt.putBoolean("sold", sold);
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static TradeListing fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        UUID id = nbt.getUuid("id");
        UUID sellerId = nbt.getUuid("seller_id");
        String sellerName = nbt.getString("seller_name");
        ItemStack itemStack = ItemStack.fromNbt(registries, nbt.getCompound("item_stack")).orElse(ItemStack.EMPTY);
        int quantity = nbt.getInt("quantity");
        int pricePerUnit = nbt.getInt("price_per_unit");
        
        TradeListing listing = new TradeListing(id, sellerId, sellerName, itemStack, quantity, pricePerUnit);
        listing.sold = nbt.getBoolean("sold");
        return listing;
    }
    
    /**
     * 写入网络包
     */
    public void write(PacketByteBuf buf, RegistryWrapper.WrapperLookup registries) {
        buf.writeUuid(id);
        buf.writeUuid(sellerId);
        buf.writeString(sellerName);
        buf.writeNbt(itemStack.toNbt(registries));
        buf.writeInt(quantity);
        buf.writeInt(pricePerUnit);
        buf.writeLong(timestamp);
        buf.writeBoolean(sold);
    }
    
    /**
     * 从网络包读取
     */
    public static TradeListing read(PacketByteBuf buf, RegistryWrapper.WrapperLookup registries) {
        UUID id = buf.readUuid();
        UUID sellerId = buf.readUuid();
        String sellerName = buf.readString();
        ItemStack itemStack = ItemStack.fromNbt(registries, buf.readNbt()).orElse(ItemStack.EMPTY);
        int quantity = buf.readInt();
        int pricePerUnit = buf.readInt();
        long timestamp = buf.readLong();
        boolean sold = buf.readBoolean();
        
        TradeListing listing = new TradeListing(id, sellerId, sellerName, itemStack, quantity, pricePerUnit);
        listing.sold = sold;
        return listing;
    }
    
    /**
     * 获取物品标识符
     */
    public String getItemIdentifier() {
        return itemStack.getItem().toString();
    }
}
