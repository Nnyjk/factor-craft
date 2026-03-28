package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.module.cycle.energy.screen.FactorCompressorScreenHandler;
import com.factorcraft.module.cycle.energy.screen.FactorEnergyScreenHandlers;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 压缩机 BlockEntity
 * 
 * 压缩逻辑：1000mB Factor → 10mB 高密度 Factor (压缩比 100:1)
 */
public class FactorCompressorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_compressor")
    );
    
    public static BlockEntityType<FactorCompressorBlockEntity> TYPE;
    
    public static final int COMPRESSION_RATIO = 100;
    public static final int INPUT_AMOUNT = 1000;
    public static final int OUTPUT_AMOUNT = 10;
    public static final int TICKS_PER_COMPRESSION = 200; // 10 秒
    
    private int factorAmount = 0;
    private int progress = 0;
    private final SimpleInventory inventory = new SimpleInventory(2);
    
    public FactorCompressorBlockEntity(BlockPos pos, BlockState state) {
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
                FactorCompressorBlockEntity::new,
                com.factorcraft.module.cycle.energy.FactorEnergyBlocks.FACTOR_COMPRESSOR
            ).build()
        );
    }
    
    /**
     * 每个 tick 执行
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorCompressorBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        // 检查是否有足够的 Factor
        if (blockEntity.factorAmount >= INPUT_AMOUNT) {
            blockEntity.progress++;
            
            if (blockEntity.progress >= TICKS_PER_COMPRESSION) {
                // 压缩完成
                blockEntity.factorAmount -= INPUT_AMOUNT;
                blockEntity.progress = 0;
                
                // 创建高密度 Factor
                ItemStack output = blockEntity.inventory.getStack(1);
                if (output.isEmpty()) {
                    blockEntity.inventory.setStack(1, new ItemStack(com.factorcraft.module.cycle.energy.item.FactorEnergyItems.DENSE_FACTOR, OUTPUT_AMOUNT / 10));
                } else if (output.isOf(com.factorcraft.module.cycle.energy.item.FactorEnergyItems.DENSE_FACTOR)) {
                    output.increment(OUTPUT_AMOUNT / 10);
                }
                
                blockEntity.markDirty();
            }
        }
    }
    
    /**
     * 添加 Factor
     */
    public void addFactor(int amount) {
        this.factorAmount += amount;
        this.markDirty();
    }
    
    public int getFactorAmount() {
        return factorAmount;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public int getMaxProgress() {
        return TICKS_PER_COMPRESSION;
    }
    
    public SimpleInventory getInventory() {
        return inventory;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("container.factorcraft.factor_compressor");
    }
    
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FactorCompressorScreenHandler(syncId, inv, this);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("FactorAmount", factorAmount);
        nbt.putInt("Progress", progress);
        Inventories.writeNbt(nbt, inventory.getHeldStacks(), lookup);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        factorAmount = nbt.getInt("FactorAmount");
        progress = nbt.getInt("Progress");
        Inventories.readNbt(nbt, inventory.getHeldStacks(), lookup);
    }
}
