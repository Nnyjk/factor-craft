package com.factorcraft.module.cultivation.blockentity;

import com.factorcraft.module.advancement.AdvancementManager;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import com.factorcraft.module.ui.handler.CultivationScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 培育核心 BlockEntity
 * 
 * 用于为物品注入 Factor 特性
 */
public class CultivationCoreBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    private ItemStack targetItem = ItemStack.EMPTY;
    private int processTime = 0;
    private static final int MAX_PROCESS_TIME = 200; // 10 秒
    private static final double FACTOR_COST_BASE = 10.0;
    
    // 属性委托（用于 GUI 同步）
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CultivationCoreBlockEntity.this.processTime;
                case 1 -> CultivationCoreBlockEntity.MAX_PROCESS_TIME;
                default -> 0;
            };
        }
        
        @Override
        public void set(int index, int value) {
            if (index == 0) {
                CultivationCoreBlockEntity.this.processTime = value;
            }
        }
        
        @Override
        public int size() {
            return 2; // processTime 和 maxProcessTime
        }
    };
    
    public CultivationCoreBlockEntity(BlockPos pos, BlockState state) {
        super(null, pos, state);
    }
    
    /**
     * 设置目标物品
     */
    public void setTargetItem(ItemStack stack) {
        this.targetItem = stack.copy();
        this.processTime = 0;
        markDirty();
    }
    
    /**
     * 获取目标物品
     */
    public ItemStack getTargetItem() {
        return targetItem;
    }
    
    /**
     * 获取进度委托
     */
    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }
    
    /**
     * Tick 逻辑
     */
    public static void tick(World world, BlockPos pos, BlockState state, CultivationCoreBlockEntity blockEntity) {
        if (world.isClient || blockEntity.targetItem.isEmpty()) return;
        
        blockEntity.processTime++;
        
        if (blockEntity.processTime >= MAX_PROCESS_TIME) {
            blockEntity.performInjection(world, pos);
            blockEntity.processTime = 0;
            blockEntity.markDirty();
        }
    }
    
    /**
     * 执行特性注入
     */
    private void performInjection(World world, BlockPos pos) {
        if (targetItem.isEmpty()) return;
        
        ChunkPos chunkPos = new ChunkPos(pos);
        double availableFactor = ChunkFactorManager.getState(chunkPos)
            .map(state -> state.getCurrentConcentration())
            .orElse(0.0);
        
        if (availableFactor < FACTOR_COST_BASE) {
            // Factor 不足，重置进度
            this.processTime = 0;
            return;
        }
        
        var random = world.random;
        // 基于浓度调整特性生成概率
        double concentrationBonus = Math.min(availableFactor / 100.0, 0.3);
        double successRate = 0.7 + concentrationBonus;
        
        List<TraitInstance> newTraits = TraitService.generateRandomTraits(1, 1, random, successRate);
        
        if (!newTraits.isEmpty()) {
            TraitInstance newTrait = newTraits.get(0);
            if (TraitService.addTrait(targetItem, newTrait.traitId(), newTrait.level())) {
                // 消耗 Factor
                ChunkFactorManager.extractFactor(world, chunkPos, FACTOR_COST_BASE);
                
                // 触发成就
                double range = 8.0;
                for (var player : world.getPlayers()) {
                    if (player instanceof ServerPlayerEntity sp && 
                        player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < range * range) {
                        AdvancementManager.checkTraitAdvancements(sp, targetItem);
                        AdvancementManager.checkCultivationAdvancements(sp, newTrait.level());
                    }
                }
                
                // 标记物品已修改
                targetItem = targetItem.copy();
            }
        }
    }
    
    // ============ NamedScreenHandlerFactory 实现 ============
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.factorcraft.cultivation_core");
    }
    
    @Override
    @Nullable
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // 需要传入 BlockEntity 和 propertyDelegate
        // ScreenHandler 需要支持 propertyDelegate
        return new CultivationScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }
    
    // ============ NBT 保存/加载 ============
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!targetItem.isEmpty()) {
            nbt.put("targetItem", targetItem.toNbt(registryLookup));
        }
        nbt.putInt("processTime", processTime);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("targetItem")) {
            targetItem = ItemStack.fromNbt(registryLookup, nbt.getCompound("targetItem")).orElse(ItemStack.EMPTY);
        }
        processTime = nbt.getInt("processTime");
    }
}
