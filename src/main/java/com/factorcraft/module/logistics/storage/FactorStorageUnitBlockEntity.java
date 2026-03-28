package com.factorcraft.module.logistics.storage;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.factor.FactorType;
import com.factorcraft.module.logistics.storage.LogisticsStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Factor 存储单元 BlockEntity
 * 
 * 功能：
 * - 每种 Factor 类型独立存储 100 万容量
 * - 支持网络访问
 * - 提供存储监控
 */
public class FactorStorageUnitBlockEntity extends BlockEntity implements IFactorNetworkNode, Inventory {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 存储的 Factor 数据 <FactorType, Amount> */
    private Map<FactorType, Long> storedFactors = new HashMap<>();
    
    /** 最大存储容量 */
    private static final long MAX_CAPACITY = 1_000_000L;
    
    public FactorStorageUnitBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsStorage.STORAGE_UNIT_ENTITY, pos, state);
        
        // 初始化所有 Factor 类型为 0
        for (FactorType type : FactorType.values()) {
            storedFactors.put(type, 0L);
        }
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.contains("NodeId", 8) ? nbt.getString("NodeId") : UUID.randomUUID().toString();
        
        // 读取存储的 Factor
        this.storedFactors = new HashMap<>();
        if (nbt.contains("StoredFactors", 10)) {
            NbtCompound factorsNbt = nbt.getCompound("StoredFactors");
            for (String key : factorsNbt.getKeys()) {
                try {
                    FactorType type = FactorType.valueOf(key);
                    storedFactors.put(type, factorsNbt.getLong(key));
                } catch (IllegalArgumentException e) {
                    // 忽略未知的 Factor 类型
                }
            }
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        
        // 写入存储的 Factor
        NbtCompound factorsNbt = new NbtCompound();
        for (Map.Entry<FactorType, Long> entry : storedFactors.entrySet()) {
            factorsNbt.putLong(entry.getKey().name(), entry.getValue());
        }
        nbt.put("StoredFactors", factorsNbt);
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
    
    /**
     * 添加 Factor 到存储
     */
    public long addFactor(FactorType type, long amount) {
        long current = storedFactors.getOrDefault(type, 0L);
        long canAdd = Math.min(amount, MAX_CAPACITY - current);
        storedFactors.put(type, current + canAdd);
        markDirty();
        return canAdd;
    }
    
    /**
     * 从存储中抽取 Factor
     */
    public long extractFactor(FactorType type, long amount) {
        long current = storedFactors.getOrDefault(type, 0L);
        long canExtract = Math.min(amount, current);
        storedFactors.put(type, current - canExtract);
        markDirty();
        return canExtract;
    }
    
    /**
     * 获取指定 Factor 类型的存储量
     */
    public long getStoredAmount(FactorType type) {
        return storedFactors.getOrDefault(type, 0L);
    }
    
    /**
     * 获取存储利用率（百分比）
     */
    public double getUtilizationRate() {
        long total = storedFactors.values().stream().mapToLong(Long::longValue).sum();
        return (double) total / (MAX_CAPACITY * FactorType.values().length) * 100.0;
    }
    
    // IFactorNetworkNode 实现
    
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
        return NodeType.SOURCE; // 存储单元是 Factor 源
    }
    
    @Override
    public double getFactorStorage() {
        return storedFactors.values().stream().mapToLong(Long::longValue).sum();
    }
    
    @Override
    public double getMaxFactorStorage() {
        return (double) MAX_CAPACITY * FactorType.values().length;
    }
    
    @Override
    public double addFactor(double amount, String from) {
        // 简化处理，添加到第一个有空间的 Factor 类型
        for (FactorType type : FactorType.values()) {
            long current = storedFactors.getOrDefault(type, 0L);
            if (current < MAX_CAPACITY) {
                long added = addFactor(type, (long) amount);
                if (added > 0) {
                    return added;
                }
            }
        }
        return 0;
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        // 简化处理，从第一个有存储的 Factor 类型抽取
        for (FactorType type : FactorType.values()) {
            long current = storedFactors.getOrDefault(type, 0L);
            if (current > 0) {
                long extracted = extractFactor(type, (long) amount);
                if (extracted > 0) {
                    return extracted;
                }
            }
        }
        return 0;
    }
    
    @Override
    public double getTransferRate() {
        return 100.0; // 存储单元传输速率
    }
    
    // Inventory 实现（用于 GUI 交互）
    
    @Override
    public int size() {
        return FactorType.values().length;
    }
    
    @Override
    public boolean isEmpty() {
        return storedFactors.values().stream().allMatch(amount -> amount == 0);
    }
    
    @Override
    public net.minecraft.item.ItemStack getStack(int slot) {
        return net.minecraft.item.ItemStack.EMPTY; // 存储单元不直接存储物品
    }
    
    @Override
    public net.minecraft.item.ItemStack removeStack(int slot, int amount) {
        return net.minecraft.item.ItemStack.EMPTY;
    }
    
    @Override
    public net.minecraft.item.ItemStack removeStack(int slot) {
        return net.minecraft.item.ItemStack.EMPTY;
    }
    
    @Override
    public void setStack(int slot, net.minecraft.item.ItemStack stack) {
        // 存储单元不直接存储物品
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(pos.toCenterPos()) <= 64.0;
    }
    
    @Override
    public void clear() {
        for (FactorType type : FactorType.values()) {
            storedFactors.put(type, 0L);
        }
        markDirty();
    }
    
    /**
     * BlockEntity tick 方法
     */
    public static void tick(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, 
                           net.minecraft.block.BlockState state, FactorStorageUnitBlockEntity blockEntity) {
        if (world.isClient) return;
        // 存储单元 tick 逻辑：处理 Factor 输入/输出
        // 这里可以添加自动输入/输出逻辑
    }
}
