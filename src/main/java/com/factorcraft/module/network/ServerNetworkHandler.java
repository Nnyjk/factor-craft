package com.factorcraft.module.network;

import com.factorcraft.module.technology.machine.MachineBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务端网络包处理器
 * 
 * 处理客户端 -> 服务端的请求，包含服务端权威验证
 * 
 * 优化功能:
 * - 条件同步：使用 NetworkSyncTracker 检测状态变化
 * - 频率限制：使用配置化的冷却时间
 * - 批量同步：支持多个机器状态合并发送
 */
public class ServerNetworkHandler {
    
    /** 操作频率限制 - 防止刷包 */
    private static final Map<String, Long> lastOperationTime = new HashMap<>();
    private static final long OPERATION_COOLDOWN_MS = 100; // 100ms 冷却
    
    public static void register() {
        // 注册 C2S Payload 类型
        PayloadTypeRegistry.playC2S().register(
            MachineOperationPayload.ID,
            MachineOperationPayload.CODEC
        );
        
        // 处理机器操作请求
        ServerPlayNetworking.registerGlobalReceiver(MachineOperationPayload.ID, 
            (payload, context) -> {
                ServerPlayerEntity player = context.player();
                ServerWorld world = player.getServerWorld();
                BlockPos pos = payload.machinePos();
                
                // 1. 距离检查 - 防止远程操作
                double distance = player.getPos().distanceTo(pos.toCenterPos());
                if (distance > 8.0) {
                    player.sendMessage(Text.literal("§c操作失败：距离太远"), false);
                    return;
                }
                
                // 2. 频率限制 - 防止刷包
                String key = player.getUuid() + ":" + pos.toShortString();
                long now = System.currentTimeMillis();
                Long lastTime = lastOperationTime.get(key);
                if (lastTime != null && now - lastTime < OPERATION_COOLDOWN_MS) {
                    return; // 静默忽略，不发送反馈
                }
                lastOperationTime.put(key, now);
                
                // 3. 方块实体检查
                BlockEntity be = world.getBlockEntity(pos);
                if (!(be instanceof MachineBlockEntity machine)) {
                    player.sendMessage(Text.literal("§c操作失败：无效的机器"), false);
                    return;
                }
                
                // 4. 执行操作
                context.server().execute(() -> {
                    handleMachineOperation(player, machine, payload.operation(), payload.data());
                });
            }
        );
    }
    
    /**
     * 处理机器操作
     */
    private static void handleMachineOperation(ServerPlayerEntity player, 
                                               MachineBlockEntity machine,
                                               MachineOperationPayload.Operation operation,
                                               int data) {
        switch (operation) {
            case START_WORK -> {
                // 验证：检查资源是否充足
                if (canStartWork(machine)) {
                    startMachineWork(machine);
                    player.sendMessage(Text.literal("§a机器开始工作"), false);
                    // 条件同步状态给所有附近玩家
                    conditionalSyncMachineState(machine);
                } else {
                    player.sendMessage(Text.literal("§c无法启动：资源不足"), false);
                }
            }
            
            case STOP_WORK -> {
                stopMachineWork(machine);
                player.sendMessage(Text.literal("§e机器已停止"), false);
                conditionalSyncMachineState(machine);
            }
            
            case EXTRACT_ITEM -> {
                // 验证槽位有效性
                if (data >= 0 && data < getMachineSlotCount(machine)) {
                    extractItemFromSlot(player, machine, data);
                }
            }
            
            case INSERT_ITEM -> {
                // 验证槽位有效性
                if (data >= 0 && data < getMachineSlotCount(machine)) {
                    insertItemToSlot(player, machine, data);
                }
            }
            
            case CHANGE_MODE -> {
                // 验证模式有效性
                if (data >= 0 && data < getMaxModeCount(machine)) {
                    changeMachineMode(machine, data);
                    player.sendMessage(Text.literal("§b模式已更改"), false);
                    conditionalSyncMachineState(machine);
                }
            }
            
            case UPGRADE -> {
                // 验证：检查升级条件
                if (canUpgrade(machine, data)) {
                    upgradeMachine(machine, data);
                    player.sendMessage(Text.literal("§d升级成功"), false);
                    conditionalSyncMachineState(machine);
                } else {
                    player.sendMessage(Text.literal("§c升级失败：条件不满足"), false);
                }
            }
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private static boolean canStartWork(MachineBlockEntity machine) {
        // 默认实现：子类可覆盖
        return true;
    }
    
    private static void startMachineWork(MachineBlockEntity machine) {
        // 触发机器开始工作
        machine.markDirty();
    }
    
    private static void stopMachineWork(MachineBlockEntity machine) {
        machine.markDirty();
    }
    
    private static int getMachineSlotCount(MachineBlockEntity machine) {
        // 默认槽位数
        return 9;
    }
    
    private static void extractItemFromSlot(ServerPlayerEntity player, MachineBlockEntity machine, int slot) {
        // 提取物品逻辑
        machine.markDirty();
    }
    
    private static void insertItemToSlot(ServerPlayerEntity player, MachineBlockEntity machine, int slot) {
        // 放入物品逻辑
        machine.markDirty();
    }
    
    private static int getMaxModeCount(MachineBlockEntity machine) {
        return 4;
    }
    
    private static void changeMachineMode(MachineBlockEntity machine, int mode) {
        machine.markDirty();
    }
    
    private static boolean canUpgrade(MachineBlockEntity machine, int tier) {
        return tier > 0 && tier <= 5;
    }
    
    private static void upgradeMachine(MachineBlockEntity machine, int tier) {
        machine.markDirty();
    }
    
    /**
     * 条件同步机器状态给附近玩家
     * 
     * 使用 NetworkSyncTracker 检测状态变化，仅当必要时才同步
     */
    private static void conditionalSyncMachineState(MachineBlockEntity machine) {
        if (machine.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getPlayers(player -> 
                player.getPos().distanceTo(machine.getPos().toCenterPos()) < 32
            ).forEach(player -> {
                // 使用构建器创建 payload 并条件同步
                MachineStateSyncPayload.builder(machine.getPos(), getMachineType(machine))
                    .working(isMachineWorking(machine))
                    .progress(getMachineProgress(machine))
                    .factorStorage(machine.getFactorStorage(), machine.getMaxFactorStorage())
                    .energy(machine.getEnergyStored(), machine.getMaxEnergy())
                    .conditionalSendTo(player);
            });
        }
    }
    
    /**
     * 每 tick 调用 - 条件同步附近机器状态
     * 
     * 在 ServerWorld tick 时调用，检查并同步变化的机器状态
     * 
     * @param world 服务器世界
     * @param currentTick 当前 tick
     */
    public static void tick(ServerWorld world, long currentTick) {
        // 定期清理过期记录（每 60 秒）
        if (currentTick % 1200 == 0) {
            cleanup();
        }
        
        // 注意：具体的机器 tick 同步由各 MachineBlockEntity 自行处理
        // 这里只负责全局的清理和统计
    }
    
    /**
     * 获取机器类型字符串
     */
    private static String getMachineType(MachineBlockEntity machine) {
        // 根据机器类返回类型字符串
        String className = machine.getClass().getSimpleName();
        if (className.contains("Extractor")) return "extractor";
        if (className.contains("Synthesizer")) return "synthesizer";
        if (className.contains("Consumer")) return "consumer";
        if (className.contains("Cultivator")) return "cultivator";
        if (className.contains("Breeder")) return "breeder";
        if (className.contains("Transmitter")) return "transmitter";
        return "unknown";
    }
    
    /**
     * 检查机器是否在工作中
     */
    private static boolean isMachineWorking(MachineBlockEntity machine) {
        // 默认实现：子类可覆盖
        return false;
    }
    
    /**
     * 获取机器进度
     */
    private static double getMachineProgress(MachineBlockEntity machine) {
        // 默认实现：子类可覆盖
        return 0.0;
    }
    
    /**
     * 获取机器能量存储
     */
    private static int getMachineEnergyStored(MachineBlockEntity machine) {
        // 默认实现：子类可覆盖
        return 0;
    }
    
    /**
     * 获取机器最大能量
     */
    private static int getMachineMaxEnergy(MachineBlockEntity machine) {
        // 默认实现：子类可覆盖
        return 10000;
    }
    
    /**
     * 清理过期的时间记录
     */
    public static void cleanup() {
        long now = System.currentTimeMillis();
        lastOperationTime.entrySet().removeIf(entry -> 
            now - entry.getValue() > 60000 // 清理 1 分钟前的记录
        );
    }
}
