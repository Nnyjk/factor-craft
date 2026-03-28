package com.factorcraft.module.ui.achievement;

import com.factorcraft.module.core.achievement.Achievement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 成就解锁通知 Toast
 * 在屏幕右上角显示成就解锁提示
 */
public class AchievementToast implements Toast {
    
    private static final Identifier TOAST_TEXTURE = Identifier.of("minecraft", "toast/toast");
    
    private final Achievement achievement;
    private final Text title;
    private final Text description;
    private long showTime;
    private boolean justShowed;
    private boolean visible;
    
    public AchievementToast(Achievement achievement) {
        this.achievement = achievement;
        this.title = Text.translatable("gui.factorcraft.achievement.unlock_title").styled(style -> style.withBold(true).withColor(0xFFFF00));
        this.description = achievement.getTitle();
        this.showTime = 0L;
        this.justShowed = true;
        this.visible = true;
    }
    
    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        if (this.justShowed) {
            this.showTime = startTime;
            this.justShowed = false;
        }
        
        int width = 160;
        int height = 32;
        
        // 绘制 Toast 背景
        context.drawTexture(RenderLayer::getGuiTextured, TOAST_TEXTURE, 0, 0, 0, 0, width, height, 256, 256);
        
        // 绘制成就图标 (用彩色方块代替)
        context.fill(6, 6, 26, 26, 0xFF4040FF);
        
        // 绘制标题
        context.drawTextWithShadow(
            textRenderer,
            title,
            32, 7,
            0xFFFF00
        );
        
        // 绘制成就名称 (截断如果太长)
        Text displayText = description;
        String textString = displayText.getString();
        if (textRenderer.getWidth(textString) > 120) {
            while (textRenderer.getWidth(textString + "...") > 120 && textString.length() > 0) {
                textString = textString.substring(0, textString.length() - 1);
            }
            displayText = Text.literal(textString + "...");
        }
        
        context.drawTextWithShadow(textRenderer, displayText, 32, 18, 0xFFFFFF);
    }
    
    @Override
    public int getHeight() {
        return 32;
    }
    
    @Override
    public int getWidth() {
        return 160;
    }
    
    @Override
    public Toast.Visibility getVisibility() {
        return this.visible ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }
    
    @Override
    public void update(ToastManager manager, long startTime) {
        if (this.justShowed) {
            this.showTime = startTime;
            this.justShowed = false;
        }
        // 5 秒后自动隐藏
        if (this.visible && startTime - this.showTime > 5000L) {
            this.visible = false;
        }
    }
}
