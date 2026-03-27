package com.factorcraft.module.research.screen;

import com.factorcraft.module.research.Research;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 科技树 UI 屏幕
 */
public class ResearchTreeScreen extends HandledScreen<ResearchTreeScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factorcraft", "textures/gui/research_tree.png");
    private static final Identifier NODE_TEXTURE = Identifier.of("factorcraft", "textures/gui/research_node.png");
    
    // 视口偏移
    private double viewportX = 0;
    private double viewportY = 0;
    
    // 缩放级别
    private double zoom = 1.0;
    
    // 节点大小
    private static final int NODE_SIZE = 32;
    private static final int NODE_SPACING = 80;
    
    // 悬停的节点
    private Research hoveredNode = null;
    
    public ResearchTreeScreen(ResearchTreeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.viewportX = 200;  // 初始居中
        this.viewportY = 100;
    }
    
    @Override
    protected void init() {
        super.init();
        this.titleX = 10;
        this.titleY = 10;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        
        // 绘制研究节点
        drawResearchNodes(context, mouseX, mouseY);
    }
    
    /**
     * 绘制所有研究节点
     */
    private void drawResearchNodes(DrawContext context, int mouseX, int mouseY) {
        List<Research> researchList = handler.getResearchByCategory(handler.getCurrentCategory());
        
        hoveredNode = null;
        
        for (Research research : researchList) {
            int nodeX = (int)(x + viewportX + research.getTreeX() * NODE_SPACING);
            int nodeY = (int)(y + viewportY + research.getTreeY() * NODE_SPACING);
            
            // 检查鼠标悬停
            if (mouseX >= nodeX && mouseX < nodeX + NODE_SIZE &&
                mouseY >= nodeY && mouseY < nodeY + NODE_SIZE) {
                hoveredNode = research;
            }
            
            // 获取节点状态
            Research.State state = handler.getResearchState(research);
            
            // 绘制节点
            drawNode(context, nodeX, nodeY, state, research);
        }
    }
    
    /**
     * 绘制单个研究节点
     */
    private void drawNode(DrawContext context, int x, int y, Research.State state, Research research) {
        // 根据状态选择纹理区域
        int v = switch (state) {
            case LOCKED -> 0;
            case AVAILABLE -> 32;
            case IN_PROGRESS -> 64;
            case COMPLETED -> 96;
        };
        
        context.drawTexture(RenderLayer::getGuiTextured, NODE_TEXTURE, x, y, 0, v, NODE_SIZE, NODE_SIZE, 256, 256);
        
        // 绘制研究点成本
        if (research.getResearchPointCost() > 0 && state != Research.State.COMPLETED) {
            context.drawText(textRenderer, String.valueOf(research.getResearchPointCost()), 
                x + 20, y + 22, 0xFFFFFF, true);
        }
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        // 绘制玩家研究点
        String pointsText = "研究点：" + getResearchPoints();
        context.drawText(textRenderer, pointsText, backgroundWidth - 10 - textRenderer.getWidth(pointsText), 
            10, 0x00AA00, false);
        
        // 绘制悬停提示
        if (hoveredNode != null) {
            drawTooltip(context, hoveredNode, mouseX, mouseY);
        }
    }
    
    /**
     * 绘制节点提示
     */
    private void drawTooltip(DrawContext context, Research research, int mouseX, int mouseY) {
        List<Text> lines = new ArrayList<>();
        
        // 名称
        lines.add(Text.literal("§6" + research.getName()));
        
        // 描述
        lines.add(Text.literal("§7" + research.getDescription()));
        
        // 类型
        lines.add(Text.literal("§b类型：" + research.getType()));
        
        // 研究时间
        lines.add(Text.literal("§a时间：" + formatTime(research.getResearchTime())));
        
        // 研究点成本
        if (research.getResearchPointCost() > 0) {
            lines.add(Text.literal("§e研究点：" + research.getResearchPointCost()));
        }
        
        // 状态
        Research.State state = handler.getResearchState(research);
        String stateText = switch (state) {
            case LOCKED -> "§c已锁定";
            case AVAILABLE -> "§a可研究";
            case IN_PROGRESS -> "§e研究中";
            case COMPLETED -> "§9已完成";
        };
        lines.add(Text.literal(stateText));
        
        // 前置研究
        if (!research.getPrerequisites().isEmpty()) {
            lines.add(Text.literal("§7前置: " + String.join(", ", research.getPrerequisites())));
        }
        
        context.drawTooltip(textRenderer, lines, mouseX - x, mouseY - y);
    }
    
    /**
     * 格式化时间
     */
    private String formatTime(int ticks) {
        int seconds = ticks / 20;
        if (seconds < 60) {
            return seconds + "秒";
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return minutes + "分" + secs + "秒";
    }
    
    /**
     * 获取玩家研究点
     */
    private int getResearchPoints() {
        // TODO: 从 ResearchPointManager 获取
        return 0;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {  // 左键拖动
            viewportX += deltaX;
            viewportY += deltaY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // 滚轮缩放
        zoom += verticalAmount * 0.1;
        zoom = Math.max(0.5, Math.min(2.0, zoom));
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredNode != null && button == 0) {
            Research.State state = handler.getResearchState(hoveredNode);
            
            if (state == Research.State.AVAILABLE) {
                handler.startResearch(hoveredNode.getId());
                return true;
            } else if (state == Research.State.IN_PROGRESS) {
                handler.cancelResearch(hoveredNode.getId());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
