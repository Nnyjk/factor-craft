package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.module.cycle.energy.FactorEnergyBlocks;
import com.factorcraft.module.cycle.energy.component.FactorStorageComponent;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 晶体 BlockEntity
 * 
 * 实现 FactorStorageComponent 接口
 * 支持存储 Factor 浓度并为机器供能
 */
public class FactorCrystalBlockEntity extends BlockEntity implements FactorStorageComponent {
    
    // Registry Key
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_crystal")
    );
    
    public static BlockEntityType<FactorCrystalBlockEntity> TYPE;
    
    // 存储配置
    private static final double DEFAULT_CAPACITY = 1000.0;
    private static final double CHARGE_RATE = 1.0; // 每 tick 自然充能速率
    
    // 存储状态
    private double storedFactor = 0.0;
    
    public FactorCrystalBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    @Override
    public double insertFactor(double amount, boolean simulate) {
        double space = getCapacity() - storedFactor;
        double toInsert = Math.min(amount, space);
        
        if (!simulate && toInsert > 0) {
            storedFactor += toInsert;
            markDirty();
        }
        
        return toInsert;
    }
    
    @Override
    public double extractFactor(double amount, boolean simulate) {
        double toExtract = Math.min(amount, storedFactor);
        
        if (!simulate && toExtract > 0) {
            storedFactor -= toExtract;
            markDirty();
        }
        
        return toExtract;
    }
    
    @Override
    public double getStoredFactor() {
        return storedFactor;
    }
    
    @Override
    public double getCapacity() {
        return DEFAULT_CAPACITY;
    }
    
    /**
     * tick 逻辑：自然充能
     */
    public static void tick(World world, BlockPos pos, BlockState state, 
                           FactorCrystalBlockEntity crystal) {
        if (!world.isClient) {
            // 从环境吸收 Factor 浓度（简化实现）
            if (!crystal.isFull()) {
                crystal.insertFactor(CHARGE_RATE, false);
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putDouble("StoredFactor", storedFactor);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        storedFactor = nbt.getDouble("StoredFactor");
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    /**
     * 初始化 BlockEntity 类型
     */
    public static void init() {
        TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            KEY.getValue(),
            FabricBlockEntityTypeBuilder.create(FactorCrystalBlockEntity::new, 
                FactorEnergyBlocks.getFactorCrystal())
                .build()
        );
    }
}
