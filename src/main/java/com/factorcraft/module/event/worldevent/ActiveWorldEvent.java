package com.factorcraft.module.event.worldevent;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * 活跃的世界事件实例
 * 
 * 表示一个正在发生的世界事件
 */
public class ActiveWorldEvent {
    private final UUID eventId;
    private final WorldEventType type;
    private final ServerWorld world;
    private final BlockPos centerPos;
    private final int radius;
    private final long startTime;
    private final int duration;
    private final int severity;
    
    private boolean active;
    private long endTime;
    private double intensity;
    
    /**
     * 创建一个新的活跃事件
     * 
     * @param type 事件类型
     * @param world 所在世界
     * @param centerPos 中心位置
     * @param radius 影响半径
     * @param duration 持续时间 (ticks)，-1 表示无限
     * @param severity 严重程度 (1-5)
     */
    public ActiveWorldEvent(WorldEventType type, ServerWorld world, 
                           BlockPos centerPos, int radius,
                           int duration, int severity) {
        this.eventId = UUID.randomUUID();
        this.type = type;
        this.world = world;
        this.centerPos = centerPos;
        this.radius = radius;
        this.startTime = world.getTime();
        this.duration = duration;
        this.severity = Math.max(1, Math.min(5, severity));
        this.active = true;
        this.endTime = duration > 0 ? startTime + duration : -1;
        this.intensity = 1.0;
    }
    
    private ActiveWorldEvent(UUID eventId, WorldEventType type, ServerWorld world,
                            BlockPos centerPos, int radius, long startTime,
                            int duration, int severity, boolean active,
                            long endTime, double intensity) {
        this.eventId = eventId;
        this.type = type;
        this.world = world;
        this.centerPos = centerPos;
        this.radius = radius;
        this.startTime = startTime;
        this.duration = duration;
        this.severity = severity;
        this.active = active;
        this.endTime = endTime;
        this.intensity = intensity;
    }
    
    /**
     * Tick 更新
     * @return true 如果事件仍然活跃
     */
    public boolean tick() {
        if (!active) {
            return false;
        }
        
        long currentTime = world.getTime();
        
        // 检查是否结束
        if (endTime > 0 && currentTime >= endTime) {
            end();
            return false;
        }
        
        // 更新强度（某些事件会有强度变化）
        if (type == WorldEventType.FACTOR_STORM) {
            // 风暴：强度先增后减
            double progress = (double)(currentTime - startTime) / duration;
            intensity = Math.sin(progress * Math.PI) * 1.5 + 0.5;
        } else if (type == WorldEventType.VOID_EROSION) {
            // 虚空侵蚀：强度逐渐增加
            intensity = Math.min(3.0, intensity + 0.001);
        }
        
        return true;
    }
    
    /**
     * 结束事件
     */
    public void end() {
        this.active = false;
        this.endTime = world.getTime();
    }
    
    /**
     * 强制终止事件
     */
    public void forceEnd() {
        this.active = false;
        this.endTime = world.getTime();
    }
    
    /**
     * 是否仍在活跃
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * 是否影响指定位置
     */
    public boolean affectsPosition(BlockPos pos) {
        if (!active) {
            return false;
        }
        // 不同维度不影响
        if (!world.getRegistryKey().getValue().toString().equals(
            world.getServer().getOverworld().getRegistryKey().getValue().toString())) {
            // 检查是否在同一维度
            return false;
        }
        double distance = Math.sqrt(pos.getSquaredDistance(centerPos));
        return distance <= radius;
    }
    
    /**
     * 获取剩余时间 (ticks)
     */
    public long getRemainingTicks() {
        if (endTime <= 0) {
            return -1; // 无限
        }
        return Math.max(0, endTime - world.getTime());
    }
    
    /**
     * 获取已用时间 (ticks)
     */
    public long getElapsedTicks() {
        return world.getTime() - startTime;
    }
    
    /**
     * 获取进度 (0.0 - 1.0)
     */
    public double getProgress() {
        if (duration <= 0) {
            return 0.0;
        }
        return Math.min(1.0, (double)getElapsedTicks() / duration);
    }
    
    // Getters
    
    public UUID getEventId() {
        return eventId;
    }
    
    public WorldEventType getType() {
        return type;
    }
    
    public ServerWorld getWorld() {
        return world;
    }
    
    public BlockPos getCenterPos() {
        return centerPos;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getSeverity() {
        return severity;
    }
    
    public double getIntensity() {
        return intensity;
    }
    
    // NBT 序列化
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("eventId", eventId.toString());
        nbt.putString("type", type.getId());
        nbt.putInt("centerX", centerPos.getX());
        nbt.putInt("centerY", centerPos.getY());
        nbt.putInt("centerZ", centerPos.getZ());
        nbt.putInt("radius", radius);
        nbt.putLong("startTime", startTime);
        nbt.putInt("duration", duration);
        nbt.putInt("severity", severity);
        nbt.putBoolean("active", active);
        nbt.putLong("endTime", endTime);
        nbt.putDouble("intensity", intensity);
        return nbt;
    }
    
    public static ActiveWorldEvent fromNbt(NbtCompound nbt, ServerWorld world) {
        UUID eventId = UUID.fromString(nbt.getString("eventId"));
        String typeId = nbt.getString("type");
        WorldEventType type = WorldEventType.valueOf(typeId.toUpperCase());
        BlockPos centerPos = new BlockPos(
            nbt.getInt("centerX"),
            nbt.getInt("centerY"),
            nbt.getInt("centerZ")
        );
        int radius = nbt.getInt("radius");
        long startTime = nbt.getLong("startTime");
        int duration = nbt.getInt("duration");
        int severity = nbt.getInt("severity");
        boolean active = nbt.getBoolean("active");
        long endTime = nbt.getLong("endTime");
        double intensity = nbt.getDouble("intensity");
        
        return new ActiveWorldEvent(eventId, type, world, centerPos, radius,
            startTime, duration, severity, active, endTime, intensity);
    }
}