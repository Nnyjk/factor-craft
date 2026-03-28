package com.factorcraft.module.logistics.pipe;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.UUID;

/**
 * 单向管道 BlockEntity
 * 
 * 功能：
 * - Factor 只能从输入端流向输出端
 * - 防止回流
 */
public class OneWayPipeBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 输入方向 */
    private Direction inputSide = Direction.DOWN;
    
    /** 输出方向 */
    private Direction outputSide = Direction.UP;
    
    /** 当前缓存的 Factor 量 */
    private double cachedFactor = 0;
    
    /** 传输进度（0-100） */
    private int transferProgress = 0;
    
    public OneWayPipeBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsPipes.ONE_WAY_PIPE_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.getString("NodeId");
        this.inputSide = Direction.byId(nbt.getInt("InputSide"));
        this.outputSide = Direction.byId(nbt.getInt("OutputSide"));
        this.cachedFactor = nbt.getDouble("CachedFactor");
        this.transferProgress = nbt.getInt("TransferProgress");
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        nbt.putInt("InputSide", inputSide.getId());
        nbt.putInt("OutputSide", outputSide.getId());
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
     * 每 tick 更新（静态方法，用于 BlockEntityTicker）
     */
    public static void tick(World world, BlockPos pos, BlockState state, OneWayPipeBlockEntity entity) {
        if (world.isClient) return;
        
        if (entity.transferProgress > 0 && entity.cachedFactor > 0) {
            entity.transferProgress += 5;
            if (entity.transferProgress >= 100) {
                entity.completeTransfer(world, pos, state);
            }
            entity.markDirty();
        }
    }
    
    /**
     * 完成传输
     */
    private void completeTransfer(World world, BlockPos pos, BlockState state) {
        BlockPos outputPos = pos.offset(outputSide);
        BlockEntity target = world.getBlockEntity(outputPos);
        
        if (target instanceof IFactorNetworkNode node) {
            double transferred = node.addFactor(cachedFactor, nodeId);
            this.cachedFactor -= transferred;
        }
        
        this.transferProgress = 0;
        markDirty();
    }
    
    /**
     * 检查方向是否为输入端
     */
    public boolean isInputSide(Direction side) {
        return side == inputSide;
    }
    
    /**
     * 检查方向是否为输出端
     */
    public boolean isOutputSide(Direction side) {
        return side == outputSide;
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
        // 只允许从输入端添加
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
        // 只允许从输出端抽取
        double canExtract = Math.min(amount, cachedFactor);
        cachedFactor -= canExtract;
        markDirty();
        return canExtract;
    }
    
    @Override
    public double getTransferRate() {
        return 10.0;
    }
    
    // Getters and Setters
    
    public Direction getInputSide() {
        return inputSide;
    }
    
    public void setInputSide(Direction side) {
        this.inputSide = side;
        markDirty();
    }
    
    public Direction getOutputSide() {
        return outputSide;
    }
    
    public void setOutputSide(Direction side) {
        this.outputSide = side;
        markDirty();
    }
    
    public int getTransferProgress() {
        return transferProgress;
    }
}
