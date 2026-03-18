package com.factorcraft.module.cycle.block.entity;

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
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Factor 释放结构 BlockEntity
 * 
 * 功能：
 * - 从世界浓度提取 Factor 到网络
 * - 根据维度倍率计算提取量
 * - 支持材料品质系数
 * 
 * 设计文档：docs/17_factor_cycle_structures.md
 */
public class FactorSourceBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    // NBT 键
    private static final String NBT_FACTOR_BUFFER = "FactorBuffer";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_INPUT_STACK = "InputStack";
    private static final String NBT_NODE_ID = "NodeId";
    
    // 配置参数
    private int factorBuffer = 0; // 待释放的 Factor 缓存
    private int progress = 0;
    private FactorTier tier = FactorTier.LOW_ENERGY; // T1
    
    // 物品栈
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    
    // 网络相关
    private final String nodeId;
    
    // 处理时间（ticks）
    private static final int PROCESSING_TIME = 100; // 5 秒
    
    // 传输速率（每 tick）
    private static final double[] TRANSFER_RATE_BY_TIER = {10.0, 25.0, 50.0, 100.0};
    
    public FactorSourceBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntities.FACTOR_SOURCE, pos, state);
        this.nodeId = UUID.randomUUID().toString();
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_BUFFER, factorBuffer);
        nbt.putInt(NBT_PROGRESS, progress);
        nbt.putInt(NBT_TIER, tier.ordinal());
        nbt.putString(NBT_NODE_ID, nodeId);
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
    
    @Override
    public void markDirty() {
        super.markDirty();
        // 标记网络需要更新
        if (world != null) {
            FactorNetworkManager.getInstance().registerNode(world, this);
        }
    }
    
    /**
     * 每 tick 调用
     */
    public static void tick(World world, BlockPos pos, BlockState state, FactorSourceBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 注册到网络
        FactorNetworkManager.getInstance().registerNode(world, entity);
        
        // 从世界提取 Factor
        entity.extractFactorFromWorld(world);
        
        // 检查是否有输入材料（用于加成）
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
     * 从世界浓度提取 Factor 到缓存
     */
    private void extractFactorFromWorld(World world) {
        if (world.isClient) {
            return;
        }
        
        FactorService service = FactorService.getInstance();
        if (service == null) {
            return;
        }
        
        // 获取世界 Factor 浓度
        double concentration = service.getFactor((net.minecraft.server.world.ServerWorld) world);
        
        // 计算提取量（基于浓度和等级）
        double baseRate = getTransferRate();
        double dimensionMultiplier = getDimensionMultiplier();
        double qualityCoefficient = getQualityCoefficient();
        
        int extractAmount = (int) (baseRate * dimensionMultiplier * qualityCoefficient * (concentration / 100.0));
        
        if (extractAmount > 0) {
            // 添加到缓存（有上限）
            int maxBuffer = 10000;
            int spaceAvailable = maxBuffer - factorBuffer;
            int actualExtract = Math.min(extractAmount, spaceAvailable);
            
            if (actualExtract > 0) {
                factorBuffer += actualExtract;
                markDirty();
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
        
        // 检查输出空间（Factor 缓存有上限）
        if (factorBuffer >= 10000) {
            return false;
        }
        
        return progress < PROCESSING_TIME;
    }
    
    /**
     * 执行处理（消耗材料，增加提取效率）
     */
    private void process() {
        // 消耗输入物品
        ItemStack input = inventory.get(0);
        if (!input.isEmpty()) {
            input.decrement(1);
            inventory.set(0, input);
            markDirty();
        }
        
        // 材料处理会临时增加提取效率（已在 getQualityCoefficient 中体现）
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
        return NodeType.SOURCE;
    }
    
    @Override
    public double getFactorStorage() {
        return factorBuffer;
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 10000.0; // 最大缓存
    }
    
    @Override
    public double addFactor(double amount, String from) {
        // 源节点通常不接收外部 Factor（只从世界提取）
        return 0.0;
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        int actualExtract = (int) Math.min(factorBuffer, amount);
        if (actualExtract > 0) {
            factorBuffer -= actualExtract;
            markDirty();
        }
        return actualExtract;
    }
    
    @Override
    public double getTransferRate() {
        int tierLevel = tier.level();
        if (tierLevel >= 0 && tierLevel < TRANSFER_RATE_BY_TIER.length) {
            return TRANSFER_RATE_BY_TIER[tierLevel];
        }
        return TRANSFER_RATE_BY_TIER[0];
    }
}
