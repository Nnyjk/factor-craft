package com.factorcraft.module.quest.ui;

import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 任务追踪界面 - 显示玩家当前任务列表和进度
 */
public class QuestTrackerScreen extends HandledScreen<QuestTrackerScreenHandler> {
    
    private final QuestManager questManager;
    private QuestListWidget questList;
    
    public QuestTrackerScreen(QuestTrackerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.questManager = handler.getQuestManager();
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 初始化任务列表
        List<QuestInstance> activeQuests = questManager.getActiveQuests(client.player.getUuid());
        questList = new QuestListWidget(client, this, activeQuests);
        addSelectableChild(questList);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 绘制标题
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        // 绘制任务数量
        int questCount = questManager.getActiveQuests(client.player.getUuid()).size();
        String countText = "Active Quests: " + questCount;
        context.drawText(textRenderer, countText, 8, 60, 0x404040, false);
    }
    
    public QuestListWidget getQuestList() {
        return questList;
    }
    
    public QuestManager getQuestManager() {
        return questManager;
    }
}
