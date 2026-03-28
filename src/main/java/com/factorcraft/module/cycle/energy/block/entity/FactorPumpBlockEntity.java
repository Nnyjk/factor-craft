package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.module.cycle.energy.FactorEnergyBlocks;
import com.factorcraft.module.cycle.energy.component.FactorConsumerComponent;
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
 * Factor 泵 BlockEntity
 * 
 * 实现 FactorConsumerComponent 接口
 * 消耗 Factor 浓度来加速传输
 */
public class FactorPumpBlockEntity extends BlockEntity implements FactorConsumerComponent {
    
    // Registry Key
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_pump")
    );
    
    public static BlockEntityType<FactorPumpBlockEntity> TYPE;
    
    // 消耗配置
    private static final double DEFAULT_CONSUMPTION_RATE = 0.5; // 每 tick 消耗量
    private static final double MAX_BUFFER = 100.0;
    private static final double MIN_CONCENTRATION_THRESHOLD = 0.1;
    
    // 状态
    private double bufferFactor = 0.0;
    private boolean active = false;
    
    public FactorPumpBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    @Override
    public double consumeFactor(double amount) {
        double toConsume = Math.min(amount, bufferFactor);
        
        if (toConsume > 0) {
            bufferFactor -= toConsume;
            markDirty();
        }
        
        return toConsume;
    }
    
    @Override
    public double getConsumptionRate() {
        return DEFAULT_CONSUMPTION_RATE;
    }
    
    @Override
    public boolean canOperate(double concentration) {
        return concentration >= getMinConcentrationThreshold() && bufferFactor >= getConsumptionRate();
    }
    
    @Override
    public double getMinConcentrationThreshold() {
        return MIN_CONCENTRATION_THRESHOLD;
    }
    
    /**
     * 获取当前缓冲的 Factor 量
     */
    public double getBufferFactor() {
        return bufferFactor;
    }
    
    /**
     * 是否处于活动状态
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * tick 逻辑：消耗 Factor 并工作
     */
    public static void tick(World world, BlockPos pos, BlockState state, 
                           FactorPumpBlockEntity pump) {
        if (!world.isClient) {
            // 从世界浓度获取 Factor（简化实现）
            // TODO: 实现从相邻方块抽取 Factor 的逻辑
            if (pump.canOperate(0.5)) { // 假设环境浓度为 0.5
                pump.consumeFactor(pump.getConsumptionRate());
                pump.active = true;
                // TODO: 实现泵的实际工作逻辑（加速传输）
            } else {
                pump.active = false;
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putDouble("BufferFactor", bufferFactor);
        nbt.putBoolean("Active", active);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        bufferFactor = nbt.getDouble("BufferFactor");
        active = nbt.getBoolean("Active");
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
            FabricBlockEntityTypeBuilder.create(FactorPumpBlockEntity::new, 
                FactorEnergyBlocks.getFactorPump())
                .build()
        );
    }
}
