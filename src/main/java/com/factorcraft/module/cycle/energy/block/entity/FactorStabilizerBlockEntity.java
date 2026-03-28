package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.module.cycle.energy.FactorEnergyBlocks;
import com.factorcraft.module.cycle.energy.screen.FactorStabilizerScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Factor 稳定器 BlockEntity
 * 
 * 功能：
 * - 存储吸收的 Factor (最多 10,000 mB)
 * - 检测周围 Factor 浓度
 * - 当浓度超过 80% 时自动吸收
 * - 降低浓度波动
 */
public class FactorStabilizerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_stabilizer")
    );
    
    public static BlockEntityType<FactorStabilizerBlockEntity> TYPE;
    
    private static final int MAX_FACTOR = 10000; // 最大 Factor 存储 (mB)
    private static final int STABILIZE_THRESHOLD = 8000; // 稳定阈值 (80%)
    private static final int ABSORB_RATE = 100; // 吸收速率 (mB/tick)
    private static final int RANGE = 16; // 工作范围 (格)
    
    private int factorAmount = 0;
    private boolean isStabilizing = false;
    
    public FactorStabilizerBlockEntity(BlockPos pos, BlockState state) {
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
                FactorStabilizerBlockEntity::new,
                FactorEnergyBlocks.FACTOR_STABILIZER
            ).build()
        );
    }
    
    /**
     * 每 tick 执行
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorStabilizerBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        
        blockEntity.tick();
    }
    
    private void tick() {
        if (world == null) return;
        
        // 检测周围 Factor 浓度
        int avgConcentration = detectFactorConcentration();
        
        // 如果浓度超过阈值，开始吸收
        if (avgConcentration > STABILIZE_THRESHOLD) {
            isStabilizing = true;
            
            // 吸收 Factor
            if (factorAmount < MAX_FACTOR) {
                int toAbsorb = Math.min(ABSORB_RATE, MAX_FACTOR - factorAmount);
                factorAmount += toAbsorb;
                
                // 降低周围浓度 (简化实现)
                reduceSurroundingConcentration(toAbsorb);
            }
        } else {
            isStabilizing = false;
        }
        
        // 同步客户端
        if (world != null && !world.isClient) {
            markDirty();
        }
    }
    
    /**
     * 检测周围 Factor 浓度
     * @return 平均浓度百分比 (0-10000)
     */
    private int detectFactorConcentration() {
        if (world == null) return 0;
        
        int totalConcentration = 0;
        int count = 0;
        
        // 扫描周围区域
        for (int x = -RANGE; x <= RANGE; x++) {
            for (int y = -RANGE; y <= RANGE; y++) {
                for (int z = -RANGE; z <= RANGE; z++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (world.isAir(checkPos)) continue;
                    
                    // 简化：检查方块是否含有 Factor 相关组件
                    BlockState checkState = world.getBlockState(checkPos);
                    // TODO: 实现真正的浓度检测逻辑
                    count++;
                }
            }
        }
        
        if (count == 0) return 0;
        
        // 返回简化的浓度值
        return MathHelper.clamp(count * 100, 0, 10000);
    }
    
    /**
     * 降低周围 Factor 浓度
     */
    private void reduceSurroundingConcentration(int amount) {
        // TODO: 实现真正的浓度降低逻辑
        // 这需要与 Factor 管道系统集成
    }
    
    /**
     * 获取当前 Factor 存储量
     */
    public int getFactorAmount() {
        return factorAmount;
    }
    
    /**
     * 获取最大 Factor 存储量
     */
    public int getMaxFactor() {
        return MAX_FACTOR;
    }
    
    /**
     * 是否正在稳定
     */
    public boolean isStabilizing() {
        return isStabilizing;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putInt("FactorAmount", factorAmount);
        nbt.putBoolean("IsStabilizing", isStabilizing);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        factorAmount = nbt.getInt("FactorAmount");
        isStabilizing = nbt.getBoolean("IsStabilizing");
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.factor_stabilizer");
    }
    
    @Override
    public FactorStabilizerScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FactorStabilizerScreenHandler(syncId, playerInventory, this);
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup lookup) {
        return createNbt(lookup);
    }
}
