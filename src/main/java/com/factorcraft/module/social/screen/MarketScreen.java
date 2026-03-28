package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.market.TradeListing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 市场 UI 屏幕
 */
public class MarketScreen extends HandledScreen<MarketScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factorcraft", "textures/gui/market.png");
    
    // 按钮区域
    private int nextPageButtonX, nextPageButtonY;
    private int prevPageButtonX, prevPageButtonY;
    private int buyButtonX, buyButtonY;
    private int cancelButtonX, cancelButtonY;
    
    // 当前选中的挂单
    private UUID selectedListingId = null;
    
    public MarketScreen(MarketScreenHandler handler, PlayerInventory inventory, Text title) {
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
        nextPageButtonX = backgroundWidth - 50;
        nextPageButtonY = backgroundHeight - 30;
        prevPageButtonX = 10;
        prevPageButtonY = backgroundHeight - 30;
        buyButtonX = backgroundWidth - 100;
        buyButtonY = backgroundHeight - 60;
        cancelButtonX = 10;
        cancelButtonY = backgroundHeight - 60;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        
        // 绘制交易列表
        drawListingList(context);
    }
    
    /**
     * 绘制交易列表
     */
    private void drawListingList(DrawContext context) {
        List<TradeListing> listings = handler.getCurrentPageListings();
        int startX = 20;
        int startY = 30;
        int rowHeight = 20;
        
        for (int i = 0; i < listings.size(); i++) {
            TradeListing listing = listings.get(i);
            int y = startY + i * rowHeight;
            
            // 绘制行背景
            if (listing.getId().equals(selectedListingId)) {
                context.fill(startX - 5, y - 2, startX + 200, y + rowHeight - 2, 0x4000AA00);
            }
            
            // 绘制物品名称
            String itemName = listing.getItemIdentifier().toString();
            context.drawText(textRenderer, itemName, startX, y, 0xFFFFFF, false);
            
            // 绘制价格
            String priceText = String.valueOf(listing.getPricePerUnit()) + " Factor";
            context.drawText(textRenderer, priceText, startX + 100, y, 0xFFFF00, false);
            
            // 绘制数量
            String quantityText = "x" + listing.getQuantity();
            context.drawText(textRenderer, quantityText, startX + 160, y, 0xAAAAAA, false);
            
            // 绘制卖家
            String sellerText = listing.getSellerName();
            context.drawText(textRenderer, sellerText, startX + 190, y, 0x00AAAA, false);
        }
        
        // 绘制页码
        String pageText = "页码：" + (handler.getCurrentPage() + 1) + "/" + handler.getTotalPages();
        context.drawText(textRenderer, pageText, backgroundWidth / 2 - textRenderer.getWidth(pageText) / 2, 
            backgroundHeight - 30, 0xFFFFFF, false);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        // 绘制翻页按钮
        drawButton(context, prevPageButtonX, prevPageButtonY, 40, 20, "上一页", 
            handler.getCurrentPage() > 0);
        drawButton(context, nextPageButtonX, nextPageButtonY, 40, 20, "下一页", 
            handler.getCurrentPage() < handler.getTotalPages() - 1);
        
        // 绘制操作按钮
        if (selectedListingId != null) {
            drawButton(context, buyButtonX, buyButtonY, 80, 20, "购买", true);
            
            TradeListing selected = handler.getMarketManager().getListing(selectedListingId);
            if (selected != null && selected.getSellerId().equals(handler.getPlayer().getUuid())) {
                drawButton(context, cancelButtonX, cancelButtonY, 80, 20, "取消", true);
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
        
        // 检查翻页按钮点击
        if (button == 0) {
            if (isPointWithinBounds(prevPageButtonX, prevPageButtonY, 40, 20, relX, relY) 
                && handler.getCurrentPage() > 0) {
                handler.previousPage();
                return true;
            }
            
            if (isPointWithinBounds(nextPageButtonX, nextPageButtonY, 40, 20, relX, relY) 
                && handler.getCurrentPage() < handler.getTotalPages() - 1) {
                handler.nextPage();
                return true;
            }
            
            // 检查购买按钮
            if (selectedListingId != null && 
                isPointWithinBounds(buyButtonX, buyButtonY, 80, 20, relX, relY)) {
                if (handler.buyListing(selectedListingId)) {
                    handler.refreshListings();
                }
                return true;
            }
            
            // 检查取消按钮
            if (selectedListingId != null && 
                isPointWithinBounds(cancelButtonX, cancelButtonY, 80, 20, relX, relY)) {
                if (handler.cancelListing(selectedListingId)) {
                    handler.refreshListings();
                    selectedListingId = null;
                }
                return true;
            }
            
            // 检查列表项点击
            List<TradeListing> listings = handler.getCurrentPageListings();
            int startX = 20;
            int startY = 30;
            int rowHeight = 20;
            
            for (int i = 0; i < listings.size(); i++) {
                TradeListing listing = listings.get(i);
                int y = startY + i * rowHeight;
                
                if (relX >= startX - 5 && relX <= startX + 200 && 
                    relY >= y - 2 && relY <= y + rowHeight - 2) {
                    selectedListingId = listing.getId();
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
