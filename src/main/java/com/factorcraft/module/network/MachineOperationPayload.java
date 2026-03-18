package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * 机器操作请求 Payload (C2S)
 * 
 * 客户端请求服务端执行机器操作
 * 
 * 所有机器操作必须通过服务端验证：
 * - 操作权限检查
 * - 资源充足性检查
 * - 作弊检测
 */
public record MachineOperationPayload(
    BlockPos machinePos,
    Operation operation,
    int data
) implements CustomPayload {
    
    public enum Operation {
        START_WORK,     // 开始工作
        STOP_WORK,      // 停止工作
        EXTRACT_ITEM,   // 提取物品
        INSERT_ITEM,    // 放入物品
        CHANGE_MODE,    // 更改模式
        UPGRADE         // 升级
    }
    
    public static final CustomPayload.Id<MachineOperationPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "machine_operation"));
    
    public static final PacketCodec<RegistryByteBuf, MachineOperationPayload> CODEC = 
        PacketCodec.of(MachineOperationPayload::write, MachineOperationPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeBlockPos(machinePos);
        buf.writeEnumConstant(operation);
        buf.writeInt(data);
    }
    
    private static MachineOperationPayload read(RegistryByteBuf buf) {
        return new MachineOperationPayload(
            buf.readBlockPos(),
            buf.readEnumConstant(Operation.class),
            buf.readInt()
        );
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 创建开始工作的请求
     */
    public static MachineOperationPayload startWork(BlockPos pos) {
        return new MachineOperationPayload(pos, Operation.START_WORK, 0);
    }
    
    /**
     * 创建停止工作的请求
     */
    public static MachineOperationPayload stopWork(BlockPos pos) {
        return new MachineOperationPayload(pos, Operation.STOP_WORK, 0);
    }
    
    /**
     * 创建提取物品的请求
     */
    public static MachineOperationPayload extractItem(BlockPos pos, int slot) {
        return new MachineOperationPayload(pos, Operation.EXTRACT_ITEM, slot);
    }
    
    /**
     * 创建放入物品的请求
     */
    public static MachineOperationPayload insertItem(BlockPos pos, int slot) {
        return new MachineOperationPayload(pos, Operation.INSERT_ITEM, slot);
    }
    
    /**
     * 创建更改模式的请求
     */
    public static MachineOperationPayload changeMode(BlockPos pos, int newMode) {
        return new MachineOperationPayload(pos, Operation.CHANGE_MODE, newMode);
    }
    
    /**
     * 创建升级的请求
     */
    public static MachineOperationPayload upgrade(BlockPos pos, int tier) {
        return new MachineOperationPayload(pos, Operation.UPGRADE, tier);
    }
}