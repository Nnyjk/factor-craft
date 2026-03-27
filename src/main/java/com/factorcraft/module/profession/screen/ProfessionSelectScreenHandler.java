package com.factorcraft.module.profession.screen;

import com.factorcraft.module.profession.model.ProfessionType;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.data.ProfessionDataStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.factorcraft.module.profession.screen.ProfessionScreens.PROFESSION_SELECT;

/**
 * 职业选择界面 ScreenHandler
 * 
 * 处理职业选择的服务端逻辑
 */
public class ProfessionSelectScreenHandler extends ScreenHandler {
    
    private final ServerPlayerEntity player;
    private final PlayerProfessionData professionData;
    
    public ProfessionSelectScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player instanceof ServerPlayerEntity sp ? sp : null);
    }
    
    public ProfessionSelectScreenHandler(int syncId, PlayerInventory playerInventory, ServerPlayerEntity player) {
        super(PROFESSION_SELECT, syncId);
        this.player = player;
        this.professionData = player != null ? ProfessionDataStorage.get(player.getServerWorld()).getPlayerData(player.getUuid()) : null;
    }
    
    @Override
    public ScreenHandlerType<?> getType() {
        return PROFESSION_SELECT;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    /**
     * 获取玩家当前职业
     */
    public ProfessionType getCurrentProfession() {
        return professionData.getProfessionType();
    }
    
    /**
     * 获取玩家职业等级
     */
    public int getProfessionLevel() {
        return professionData.getLevel();
    }
    
    /**
     * 检查是否可以选择职业
     */
    public boolean canSelectProfession() {
        return !professionData.hasProfession();
    }
    
    /**
     * 选择职业
     * @param professionType 职业类型
     * @return 是否成功
     */
    public boolean selectProfession(ProfessionType professionType) {
        if (!canSelectProfession()) {
            return false;
        }
        
        // 检查是否为隐藏职业（需要解锁条件）
        if (professionType.isHidden()) {
            if (!checkUnlockCondition(professionType)) {
                player.sendMessage(Text.translatable("profession.factorcraft.unlock_failed", 
                    professionType.getDisplayName()), false);
                return false;
            }
        }
        
        // 设置职业
        professionData.setProfessionType(professionType);
        professionData.setLevel(1);
        professionData.setExperience(0);
        professionData.setTalentPoints(1); // 初始1点天赋
        
        // 标记数据已修改，需要保存
        ProfessionDataStorage.get(player.getServerWorld()).markDirty();
        
        // 发送成功消息
        player.sendMessage(Text.translatable("profession.factorcraft.selected", 
            professionType.getDisplayName()), false);
        
        // 关闭界面
        player.closeHandledScreen();
        
        return true;
    }
    
    /**
     * 检查隐藏职业解锁条件
     */
    private boolean checkUnlockCondition(ProfessionType professionType) {
        if (professionType != ProfessionType.MASTER) {
            return false;
        }
        
        // 因子掌控者解锁条件：
        // 1. 完成主线任务"因子融合"
        // 2. 三个基础职业均达到10级
        // TODO: 实现任务系统检查
        
        // 暂时返回 false，隐藏职业暂不可选
        return false;
    }
    
    /**
     * 获取所有可选职业
     */
    public ProfessionType[] getAvailableProfessions() {
        return ProfessionType.getBasicProfessions();
    }
    
    /**
     * 获取职业描述
     */
    public String getProfessionDescription(ProfessionType type) {
        return type.getDescription();
    }
    
    /**
     * 获取职业核心标签
     */
    public String[] getProfessionTags(ProfessionType type) {
        return type.getCoreTags();
    }
}