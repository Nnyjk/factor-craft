package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 材料信息面板
 * 
 * 显示：
 * - 材料名称和 Tier
 * - Factor 值
 * - 特性槽位
 * - 维度限制
 * - 用途建议
 */
public class MaterialInfoPanel extends Screen {
    
    private static final int WIDTH = 180;
    private static final int HEIGHT = 200;
    
    // 材料数据
    private final String materialName;
    private final int tier;
    private final double factorValue;
    private final int traitSlots;
    private final List<String> traits;
    private final String dimensionLimit;
    private final List<String> usedInRecipes;
    
    // UI 位置
    private int x;
    private int y;
    
    public MaterialInfoPanel(MaterialData data) {
        super(Text.literal(data.materialName()));
        this.materialName = data.materialName();
        this.tier = data.tier();
        this.factorValue = data.factorValue();
        this.traitSlots = data.traitSlots();
        this.traits = data.traits();
        this.dimensionLimit = data.dimensionLimit();
        this.usedInRecipes = data.usedInRecipes();
    }
    
    @Override
    protected void init() {
        super.init();
        this.x = (width - WIDTH) / 2;
        this.y = (height - HEIGHT) / 2;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // 背景
        drawBackground(context);
        
        // 标题
        drawTitle(context);
        
        // Tier 和 Factor 值
        drawBasicInfo(context);
        
        // 特性槽位
        drawTraitSlots(context);
        
        // 维度限制
        drawDimensionLimit(context);
        
        // 用途
        drawUsages(context);
    }
    
    private void drawBackground(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xDD000000);
        
        // Tier 对应的边框颜色
        int borderColor = getTierColor(tier);
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        
        // 顶部渐变
        for (int i = 0; i < 25; i++) {
            int alpha = (int) (100 * (1 - i / 25.0));
            int color = (alpha << 24) | (borderColor & 0x00FFFFFF);
            context.fill(x + 1, y + 1 + i, x + WIDTH - 1, y + 2 + i, color);
        }
    }
    
    private void drawTitle(DrawContext context) {
        // 材料名称
        context.drawCenteredTextWithShadow(textRenderer, materialName, x + WIDTH / 2, y + 8, 0xFFFFFF);
        
        // Tier 徽章
        int badgeX = x + WIDTH - 30;
        int badgeY = y + 6;
        int tierColor = getTierColor(tier);
        context.fill(badgeX, badgeY, badgeX + 22, badgeY + 12, tierColor);
        String tierText = "T" + tier;
        context.drawCenteredTextWithShadow(textRenderer, tierText, badgeX + 11, badgeY + 2, 0xFFFFFF);
    }
    
    private void drawBasicInfo(DrawContext context) {
        int infoY = y + 28;
        
        // Factor 值
        String factorText = String.format("Factor: %.0f", factorValue);
        int factorColor = getFactorValueColor(factorValue);
        context.drawTextWithShadow(textRenderer, factorText, x + 10, infoY, factorColor);
        
        // 分隔线
        context.fill(x + 10, infoY + 15, x + WIDTH - 10, infoY + 16, 0xFF444444);
    }
    
    private void drawTraitSlots(DrawContext context) {
        int slotY = y + 50;
        
        // 标题
        context.drawTextWithShadow(textRenderer, "特性槽位", x + 10, slotY, 0xAAAAAA);
        
        // 槽位显示
        int slotX = x + 10;
        for (int i = 0; i < traitSlots; i++) {
            // 槽位背景
            context.fill(slotX, slotY + 12, slotX + 70, slotY + 24, 0xFF333333);
            context.drawBorder(slotX, slotY + 12, 70, 12, 0xFF666666);
            
            // 特性名称（如果有）
            if (i < traits.size()) {
                String traitName = traits.get(i);
                int traitColor = getTraitColor(traitName);
                context.drawTextWithShadow(textRenderer, traitName, slotX + 3, slotY + 14, traitColor);
            } else {
                context.drawTextWithShadow(textRenderer, "空", slotX + 30, slotY + 14, 0x666666);
            }
            
            slotX += 80;
        }
        
        // 槽位数量提示
        String slotInfo = String.format("%d / %d 槽位已使用", traits.size(), traitSlots);
        context.drawTextWithShadow(textRenderer, slotInfo, x + 10, slotY + 28, 0x888888);
    }
    
    private void drawDimensionLimit(DrawContext context) {
        int dimY = y + 115;
        
        // 标题
        context.drawTextWithShadow(textRenderer, "维度限制", x + 10, dimY, 0xAAAAAA);
        
        // 限制信息
        if (dimensionLimit == null || dimensionLimit.isEmpty()) {
            context.drawTextWithShadow(textRenderer, "无限制", x + 10, dimY + 12, 0x00FF00);
        } else {
            String limitText = "仅限: " + getDimensionDisplayName(dimensionLimit);
            context.drawTextWithShadow(textRenderer, limitText, x + 10, dimY + 12, 0xFF9900);
        }
    }
    
    private void drawUsages(DrawContext context) {
        int usageY = y + 145;
        
        // 标题
        context.drawTextWithShadow(textRenderer, "用途", x + 10, usageY, 0xAAAAAA);
        
        // 用途列表
        if (usedInRecipes.isEmpty()) {
            context.drawTextWithShadow(textRenderer, "无已知用途", x + 10, usageY + 12, 0x666666);
        } else {
            int lineY = usageY + 12;
            int maxLines = Math.min(3, usedInRecipes.size());
            for (int i = 0; i < maxLines; i++) {
                context.drawTextWithShadow(textRenderer, "• " + usedInRecipes.get(i), x + 10, lineY, 0xCCCCCC);
                lineY += 10;
            }
            
            if (usedInRecipes.size() > 3) {
                String more = String.format("... 还有 %d 项", usedInRecipes.size() - 3);
                context.drawTextWithShadow(textRenderer, more, x + 10, lineY, 0x888888);
            }
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private int getTierColor(int tier) {
        return switch (tier) {
            case 1 -> 0xFF8B4513; // 尘铜 - 棕色
            case 2 -> 0xFF708090; // 暗影钢 - 灰色
            case 3 -> 0xFF4169E1; // 星尘 - 蓝色
            case 4 -> 0xFF9932CC; // 远古合金 - 紫色
            case 5 -> 0xFFFFD700; // 虚空结晶 - 金色
            default -> 0xFF666666;
        };
    }
    
    private int getFactorValueColor(double value) {
        if (value >= 10000) return 0xFFFFD700; // 金色
        if (value >= 1000) return 0xFF9932CC;  // 紫色
        if (value >= 100) return 0xFF4169E1;   // 蓝色
        if (value >= 10) return 0xFF708090;    // 灰色
        return 0xFF8B4513;                      // 棕色
    }
    
    private int getTraitColor(String traitName) {
        if (traitName == null) return 0xAAAAAA;
        
        String lower = traitName.toLowerCase();
        
        // 正面特性 - 绿色
        if (lower.contains("效率") || lower.contains("增益") || lower.contains("强化")) {
            return 0x00FF00;
        }
        
        // 负面特性 - 红色
        if (lower.contains("损耗") || lower.contains("削弱") || lower.contains("诅咒")) {
            return 0xFF5555;
        }
        
        // 特殊特性 - 金色
        if (lower.contains("远古") || lower.contains("仲裁") || lower.contains("传说")) {
            return 0xFFFF00;
        }
        
        // 普通特性 - 白色
        return 0xFFFFFF;
    }
    
    private String getDimensionDisplayName(String dimension) {
        if (dimension == null) return "无";
        return switch (dimension) {
            case "minecraft:overworld" -> "主世界";
            case "minecraft:the_nether" -> "下界";
            case "minecraft:the_end" -> "末地";
            default -> dimension.replace("minecraft:", "");
        };
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    /**
     * 材料数据记录
     */
    public record MaterialData(
        String materialName,
        int tier,
        double factorValue,
        int traitSlots,
        List<String> traits,
        String dimensionLimit,
        List<String> usedInRecipes
    ) {
        /**
         * 创建 T1 材料
         */
        public static MaterialData t1DustCopper() {
            return new MaterialData(
                "尘铜锭",
                1,
                10,
                1,
                List.of(),
                null,
                List.of("星辰收集器", "远古合成阵")
            );
        }
        
        /**
         * 创建 T5 材料
         */
        public static MaterialData t5VoidCrystal() {
            return new MaterialData(
                "虚空结晶",
                5,
                100000,
                3,
                List.of(),
                "minecraft:the_end",
                List.of("虚空漩涡", "本源祭坛", "轮回之门")
            );
        }
    }
}