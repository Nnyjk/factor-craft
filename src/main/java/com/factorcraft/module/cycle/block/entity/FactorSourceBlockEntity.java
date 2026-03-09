package com.factorcraft.module.cycle.block.entity;

import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.FactorTier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.registry.RegistryWrapper;
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
    
    // 处理时间（ticks）
    private static final int PROCESSING_TIME = 100; // 5 秒
    
    public FactorSourceBlockEntity(BlockPos pos, BlockState state) {
        // TODO: 恢复 BlockEntity 后修复
        super(null, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_BUFFER, factorBuffer);
        nbt.putInt(NBT_PROGRESS, progress);
        nbt.putInt(NBT_TIER, tier.ordinal());
        // TODO: 保存输入物品栈
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        factorBuffer = nbt.getInt(NBT_FACTOR_BUFFER);
        progress = nbt.getInt(NBT_PROGRESS);
        tier = FactorTier.values()[nbt.getInt(NBT_TIER)];
        // TODO: 加载输入物品栈
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
        // TODO: 检查输入物品
        // TODO: 检查输出空间（Factor 缓存）
        return progress < PROCESSING_TIME;
    }
    
    /**
     * 执行处理（消耗材料，缓存 Factor）
     */
    private void process() {
        // TODO: 消耗输入物品
        // TODO: 计算 Factor 产出
        // TODO: 缓存 Factor
        
        int factorProduced = calculateFactorProduction();
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
        
        // TODO: 获取当前维度类型
        // 根据 tier 和维度返回倍率
        return 1.0; // 默认
    }
    
    /**
     * 获取材料品质系数
     * 
     * 普通品质：×1.0
     * 高纯度（ΔF > 40）：×1.2
     * 完美品质（ΔF > 70）：×1.5
     */
    private double getQualityCoefficient() {
        // TODO: 检查 ΔF 值
        // 根据 ΔF 返回品质系数
        return 1.0; // 默认普通品质
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
