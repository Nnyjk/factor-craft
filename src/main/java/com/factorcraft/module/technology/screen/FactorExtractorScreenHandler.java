package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.FactorExtractorCoreBlockEntity;
import net.minecraft.block.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

/**
 * Factor 提取器屏幕处理器
 */
public class FactorExtractorScreenHandler extends ScreenHandler {
    
    private final FactorExtractorCoreBlockEntity entity;
    private final World world;
    private final BlockPos pos;
    
    public FactorExtractorScreenHandler(int syncId, PlayerInventory playerInventory, FactorExtractorCoreBlockEntity entity) {
        super(null, syncId);
        this.entity = entity;
        this.world = playerInventory.player.getWorld();
        this.pos = entity.getPos();
        
        // 添加物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    public FactorExtractorCoreBlockEntity getEntity() {
        return entity;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
