package com.factorcraft.module.technology.machine;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 传递器方块实体 - 跨维度传输 Factor
 * 
 * T1: 基础传递器 - 仅同维度传输，效率 80%
 * T2: 维度传递器 - 跨维度传输，效率 85%
 * T3: 远古传递器 - 跨维度传输，效率 90%
 * T4: 仲裁传递器 - 跨维度传输，效率 95%
 * 
 * 传输公式:
 * 接收 = 发送 × 维度倍率 × 传递器效率 × (1 - 距离损耗)
 */
public class TransmitterBlockEntity extends MachineBlockEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/Transmitter");
    
    // ==================== 状态 ====================
    
    private double buffer;
    private double maxBuffer;
    private int tier;
    
    // 链接目标
    private UUID linkedTransmitterId;
    private BlockPos linkedPos;
    private String linkedDimension;
    
    // 冷却
    private int cooldownRemaining;
    
    // 统计
    private double totalTransmitted;
    private int transferCount;
    
    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModMachines.TRANSMITTER, pos, state);
        this.buffer = 0.0;
        this.maxBuffer = TransmitterConfig.BUFFER_T1;
        this.tier = 1;
        this.cooldownRemaining = 0;
        this.totalTransmitted = 0;
        this.transferCount = 0;
    }
    
    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        
        // 冷却计时
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
        
        markDirty();
    }
    
    /**
     * 尝试传输 Factor 到链接的传递器
     * 
     * @param amount 请求传输量
     * @return 实际传输量（0 表示失败）
     */
    public double transmit(double amount) {
        if (world == null || world.isClient) {
            return 0;
        }
        
        ServerWorld fromWorld = (ServerWorld) world;
        
        // 检查冷却
        if (cooldownRemaining > 0) {
            LOGGER.debug("传递器冷却中：{} ticks 剩余", cooldownRemaining);
            return 0;
        }
        
        // 检查链接
        if (linkedPos == null || linkedDimension == null) {
            LOGGER.debug("未链接到目标传递器");
            return 0;
        }
        
        // 限制传输量
        double actualAmount = Math.min(amount, buffer);
        actualAmount = Math.min(actualAmount, TransmitterConfig.getMaxTransfer(tier));
        
        if (actualAmount <= 0) {
            return 0;
        }
        
        // 获取维度信息
        String fromDimension = fromWorld.getRegistryKey().getValue().toString();
        
        // 检查是否可以传输
        if (!TransmitterConfig.canTransfer(tier, fromDimension, linkedDimension)) {
            LOGGER.debug("T1 传递器无法跨维度传输");
            return 0;
        }
        
        // 计算距离
        double distance = 0;
        if (fromDimension.equals(linkedDimension)) {
            distance = this.pos.getSquaredDistance(linkedPos);
        }
        
        // 计算实际接收量
        double received;
        if (fromDimension.equals(linkedDimension)) {
            received = TransmitterConfig.calculateSameDimensionTransfer(actualAmount, tier, distance);
        } else {
            received = TransmitterConfig.calculateTransfer(actualAmount, fromDimension, linkedDimension, tier, 0);
        }
        
        // 获取目标维度世界
        ServerWorld toWorld = getTargetWorld(fromWorld);
        if (toWorld == null) {
            LOGGER.error("无法找到目标维度：{}", linkedDimension);
            markDirty();
            return 0;
        }
        
        // 从缓冲区扣除
        buffer -= actualAmount;
        
        // 在目标位置添加 Factor
        boolean delivered = deliverFactorToWorld(toWorld, received);
        
        if (delivered) {
            // 更新统计
            totalTransmitted += received;
            transferCount++;
            
            // 设置冷却
            cooldownRemaining = TransmitterConfig.getCooldown(tier);
            
            LOGGER.info("传输完成：{} Factor (发送) → {} Factor (接收), 维度：{} → {}", 
                actualAmount, received, fromDimension, linkedDimension);
        } else {
            // 传输失败，返还 Factor
            buffer += actualAmount;
            LOGGER.warn("传输失败：无法在目标位置添加 Factor");
        }
        
        markDirty();
        return delivered ? received : 0;
    }
    
    /**
     * 获取目标维度世界
     */
    private ServerWorld getTargetWorld(ServerWorld currentWorld) {
        if (linkedDimension == null) {
            return null;
        }
        
        net.minecraft.util.Identifier dimId = net.minecraft.util.Identifier.tryParse(linkedDimension);
        if (dimId == null) {
            return null;
        }
        
        net.minecraft.registry.RegistryKey<net.minecraft.world.World> worldKey = 
            net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, dimId);
        
        return currentWorld.getServer().getWorld(worldKey);
    }
    
    /**
     * 在目标世界添加 Factor
     * 
     * @param world 目标世界
     * @param amount Factor 数量
     * @return 是否成功添加
     */
    private boolean deliverFactorToWorld(ServerWorld world, double amount) {
        if (world == null || linkedPos == null) {
            return false;
        }
        
        // 尝试在目标位置找到 TransmitterBlockEntity
        net.minecraft.block.entity.BlockEntity blockEntity = world.getBlockEntity(linkedPos);
        
        if (blockEntity instanceof TransmitterBlockEntity targetTransmitter) {
            // 验证链接是否匹配（双向链接检查）
            if (targetTransmitter.getLinkedPos() != null && 
                targetTransmitter.getLinkedDimension() != null &&
                targetTransmitter.getLinkedPos().equals(this.pos) &&
                targetTransmitter.getLinkedDimension().equals(world.getRegistryKey().getValue().toString())) {
                
                // 链接匹配，添加 Factor
                targetTransmitter.receive(amount);
                LOGGER.debug("Factor 已添加到目标传递器：{}", amount);
                return true;
            } else {
                LOGGER.warn("目标传递器链接不匹配");
                return false;
            }
        } else {
            // 目标位置没有传递器，直接添加到区块 Factor 浓度
            LOGGER.debug("目标位置无传递器，直接添加到区块 Factor 浓度");
            net.minecraft.util.math.ChunkPos chunkPos = new net.minecraft.util.math.ChunkPos(linkedPos);
            com.factorcraft.module.factor.management.ChunkFactorManager.injectFactor(world, chunkPos, amount);
            return true;
        }
    }
    
    /**
     * 接收 Factor
     */
    public void receive(double amount) {
        buffer = Math.min(maxBuffer, buffer + amount);
        markDirty();
    }
    
    /**
     * 向缓冲区添加 Factor
     */
    public void addToBuffer(double amount) {
        buffer = Math.min(maxBuffer, buffer + amount);
        markDirty();
    }
    
    /**
     * 从缓冲区提取 Factor
     */
    public double extractFromBuffer(double amount) {
        double actual = Math.min(buffer, amount);
        buffer -= actual;
        markDirty();
        return actual;
    }
    
    /**
     * 链接到目标传递器
     */
    public boolean linkTo(BlockPos targetPos, String targetDimension) {
        if (targetPos == null || targetDimension == null) {
            return false;
        }
        
        // T1 只能链接同维度
        String currentDimension = world != null ? world.getRegistryKey().getValue().toString() : "";
        if (tier == 1 && !currentDimension.equals(targetDimension)) {
            LOGGER.debug("T1 传递器无法链接到其他维度");
            return false;
        }
        
        this.linkedPos = targetPos;
        this.linkedDimension = targetDimension;
        this.linkedTransmitterId = UUID.randomUUID(); // 生成新的链接 ID
        
        LOGGER.info("传递器已链接：{} → {} @ {}", pos, targetPos, targetDimension);
        markDirty();
        return true;
    }
    
    /**
     * 解除链接
     */
    public void unlink() {
        this.linkedTransmitterId = null;
        this.linkedPos = null;
        this.linkedDimension = null;
        LOGGER.info("传递器链接已解除");
        markDirty();
    }
    
    /**
     * 升级传递器等级
     */
    public boolean upgrade() {
        if (tier >= 4) {
            return false; // 已是最高等级
        }
        
        tier++;
        maxBuffer = TransmitterConfig.getBuffer(tier);
        LOGGER.info("传递器升级到 T{}", tier);
        markDirty();
        return true;
    }
    
    // ==================== Getters ====================
    
    public double getBuffer() { return buffer; }
    public double getMaxBuffer() { return maxBuffer; }
    public int getTier() { return tier; }
    public boolean isLinked() { return linkedPos != null; }
    public BlockPos getLinkedPos() { return linkedPos; }
    public String getLinkedDimension() { return linkedDimension; }
    public int getCooldownRemaining() { return cooldownRemaining; }
    public double getTotalTransmitted() { return totalTransmitted; }
    public int getTransferCount() { return transferCount; }
    public UUID getLinkedTransmitterId() { return linkedTransmitterId; }
    
    /**
     * 获取缓冲区百分比
     */
    public double getBufferPercentage() {
        return maxBuffer > 0 ? (buffer / maxBuffer) * 100 : 0;
    }
    
    /**
     * 是否可以传输
     */
    public boolean canTransmit() {
        return cooldownRemaining == 0 && buffer > 0 && isLinked();
    }
    
    /**
     * 获取调试信息
     */
    public String getDebugInfo() {
        String linkInfo = isLinked() ? 
            String.format("→ %s @ %s", linkedPos, linkedDimension) : "未链接";
        String cooldownInfo = cooldownRemaining > 0 ? 
            String.format(" (冷却：%d)", cooldownRemaining) : "";
        
        return String.format("T%d | %.0f/%.0f F | %s%s",
            tier, buffer, maxBuffer, linkInfo, cooldownInfo);
    }
    
    // ==================== NBT ====================
    
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putDouble("Buffer", buffer);
        nbt.putDouble("MaxBuffer", maxBuffer);
        nbt.putInt("Tier", tier);
        nbt.putInt("CooldownRemaining", cooldownRemaining);
        nbt.putDouble("TotalTransmitted", totalTransmitted);
        nbt.putInt("TransferCount", transferCount);
        
        // 链接信息
        if (linkedPos != null) {
            nbt.putInt("LinkedX", linkedPos.getX());
            nbt.putInt("LinkedY", linkedPos.getY());
            nbt.putInt("LinkedZ", linkedPos.getZ());
        }
        if (linkedDimension != null) {
            nbt.putString("LinkedDimension", linkedDimension);
        }
        if (linkedTransmitterId != null) {
            nbt.putString("LinkedTransmitterId", linkedTransmitterId.toString());
        }
    }
    
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        buffer = nbt.getDouble("Buffer");
        maxBuffer = nbt.getDouble("MaxBuffer");
        tier = nbt.getInt("Tier");
        cooldownRemaining = nbt.getInt("CooldownRemaining");
        totalTransmitted = nbt.getDouble("TotalTransmitted");
        transferCount = nbt.getInt("TransferCount");
        
        // 链接信息
        if (nbt.contains("LinkedX")) {
            linkedPos = new BlockPos(
                nbt.getInt("LinkedX"),
                nbt.getInt("LinkedY"),
                nbt.getInt("LinkedZ")
            );
        }
        if (nbt.contains("LinkedDimension")) {
            linkedDimension = nbt.getString("LinkedDimension");
        }
        if (nbt.contains("LinkedTransmitterId")) {
            linkedTransmitterId = UUID.fromString(nbt.getString("LinkedTransmitterId"));
        }
        
        // 兼容旧数据
        if (maxBuffer == 0) {
            maxBuffer = TransmitterConfig.getBuffer(tier);
        }
    }
}
