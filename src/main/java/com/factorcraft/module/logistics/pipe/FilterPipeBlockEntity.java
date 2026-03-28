package com.factorcraft.module.logistics.pipe;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.factor.FactorType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * 过滤管道 BlockEntity
 * 
 * 功能：
 * - 白名单/黑名单过滤
 * - 只允许特定 Factor 类型通过
 */
public class FilterPipeBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 过滤模式：WHITELIST=白名单，BLACKLIST=黑名单 */
    public enum FilterMode {
        WHITELIST, BLACKLIST
    }
    
    private FilterMode filterMode = FilterMode.WHITELIST;
    
    /** 过滤器（Factor 类型名称列表） */
    private Set<String> filter = new HashSet<>();
    
    /** 当前缓存的 Factor 量 */
    private double cachedFactor = 0;
    
    /** 当前缓存的 Factor 类型 */
    private FactorType cachedFactorType = null;
    
    public FilterPipeBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsPipes.FILTER_PIPE_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.contains("NodeId", 8) ? nbt.getString("NodeId") : UUID.randomUUID().toString();
        this.filterMode = nbt.contains("FilterMode", 8) ? FilterMode.valueOf(nbt.getString("FilterMode")) : FilterMode.WHITELIST;
        this.cachedFactor = nbt.contains("CachedFactor", 6) ? nbt.getDouble("CachedFactor") : 0.0;
        
        if (nbt.contains("CachedFactorType", 8)) {
            String factorTypeName = nbt.getString("CachedFactorType");
            if (!factorTypeName.isEmpty()) {
                try {
                    this.cachedFactorType = FactorType.valueOf(factorTypeName);
                } catch (IllegalArgumentException e) {
                    this.cachedFactorType = null;
                }
            }
        }
        
        // 读取过滤器
        this.filter = new HashSet<>();
        NbtList filterList = nbt.getList("Filter", 8);
        for (int i = 0; i < filterList.size(); i++) {
            this.filter.add(filterList.getString(i));
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        nbt.putString("FilterMode", filterMode.name());
        nbt.putDouble("CachedFactor", this.cachedFactor);
        
        if (cachedFactorType != null) {
            nbt.putString("CachedFactorType", cachedFactorType.name());
        }
        
        // 写入过滤器
        NbtList filterList = new NbtList();
        for (String f : filter) {
            filterList.add(NbtString.of(f));
        }
        nbt.put("Filter", filterList);
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
     * 检查 Factor 类型是否可以通过过滤器
     */
    public boolean canPass(FactorType type) {
        if (filter.isEmpty()) {
            return filterMode == FilterMode.BLACKLIST; // 空过滤器：黑名单允许所有，白名单阻止所有
        }
        
        boolean contains = filter.contains(type.name());
        return filterMode == FilterMode.WHITELIST ? contains : !contains;
    }
    
    /**
     * 尝试添加 Factor
     */
    public double tryAddFactor(FactorType type, double amount) {
        if (!canPass(type)) {
            return 0; // 被过滤器阻止
        }
        
        double canAdd = Math.min(amount, getMaxFactorStorage() - cachedFactor);
        if (canAdd > 0) {
            cachedFactor += canAdd;
            if (cachedFactorType == null || cachedFactorType == type) {
                cachedFactorType = type;
            }
            markDirty();
        }
        return canAdd;
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
        return NodeType.TRANSMITTER;
    }
    
    @Override
    public double getFactorStorage() {
        return cachedFactor;
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 1000.0;
    }
    
    @Override
    public double addFactor(double amount, String from) {
        // 从字符串解析 FactorType（简化处理）
        try {
            FactorType type = FactorType.valueOf(from.toUpperCase());
            return tryAddFactor(type, amount);
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        double canExtract = Math.min(amount, cachedFactor);
        cachedFactor -= canExtract;
        if (cachedFactor <= 0) {
            cachedFactorType = null;
        }
        markDirty();
        return canExtract;
    }
    
    @Override
    public double getTransferRate() {
        return 10.0;
    }
    
    // Getters and Setters
    
    public FilterMode getFilterMode() {
        return filterMode;
    }
    
    public void setFilterMode(FilterMode mode) {
        this.filterMode = mode;
        markDirty();
    }
    
    public Set<String> getFilter() {
        return Collections.unmodifiableSet(filter);
    }
    
    public void setFilter(Set<String> filter) {
        this.filter = new HashSet<>(filter);
        markDirty();
    }
    
    public FactorType getCachedFactorType() {
        return cachedFactorType;
    }
    
    /**
     * BlockEntity tick 方法
     */
    public static void tick(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, 
                           net.minecraft.block.BlockState state, FilterPipeBlockEntity blockEntity) {
        if (world.isClient) return;
        // 管道 tick 逻辑：处理 Factor 传输
        // 这里可以添加传输逻辑
    }
}
