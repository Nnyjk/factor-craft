package com.factorcraft.module.logistics.storage;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.factor.FactorType;
import com.factorcraft.module.logistics.storage.LogisticsStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
 * 存储监控器 BlockEntity
 * 
 * 功能：
 * - 显示连接的 Factor 网络中的 Factor 存储总量
 * - 支持搜索功能
 * - 只读访问，不存储 Factor
 */
public class StorageMonitorBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 缓存的 Factor 数据 */
    private Map<FactorType, Integer> factorCache = new HashMap<>();
    
    /** 搜索过滤器 */
    private String searchFilter = "";
    
    /** 最后更新时间 */
    private long lastUpdateTime = 0;
    
    public StorageMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsStorage.STORAGE_MONITOR_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.contains("NodeId", 8) ? nbt.getString("NodeId") : UUID.randomUUID().toString();
        this.searchFilter = nbt.contains("SearchFilter", 8) ? nbt.getString("SearchFilter") : "";
        this.lastUpdateTime = nbt.contains("LastUpdateTime", 4) ? nbt.getLong("LastUpdateTime") : 0L;
        
        // 读取 Factor 缓存
        this.factorCache = new HashMap<>();
        if (nbt.contains("FactorCache", 10)) {
            NbtCompound factorNbt = nbt.getCompound("FactorCache");
            for (String key : factorNbt.getKeys()) {
                try {
                    FactorType type = FactorType.valueOf(key);
                    factorCache.put(type, factorNbt.getInt(key));
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
        nbt.putString("SearchFilter", this.searchFilter);
        nbt.putLong("LastUpdateTime", this.lastUpdateTime);
        
        // 写入 Factor 缓存
        NbtCompound factorNbt = new NbtCompound();
        for (Map.Entry<FactorType, Integer> entry : factorCache.entrySet()) {
            factorNbt.putInt(entry.getKey().name(), entry.getValue());
        }
        nbt.put("FactorCache", factorNbt);
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
     * 更新 Factor 缓存
     */
    public void updateFactorCache(Map<FactorType, Integer> newData) {
        this.factorCache = new HashMap<>(newData);
        this.lastUpdateTime = System.currentTimeMillis();
        markDirty();
    }
    
    /**
     * 获取过滤后的 Factor 数据
     */
    public Map<FactorType, Integer> getFilteredFactorData() {
        if (searchFilter.isEmpty()) {
            return factorCache;
        }
        
        Map<FactorType, Integer> filtered = new HashMap<>();
        for (Map.Entry<FactorType, Integer> entry : factorCache.entrySet()) {
            if (entry.getKey().name().toLowerCase().contains(searchFilter.toLowerCase())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
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
        return NodeType.SINK; // 监控器是数据接收器
    }
    
    @Override
    public double getFactorStorage() {
        return 0; // 监控器不存储 Factor
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 0;
    }
    
    @Override
    public double addFactor(double amount, String from) {
        return 0; // 监控器不接收 Factor
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        return 0; // 监控器不提供 Factor
    }
    
    @Override
    public double getTransferRate() {
        return 0; // 监控器不传输 Factor
    }
    
    // Getters and Setters
    
    public Map<FactorType, Integer> getFactorCache() {
        return factorCache;
    }
    
    public String getSearchFilter() {
        return searchFilter;
    }
    
    public void setSearchFilter(String filter) {
        this.searchFilter = filter;
        markDirty();
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    /**
     * BlockEntity tick 方法
     */
    public static void tick(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, 
                           net.minecraft.block.BlockState state, StorageMonitorBlockEntity blockEntity) {
        if (world.isClient) return;
        // 监控器 tick 逻辑：更新缓存数据
        // 这里可以添加网络扫描逻辑
    }
}
