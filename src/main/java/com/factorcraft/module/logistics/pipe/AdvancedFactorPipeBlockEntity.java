package com.factorcraft.module.logistics.pipe;

import com.factorcraft.api.IFactorNetworkNode;
import com.factorcraft.factor.FactorType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

/**
 * 智能管道 BlockEntity
 * 
 * 功能：
 * - 路由配置（输入/输出方向）
 * - 优先级设置
 * - 流量监控
 */
public class AdvancedFactorPipeBlockEntity extends BlockEntity implements IFactorNetworkNode {
    
    /** 节点唯一 ID */
    private String nodeId = UUID.randomUUID().toString();
    
    /** 输入方向集合 */
    private Set<Direction> inputSides = EnumSet.noneOf(Direction.class);
    
    /** 输出方向集合 */
    private Set<Direction> outputSides = EnumSet.noneOf(Direction.class);
    
    /** 优先级（0=低，1=中，2=高） */
    private int priority = 1;
    
    /** 当前传输的 Factor 量 */
    private double currentFactorAmount = 0;
    
    /** 传输进度（0-100） */
    private int transferProgress = 0;
    
    /** 过滤器（允许通过的 Factor 类型） */
    private Set<String> filter = new HashSet<>();
    
    public AdvancedFactorPipeBlockEntity(BlockPos pos, BlockState state) {
        super(LogisticsPipes.ADVANCED_PIPE_ENTITY, pos, state);
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        
        this.nodeId = nbt.getString("NodeId");
        this.currentFactorAmount = nbt.getDouble("FactorAmount");
        this.priority = nbt.getInt("Priority");
        this.transferProgress = nbt.getInt("TransferProgress");
        
        // 读取输入输出方向
        int[] inputDirs = nbt.getIntArray("InputSides");
        this.inputSides = Arrays.stream(inputDirs)
            .mapToObj(Direction::byId)
            .collect(java.util.stream.Collectors.toSet());
        
        int[] outputDirs = nbt.getIntArray("OutputSides");
        this.outputSides = Arrays.stream(outputDirs)
            .mapToObj(Direction::byId)
            .collect(java.util.stream.Collectors.toSet());
        
        // 读取过滤器
        this.filter = new HashSet<>();
        net.minecraft.nbt.NbtList filterList = nbt.getList("Filter", 8);
        for (int i = 0; i < filterList.size(); i++) {
            this.filter.add(filterList.getString(i));
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        
        nbt.putString("NodeId", this.nodeId);
        nbt.putDouble("FactorAmount", this.currentFactorAmount);
        nbt.putInt("Priority", this.priority);
        nbt.putInt("TransferProgress", this.transferProgress);
        
        // 写入输入输出方向
        nbt.putIntArray("InputSides", 
            this.inputSides.stream().mapToInt(Direction::getId).toArray());
        nbt.putIntArray("OutputSides", 
            this.outputSides.stream().mapToInt(Direction::getId).toArray());
        
        // 写入过滤器
        net.minecraft.nbt.NbtList filterList = new net.minecraft.nbt.NbtList();
        for (String f : this.filter) {
            filterList.add(net.minecraft.nbt.NbtString.of(f));
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
     * 每 tick 更新传输状态
     */
    public void tick() {
        if (world == null || world.isClient) return;
        
        // 传输逻辑
        if (transferProgress > 0 && currentFactorAmount > 0) {
            transferProgress++;
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
        if (world != null) {
            // 输出 Factor 到相邻 BlockEntity
            BlockPos outputPos = pos.offset(getFirstOutputDirection());
            BlockEntity target = world.getBlockEntity(outputPos);
            
            if (target instanceof IFactorNetworkNode node) {
                double transferred = node.addFactor(currentFactorAmount, nodeId);
                this.currentFactorAmount -= transferred;
            }
        }
        
        this.transferProgress = 0;
        markDirty();
    }
    
    private Direction getFirstOutputDirection() {
        return outputSides.isEmpty() ? Direction.UP : outputSides.iterator().next();
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
        return currentFactorAmount;
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 1000.0; // 管道缓存容量
    }
    
    @Override
    public double addFactor(double amount, String from) {
        if (!filter.isEmpty() && !filter.contains(from)) {
            return 0; // 过滤器阻止
        }
        
        double canAdd = Math.min(amount, getMaxFactorStorage() - currentFactorAmount);
        this.currentFactorAmount += canAdd;
        
        if (canAdd > 0 && !outputSides.isEmpty()) {
            this.transferProgress = 1;
        }
        
        markDirty();
        return canAdd;
    }
    
    @Override
    public double extractFactor(double amount, String to) {
        double canExtract = Math.min(amount, currentFactorAmount);
        this.currentFactorAmount -= canExtract;
        markDirty();
        return canExtract;
    }
    
    @Override
    public double getTransferRate() {
        return 10.0 * priority; // 优先级越高，传输越快
    }
    
    // Getters and Setters
    
    public Set<Direction> getInputSides() {
        return inputSides;
    }
    
    public void setInputSides(Set<Direction> sides) {
        this.inputSides = sides;
        markDirty();
    }
    
    public Set<Direction> getOutputSides() {
        return outputSides;
    }
    
    public void setOutputSides(Set<Direction> sides) {
        this.outputSides = sides;
        markDirty();
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
        markDirty();
    }
    
    public int getTransferProgress() {
        return transferProgress;
    }
    
    public Set<String> getFilter() {
        return Collections.unmodifiableSet(filter);
    }
    
    public void setFilter(Set<String> filter) {
        this.filter = new HashSet<>(filter);
        markDirty();
    }
}
