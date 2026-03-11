package com.factorcraft.module.quest.ui;

import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 任务追踪界面 - 简化版本
 */
public class QuestTrackerScreen extends Screen {
    
    private final QuestManager questManager;
    private List<QuestInstance> activeQuests;
    
    public QuestTrackerScreen(QuestManager questManager) {
        super(Text.literal("Quest Tracker"));
        this.questManager = questManager;
    }
    
    @Override
    protected void init() {
        super.init();
        // 从客户端获取玩家 UUID
        // 待完善：实现完整的客户端-服务端同步
        if (client != null && client.player != null) {
            // activeQuests = questManager.getActiveQuests(client.player.getUuid());
            activeQuests = List.of();
        } else {
            activeQuests = List.of();
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // 绘制标题
        context.drawTextWithShadow(textRenderer, getTitle(), 10, 10, 0xFFFFFF);
        
        // 绘制任务列表
        int y = 30;
        for (QuestInstance quest : activeQuests) {
            context.drawTextWithShadow(textRenderer, quest.getTemplate().getTitle(), 10, y, 0xFFFF00);
            y += 12;
            context.drawTextWithShadow(textRenderer, quest.getTemplate().getDescription(), 10, y, 0xAAAAAA);
            y += 12;
            
            // 进度条
            float progress = quest.getOverallProgress();
            int barWidth = (int) (progress * 200);
            context.fill(10, y, 10 + barWidth, y + 5, 0xFF00FF00);
            context.fill(10 + barWidth, y, 210, y + 5, 0xFF333333);
            y += 15;
        }
    }
}
