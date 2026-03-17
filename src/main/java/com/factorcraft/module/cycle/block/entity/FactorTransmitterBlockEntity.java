package com.factorcraft.module.cycle.block.entity;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.module.cycle.network.FactorNetworkManager;
import com.factorcraft.module.factor.DimensionManager;
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

import java.util.UUID;

/**
 * 跨维度 Factor 传递器 BlockEntity
 * 
 * 功能：
 * - 将 Factor 传输到其他维度
 * - 根据维度基准比计算传输倍率
 * - 支持距离损耗
 * 
 * 设计文档：docs/17_factor_cycle_structures.md
 * 
 * 传输公式：
 * 接收 Factor = 发送 Factor × (发送端基准 / 接收端基准) × 效率 × (1 - 距离损耗)
 */
public class FactorTransmitterBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    // NBT 键
    private static final String NBT_FACTOR_STORED = "FactorStored";
    private static final String NBT_TARGET_DIMENSION = "TargetDimension";
    private static final String NBT_TARGET_POS = "TargetPos";
    private static final String NBT_TIER = "Tier";
    private static final String NBT_LINKED = "Linked";
    private static final String NBT_TRANSMIT_PROGRESS = "TransmitProgress";
    private static final String NBT_NODE_ID = "NodeId";
    
    // 配置参数
    private int factorStored = 0;
    private String targetDimension = ""; // 目标维度 ID
    private BlockPos targetPos = BlockPos.ORIGIN;
    private FactorTier tier = FactorTier.LOW_ENERGY; // T1
    private boolean linked = false; // 是否已配对
    private int transmitProgress = 0;
    private String nodeId; // 节点唯一 ID
    
    // 传输时间（ticks）
    private static final int TRANSMIT_TIME = 100; // 5 秒
    
    // 传输效率
    private static final double[] EFFICIENCY_BY_TIER = {0.80, 0.85, 0.90, 0.95};
    
    // 距离损耗（每 100 格）
    private static final double[] DISTANCE_LOSS_BY_TIER = {0.01, 0.008, 0.005, 0.003};
    
    // 传输速率（每 tick）
    private static final double[] TRANSFER_RATE_BY_TIER = {10.0, 25.0, 50.0, 100.0};
    
    public FactorTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(CycleBlockEntities.FACTOR_TRANSMITTER, pos, state);
        this.nodeId = UUID.randomUUID().toString();
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(NBT_FACTOR_STORED, factorStored);
        nbt.putString(NBT_TARGET_DIMENSION, targetDimension);
        nbt.putLong(NBT_TARGET_POS, targetPos.asLong());
        nbt.putInt(NBT_TIER, tier.ordinal());
        nbt.putBoolean(NBT_LINKED, linked);
        nbt.putInt(NBT_TRANSMIT_PROGRESS, transmitProgress);
        nbt.putString(NBT_NODE_ID, nodeId);
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        factorStored = nbt.getInt(NBT_FACTOR_STORED);
        targetDimension = nbt.getString(NBT_TARGET_DIMENSION);
        targetPos = BlockPos.fromLong(nbt.getLong(NBT_TARGET_POS));
        tier = FactorTier.values()[nbt.getInt(NBT_TIER)];
        linked = nbt.getBoolean(NBT_LINKED);
        transmitProgress = nbt.getInt(NBT_TRANSMIT_PROGRESS);
        nodeId = nbt.getString(NBT_NODE_ID);
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
    public static void tick(World world, BlockPos pos, BlockState state, FactorTransmitterBlockEntity entity) {
        if (world.isClient) {
            return;
        }
        
        // 注册到网络
        FactorNetworkManager.getInstance().registerNode(world, entity);
        
        // 如果已配对且有 Factor，开始传输
        if (entity.linked && entity.factorStored > 0 && entity.transmitProgress < TRANSMIT_TIME) {
            entity.transmitProgress++;
            entity.markDirty();
            
            // 发送更新包
            world.updateListeners(pos, state, state, 3);
            
            // 传输完成
            if (entity.transmitProgress >= TRANSMIT_TIME) {
                entity.transmit(world, pos);
                entity.transmitProgress = 0;
            }
        }
    }
    
    /**
     * 执行跨维度传输
     */
    private void transmit(World world, BlockPos pos) {
        if (world.isClient || !linked) {
            return;
        }
        
        FactorService service = FactorService.getInstance();
        if (service == null) {
            return;
        }
        
        // 计算传输量
        int amountToSend = Math.min(factorStored, 1000); // 每次最多传输 1000
        int received = calculateReceivedAmount(amountToSend, world);
        
        // 消耗发送端 Factor
        factorStored -= amountToSend;
        markDirty();
        
        // 获取目标维度的世界并传输 Factor
        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            net.minecraft.server.MinecraftServer server = serverWorld.getServer();
            net.minecraft.registry.RegistryKey<net.minecraft.world.World> targetKey = 
                parseDimensionKey(targetDimension);
            net.minecraft.server.world.ServerWorld targetWorld = server.getWorld(targetKey);
            
            if (targetWorld != null) {
                // 在目标维度添加 Factor
                service.addFactor(targetWorld, targetPos, received);
            } else {
                // 目标维度未加载，在同一维度添加（降级处理）
                service.addFactor(targetPos, received);
            }
        }
    }
    
    /**
     * 解析维度 ID 为 RegistryKey
     */
    private net.minecraft.registry.RegistryKey<net.minecraft.world.World> parseDimensionKey(String dimId) {
        if (dimId.contains("the_nether")) {
            return net.minecraft.world.World.NETHER;
        } else if (dimId.contains("the_end")) {
            return net.minecraft.world.World.END;
        }
        return net.minecraft.world.World.OVERWORLD;
    }
    
    /**
     * 计算接收端实际收到的 Factor 量
     * 
     * 公式：接收 = 发送 × (发送基准/接收基准) × 效率 × (1-距离损耗)
     */
    private int calculateReceivedAmount(int amount, World world) {
        double senderBase = getSenderDimensionBase(world);
        double receiverBase = getReceiverDimensionBase();
        double efficiency = getEfficiency();
        double distanceLoss = getDistanceLoss(world);
        
        double dimensionRatio = senderBase / receiverBase;
        double finalAmount = amount * dimensionRatio * efficiency * (1 - distanceLoss);
        
        return (int) Math.max(1, finalAmount);
    }
    
    /**
     * 获取发送端维度基准值
     */
    private double getSenderDimensionBase(World world) {
        if (world == null) {
            return 1.0;
        }
        
        DimensionManager dm = DimensionManager.getInstance();
        return dm.getDimensionBaseValue(world.getRegistryKey());
    }
    
    /**
     * 获取接收端维度基准值
     */
    private double getReceiverDimensionBase() {
        DimensionManager dm = DimensionManager.getInstance();
        if (targetDimension.isEmpty()) {
            return 1.0;
        }
        return dm.getDimensionBaseValueFromString(targetDimension);
    }
    
    /**
     * 获取传输效率（根据科技等级）
     */
    private double getEfficiency() {
        return EFFICIENCY_BY_TIER[tier.ordinal()];
    }
    
    /**
     * 获取距离损耗（根据科技等级和实际距离）
     */
    private double getDistanceLoss(World world) {
        if (targetPos == null || world == null) {
            return 0.0;
        }
        
        double distance = Math.sqrt(pos.getSquaredDistance(targetPos));
        double lossPer100 = DISTANCE_LOSS_BY_TIER[tier.ordinal()];
        
        return Math.min(0.5, (distance / 100.0) * lossPer100); // 最多损耗 50%
    }
    
    /**
     * 链接到目标传递器
     */
    public void linkTo(String dimension, BlockPos target) {
        this.targetDimension = dimension;
        this.targetPos = target;
        this.linked = true;
        this.transmitProgress = 0;
        markDirty();
    }
    
    /**
     * 断开链接
     */
    public void unlink() {
        this.targetDimension = "";
        this.targetPos = BlockPos.ORIGIN;
        this.linked = false;
        this.transmitProgress = 0;
        markDirty();
    }
    
    /**
     * 添加 Factor（IFactorNetworkNode 接口）
     */
    @Override
    public double addFactor(double amount, String from) {
        int oldStored = factorStored;
        int maxStorage = (int) getMaxFactorStorage();
        factorStored = Math.min(maxStorage, factorStored + (int) amount);
        int actual = factorStored - oldStored;
        markDirty();
        return actual;
    }
    
    /**
     * 抽取 Factor（IFactorNetworkNode 接口）
     */
    @Override
    public double extractFactor(double amount, String to) {
        int actual = Math.min(factorStored, (int) amount);
        factorStored -= actual;
        markDirty();
        return actual;
    }
    
    /**
     * 获取当前存储的 Factor
     */
    public int getFactorStored() {
        return factorStored;
    }
    
    /**
     * 获取传输进度（0-100）
     */
    public int getTransmitProgressPercent() {
        return (transmitProgress * 100) / TRANSMIT_TIME;
    }
    
    /**
     * 获取科技等级
     */
    public FactorTier getTier() {
        return tier;
    }
    
    /**
     * 是否已链接
     */
    public boolean isLinked() {
        return linked;
    }
    
    /**
     * 获取目标位置
     */
    public BlockPos getTargetPos() {
        return targetPos;
    }
    
    /**
     * 获取目标维度
     */
    public String getTargetDimension() {
        return targetDimension;
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
        return NodeType.TRANSMITTER;
    }
    
    @Override
    public double getFactorStorage() {
        return factorStored;
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 100000.0; // 最大存储 100K
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
