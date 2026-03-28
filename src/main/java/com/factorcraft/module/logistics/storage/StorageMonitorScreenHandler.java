package com.factorcraft.module.logistics.storage;

import com.factorcraft.factor.FactorType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储监控器屏幕处理器
 */
public class StorageMonitorScreenHandler extends ScreenHandler {
    
    private final StorageMonitorBlockEntity blockEntity;
    private final BlockPos pos;
    private Map<FactorType, Integer> factorData = new HashMap<>();
    private String searchFilter = "";
    
    /**
     * 服务器端构造函数 (带 BlockEntity)
     */
    public StorageMonitorScreenHandler(int syncId, PlayerInventory playerInventory, StorageMonitorBlockEntity blockEntity) {
        super(LogisticsStorage.STORAGE_MONITOR_HANDLER, syncId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getPos();
        updateFactorData();
        
        // 添加玩家物品栏
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // 添加玩家快捷栏
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
        }
    }
    
    /**
     * 客户端构造函数 (带 SyncData)
     */
    public StorageMonitorScreenHandler(int syncId, PlayerInventory playerInventory, SyncData data) {
        super(LogisticsStorage.STORAGE_MONITOR_HANDLER, syncId);
        this.pos = data.pos();
        this.blockEntity = null; // 客户端通过 pos 查找
        
        // 添加玩家物品栏
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }
        
        // 添加玩家快捷栏
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
        }
    }
    
    /**
     * 获取 BlockEntity (客户端需要查找)
     */
    public StorageMonitorBlockEntity getBlockEntity() {
        if (blockEntity != null) {
            return blockEntity;
        }
        // 客户端查找逻辑（需要在 Screen 中实现）
        return null;
    }
    
    public BlockPos getBlockPos() {
        return pos;
    }
    
    /**
     * 更新 Factor 数据
     */
    public void updateFactorData() {
        if (blockEntity != null) {
            this.factorData = blockEntity.getFilteredFactorData();
            this.searchFilter = blockEntity.getSearchFilter();
        }
    }
    
    public Map<FactorType, Integer> getFactorData() {
        return factorData;
    }
    
    public String getSearchFilter() {
        return searchFilter;
    }
    
    public void setSearchFilter(String filter) {
        if (blockEntity != null) {
            blockEntity.setSearchFilter(filter);
            this.searchFilter = filter;
            updateFactorData();
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return pos != null && player.squaredDistanceTo(pos.toCenterPos()) <= 64.0;
    }
    
    @Override
    public net.minecraft.item.ItemStack quickMove(PlayerEntity player, int index) {
        net.minecraft.item.ItemStack itemStack = net.minecraft.item.ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasStack()) {
            net.minecraft.item.ItemStack originalStack = slot.getStack();
            itemStack = originalStack.copy();
            
            // 尝试移动到玩家物品栏
            if (index < this.slots.size() - 36) {
                if (!this.insertItem(originalStack, this.slots.size() - 36, this.slots.size(), true)) {
                    return net.minecraft.item.ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.slots.size() - 36, false)) {
                return net.minecraft.item.ItemStack.EMPTY;
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(net.minecraft.item.ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemStack;
    }
    
    /**
     * 同步数据记录
     */
    public record SyncData(BlockPos pos) {
        public static final PacketCodec<PacketByteBuf, SyncData> PACKET_CODEC =
            PacketCodec.tuple(
                BlockPos.PACKET_CODEC, SyncData::pos,
                SyncData::new
            );
    }
}
