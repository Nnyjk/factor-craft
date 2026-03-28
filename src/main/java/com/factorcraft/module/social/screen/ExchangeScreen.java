package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.exchange.TradeOrder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

/**
 * 交易所 UI 屏幕
 */
public class ExchangeScreen extends HandledScreen<ExchangeScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factorcraft", "textures/gui/exchange.png");
    
    // 按钮区域
    private int buyOrderButtonX, buyOrderButtonY;
    private int sellOrderButtonX, sellOrderButtonY;
    private int cancelButtonX, cancelButtonY;
    
    // 当前选中的订单
    private UUID selectedOrderId = null;
    
    public ExchangeScreen(ExchangeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.titleY = 10;
        
        // 计算按钮位置
        buyOrderButtonX = 10;
        buyOrderButtonY = backgroundHeight - 60;
        sellOrderButtonX = 100;
        sellOrderButtonY = backgroundHeight - 60;
        cancelButtonX = backgroundWidth - 100;
        cancelButtonY = backgroundHeight - 60;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        
        // 绘制订单列表
        drawOrderList(context);
    }
    
    /**
     * 绘制订单列表
     */
    private void drawOrderList(DrawContext context) {
        List<TradeOrder> orders = handler.getOrders();
        int startX = 20;
        int startY = 30;
        int rowHeight = 20;
        
        for (int i = 0; i < Math.min(orders.size(), 8); i++) {
            TradeOrder order = orders.get(i);
            int y = startY + i * rowHeight;
            
            // 绘制行背景
            if (order.getId() != null && order.getId().equals(selectedOrderId)) {
                context.fill(startX - 5, y - 2, startX + 220, y + rowHeight - 2, 0x4000AA00);
            }
            
            // 绘制类型（买/卖）
            String typeText = order.getType() == TradeOrder.OrderType.BUY ? "买" : "卖";
            int typeColor = order.getType() == TradeOrder.OrderType.BUY ? 0x00AA00 : 0xAA0000;
            context.drawText(textRenderer, typeText, startX, y, typeColor, false);
            
            // 绘制 Factor 类型
            context.drawText(textRenderer, order.getFactorType(), startX + 30, y, 0xFFFFFF, false);
            
            // 绘制价格
            String priceText = String.format("%.2f", order.getPricePerUnit());
            context.drawText(textRenderer, priceText, startX + 100, y, 0xFFFF00, false);
            
            // 绘制数量
            String quantityText = "x" + order.getQuantity();
            context.drawText(textRenderer, quantityText, startX + 150, y, 0xAAAAAA, false);
            
            // 绘制创建者
            String creatorText = order.getCreatorName();
            context.drawText(textRenderer, creatorText, startX + 180, y, 0x00AAAA, false);
        }
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        // 绘制操作按钮
        drawButton(context, buyOrderButtonX, buyOrderButtonY, 80, 20, "创建买单", true);
        drawButton(context, sellOrderButtonX, sellOrderButtonY, 80, 20, "创建卖单", true);
        
        if (selectedOrderId != null) {
            drawButton(context, cancelButtonX, cancelButtonY, 80, 20, "取消订单", true);
        }
        
        // 绘制 Factor 价格信息
        drawPriceInfo(context);
    }
    
    /**
     * 绘制价格信息
     */
    private void drawPriceInfo(DrawContext context) {
        String selectedFactor = handler.getSelectedFactorType();
        if (selectedFactor != null) {
            var priceData = handler.getExchangeManager().getFactorPrice(selectedFactor);
            if (priceData != null) {
                int infoX = backgroundWidth - 150;
                int infoY = 30;
                
                context.drawText(textRenderer, "当前价格：" + String.format("%.2f", priceData.currentPrice()), 
                    infoX, infoY, 0xFFFF00, false);
                
                double changePercent = priceData.get24hChangePercent();
                String changeText = String.format("24h 变化：%.2f%%", changePercent);
                int changeColor = changePercent >= 0 ? 0x00AA00 : 0xAA0000;
                context.drawText(textRenderer, changeText, infoX, infoY + 10, changeColor, false);
                
                context.drawText(textRenderer, "24h 成交量：" + priceData.get24hVolume(), 
                    infoX, infoY + 20, 0xAAAAAA, false);
            }
        }
    }
    
    /**
     * 绘制按钮
     */
    private void drawButton(DrawContext context, int x, int y, int width, int height, 
                           String text, boolean enabled) {
        int color = enabled ? 0xFF888888 : 0xFF444444;
        context.fill(x, y, x + width, y + height, color);
        context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 
            enabled ? 0xFFAAAAAA : 0xFF666666);
        
        int textColor = enabled ? 0xFFFFFF : 0x888888;
        int textX = x + (width - textRenderer.getWidth(text)) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, text, textX, textY, textColor, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int relX = (int) (mouseX - x);
        int relY = (int) (mouseY - y);
        
        if (button == 0) {
            // 检查创建买单按钮
            if (isPointWithinBounds(buyOrderButtonX, buyOrderButtonY, 80, 20, relX, relY)) {
                // TODO: 打开创建买单界面
                return true;
            }
            
            // 检查创建卖单按钮
            if (isPointWithinBounds(sellOrderButtonX, sellOrderButtonY, 80, 20, relX, relY)) {
                // TODO: 打开创建卖单界面
                return true;
            }
            
            // 检查取消订单按钮
            if (selectedOrderId != null && 
                isPointWithinBounds(cancelButtonX, cancelButtonY, 80, 20, relX, relY)) {
                if (handler.cancelOrder(selectedOrderId)) {
                    handler.refreshOrders();
                    selectedOrderId = null;
                }
                return true;
            }
            
            // 检查列表项点击
            List<TradeOrder> orders = handler.getOrders();
            int startX = 20;
            int startY = 30;
            int rowHeight = 20;
            
            for (int i = 0; i < Math.min(orders.size(), 8); i++) {
                TradeOrder order = orders.get(i);
                int y = startY + i * rowHeight;
                
                if (relX >= startX - 5 && relX <= startX + 220 && 
                    relY >= y - 2 && relY <= y + rowHeight - 2) {
                    selectedOrderId = order.getId();
                    handler.setSelectedFactorType(order.getFactorType());
                    return true;
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
