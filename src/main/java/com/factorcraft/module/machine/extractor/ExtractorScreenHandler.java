package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.resource.featuretoggle.FeatureFlags;

/**
 * 提取器屏幕处理器
 * 
 * GUI 布局:
 * - 槽位 0: 输入物品
 * - 槽位 1: 燃料/能源物品
 * - 槽位 2-3: 输出槽位
 * - 属性: 提取进度、能量、配方信息
 */
public class ExtractorScreenHandler extends ScreenHandler {
    
    public static final Identifier SCREEN_ID = Identifier.of(FactorCraftMod.MOD_ID, "extractor");
    
    // GUI 布局常量
    private static final int INPUT_SLOT_X = 56;
    private static final int INPUT_SLOT_Y = 17;
    private static final int FUEL_SLOT_X = 56;
    private static final int FUEL_SLOT_Y = 53;
    private static final int OUTPUT_SLOT_START_X = 116;
    private static final int OUTPUT_SLOT_START_Y = 35;
    
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final ScreenHandlerContext context;
    
    // 属性索引
    public static final int PROP_PROGRESS = 0;
    public static final int PROP_MAX_PROGRESS = 1;
    public static final int PROP_ENERGY = 2;
    public static final int PROP_MAX_ENERGY = 3;
    public static final int PROP_TIER = 4;
    public static final int PROP_RECIPE_VALID = 5;
    
    /**
     * 服务端构造器
     */
    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory, 
                                   Inventory inventory, PropertyDelegate propertyDelegate,
                                   ScreenHandlerContext context) {
        super(createScreenHandlerType(), syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.context = context;
        
        // 添加属性
        addProperties(propertyDelegate);
        
        // 输入槽位
        addSlot(new Slot(inventory, ExtractorBlockEntity.SLOT_INPUT, INPUT_SLOT_X, INPUT_SLOT_Y));
        
        // 燃料槽位
        addSlot(new Slot(inventory, ExtractorBlockEntity.SLOT_FUEL, FUEL_SLOT_X, FUEL_SLOT_Y));
        
        // 输出槽位
        for (int i = 0; i < ExtractorBlockEntity.OUTPUT_SLOTS; i++) {
            addSlot(new ExtractorOutputSlot(inventory, 
                ExtractorBlockEntity.SLOT_OUTPUT_START + i,
                OUTPUT_SLOT_START_X + i * 18,
                OUTPUT_SLOT_START_Y));
        }
        
        // 玩家背包 (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, 
                    col + row * 9 + 9,
                    8 + col * 18,
                    84 + row * 18));
            }
        }
        
        // 玩家快捷栏
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    /**
     * 客户端构造器
     */
    public ExtractorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(ExtractorBlockEntity.INVENTORY_SIZE),
             new ArrayPropertyDelegate(6), ScreenHandlerContext.EMPTY);
    }
    
    /**
     * 创建屏幕处理器类型
     */
    private static ScreenHandlerType<ExtractorScreenHandler> createScreenHandlerType() {
        return new ScreenHandlerType<>(ExtractorScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    }
    
    /**
     * 快速转移物品
     */
    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            // 从机器槽位转移到玩家背包
            if (slotIndex < ExtractorBlockEntity.INVENTORY_SIZE) {
                if (!insertItem(originalStack, ExtractorBlockEntity.INVENTORY_SIZE, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 从玩家背包转移到机器槽位
            else {
                // 优先尝试放入输入槽
                if (!insertItem(originalStack, ExtractorBlockEntity.SLOT_INPUT, ExtractorBlockEntity.SLOT_INPUT + 1, false)) {
                    // 然后尝试放入燃料槽
                    if (!insertItem(originalStack, ExtractorBlockEntity.SLOT_FUEL, ExtractorBlockEntity.SLOT_FUEL + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return newStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
    
    // ========== 属性访问方法 ==========
    
    public int getProgress() {
        return propertyDelegate.get(PROP_PROGRESS);
    }
    
    public int getMaxProgress() {
        return propertyDelegate.get(PROP_MAX_PROGRESS);
    }
    
    public int getEnergy() {
        return propertyDelegate.get(PROP_ENERGY);
    }
    
    public int getMaxEnergy() {
        return propertyDelegate.get(PROP_MAX_ENERGY);
    }
    
    public int getTier() {
        return propertyDelegate.get(PROP_TIER);
    }
    
    public boolean hasValidRecipe() {
        return propertyDelegate.get(PROP_RECIPE_VALID) != 0;
    }
    
    /**
     * 输出槽位（只能取出）
     */
    private static class ExtractorOutputSlot extends Slot {
        
        public ExtractorOutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        
        @Override
        public boolean canInsert(ItemStack stack) {
            return false; // 输出槽只能取出
        }
    }
}