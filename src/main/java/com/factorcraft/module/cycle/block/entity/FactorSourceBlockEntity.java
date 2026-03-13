package com.factorcraft.module.cycle.block.entity;

import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.FactorTier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 释放结构 BlockEntity
 * 
 * 功能：
 * - 消耗材料产生 Factor
 * - 根据维度倍率计算释放量
 * - 支持材料品质系数
 * 
 * 设计文档：docs/17_factor_cycle_structures.md
 */
public class FactorSourceBlockEntity extends BlockEntity {
    
    // NBT 键
    private static final String NBT_FACTOR_BUFFER = "FactorBuffer";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_INPUT_STACK = "InputStack";
    
    // 配置参数
    private int factorBuffer = 0; // 待释放的 Factor 缓存
    private int progress = 0;
    private FactorTier tier = FactorTier.LOW_ENERGY; // T1
    
    // 物品栈
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    
    // 处理时间（ticks）
    private static final int PROCESSING_TIME = 100; // 5 秒
    
    public FactorSourceBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntities.FACTOR_SOURCE, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_BUFFER, factorBuffer);
        nbt.putInt(NBT_PROGRESS, progress);
        nbt.putInt(NBT_TIER, tier.ordinal());
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        factorBuffer = nbt.getInt(NBT_FACTOR_BUFFER);
        progress = nbt.getInt(NBT_PROGRESS);
        tier = FactorTier.values()[nbt.getInt(NBT_TIER)];
        Inventories.readNbt(nbt, inventory, registryLookup);
    }
    
    /**
     * 每 tick 调用
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorSourceBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 检查是否有输入材料
        if (entity.canProcess()) {
            entity.progress++;
            entity.markDirty();
            
            // 发送更新包
            world.updateListeners(pos, state, state, 3);
            
            // 处理完成
            if (entity.progress >= PROCESSING_TIME) {
                entity.process();
                entity.progress = 0;
            }
        }
        
        // 释放缓存的 Factor 到世界
        if (entity.factorBuffer > 0) {
            entity.releaseFactor(world, pos);
        }
    }
    
    /**
     * 检查是否可以处理
     */
    private boolean canProcess() {
        // 检查输入物品
        ItemStack input = inventory.get(0);
        if (input.isEmpty()) {
            return false;
        }
        
        // 检查输出空间（Factor 缓存有上限）
        if (factorBuffer >= 10000) {
            return false;
        }
        
        return progress < PROCESSING_TIME;
    }
    
    /**
     * 执行处理（消耗材料，缓存 Factor）
     */
    private void process() {
        // 消耗输入物品
        ItemStack input = inventory.get(0);
        if (!input.isEmpty()) {
            input.decrement(1);
            inventory.set(0, input);
            markDirty();
        }
        
        // 计算 Factor 产出
        int factorProduced = calculateFactorProduction();
        
        // 缓存 Factor
        factorBuffer += factorProduced;
        markDirty();
    }
    
    /**
     * 释放 Factor 到世界
     */
    private void releaseFactor(World world, BlockPos pos) {
        if (world.isClient) {
            return;
        }
        
        FactorService service = FactorService.getInstance();
        if (service != null && factorBuffer > 0) {
            // 释放 Factor（根据维度倍率计算）
            int released = calculateFactorProduction();
            service.addFactor(pos, released);
            factorBuffer = 0;
            markDirty();
        }
    }
    
    /**
     * 计算 Factor 产出（考虑维度倍率和材料品质）
     * 
     * 公式：实际释放 = 基础释放 × 维度倍率 × 材料品质系数
     */
    private int calculateFactorProduction() {
        int baseProduction = getBaseProduction();
        double dimensionMultiplier = getDimensionMultiplier();
        double qualityCoefficient = getQualityCoefficient();
        
        return (int) (baseProduction * dimensionMultiplier * qualityCoefficient);
    }
    
    /**
     * 获取基础产出（根据科技等级）
     */
    private int getBaseProduction() {
        int level = tier.level();
        switch (level) {
            case 1: return 50;    // T1: 基础共振器
            case 2: return 150;   // T2: 能量分解机
            case 3: return 500;   // T3: 物质转化炉
            case 4: return 2000;  // T4: 维度裂变器
            default: return 50;   // T1 默认
        }
    }
    
    /**
     * 获取维度倍率
     * 
     * 主世界：×1.0-1.5
     * 下界：×1.5-1.8
     * 末地：×2.0（推荐）
     */
    private double getDimensionMultiplier() {
        if (world == null) {
            return 1.0;
        }
        
        // 获取当前维度类型
        String dimensionType = getDimensionType();
        
        // 根据 tier 和维度返回倍率
        return switch (dimensionType) {
            case "nether" -> 1.5 + (tier.level() * 0.1);
            case "end" -> 2.0;
            default -> 1.0 + (tier.level() * 0.1); // 主世界
        };
    }
    
    /**
     * 获取维度类型
     */
    private String getDimensionType() {
        if (world == null) return "overworld";
        String dimId = world.getRegistryKey().getValue().toString();
        if (dimId.contains("the_nether")) return "nether";
        if (dimId.contains("the_end")) return "end";
        return "overworld";
    }
    
    /**
     * 获取材料品质系数
     * 
     * 普通品质：×1.0
     * 高纯度（当前 Factor > 70）：×1.2
     * 完美品质（当前 Factor > 90）：×1.5
     */
    private double getQualityCoefficient() {
        // 检查当前 Factor 值（作为品质指标）
        if (world == null) {
            return 1.0;
        }
        
        FactorService service = FactorService.getInstance();
        if (service == null) {
            return 1.0;
        }
        
        // 获取当前维度的 Factor 值
        double currentFactor = service.getFactor((net.minecraft.server.world.ServerWorld) world);
        
        // 根据 Factor 值返回品质系数
        if (currentFactor > 90) {
            return 1.5; // 完美品质
        } else if (currentFactor > 70) {
            return 1.2; // 高纯度
        }
        return 1.0; // 普通品质
    }
    
    /**
     * 获取 Factor 缓存
     */
    public int getFactorBuffer() {
        return factorBuffer;
    }
    
    /**
     * 获取处理进度（0-100）
     */
    public int getProgressPercent() {
        return (progress * 100) / PROCESSING_TIME;
    }
    
    /**
     * 获取科技等级
     */
    public FactorTier getTier() {
        return tier;
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
