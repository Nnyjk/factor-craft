package com.factorcraft.module.logistics.request;

import com.factorcraft.module.logistics.storage.LogisticsStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

/**
 * 自动请求终端屏幕处理器
 */
public class RequestTerminalScreenHandler extends ScreenHandler {
    
    public static final int INVENTORY_SIZE = 0; // 纯显示，无物品栏
    
    private final Inventory inventory;
    private final BlockPos pos;
    
    /**
     * 服务器端构造函数
     */
    public RequestTerminalScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(LogisticsStorage.REQUEST_TERMINAL_HANDLER, syncId);
        this.inventory = new SimpleInventory(INVENTORY_SIZE);
        this.pos = pos;
        addPlayerInventory(playerInventory);
    }
    
    /**
     * 客户端构造函数 (带 SyncData)
     */
    public RequestTerminalScreenHandler(int syncId, PlayerInventory playerInventory, SyncData data) {
        super(LogisticsStorage.REQUEST_TERMINAL_HANDLER, syncId);
        this.inventory = new SimpleInventory(INVENTORY_SIZE);
        this.pos = data.pos();
        addPlayerInventory(playerInventory);
    }
    
    private void addPlayerInventory(PlayerInventory playerInventory) {
        // 主物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // 快捷栏
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    public BlockPos getBlockPos() {
        return pos;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return pos != null && player.squaredDistanceTo(pos.toCenterPos()) <= 64.0;
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
