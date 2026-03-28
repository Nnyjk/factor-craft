package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.exchange.ExchangeManager;
import com.factorcraft.module.social.exchange.TradeOrder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 交易所屏幕处理器
 */
public class ExchangeScreenHandler extends ScreenHandler {
    
    private final PlayerEntity player;
    private final ExchangeManager exchangeManager;
    
    // 当前显示的订单列表
    private List<TradeOrder> orders = new ArrayList<>();
    
    // 当前选中的 Factor 类型
    private String selectedFactorType = null;
    
    public ExchangeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }
    
    public ExchangeScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModScreenHandlers.EXCHANGE, syncId);
        this.player = player;
        this.exchangeManager = ExchangeManager.getInstance();
        this.orders = exchangeManager.getActiveOrders(player.getUuid());
        
        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 224));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    public ExchangeManager getExchangeManager() {
        return exchangeManager;
    }
    
    public List<TradeOrder> getOrders() {
        return orders;
    }
    
    public String getSelectedFactorType() {
        return selectedFactorType;
    }
    
    public void setSelectedFactorType(String factorType) {
        this.selectedFactorType = factorType;
    }
    
    /**
     * 创建买单
     */
    public boolean createBuyOrder(String factorType, int quantity, int pricePerUnit) {
        TradeOrder order = exchangeManager.createOrder(
            player.getUuid(),
            player.getName().getString(),
            factorType,
            TradeOrder.OrderType.BUY,
            TradeOrder.OrderMode.LIMIT,
            quantity,
            pricePerUnit
        );
        if (order != null) {
            selectedFactorType = factorType;
            orders.add(order);
            return true;
        }
        return false;
    }
    
    /**
     * 创建卖单
     */
    public boolean createSellOrder(String factorType, int quantity, int pricePerUnit) {
        TradeOrder order = exchangeManager.createOrder(
            player.getUuid(),
            player.getName().getString(),
            factorType,
            TradeOrder.OrderType.SELL,
            TradeOrder.OrderMode.LIMIT,
            quantity,
            pricePerUnit
        );
        if (order != null) {
            selectedFactorType = factorType;
            orders.add(order);
            return true;
        }
        return false;
    }
    
    /**
     * 取消订单
     */
    public boolean cancelOrder(UUID orderId) {
        boolean result = exchangeManager.cancelOrder(orderId, player.getUuid());
        if (result) {
            orders.removeIf(order -> order.getId().equals(orderId));
        }
        return result;
    }
    
    /**
     * 获取价格数据
     */
    public com.factorcraft.module.social.exchange.FactorPrice getPriceData(String factorType) {
        // 简化处理：返回当前 Factor 价格
        return exchangeManager.getFactorPrice();
    }
    
    /**
     * 刷新订单列表
     */
    public void refreshOrders() {
        this.orders = exchangeManager.getActiveOrders(player.getUuid());
    }
}
