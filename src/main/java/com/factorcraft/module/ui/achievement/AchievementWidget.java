package com.factorcraft.module.ui.achievement;

import com.factorcraft.module.core.achievement.Achievement;
import com.factorcraft.module.core.achievement.AchievementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 成就 UI 组件
 * 显示单个成就的图标、名称、进度和锁定状态
 */
public class AchievementWidget extends PressableWidget {
    
    private static final Identifier LOCKED_ICON = Identifier.of("factor-craft", "textures/ui/locked.png");
    private static final Identifier UNLOCKED_ICON = Identifier.of("factor-craft", "textures/ui/unlocked.png");
    private static final Identifier PROGRESS_BG = Identifier.of("factor-craft", "textures/ui/progress_bg.png");
    
    private final Achievement achievement;
    private final AchievementProgress progress;
    private final boolean locked;
    private final int progressPercent;
    
    public AchievementWidget(int x, int y, int size, Achievement achievement, AchievementProgress progress, boolean locked) {
        super(x, y, size, size, Text.empty());
        this.achievement = achievement;
        this.progress = progress;
        this.locked = locked;
        this.progressPercent = calculateProgress();
    }
    
    private int calculateProgress() {
        String achievementId = achievement.getId().toString();
        if (progress.isUnlocked(achievementId)) {
            return 100;
        }
        
        int current = progress.getProgress(achievementId);
        int required = achievement.getRequiredAmount();
        
        if (required <= 0) return 0;
        return Math.min(100, (current * 100) / required);
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        // 绘制背景框
        context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF303030);
        context.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0xFF505050);
        
        // 绘制图标
        if (locked) {
            // 锁定状态：显示灰色问号
            context.fill(getX() + 4, getY() + 4, getX() + width - 4, getY() + height - 4, 0xFF808080);
            context.drawTextWithShadow(
                textRenderer, "?",
                getX() + width / 2 - textRenderer.getWidth("?") / 2,
                getY() + height / 2 - textRenderer.fontHeight / 2,
                0xFFFFFFFF
            );
        } else {
            // 解锁状态：显示成就图标
            Identifier icon = achievement.getIcon();
            if (icon != null) {
                // TODO: 渲染物品图标
                context.fill(getX() + 4, getY() + 4, getX() + width - 4, getY() + height - 4, 0xFF4040FF);
            }
        }
        
        // 绘制进度条 (仅当未完全解锁且未锁定时)
        if (!locked && progressPercent < 100 && progressPercent > 0) {
            int barWidth = width - 4;
            int barHeight = 4;
            int barX = getX() + 2;
            int barY = getY() + height - barHeight - 2;
            
            // 背景
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF303030);
            // 进度
            int filledWidth = (barWidth * progressPercent) / 100;
            context.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF40FF40);
        }
        
        // 悬停时显示边框
        if (isMouseOver(mouseX, mouseY)) {
            context.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0xFFFFFF00);
        }
    }
    
    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        // 无障碍叙述支持 - 简单实现，跳过复杂叙述
        // Fabric 1.21.4 NarrationMessageBuilder API 变化，暂不实现详细叙述
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public Achievement getAchievement() {
        return achievement;
    }
    
    @Override
    public void onPress() {
        // 点击处理在 AchievementScreen 中完成
    }
    
    /**
     * 获取成就详情文本，用于 tooltip
     */
    public Text getTooltipText() {
        if (locked) {
            return Text.translatable("gui.factorcraft.achievement.hidden").styled(style -> style.withItalic(true));
        }
        
        Text title = achievement.getTitle();
        Text description = achievement.getDescription();
        Text progressText;
        
        if (progressPercent >= 100) {
            progressText = Text.translatable("gui.factorcraft.achievement.completed").styled(style -> style.withColor(0x00FF00));
        } else {
            String achievementId = achievement.getId().toString();
            int current = progress.getProgress(achievementId);
            int required = achievement.getRequiredAmount();
            progressText = Text.translatable("gui.factorcraft.achievement.progress", current, required)
                .styled(style -> style.withColor(0xFFFF00));
        }
        
        return Text.empty()
            .append(title.copy().styled(style -> style.withBold(true)))
            .append(Text.literal("\n"))
            .append(description)
            .append(Text.literal("\n"))
            .append(progressText);
    }
}
