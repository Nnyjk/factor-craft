package com.factorcraft.module.logistics.pipe;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * 优先级管道 BlockEntity
 * 
 * 功能：
 * - 优先级 1-10（10 最高）
 * - 高优先级的管道优先传输
 */
public class PriorityPipeBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 优先级（1-10） */
    private int priority = 5;
    
    /** 当前缓存的 Factor 量 */
    private double cachedFactor = 0;
    
    /** 传输进度（0-100） */
    private int transferProgress = 0;
    
    public PriorityPipeBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsPipes.PRIORITY_PIPE_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.contains("NodeId", 8) ? nbt.getString("NodeId") : UUID.randomUUID().toString();
        this.priority = nbt.contains("Priority", 3) ? nbt.getInt("Priority") : 5;
        this.cachedFactor = nbt.contains("CachedFactor", 6) ? nbt.getDouble("CachedFactor") : 0.0;
        this.transferProgress = nbt.contains("TransferProgress", 3) ? nbt.getInt("TransferProgress") : 0;
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        nbt.putInt("Priority", this.priority);
        nbt.putDouble("CachedFactor", this.cachedFactor);
        nbt.putInt("TransferProgress", this.transferProgress);
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
        
        if (transferProgress > 0 && cachedFactor > 0) {
            transferProgress += priority; // 优先级越高，传输越快
            if (transferProgress >= 100) {
                completeTransfer();
            }
            markDirty();
        }
    }
    
    /**
     * 完成传输
     */
    private void completeTransfer() {
        // TODO: 实现传输逻辑
        this.transferProgress = 0;
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
        return 1000.0;
    }
    
    @Override
    public double addFactor(double amount, String from) {
        double canAdd = Math.min(amount, getMaxFactorStorage() - cachedFactor);
        cachedFactor += canAdd;
        if (canAdd > 0) {
            transferProgress = 1;
        }
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
        return priority * 2.0; // 优先级影响传输速率
    }
    
    // Getters and Setters
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = Math.max(1, Math.min(10, priority));
        markDirty();
    }
    
    public int getTransferProgress() {
        return transferProgress;
    }
}
