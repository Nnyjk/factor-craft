package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.module.cycle.energy.block.FactorReactorBlock;
import com.factorcraft.module.cycle.energy.screen.FactorReactorScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 反应堆 BlockEntity
 * 
 * 功能：
 * - 存储高密度 Factor
 * - 转换为能量输出
 * - 温度管理（过热保护）
 * - 冷却剂消耗
 */
public class FactorReactorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    
    public static final RegistryKey<BlockEntityType<?>> KEY = RegistryKey.of(
        Registries.BLOCK_ENTITY_TYPE.getKey(),
        Identifier.of("factorcraft", "factor_reactor")
    );
    
    public static BlockEntityType<FactorReactorBlockEntity> TYPE;
    
    /**
     * 初始化 BlockEntity 类型
     */
    public static void init() {
        TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            KEY,
            FabricBlockEntityTypeBuilder.create(FactorReactorBlockEntity::new, FactorReactorBlock.FACTOR_REACTOR).build()
        );
    }
    
    public static final int MAX_ENERGY = 100000000;  // 1 亿能量存储
    public static final int MAX_TEMPERATURE = 10000;  // 最高温度
    public static final int OPTIMAL_TEMPERATURE = 5000;  // 最佳工作温度
    public static final int COOLING_RATE = 100;  // 每 tick 冷却速率
    
    private long energyStored;
    private int temperature;
    private int coolingValue;
    
    public FactorReactorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
        this.energyStored = 0;
        this.temperature = 0;
        this.coolingValue = 0;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(nbt, lookup);
        nbt.putLong("EnergyStored", energyStored);
        nbt.putInt("Temperature", temperature);
        nbt.putInt("CoolingValue", coolingValue);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        energyStored = nbt.getLong("EnergyStored");
        temperature = nbt.getInt("Temperature");
        coolingValue = nbt.getInt("CoolingValue");
    }
    
    /**
     * 每 tick 更新
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorReactorBlockEntity entity) {
        if (world.isClient) return;
        
        // 温度自然下降
        if (entity.temperature > 0) {
            entity.temperature = Math.max(0, entity.temperature - COOLING_RATE);
        }
        
        // 使用冷却剂
        if (entity.coolingValue > 0) {
            entity.coolingValue--;
        }
        
        // 过热检测
        if (entity.temperature > MAX_TEMPERATURE) {
            // 爆炸逻辑（可配置）
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5.0f, World.ExplosionSourceType.BLOCK);
            world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
        }
        
        entity.markDirty();
    }
    
    /**
     * 添加能量
     */
    public void addEnergy(long amount) {
        this.energyStored = Math.min(MAX_ENERGY, energyStored + amount);
        this.temperature += 100;  // 每添加能量增加温度
        markDirty();
    }
    
    /**
     * 提取能量
     */
    public long extractEnergy(long amount, Direction side) {
        long extracted = Math.min(amount, energyStored);
        energyStored -= extracted;
        markDirty();
        return extracted;
    }
    
    /**
     * 添加冷却剂
     */
    public void addCoolant(int amount) {
        this.coolingValue = Math.min(MAX_TEMPERATURE, coolingValue + amount);
        markDirty();
    }
    
    /**
     * 获取当前能量存储
     */
    public long getEnergyStored() {
        return energyStored;
    }
    
    /**
     * 获取当前温度
     */
    public int getTemperature() {
        return temperature;
    }
    
    /**
     * 获取冷却值
     */
    public int getCoolingValue() {
        return coolingValue;
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.factorcraft.factor_reactor");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FactorReactorScreenHandler(syncId, playerInventory, this);
    }
    
    /**
     * 掉落物品
     */
    public void dropInventory() {
        // TODO: 实现物品掉落逻辑
    }
}
