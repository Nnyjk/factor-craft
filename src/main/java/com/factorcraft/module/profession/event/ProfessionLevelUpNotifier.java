package com.factorcraft.module.profession.event;

import com.factorcraft.module.profession.data.ProfessionDataStorage;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * 职业升级通知处理器
 * 
 * 当玩家职业升级时发送提示消息
 */
public class ProfessionLevelUpNotifier {
    
    private static final ProfessionLevelUpNotifier INSTANCE = new ProfessionLevelUpNotifier();
    
    private ProfessionLevelUpNotifier() {}
    
    public static ProfessionLevelUpNotifier getInstance() {
        return INSTANCE;
    }
    
    /**
     * 注册到事件总线
     */
    public void register() {
        ProfessionEventBus eventBus = ProfessionEventBus.getInstance();
        eventBus.subscribe(ProfessionEventType.LEVEL_UP, this::onLevelUp);
    }
    
    /**
     * 处理升级事件
     */
    private void onLevelUp(ProfessionEvent event) {
        if (!(event instanceof ProfessionLevelUpEvent levelUpEvent)) {
            return;
        }
        
        ServerPlayerEntity player = levelUpEvent.getPlayer();
        int oldLevel = levelUpEvent.getOldLevel();
        int newLevel = levelUpEvent.getNewLevel();
        
        // 从数据存储获取玩家职业类型
        PlayerProfessionData data = ProfessionDataStorage.get(player.getServerWorld()).getPlayerData(player.getUuid());
        ProfessionType professionType = data.getProfessionType();
        
        if (professionType == null) {
            return;
        }
        
        // 发送升级提示
        sendLevelUpMessage(player, professionType, oldLevel, newLevel);
    }
    
    /**
     * 发送升级消息给玩家
     */
    private void sendLevelUpMessage(ServerPlayerEntity player, ProfessionType professionType, int oldLevel, int newLevel) {
        // 使用 ActionBar 显示醒目的升级提示
        Text actionBarText = Text.literal("✦ ")
            .formatted(Formatting.GOLD)
            .append(Text.literal("职业升级!")
                .formatted(Formatting.YELLOW, Formatting.BOLD))
            .append(Text.literal(" ✦")
                .formatted(Formatting.GOLD));
        
        player.sendMessage(actionBarText, true); // true = ActionBar
        
        // 发送详细的聊天消息
        Text chatMessage = Text.literal("")
            .formatted(Formatting.GREEN)
            .append(Text.literal("【职业升级】")
                .formatted(Formatting.GOLD, Formatting.BOLD))
            .append(Text.literal(" 恭喜！你的 ")
                .formatted(Formatting.GREEN))
            .append(Text.literal(getProfessionDisplayName(professionType))
                .formatted(Formatting.AQUA, Formatting.BOLD))
            .append(Text.literal(" 等级提升到 ")
                .formatted(Formatting.GREEN))
            .append(Text.literal(String.valueOf(newLevel))
                .formatted(Formatting.YELLOW, Formatting.BOLD))
            .append(Text.literal(" 级！")
                .formatted(Formatting.GREEN));
        
        player.sendMessage(chatMessage, false); // false = Chat
        
        // 如果达到里程碑等级（5, 10, 15, 20, 25, 30），额外提示
        if (newLevel % 5 == 0) {
            sendMilestoneMessage(player, professionType, newLevel);
        }
        
        // 提示获得的天赋点
        sendTalentPointMessage(player);
    }
    
    /**
     * 发送里程碑等级提示
     */
    private void sendMilestoneMessage(ServerPlayerEntity player, ProfessionType professionType, int level) {
        Text milestoneMessage = Text.literal("")
            .formatted(Formatting.LIGHT_PURPLE)
            .append(Text.literal("★ ")
                .formatted(Formatting.GOLD))
            .append(Text.literal("里程碑达成！")
                .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD))
            .append(Text.literal(" 你已成为 Lv.")
                .formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(String.valueOf(level))
                .formatted(Formatting.GOLD, Formatting.BOLD))
            .append(Text.literal(" 的 ")
                .formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(getProfessionDisplayName(professionType))
                .formatted(Formatting.AQUA, Formatting.BOLD))
            .append(Text.literal("！")
                .formatted(Formatting.LIGHT_PURPLE))
            .append(Text.literal(" ★")
                .formatted(Formatting.GOLD));
        
        player.sendMessage(milestoneMessage, false);
    }
    
    /**
     * 发送天赋点获取提示
     */
    private void sendTalentPointMessage(ServerPlayerEntity player) {
        Text talentMessage = Text.literal("")
            .formatted(Formatting.AQUA)
            .append(Text.literal("→ ")
                .formatted(Formatting.DARK_AQUA))
            .append(Text.literal("获得 1 天赋点")
                .formatted(Formatting.AQUA))
            .append(Text.literal("，使用 ")
                .formatted(Formatting.GRAY))
            .append(Text.literal("/profession talent")
                .formatted(Formatting.YELLOW))
            .append(Text.literal(" 查看天赋树")
                .formatted(Formatting.GRAY));
        
        player.sendMessage(talentMessage, false);
    }
    
    /**
     * 获取职业显示名称
     */
    private String getProfessionDisplayName(ProfessionType type) {
        return switch (type) {
            case ENGINEER -> "Factor工程师";
            case CULTIVATOR -> "能量培育师";
            case EXPLORER -> "潮汐探索者";
            case MASTER -> "因子掌控者";
            case null -> "未知职业";
        };
    }
}