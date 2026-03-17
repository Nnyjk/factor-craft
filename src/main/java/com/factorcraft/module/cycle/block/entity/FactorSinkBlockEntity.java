package com.factorcraft.module.cycle.block.entity;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.module.cycle.network.FactorNetworkManager;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Factor 吸收结构 BlockEntity
 * 
 * 功能：
 * - 从网络接收 Factor
 * - 消耗 Factor 生产材料
 * - 根据维度基准值计算消耗倍率
 * - 支持环境加成（Factor 窗口、ΔF）
 * 
 * 设计文档：docs/17_factor_cycle_structures.md
 */
public class FactorSinkBlockEntity extends BlockEntity implements IFactorNetworkNode, IFactorContainer {
    
    // NBT 键
    private static final String NBT_FACTOR_STORED = "FactorStored";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_RECIPE_INDEX = "RecipeIndex";
    private static final String NBT_NODE_ID = "NodeId";
    
    // 配置参数
    private int factorStored = 0;
    private int progress = 0;
    private FactorTier tier = FactorTier.LOW_ENERGY; // T1
    private int recipeIndex = 0;
    
    // 物品栈 (输入槽 + 输出槽)
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    
    // 网络相关
    private final String nodeId;
    
    // 处理时间（ticks）
    private static final int PROCESSING_TIME = 200; // 10 秒
    
    // 传输速率（每 tick）
    private static final double[] TRANSFER_RATE_BY_TIER = {10.0, 25.0, 50.0, 100.0};
    
    public FactorSinkBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntities.FACTOR_SINK, pos, state);
        this.nodeId = UUID.randomUUID().toString();
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_STORED, factorStored);
        nbt.putInt(NBT_PROGRESS, progress);
        nbt.putInt(NBT_TIER, tier.ordinal());
        nbt.putInt(NBT_RECIPE_INDEX, recipeIndex);
        nbt.putString(NBT_NODE_ID, nodeId);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        factorStored = nbt.getInt(NBT_FACTOR_STORED);
        progress = nbt.getInt(NBT_PROGRESS);
        tier = FactorTier.values()[nbt.getInt(NBT_TIER)];
        recipeIndex = nbt.getInt(NBT_RECIPE_INDEX);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            FactorNetworkManager.getInstance().registerNode(world, this);
        }
    }
    
    /**
     * 每 tick 调用
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorSinkBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 注册到网络
        FactorNetworkManager.getInstance().registerNode(world, entity);
        
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
        // 检查输入物品
        ItemStack input = inventory.get(0);
        if (input.isEmpty()) {
            return false;
        }
        
        // 检查 Factor 存储
        if (factorStored <= 0) {
            return false;
        }
        
        // 检查输出空间
        ItemStack output = inventory.get(1);
        if (!output.isEmpty() && output.getCount() >= output.getMaxCount()) {
            return false;
        }
        
        return progress < PROCESSING_TIME;
    }
    
    /**
     * 执行处理
     */
    private void process() {
        // 消耗 Factor
        int consumed = calculateFactorConsumption();
        factorStored = Math.max(0, factorStored - consumed);
        
        // 消耗输入物品
        ItemStack input = inventory.get(0);
        if (!input.isEmpty()) {
            input.decrement(1);
            inventory.set(0, input);
        }
        
        // 生成输出物品（简化：直接生成因子碎片）
        ItemStack output = inventory.get(1);
        if (output.isEmpty()) {
            output = new ItemStack(net.minecraft.item.Items.AMETHYST_SHARD, 1); // 占位物品
        } else {
            output.increment(1);
        }
        inventory.set(1, output);
        
        // 触发事件
        markDirty();
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
     * 
     * T1/T2 (LOW_ENERGY/STABLE): 主世界推荐
     * T3 (HIGH_ENERGY): 下界推荐
     * T4 (OVERLOAD): 末地推荐
     */
    private double getDimensionPenalty() {
        if (world == null) {
            return 1.0;
        }
        
        // 获取当前维度类型
        String dimensionKey = world.getRegistryKey().getValue().toString();
        
        // 根据 tier 判断是否为推荐维度
        int level = tier.level();
        boolean isRecommended = switch (level) {
            case 0, 1, 2 -> dimensionKey.equals("minecraft:overworld"); // T1/T2: 主世界
            case 3 -> dimensionKey.equals("minecraft:the_nether"); // T3: 下界
            case 4 -> dimensionKey.equals("minecraft:the_end"); // T4: 末地
            default -> true;
        };
        
        return isRecommended ? 1.0 : 10.0;
    }
    
    /**
     * 获取环境加成（Factor 窗口、ΔF）
     */
    private double getEnvironmentBonus() {
        double bonus = 0.0;
        
        if (world == null || world.isClient) {
            return bonus;
        }
        
        FactorService service = FactorService.getInstance();
        if (service == null) {
            return bonus;
        }
        
        // 获取当前 Factor 值
        double currentFactor = service.getFactor((net.minecraft.server.world.ServerWorld) world);
        
        // 检查 Factor 是否在推荐窗口 (50-70)
        if (currentFactor >= 50 && currentFactor <= 70) {
            bonus += 0.20; // 20% 加成
        }
        
        // 基于 Factor 值的趋势判断（简化为当前值与基准值的偏差）
        double baseFactor = FactorService.baseForDimension(world.getRegistryKey().getValue().toString());
        double delta = Math.abs(currentFactor - baseFactor);
        
        // ΔF 30-50: 10% 加成
        // ΔF 50+: 15% 加成
        if (delta >= 50) {
            bonus += 0.15;
        } else if (delta >= 30) {
            bonus += 0.10;
        }
        
        return Math.min(bonus, 0.35); // 最大 35% 加成
    }
    
    /**
     * 添加 Factor（IFactorContainer 接口）
     */
    @Override
    public double addFactor(double amount) {
        int oldStored = factorStored;
        int maxStorage = (int) getMaxFactorStorage();
        factorStored = Math.min(maxStorage, factorStored + (int) amount);
        int actual = factorStored - oldStored;
        markDirty();
        return actual;
    }
    
    /**
     * 抽取 Factor（IFactorContainer 接口）
     */
    @Override
    public double extractFactor(double amount) {
        int actual = Math.min(factorStored, (int) amount);
        factorStored -= actual;
        markDirty();
        return actual;
    }
    
    /**
     * 获取 Factor 存储量
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
    
    // ==================== IFactorNetworkNode 实现 ====================
    
    @Override
    public String getNodeId() {
        return nodeId;
    }
    
    @Override
    public BlockPos getNodePos() {
        return pos;
    }
    
    @Override
    public NodeType getNodeType() {
        return NodeType.SINK;
    }
    
    @Override
    public double getFactorStorage() {
        return factorStored;
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 50000.0; // 最大存储
    }
    
    @Override
    public double addFactor(double amount, String from) {
        // 从网络接收 Factor
        return addFactor(amount);
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        // 汇节点通常不向网络输出（只输出到机器）
        return 0.0;
    }
    
    @Override
    public double getTransferRate() {
        int tierLevel = tier.level();
        if (tierLevel >= 0 && tierLevel < TRANSFER_RATE_BY_TIER.length) {
            return TRANSFER_RATE_BY_TIER[tierLevel];
        }
        return TRANSFER_RATE_BY_TIER[0];
    }
    
    @Override
    public boolean canExtractFactor() {
        return factorStored > 0;
    }
    
    @Override
    public boolean canReceiveFactor() {
        return factorStored < getMaxFactorStorage();
    }
}
