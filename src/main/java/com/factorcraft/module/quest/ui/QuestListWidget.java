package com.factorcraft.module.quest.ui;

import com.factorcraft.module.quest.instance.QuestInstance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * 任务列表组件 - 显示任务列表和进度条
 */
public class QuestListWidget extends AlwaysSelectedEntryListWidget<QuestListWidget.QuestEntry> {
    
    private final QuestTrackerScreen screen;
    
    public QuestListWidget(MinecraftClient client, QuestTrackerScreen screen, List<QuestInstance> quests) {
        super(client, screen.width - 40, screen.height - 100, 60, 25);
        this.screen = screen;
        
        // 添加任务条目
        for (QuestInstance quest : quests) {
            addEntry(new QuestEntry(quest));
        }
    }
    
    @Override
    public int getRowWidth() {
        return 220;
    }
    
    @Override
    protected int getScrollbarPositionX() {
        return screen.width - 10;
    }
    
    public class QuestEntry extends AlwaysSelectedEntryListWidget.Entry<QuestEntry> {
        
        private final QuestInstance quest;
        
        public QuestEntry(QuestInstance quest) {
            this.quest = quest;
        }
        
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            // 任务标题
            String title = quest.getTemplate().getTitle();
            context.drawText(screen.getTextRenderer(), title, x + 5, y + 5, 0xFFFFFF, false);
            
            // 任务描述
            String description = quest.getTemplate().getDescription();
            context.drawText(screen.getTextRenderer(), description, x + 5, y + 15, 0xAAAAAA, false);
            
            // 进度条
            float progress = quest.getOverallProgress();
            int barWidth = (int) (progress * 200);
            context.fill(x + 5, y + 25, x + 5 + barWidth, y + 30, 0xFF00FF00);
            context.fill(x + 5 + barWidth, y + 25, x + 205, y + 30, 0xFF333333);
            
            // 进度百分比
            String progressText = String.format("%.0f%%", progress * 100);
            context.drawText(screen.getTextRenderer(), progressText, x + 210, y + 26, 0xFFFFFF, false);
        }
        
        @Override
        public List<? extends Element> children() {
            return List.of();
        }
        
        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }
        
        public QuestInstance getQuest() {
            return quest;
        }
    }
}
