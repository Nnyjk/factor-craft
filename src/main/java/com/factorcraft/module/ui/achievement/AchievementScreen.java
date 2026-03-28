package com.factorcraft.module.ui.achievement;

import com.factorcraft.module.core.achievement.Achievement;
import com.factorcraft.module.core.achievement.AchievementCategory;
import com.factorcraft.module.core.achievement.AchievementManager;
import com.factorcraft.module.core.achievement.AchievementProgress;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成就系统主界面
 * 显示成就树、进度和分类标签页
 */
public class AchievementScreen extends HandledScreen<AchievementTreeScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factor-craft", "textures/ui/achievement_tree.png");
    private static final int CATEGORY_TAB_WIDTH = 26;
    private static final int CATEGORY_TAB_HEIGHT = 32;
    private static final int ACHIEVEMENT_SIZE = 26;
    private static final int ACHIEVEMENT_SPACING = 10;
    
    private final Map<AchievementCategory, ButtonWidget> categoryTabs = new HashMap<>();
    private final List<AchievementWidget> achievementWidgets = new ArrayList<>();
    private AchievementCategory currentCategory = AchievementCategory.STORY;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    
    public AchievementScreen(AchievementTreeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.titleX = 10;
        this.titleY = 10;
        this.backgroundWidth = 256;
        this.backgroundHeight = 224;
    }
    
    @Override
    protected void init() {
        super.init();
        // x 和 y 在 init() 中由父类自动设置
        
        // 初始化分类标签页
        initCategoryTabs();
        
        // 加载当前分类的成就
        loadAchievements(currentCategory);
    }
    
    private void initCategoryTabs() {
        int tabIndex = 0;
        for (AchievementCategory category : AchievementCategory.values()) {
            int x = this.x - CATEGORY_TAB_WIDTH;
            int y = this.y + 10 + tabIndex * (CATEGORY_TAB_HEIGHT + 4);
            
            ButtonWidget tab = ButtonWidget.builder(
                getCategoryShortName(category),
                button -> switchCategory(category)
            )
            .dimensions(x, y, CATEGORY_TAB_WIDTH, CATEGORY_TAB_HEIGHT)
            .build();
            
            this.addDrawableChild(tab);
            categoryTabs.put(category, tab);
            tabIndex++;
        }
    }
    
    private Text getCategoryShortName(AchievementCategory category) {
        return switch (category) {
            case STORY -> Text.literal("剧情");
            case FACTOR -> Text.literal("因子");
            case MACHINE -> Text.literal("机器");
            case EXPLORATION -> Text.literal("探索");
            case COMBAT -> Text.literal("战斗");
        };
    }
    
    private void switchCategory(AchievementCategory category) {
        this.currentCategory = category;
        this.scrollOffset = 0;
        this.achievementWidgets.clear();
        loadAchievements(category);
    }
    
    private void loadAchievements(AchievementCategory category) {
        AchievementManager manager = AchievementManager.getInstance();
        AchievementProgress progress = manager.getPlayerProgress(client.player.getUuid());
        
        List<Achievement> achievements = manager.getAchievementsByCategory(category);
        
        int yOffset = 20;
        int xOffset = 20;
        int rowHeight = ACHIEVEMENT_SIZE + ACHIEVEMENT_SPACING;
        
        for (int i = 0; i < achievements.size(); i++) {
            Achievement achievement = achievements.get(i);
            
            // 计算位置 (每行 4 个成就)
            int col = i % 4;
            int row = i / 4;
            int x = xOffset + col * (ACHIEVEMENT_SIZE + ACHIEVEMENT_SPACING);
            int y = yOffset + row * rowHeight - scrollOffset;
            
            // 检查是否解锁前置成就
            boolean locked = !areRequirementsMet(achievement, progress);
            
            AchievementWidget widget = new AchievementWidget(
                x, y, ACHIEVEMENT_SIZE, achievement, progress, locked
            );
            
            achievementWidgets.add(widget);
        }
        
        // 计算最大滚动距离
        int totalHeight = ((achievements.size() + 3) / 4) * rowHeight;
        int visibleHeight = backgroundHeight - 40;
        maxScroll = Math.max(0, totalHeight - visibleHeight);
    }
    
    private boolean areRequirementsMet(Achievement achievement, AchievementProgress progress) {
        for (Identifier requiredId : achievement.getPrerequisites()) {
            if (!progress.isUnlocked(requiredId.toString())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制分类标题
        context.drawText(textRenderer, currentCategory.getDisplayName(), 10, 6, 0x404040, true);
        
        // 绘制成就组件
        for (AchievementWidget widget : achievementWidgets) {
            // 检查是否悬停
            boolean isHovered = widget.isMouseOver(mouseX - x, mouseY - y);
            widget.render(context, mouseX - x, mouseY - y, 0.0f);
            
            // 悬停时显示 tooltip
            if (isHovered && !widget.isLocked()) {
                context.drawTooltip(textRenderer, widget.getTooltipText(), mouseX - x, mouseY - y);
            }
        }
        
        // 绘制滚动条
        drawScrollbar(context);
    }
    
    private void drawScrollbar(DrawContext context) {
        if (maxScroll > 0) {
            int scrollbarHeight = Math.max(20, (backgroundHeight - 40) * (backgroundHeight - 40) / 
                (((achievementWidgets.size() + 3) / 4) * (ACHIEVEMENT_SIZE + ACHIEVEMENT_SPACING)));
            int scrollbarY = y + 20 + (scrollOffset * (backgroundHeight - 40 - scrollbarHeight)) / maxScroll;
            
            context.fill(
                x + backgroundWidth - 12, scrollbarY,
                x + backgroundWidth - 6, scrollbarY + scrollbarHeight,
                0xFF808080
            );
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            scrollOffset = Math.min(scrollOffset + 10, maxScroll);
        } else {
            scrollOffset = Math.max(scrollOffset - 10, 0);
        }
        // 重新计算成就位置
        loadAchievements(currentCategory);
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击了成就
        for (AchievementWidget widget : achievementWidgets) {
            if (widget.isMouseOver(mouseX - x, mouseY - y)) {
                if (button == 0 && !widget.isLocked()) {
                    // 显示成就详情
                    // TODO: 显示成就详情弹窗
                }
                return true;
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
