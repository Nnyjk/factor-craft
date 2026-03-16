package com.factorcraft.module.quest.ui;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 任务追踪界面 - 增强版
 * 
 * 功能：
 * - 任务分类显示
 * - 进度追踪
 * - 奖励预览
 * - 筛选功能
 */
public class QuestTrackerScreen extends Screen {
    
    private static final int WIDTH = 320;
    private static final int HEIGHT = 240;
    private static final int QUEST_ITEM_HEIGHT = 50;
    
    private final QuestManager questManager;
    private List<QuestInstance> activeQuests;
    private List<QuestInstance> completedQuests;
    
    // 当前选中的任务
    private QuestInstance selectedQuest;
    
    // 筛选
    private QuestFilter currentFilter = QuestFilter.ALL;
    
    // UI 位置
    private int x;
    private int y;
    private int scrollOffset;
    
    public QuestTrackerScreen(QuestManager questManager) {
        super(Text.literal("任务追踪"));
        this.questManager = questManager;
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.scrollOffset = 0;
    }
    
    @Override
    protected void init() {
        super.init();
        this.x = (width - WIDTH) / 2;
        this.y = (height - HEIGHT) / 2;
        
        // 加载任务数据
        loadQuestData();
        
        // 创建筛选按钮
        createFilterButtons();
    }
    
    private void loadQuestData() {
        // 从 QuestManager 获取玩家的任务数据
        // 在单人游戏中，QuestManager 已经包含当前玩家的数据
        // 在多人游戏中，需要通过网络包从服务端同步
        try {
            // 获取当前玩家（如果是单人游戏或本地）
            if (client != null && client.player != null) {
                UUID playerId = client.player.getUuid();
                
                // 获取活跃任务
                activeQuests = new ArrayList<>(questManager.getActiveQuests(playerId));
                
                // 获取已完成任务
                Set<Identifier> completedIds = questManager.getCompletedQuests(playerId);
                completedQuests = new ArrayList<>();
                for (Identifier id : completedIds) {
                    QuestTemplate template = questManager.getTemplate(id);
                    if (template != null) {
                        // 创建已完成的任务实例（用于显示）
                        QuestInstance completedInstance = new QuestInstance(template, playerId);
                        completedQuests.add(completedInstance);
                    }
                }
                
                FactorCraftMod.LOGGER.debug("加载任务数据：{} 个活跃，{} 个已完成", 
                    activeQuests.size(), completedQuests.size());
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("无法从服务端同步任务数据，使用空列表", e);
            activeQuests = new ArrayList<>();
            completedQuests = new ArrayList<>();
        }
    }
    
    private void createFilterButtons() {
        int buttonY = y + 25;
        int buttonX = x + 10;
        
        // 全部
        addDrawableChild(ButtonWidget.builder(Text.literal("全部"), btn -> {
            currentFilter = QuestFilter.ALL;
        }).dimensions(buttonX, buttonY, 50, 18).build());
        
        // 进行中
        addDrawableChild(ButtonWidget.builder(Text.literal("进行中"), btn -> {
            currentFilter = QuestFilter.ACTIVE;
        }).dimensions(buttonX + 55, buttonY, 55, 18).build());
        
        // 已完成
        addDrawableChild(ButtonWidget.builder(Text.literal("已完成"), btn -> {
            currentFilter = QuestFilter.COMPLETED;
        }).dimensions(buttonX + 115, buttonY, 55, 18).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // 主背景
        drawBackground(context);
        
        // 标题
        drawTitle(context);
        
        // 任务列表
        drawQuestList(context, mouseX, mouseY);
        
        // 选中任务详情
        if (selectedQuest != null) {
            drawQuestDetails(context, selectedQuest);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawBackground(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xDD000000);
        context.drawBorder(x, y, WIDTH, HEIGHT, 0xFF444444);
        
        // 顶部栏
        context.fill(x, y, x + WIDTH, y + 45, 0xFF222222);
    }
    
    private void drawTitle(DrawContext context) {
        context.drawCenteredTextWithShadow(textRenderer, "任务追踪", x + WIDTH / 2, y + 8, 0xFFFFFF);
        
        // 任务数量
        String countText = String.format("%d 个任务", getFilteredQuests().size());
        context.drawTextWithShadow(textRenderer, countText, x + WIDTH - 70, y + 8, 0xAAAAAA);
    }
    
    private void drawQuestList(DrawContext context, int mouseX, int mouseY) {
        List<QuestInstance> quests = getFilteredQuests();
        
        int listY = y + 50;
        int listHeight = HEIGHT - 60;
        
        // 列表背景
        context.fill(x + 5, listY, x + WIDTH - 5, listY + listHeight, 0x44111111);
        
        // 绘制任务项
        int itemY = listY + 2 - scrollOffset;
        for (QuestInstance quest : quests) {
            if (itemY + QUEST_ITEM_HEIGHT > listY && itemY < listY + listHeight) {
                drawQuestItem(context, quest, x + 7, itemY, mouseX, mouseY);
            }
            itemY += QUEST_ITEM_HEIGHT + 2;
        }
        
        // 滚动条
        int totalHeight = quests.size() * (QUEST_ITEM_HEIGHT + 2);
        if (totalHeight > listHeight) {
            int scrollBarHeight = (int) ((listHeight / (double) totalHeight) * listHeight);
            int scrollBarY = listY + (int) ((scrollOffset / (double) (totalHeight - listHeight)) * (listHeight - scrollBarHeight));
            context.fill(x + WIDTH - 10, scrollBarY, x + WIDTH - 6, scrollBarY + scrollBarHeight, 0xFF666666);
        }
    }
    
    private void drawQuestItem(DrawContext context, QuestInstance quest, int itemX, int itemY, int mouseX, int mouseY) {
        boolean isHovered = mouseX >= itemX && mouseX < itemX + WIDTH - 15 && 
                           mouseY >= itemY && mouseY < itemY + QUEST_ITEM_HEIGHT;
        boolean isSelected = quest == selectedQuest;
        
        // 背景
        int bgColor = isSelected ? 0xFF2A2A2A : (isHovered ? 0xFF333333 : 0xFF222222);
        context.fill(itemX, itemY, itemX + WIDTH - 15, itemY + QUEST_ITEM_HEIGHT, bgColor);
        
        // 边框
        if (isSelected) {
            context.drawBorder(itemX, itemY, WIDTH - 15, QUEST_ITEM_HEIGHT, 0xFFFFAA00);
        } else if (isHovered) {
            context.drawBorder(itemX, itemY, WIDTH - 15, QUEST_ITEM_HEIGHT, 0xFF555555);
        }
        
        // 任务标题
        String title = quest.getTemplate().getTitle();
        context.drawTextWithShadow(textRenderer, title, itemX + 5, itemY + 5, 0xFFFFFF);
        
        // 任务描述（截断）
        String desc = quest.getTemplate().getDescription();
        if (desc.length() > 40) {
            desc = desc.substring(0, 37) + "...";
        }
        context.drawTextWithShadow(textRenderer, desc, itemX + 5, itemY + 18, 0xAAAAAA);
        
        // 进度条
        float progress = quest.getOverallProgress();
        int barWidth = WIDTH - 30;
        int barX = itemX + 5;
        int barY = itemY + 32;
        
        // 进度条背景
        context.fill(barX, barY, barX + barWidth, barY + 8, 0xFF333333);
        
        // 进度条填充
        int fillColor = progress >= 1.0 ? 0xFF00FF00 : 0xFF00AA00;
        context.fill(barX, barY, barX + (int) (barWidth * progress), barY + 8, fillColor);
        
        // 进度文本
        String progressText = String.format("%.0f%%", progress * 100);
        context.drawCenteredTextWithShadow(textRenderer, progressText, barX + barWidth / 2, barY, 0xFFFFFF);
    }
    
    private void drawQuestDetails(DrawContext context, QuestInstance quest) {
        // 右侧详情面板（简化版）
        // 完整实现可以添加更多细节
    }
    
    private List<QuestInstance> getFilteredQuests() {
        return switch (currentFilter) {
            case ACTIVE -> activeQuests;
            case COMPLETED -> completedQuests;
            case ALL -> {
                List<QuestInstance> all = new ArrayList<>();
                all.addAll(activeQuests);
                all.addAll(completedQuests);
                yield all;
            }
        };
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查点击任务项
        List<QuestInstance> quests = getFilteredQuests();
        int listY = y + 50;
        int itemY = listY + 2 - scrollOffset;
        
        for (QuestInstance quest : quests) {
            if (mouseX >= x + 7 && mouseX < x + WIDTH - 15 &&
                mouseY >= itemY && mouseY < itemY + QUEST_ITEM_HEIGHT) {
                selectedQuest = quest;
                return true;
            }
            itemY += QUEST_ITEM_HEIGHT + 2;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<QuestInstance> quests = getFilteredQuests();
        int listHeight = HEIGHT - 60;
        int totalHeight = quests.size() * (QUEST_ITEM_HEIGHT + 2);
        
        if (totalHeight > listHeight) {
            int maxScroll = totalHeight - listHeight;
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) (verticalAmount * 10)));
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    /**
     * 任务筛选类型
     */
    private enum QuestFilter {
        ALL,        // 全部
        ACTIVE,     // 进行中
        COMPLETED   // 已完成
    }
}