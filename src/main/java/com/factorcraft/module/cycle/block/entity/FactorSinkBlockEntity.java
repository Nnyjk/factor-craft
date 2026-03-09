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
 * Factor 吸收结构 BlockEntity
 * 
 * 功能：
 * - 消耗 Factor 生产材料
 * - 根据维度基准值计算消耗倍率
 * - 支持环境加成（Factor 窗口、ΔF）
 * 
 * 设计文档：docs/17_factor_cycle_structures.md
 */
public class FactorSinkBlockEntity extends BlockEntity {
    
    // NBT 键
    private static final String NBT_FACTOR_STORED = "FactorStored";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_RECIPE_INDEX = "RecipeIndex";
    
    // 配置参数
    private int factorStored = 0;
    private int progress = 0;
    private FactorTier tier = FactorTier.LOW_ENERGY; // T1
    private int recipeIndex = 0;
    
    // 处理时间（ticks）
    private static final int PROCESSING_TIME = 200; // 10 秒
    
    public FactorSinkBlockEntity(BlockPos pos, BlockState state) {
        // TODO: 恢复 BlockEntity 后修复
        super(null, pos, state);
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_STORED, factorStored);
        nbt.putInt(NBT_PROGRESS, progress);
        nbt.putInt(NBT_TIER, tier.ordinal());
        nbt.putInt(NBT_RECIPE_INDEX, recipeIndex);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        factorStored = nbt.getInt(NBT_FACTOR_STORED);
        progress = nbt.getInt(NBT_PROGRESS);
        tier = FactorTier.values()[nbt.getInt(NBT_TIER)];
        recipeIndex = nbt.getInt(NBT_RECIPE_INDEX);
    }
    
    /**
     * 每 tick 调用
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorSinkBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 检查是否有足够的 Factor 和材料
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
    }
    
    /**
     * 检查是否可以处理
     */
    private boolean canProcess() {
        // TODO: 检查输入物品
        // TODO: 检查 Factor 存储
        // TODO: 检查输出空间
        return factorStored > 0 && progress < PROCESSING_TIME;
    }
    
    /**
     * 执行处理
     */
    private void process() {
        // TODO: 消耗 Factor
        // TODO: 消耗输入物品
        // TODO: 生成输出物品
        // TODO: 触发事件
        
        if (world != null && !world.isClient) {
            FactorService service = FactorService.getInstance();
            if (service != null) {
                // 消耗 Factor（根据维度基准值计算）
                int consumed = calculateFactorConsumption();
                service.consumeFactor(pos, consumed);
            }
        }
    }
    
    /**
     * 计算 Factor 消耗（考虑维度倍率和环境加成）
     * 
     * 公式：实际消耗 = 基础消耗 × 维度惩罚 × (1 - 环境加成)
     */
    private int calculateFactorConsumption() {
        int baseConsumption = getBaseConsumption();
        double dimensionPenalty = getDimensionPenalty();
        double environmentBonus = getEnvironmentBonus();
        
        return (int) (baseConsumption * dimensionPenalty * (1 - environmentBonus));
    }
    
    /**
     * 获取基础消耗（根据科技等级）
     * 
     * 注意：使用 FactorTier 的 level 值
     * DEPLETED=0, LOW_ENERGY=1, STABLE=2, HIGH_ENERGY=3, OVERLOAD=4
     */
    private int getBaseConsumption() {
        int level = tier.level();
        switch (level) {
            case 1: return 1000;   // T1: 基础共振炉
            case 2: return 5000;   // T2: 维度结晶器
            case 3: return 25000;  // T3: 远古合成阵
            case 4: return 125000; // T4: 仲裁反应堆
            default: return 1000;  // T1 默认
        }
    }
    
    /**
     * 获取维度惩罚（推荐维度×1.0，非推荐×10）
     */
    private double getDimensionPenalty() {
        if (world == null) {
            return 1.0;
        }
        
        // TODO: 获取当前维度类型
        // TODO: 根据 tier 判断是否为推荐维度
        return 1.0; // 默认推荐维度
    }
    
    /**
     * 获取环境加成（Factor 窗口、ΔF）
     */
    private double getEnvironmentBonus() {
        double bonus = 0.0;
        
        // TODO: 检查 Factor 是否在推荐窗口
        // TODO: 检查 ΔF 值
        // Factor 50-70: -20%
        // ΔF 30-50: -10%
        // ΔF 50+: -15%
        
        return bonus;
    }
    
    /**
     * 添加 Factor
     */
    public void addFactor(int amount) {
        factorStored += amount;
        markDirty();
    }
    
    /**
     * 获取当前存储的 Factor
     */
    public int getFactorStored() {
        return factorStored;
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
