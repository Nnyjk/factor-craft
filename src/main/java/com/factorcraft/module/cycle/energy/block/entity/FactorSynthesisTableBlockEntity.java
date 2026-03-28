package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.module.cycle.energy.FactorEnergyBlocks;
import com.factorcraft.module.cycle.energy.screen.FactorSynthesisTableScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 合成台 BlockEntity
 * 
 * 功能:
 * - 3x3 合成网格 + 输出格
 * - 使用 DenseFactor 作为能源
 * - 支持高级配方合成
 */
public class FactorSynthesisTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_synthesis_table")
    );
    
    public static BlockEntityType<FactorSynthesisTableBlockEntity> TYPE;
    
    private static final int INPUT_SLOTS = 9;
    private static final int OUTPUT_SLOT = 9;
    private static final int TOTAL_SLOTS = 10;
    
    private final SimpleInventory inventory = new SimpleInventory(TOTAL_SLOTS) {
        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return FactorSynthesisTableBlockEntity.this.canPlayerUse(player);
        }
    };
    
    private int synthesisProgress;
    private int synthesisTime;
    private int denseFactorAmount; // mB
    
    public FactorSynthesisTableBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    /**
     * 初始化 BlockEntity 类型
     */
    public static void init() {
        TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            KEY.getValue(),
            FabricBlockEntityTypeBuilder.create(
                FactorSynthesisTableBlockEntity::new,
                FactorEnergyBlocks.getFactorSynthesisTable()
            ).build()
        );
    }
    
    /**
     * 获取物品栏
     */
    public SimpleInventory getInventory() {
        return inventory;
    }
    
    /**
     * 每 tick 执行
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorSynthesisTableBlockEntity blockEntity) {
        if (world.isClient) return;
        
        blockEntity.synthesisTime++;
        
        // 检查是否可以合成
        if (blockEntity.canCraft()) {
            blockEntity.synthesisProgress++;
            
            if (blockEntity.synthesisProgress >= blockEntity.synthesisTime) {
                blockEntity.craft();
                blockEntity.synthesisProgress = 0;
            }
        } else {
            blockEntity.synthesisProgress = 0;
        }
        
        blockEntity.markDirty();
    }
    
    /**
     * 检查是否可以合成
     */
    private boolean canCraft() {
        // 简化：检查输入槽是否有物品
        boolean hasInput = false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            if (!inventory.getStack(i).isEmpty()) {
                hasInput = true;
                break;
            }
        }
        
        if (!hasInput) return false;
        
        // 检查输出槽是否有空间
        ItemStack output = inventory.getStack(OUTPUT_SLOT);
        return output.isEmpty() || output.getCount() < output.getMaxCount();
    }
    
    /**
     * 执行合成
     */
    private void craft() {
        // 简化：将输入物品移动到输出（实际应该根据配方）
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                stack.decrement(1);
            }
        }
        
        // 输出物品（简化）
        ItemStack output = inventory.getStack(OUTPUT_SLOT);
        if (output.isEmpty()) {
            output = inventory.getStack(0).copy();
            output.setCount(1);
            inventory.setStack(OUTPUT_SLOT, output);
        } else {
            output.increment(1);
        }
        
        // 消耗 Factor
        if (denseFactorAmount >= 10) {
            denseFactorAmount -= 10;
        }
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FactorSynthesisTableScreenHandler(syncId, playerInventory, this, new int[]{synthesisProgress, synthesisTime});
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.factor_synthesis_table");
    }
    
    private boolean canPlayerUse(PlayerEntity player) {
        if (world == null || pos == null) return false;
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
    
    public int getSynthesisProgress() {
        return synthesisProgress;
    }
    
    public int getSynthesisTime() {
        return synthesisTime;
    }
    
    public int getDenseFactorAmount() {
        return denseFactorAmount;
    }
    
    public void setDenseFactorAmount(int amount) {
        this.denseFactorAmount = amount;
        markDirty();
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), lookup);
        nbt.putInt("SynthesisProgress", synthesisProgress);
        nbt.putInt("DenseFactorAmount", denseFactorAmount);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        Inventories.readNbt(nbt, inventory.getHeldStacks(), lookup);
        synthesisProgress = nbt.getInt("SynthesisProgress");
        denseFactorAmount = nbt.getInt("DenseFactorAmount");
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(net.minecraft.registry.RegistryWrapper.WrapperLookup lookup) {
        return createNbt(lookup);
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
