package com.factorcraft.module.technology.machine;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
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
        String fromDimension = world.getRegistryKey().getValue().toString();
        
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
        
        // 执行传输
        ServerWorld targetWorld = getTargetWorld((ServerWorld) world);
        
        // 从缓冲区扣除
        buffer -= actualAmount;
        
        // 更新统计
        totalTransmitted += received;
        transferCount++;
        
        // 设置冷却
        cooldownRemaining = TransmitterConfig.getCooldown(tier);
        
        LOGGER.info("传输完成：{} Factor (发送) → {} Factor (接收), 维度：{} → {}", 
            actualAmount, received, fromDimension, linkedDimension);
        
        // 在目标位置添加 Factor
        deliverFactorToWorld(targetWorld, received);
        
        markDirty();
        return received;
    }
    
    /**
     * 获取目标维度的世界
     */
    private ServerWorld getTargetWorld(ServerWorld currentWorld) {
        if (currentWorld == null || currentWorld.getServer() == null) {
            return currentWorld;
        }
        
        Identifier targetDimId = Identifier.tryParse(linkedDimension);
        if (targetDimId == null) {
            LOGGER.warn("无效的维度 ID: {}", linkedDimension);
            return currentWorld;
        }
        
        ServerWorld targetWorld = currentWorld.getServer().getWorld(
            net.minecraft.registry.RegistryKey.of(net.minecraft.registry.RegistryKeys.WORLD, targetDimId)
        );
        if (targetWorld == null) {
            LOGGER.warn("目标维度未加载：{}", linkedDimension);
            return currentWorld;
        }
        
        return targetWorld;
    }
    
    /**
     * 在目标世界添加 Factor
     * - 如果目标位置有传递器，调用其 receive() 方法
     * - 否则直接注入到区块 Factor 浓度
     */
    private void deliverFactorToWorld(ServerWorld targetWorld, double amount) {
        if (targetWorld == null || linkedPos == null) {
            LOGGER.warn("无法交付 Factor：目标世界或位置为空");
            return;
        }
        
        // 尝试获取目标位置的 BlockEntity
        BlockEntity targetEntity = targetWorld.getBlockEntity(linkedPos);
        
        if (targetEntity instanceof TransmitterBlockEntity targetTransmitter) {
            // 验证双向链接
            if (targetTransmitter.isLinked() && 
                targetTransmitter.getLinkedPos().equals(this.pos) &&
                targetTransmitter.getLinkedDimension().equals(world.getRegistryKey().getValue().toString())) {
                // 链接有效，接收 Factor
                targetTransmitter.receive(amount);
                LOGGER.debug("Factor 已传递到目标传递器：{} @ {}", amount, linkedPos);
            } else {
                // 链接无效，Factor 损失
                LOGGER.warn("目标传递器链接不匹配，Factor 损失：{}", amount);
            }
        } else {
            // 目标位置无传递器，直接注入到区块 Factor 浓度
            ChunkPos chunkPos = new ChunkPos(linkedPos);
            ChunkFactorManager.injectFactor(targetWorld, chunkPos, amount);
            LOGGER.debug("Factor 已注入到区块：{} @ {} {}", amount, linkedPos, targetWorld.getRegistryKey().getValue());
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
