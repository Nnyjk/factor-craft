package com.factorcraft.module.profession.screen;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 职业选择界面
 * 
 * 客户端UI，显示可选职业列表和详细信息
 */
public class ProfessionSelectScreen extends HandledScreen<ProfessionSelectScreenHandler> {
    
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("factorcraft", "textures/gui/profession_select.png");
    private static final int BACKGROUND_WIDTH = 320;
    private static final int BACKGROUND_HEIGHT = 200;
    
    private int selectedProfessionIndex = -1;
    private final List<ButtonWidget> professionButtons = new ArrayList<>();
    
    public ProfessionSelectScreen(ProfessionSelectScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = BACKGROUND_WIDTH;
        this.backgroundHeight = BACKGROUND_HEIGHT;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        
        professionButtons.clear();
        
        // 创建职业选择按钮
        ProfessionType[] professions = handler.getAvailableProfessions();
        for (int i = 0; i < professions.length; i++) {
            final int index = i;
            final ProfessionType type = professions[i];
            
            ButtonWidget button = ButtonWidget.builder(
                Text.literal(type.getDisplayName()),
                btn -> selectProfession(index)
            ).dimensions(
                centerX - 150,
                startY + i * 25,
                140,
                20
            ).build();
            
            professionButtons.add(button);
            this.addDrawableChild(button);
        }
        
        // 确认按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.factorcraft.profession_select.confirm"),
            btn -> confirmSelection()
        ).dimensions(
            centerX + 20,
            startY + professions.length * 25 + 10,
            120,
            20
        ).build());
        
        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.factorcraft.profession_select.cancel"),
            btn -> this.close()
        ).dimensions(
            centerX + 20,
            startY + professions.length * 25 + 35,
            120,
            20
        ).build());
    }
    
    private void selectProfession(int index) {
        this.selectedProfessionIndex = index;
        // 更新按钮样式
        for (int i = 0; i < professionButtons.size(); i++) {
            // 高亮选中的按钮
        }
    }
    
    private void confirmSelection() {
        if (selectedProfessionIndex >= 0) {
            ProfessionType[] professions = handler.getAvailableProfessions();
            if (selectedProfessionIndex < professions.length) {
                // 发送职业选择请求到服务端
                // TODO: 实现网络包发送
                this.close();
            }
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染背景
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // 渲染标题
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            20,
            0xFFFFFF
        );
        
        // 渲制职业详情面板
        if (selectedProfessionIndex >= 0) {
            renderProfessionDetails(context, selectedProfessionIndex);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 渲染半透明背景
        int x = (this.width - BACKGROUND_WIDTH) / 2;
        int y = (this.height - BACKGROUND_HEIGHT) / 2;
        context.fill(x, y, x + BACKGROUND_WIDTH, y + BACKGROUND_HEIGHT, 0x80000000);
    }
    
    private void renderProfessionDetails(DrawContext context, int index) {
        ProfessionType[] professions = handler.getAvailableProfessions();
        if (index >= professions.length) return;
        
        ProfessionType type = professions[index];
        int panelX = this.width / 2 + 10;
        int panelY = this.height / 2 - 50;
        
        // 渲染职业名称
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(type.getDisplayName()),
            panelX,
            panelY,
            0xFFD700
        );
        
        // 渲染职业描述
        String description = handler.getProfessionDescription(type);
        context.drawTextWithShadow(
            this.textRenderer,
            Text.literal(description),
            panelX,
            panelY + 20,
            0xAAAAAA
        );
        
        // 渲染核心标签
        String[] tags = handler.getProfessionTags(type);
        int tagY = panelY + 60;
        for (String tag : tags) {
            context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("• " + tag),
                panelX,
                tagY,
                0x88FF88
            );
            tagY += 12;
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}