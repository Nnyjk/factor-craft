package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.leaderboard.LeaderboardEntry;
import com.factorcraft.module.social.leaderboard.LeaderboardType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * 排行榜 UI 屏幕
 */
public class LeaderboardScreen extends HandledScreen<LeaderboardScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factorcraft", "textures/gui/leaderboard.png");
    
    // 类型选择按钮区域
    private int[] typeButtonX;
    private int[] typeButtonY;
    private static final int TYPE_BUTTON_WIDTH = 60;
    private static final int TYPE_BUTTON_HEIGHT = 16;
    
    // 当前选中的类型索引
    private int selectedTypeIndex = 0;
    
    public LeaderboardScreen(LeaderboardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        this.titleY = 10;
        
        // 初始化类型按钮位置
        LeaderboardType[] types = handler.getTypes();
        typeButtonX = new int[types.length];
        typeButtonY = new int[types.length];
        
        for (int i = 0; i < types.length; i++) {
            typeButtonX[i] = 10 + (i % 4) * (TYPE_BUTTON_WIDTH + 5);
            typeButtonY[i] = 30 + (i / 4) * (TYPE_BUTTON_HEIGHT + 5);
        }
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        
        // 绘制排行榜列表
        drawLeaderboardList(context);
    }
    
    /**
     * 绘制排行榜列表
     */
    private void drawLeaderboardList(DrawContext context) {
        List<LeaderboardEntry> entries = handler.getEntries();
        int startX = 20;
        int startY = 100;
        int rowHeight = 18;
        
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            int y = startY + i * rowHeight;
            
            // 绘制排名
            String rankText = String.format("#%d", entry.getRank());
            int rankColor = getRankColor(entry.getRank());
            context.drawText(textRenderer, rankText, startX, y, rankColor, false);
            
            // 绘制玩家名
            String playerName = entry.getPlayerName();
            context.drawText(textRenderer, playerName, startX + 40, y, 0xFFFFFF, false);
            
            // 绘制数值
            String valueText = formatValue(entry.getScore());
            context.drawText(textRenderer, valueText, startX + 150, y, 0xFFFF00, false);
        }
        
        // 绘制玩家自己的排名
        int playerRank = handler.getPlayerRank();
        if (playerRank > 0) {
            String playerRankText = "你的排名：#" + playerRank;
            context.drawText(textRenderer, playerRankText, startX, startY + entries.size() * rowHeight + 10, 
                0x00AAAA, false);
        }
    }
    
    /**
     * 获取排名颜色
     */
    private int getRankColor(int rank) {
        switch (rank) {
            case 1: return 0xFFAA00; // 金色
            case 2: return 0xAAAAAA; // 银色
            case 3: return 0xAA6600; // 铜色
            default: return 0xFFFFFF;
        }
    }
    
    /**
     * 格式化数值
     */
    private String formatValue(long value) {
        if (value >= 1000000) {
            return String.format("%.2fM", value / 1000000.0);
        } else if (value >= 1000) {
            return String.format("%.2fK", value / 1000.0);
        }
        return String.valueOf(value);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        // 绘制类型选择按钮
        LeaderboardType[] types = handler.getTypes();
        for (int i = 0; i < types.length; i++) {
            boolean isSelected = (i == selectedTypeIndex);
            drawTypeButton(context, typeButtonX[i], typeButtonY[i], TYPE_BUTTON_WIDTH, TYPE_BUTTON_HEIGHT, 
                getLocalizedTypeName(types[i]), isSelected);
        }
        
        // 绘制当前类型说明
        LeaderboardType currentType = handler.getCurrentType();
        String typeDesc = getTypeDescription(currentType);
        context.drawText(textRenderer, typeDesc, 20, 80, 0x888888, false);
    }
    
    /**
     * 获取本地化类型名称
     */
    private String getLocalizedTypeName(LeaderboardType type) {
        return type.getDisplayName();
    }
    
    /**
     * 获取类型说明
     */
    private String getTypeDescription(LeaderboardType type) {
        switch (type) {
            case PRODUCTION: return "按机器总产量排名";
            case EFFICIENCY: return "按每小时产出效率排名";
            case EXPLORATION: return "按发现的配方数量排名";
            case WEALTH: return "按总资产价值排名";
            case FACTOR_COLLECTOR: return "按收集的 Factor 总量排名";
            case QUEST_COMPLETION: return "按完成任务数量排名";
            case MARKET_TRADER: return "按市场交易次数排名";
            default: return "";
        }
    }
    
    /**
     * 绘制类型按钮
     */
    private void drawTypeButton(DrawContext context, int x, int y, int width, int height, 
                               String text, boolean selected) {
        int bgColor = selected ? 0xFF6666AA : 0xFF444444;
        context.fill(x, y, x + width, y + height, bgColor);
        
        int textColor = selected ? 0xFFFFFF : 0xAAAAAA;
        int textX = x + (width - textRenderer.getWidth(text)) / 2;
        int textY = y + (height - textRenderer.fontHeight) / 2;
        context.drawText(textRenderer, text, textX, textY, textColor, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int relX = (int) (mouseX - x);
        int relY = (int) (mouseY - y);
        
        if (button == 0) {
            // 检查类型按钮点击
            LeaderboardType[] types = handler.getTypes();
            for (int i = 0; i < types.length; i++) {
                if (isPointWithinBounds(typeButtonX[i], typeButtonY[i], TYPE_BUTTON_WIDTH, TYPE_BUTTON_HEIGHT, 
                    relX, relY)) {
                    selectedTypeIndex = i;
                    handler.setCurrentType(types[i]);
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
