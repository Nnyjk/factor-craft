package com.factorcraft.module.logistics.storage;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.module.logistics.storage.LogisticsStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.UUID;

/**
 * 存储总线 BlockEntity
 * 
 * 功能：
 * - 连接外部容器到 Factor 网络
 * - 自动导入/导出 Factor
 * - 配置方向
 */
public class StorageBusBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 操作模式：IMPORT=导入，EXPORT=导出，BOTH=双向 */
    public enum Mode {
        IMPORT, EXPORT, BOTH
    }
    
    private Mode mode = Mode.BOTH;
    
    /** 传输方向 */
    private Direction facing = Direction.UP;
    
    /** 当前缓存的 Factor 量 */
    private double cachedFactor = 0;
    
    /** 传输速率 */
    private double transferRate = 10.0;
    
    public StorageBusBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsStorage.STORAGE_BUS_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.contains("NodeId", 8) ? nbt.getString("NodeId") : UUID.randomUUID().toString();
        this.mode = nbt.contains("Mode", 8) ? Mode.valueOf(nbt.getString("Mode")) : Mode.BOTH;
        this.facing = nbt.contains("Facing", 3) ? Direction.byId(nbt.getInt("Facing")) : Direction.NORTH;
        this.cachedFactor = nbt.contains("CachedFactor", 6) ? nbt.getDouble("CachedFactor") : 0.0;
        this.transferRate = nbt.contains("TransferRate", 6) ? nbt.getDouble("TransferRate") : 10.0;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        nbt.putString("Mode", mode.name());
        nbt.putInt("Facing", facing.getId());
        nbt.putDouble("CachedFactor", cachedFactor);
        nbt.putDouble("TransferRate", transferRate);
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
     * 每 tick 更新
     */
    public void tick() {
        if (world == null || world.isClient) return;
        
        // 尝试与相邻容器交互
        BlockPos targetPos = pos.offset(facing);
        BlockEntity target = world.getBlockEntity(targetPos);
        
        if (target instanceof IFactorNetworkNode node) {
            if (mode == Mode.IMPORT || mode == Mode.BOTH) {
                // 从目标导入
                double extracted = node.extractFactor(transferRate, nodeId);
                if (extracted > 0) {
                    cachedFactor += extracted;
                }
            }
            
            if (mode == Mode.EXPORT || mode == Mode.BOTH) {
                // 向目标导出
                double canExport = Math.min(cachedFactor, transferRate);
                if (canExport > 0) {
                    double added = node.addFactor(canExport, nodeId);
                    cachedFactor -= added;
                }
            }
        }
        
        markDirty();
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
        return 1000.0; // 总线缓存容量
    }
    
    @Override
    public double addFactor(double amount, String from) {
        double canAdd = Math.min(amount, getMaxFactorStorage() - cachedFactor);
        cachedFactor += canAdd;
        markDirty();
        return canAdd;
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        double canExtract = Math.min(amount, cachedFactor);
        cachedFactor -= canExtract;
        markDirty();
        return canExtract;
    }
    
    @Override
    public double getTransferRate() {
        return transferRate;
    }
    
    // Getters and Setters
    
    public Mode getMode() {
        return mode;
    }
    
    public void setMode(Mode mode) {
        this.mode = mode;
        markDirty();
    }
    
    public Direction getFacing() {
        return facing;
    }
    
    public void setFacing(Direction facing) {
        this.facing = facing;
        markDirty();
    }
    
    public void setTransferRate(double rate) {
        this.transferRate = rate;
        markDirty();
    }
}
