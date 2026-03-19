package com.factorcraft.update.mixin;

import com.factorcraft.update.UpdateInfo;
import com.factorcraft.update.UpdateNotifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 主菜单更新提示 Mixin
 */
@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        UpdateNotifier notifier = UpdateNotifier.getInstance();
        if (notifier == null) return;
        
        UpdateInfo update = notifier.getPendingUpdate();
        if (update == null || !update.hasUpdate()) return;
        
        // 在屏幕底部显示更新提示
        TitleScreen screen = (TitleScreen) (Object) this;
        int width = screen.width;
        int height = screen.height;
        
        Text updateText = Text.literal("§e[FactorCraft] §a新版本可用: §f" + update.getLatestVersion())
            .copy()
            .formatted(Formatting.YELLOW);
        
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(updateText);
        int x = (width - textWidth) / 2;
        int y = height - 35;
        
        context.drawTextWithShadow(textRenderer, updateText, x, y, 0xFFFFFF);
    }
}