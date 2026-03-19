package com.factorcraft.module.guide;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导书屏幕
 * 
 * 标签页：
 * - 概览：基础介绍
 * - Factor：Factor 系统说明
 * - 科技树：T1-T5 结构
 * - 材料：材料系统
 * - 维度：维度机制
 */
public class GuideScreen extends Screen {
    
    private static final int WIDTH = 320;
    private static final int HEIGHT = 220;
    private static final int TAB_HEIGHT = 24;
    private static final int TAB_WIDTH = 42;
    
    private int x;
    private int y;
    private int currentTab = 0;
    
    private final List<GuideTab> tabs = new ArrayList<>();
    
    public GuideScreen() {
        super(Text.literal("Factor Craft 引导"));
        initTabs();
    }
    
    private void initTabs() {
        tabs.add(new GuideTab("概览", GuideContent::getOverview));
        tabs.add(new GuideTab("Factor", GuideContent::getFactorGuide));
        tabs.add(new GuideTab("科技树", GuideContent::getTechTree));
        tabs.add(new GuideTab("材料", GuideContent::getMaterials));
        tabs.add(new GuideTab("维度", GuideContent::getDimensions));
        tabs.add(new GuideTab("机器", GuideContent::getMachines));
        tabs.add(new GuideTab("任务", GuideContent::getQuests));
    }
    
    @Override
    protected void init() {
        this.x = (width - WIDTH) / 2;
        this.y = (height - HEIGHT) / 2;
        
        // 创建标签按钮
        for (int i = 0; i < tabs.size(); i++) {
            final int tabIndex = i;
            int buttonX = x + 10 + i * (TAB_WIDTH + 4);
            
            addDrawableChild(ButtonWidget.builder(
                Text.literal(tabs.get(i).name),
                btn -> currentTab = tabIndex
            ).dimensions(buttonX, y + 10, TAB_WIDTH, 18).build());
        }
        
        // 关闭按钮
        addDrawableChild(ButtonWidget.builder(
            Text.literal("×"),
            btn -> close()
        ).dimensions(x + WIDTH - 25, y + 5, 20, 18).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // 主背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xDD222222);
        context.drawBorder(x, y, WIDTH, HEIGHT, 0xFF444444);
        
        // 标签栏背景
        context.fill(x, y, x + WIDTH, y + 35, 0xFF333333);
        
        // 内容区域
        int contentY = y + 40;
        context.fill(x + 5, contentY, x + WIDTH - 5, y + HEIGHT - 5, 0x44111111);
        
        // 渲染当前标签内容
        if (currentTab < tabs.size()) {
            renderTabContent(context, tabs.get(currentTab), contentY + 5);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderTabContent(DrawContext context, GuideTab tab, int startY) {
        List<String> lines = tab.getContent();
        
        int lineY = startY;
        for (String line : lines) {
            // 处理标题
            if (line.startsWith("##")) {
                context.drawTextWithShadow(textRenderer, 
                    Text.literal(line.substring(2).trim()), 
                    x + 15, lineY, 0xFFFFAA00);
                lineY += 14;
            } 
            // 处理项目符号
            else if (line.startsWith("- ")) {
                context.drawTextWithShadow(textRenderer, 
                    Text.literal("• " + line.substring(2)), 
                    x + 20, lineY, 0xAAAAAA);
                lineY += 11;
            }
            // 普通文本
            else if (!line.isEmpty()) {
                // 自动换行
                int maxWidth = WIDTH - 30;
                int pos = 0;
                while (pos < line.length()) {
                    int end = Math.min(line.length(), pos + maxWidth / 6);
                    String subLine = line.substring(pos, end);
                    context.drawTextWithShadow(textRenderer, 
                        Text.literal(subLine), 
                        x + 15, lineY, 0xCCCCCC);
                    lineY += 11;
                    pos = end;
                    if (lineY > y + HEIGHT - 20) break;
                }
            } else {
                lineY += 5; // 空行
            }
            
            if (lineY > y + HEIGHT - 20) break;
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    /**
     * 标签页数据
     */
    private static class GuideTab {
        final String name;
        final java.util.function.Supplier<List<String>> contentSupplier;
        
        GuideTab(String name, java.util.function.Supplier<List<String>> contentSupplier) {
            this.name = name;
            this.contentSupplier = contentSupplier;
        }
        
        List<String> getContent() {
            return contentSupplier.get();
        }
    }
}