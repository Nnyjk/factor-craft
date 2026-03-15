package com.factorcraft.module.ui.screen;

import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 特性显示界面
 * 
 * 按 J 键打开，显示手持物品的特性信息
 */
public class TraitDisplayScreen extends Screen {
    private final ItemStack stack;
    
    private static final int WIDTH = 280;
    private static final int HEIGHT = 220;
    
    private int x;
    private int y;
    private List<TraitInstance> traits;
    
    public TraitDisplayScreen(ItemStack stack) {
        super(Text.translatable("factorcraft.screen.traits.title"));
        this.stack = stack;
    }
    
    @Override
    protected void init() {
        super.init();
        this.x = (this.width - WIDTH) / 2;
        this.y = (this.height - HEIGHT) / 2;
        
        // 获取特性列表
        this.traits = TraitService.getTraits(stack);
        
        // 添加关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.close"),
            button -> this.close()
        ).dimensions(x + WIDTH / 2 - 40, y + HEIGHT - 25, 80, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染背景
        renderBackground(context, mouseX, mouseY, delta);
        
        // 绘制面板背景
        drawPanelBackground(context);
        
        // 绘制标题
        drawTitle(context);
        
        // 绘制物品信息
        drawItemInfo(context);
        
        // 绘制特性列表
        drawTraits(context);
        
        // 绘制提示
        if (traits.isEmpty()) {
            drawEmptyMessage(context);
        }
        
        // 绘制提示文本
        drawTooltips(context, mouseX, mouseY);
        
        // 渲染子组件（包括关闭按钮）
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawPanelBackground(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        
        // 边框
        context.drawBorder(x, y, WIDTH, HEIGHT, 0xFF666666);
        
        // 顶部高亮
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, 0xFF555555);
    }
    
    private void drawTitle(DrawContext context) {
        Text title = Text.translatable("factorcraft.screen.traits.title");
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, y + 8, 0xFFFFFF);
    }
    
    private void drawItemInfo(DrawContext context) {
        // 物品名称
        Text itemName = stack.isEmpty() 
            ? Text.translatable("factorcraft.screen.traits.no_item")
            : stack.getName();
        
        context.drawTextWithShadow(this.textRenderer, itemName, x + 10, y + 28, 
            stack.isEmpty() ? 0x888888 : 0xFFFFAA);
        
        if (!stack.isEmpty()) {
            // 物品图标
            context.drawItem(stack, x + WIDTH - 30, y + 24);
        }
    }
    
    private void drawTraits(DrawContext context) {
        if (traits.isEmpty()) {
            return;
        }
        
        int offsetY = 50;
        for (int i = 0; i < traits.size() && offsetY < HEIGHT - 50; i++) {
            TraitInstance trait = traits.get(i);
            final int currentY = y + offsetY;
            final int traitIndex = i;
            final int totalTraits = traits.size();
            
            trait.getDefinition().ifPresent(def -> {
                // 特性名称和等级
                Text traitName = Text.literal(def.name() + " ");
                Text levelText = Text.translatable("factorcraft.trait.level", trait.level());
                
                int nameColor = def.isPositive() ? 0x55FF55 : 0xFF5555;
                context.drawTextWithShadow(this.textRenderer, traitName, x + 15, currentY, nameColor);
                context.drawTextWithShadow(this.textRenderer, levelText, 
                    x + 15 + this.textRenderer.getWidth(traitName), currentY, 0xAAAAAA);
                
                // 特性描述
                Text description = Text.literal(def.description());
                context.drawTextWithShadow(this.textRenderer, description, x + 25, currentY + 12, 0x888888);
                
                // 分隔线
                if (traitIndex < totalTraits - 1) {
                    context.fill(x + 15, currentY + 26, x + WIDTH - 15, currentY + 27, 0xFF333333);
                }
            });
            
            offsetY += 35;
        }
    }
    
    private void drawEmptyMessage(DrawContext context) {
        Text emptyText = Text.translatable("factorcraft.screen.traits.no_traits");
        context.drawCenteredTextWithShadow(this.textRenderer, emptyText, this.width / 2, y + 80, 0x888888);
        
        Text hint = Text.translatable("factorcraft.screen.traits.hint");
        context.drawCenteredTextWithShadow(this.textRenderer, hint, this.width / 2, y + 100, 0x666666);
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        // 特性提示
        int offsetY = 50;
        for (int i = 0; i < traits.size() && offsetY < HEIGHT - 50; i++) {
            TraitInstance trait = traits.get(i);
            final int traitY = y + offsetY;
            
            if (isHovering(x + 10, traitY, WIDTH - 20, 30, mouseX, mouseY)) {
                trait.getDefinition().ifPresent(def -> {
                    List<Text> tooltip = new ArrayList<>();
                    tooltip.add(Text.literal(def.name()));
                    tooltip.add(Text.literal(def.description()));
                    tooltip.add(Text.empty());
                    tooltip.add(Text.translatable("factorcraft.trait.type", 
                        def.isPositive() ? "§a正面特性" : "§c负面特性"));
                    context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                });
            }
            
            offsetY += 35;
        }
    }
    
    private boolean isHovering(int rectX, int rectY, int width, int height, int mouseX, int mouseY) {
        return mouseX >= rectX && mouseX < rectX + width && mouseY >= rectY && mouseY < rectY + height;
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC 键关闭
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击背景外部关闭
        if (!isHovering(x, y, WIDTH, HEIGHT, (int) mouseX, (int) mouseY)) {
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}