package com.factorcraft.module.research.screen;

import com.factorcraft.module.research.Research;
import com.factorcraft.module.research.ResearchManager;
import com.factorcraft.module.research.ResearchProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * 科技树屏幕处理器
 */
public class ResearchTreeScreenHandler extends ScreenHandler {
    
    private final PlayerEntity player;
    private final ResearchProgress progress;
    
    // 当前显示的研究分类
    private String currentCategory = "all";
    
    // 所有研究节点
    private List<Research> allResearch;
    
    public ResearchTreeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }
    
    public ResearchTreeScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModScreenHandlers.RESEARCH_TREE, syncId);
        this.player = player;
        this.progress = ResearchManager.getInstance().getProgress(player);
        this.allResearch = new ArrayList<>(ResearchManager.getInstance().getAllResearch());
        
        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 224));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    public ResearchProgress getProgress() {
        return progress;
    }
    
    /**
     * 获取研究节点状态
     */
    public Research.State getResearchState(Research research) {
        if (progress.isCompleted(research.getId())) {
            return Research.State.COMPLETED;
        }
        
        if (progress.isInProgress(research.getId())) {
            return Research.State.IN_PROGRESS;
        }
        
        // 检查前置研究
        for (String prereq : research.getPrerequisites()) {
            if (!progress.isCompleted(prereq)) {
                return Research.State.LOCKED;
            }
        }
        
        return Research.State.AVAILABLE;
    }
    
    /**
     * 开始研究
     */
    public boolean startResearch(String researchId) {
        Research research = ResearchManager.getInstance().getResearch(researchId);
        if (research == null) return false;
        
        // 检查状态
        if (getResearchState(research) != Research.State.AVAILABLE) {
            return false;
        }
        
        // 开始研究
        return ResearchManager.getInstance().startResearch(researchId, (net.minecraft.server.network.ServerPlayerEntity) player);
    }
    
    /**
     * 取消研究
     */
    public boolean cancelResearch(String researchId) {
        return ResearchManager.getInstance().cancelResearch(researchId, (net.minecraft.server.network.ServerPlayerEntity) player);
    }
    
    /**
     * 获取所有研究节点
     */
    public List<Research> getAllResearch() {
        return allResearch;
    }
    
    /**
     * 获取分类列表
     */
    public Set<String> getCategories() {
        Set<String> categories = new HashSet<>();
        categories.add("all");
        for (Research research : allResearch) {
            categories.add(research.getCategory());
        }
        return categories;
    }
    
    /**
     * 按分类过滤研究
     */
    public List<Research> getResearchByCategory(String category) {
        if ("all".equals(category)) {
            return allResearch;
        }
        return allResearch.stream()
            .filter(r -> category.equals(r.getCategory()))
            .toList();
    }
    
    /**
     * 设置当前分类
     */
    public void setCurrentCategory(String category) {
        this.currentCategory = category;
    }
    
    public String getCurrentCategory() {
        return currentCategory;
    }
}
