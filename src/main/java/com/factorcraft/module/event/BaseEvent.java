package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * 基础事件实现
 * 
 * 提供通用功能和默认实现
 */
public abstract class BaseEvent implements IEvent {
    protected final UUID id;
    protected final EventType type;
    protected final int duration;
    protected final Identifier worldKey;
    protected boolean finished;
    
    protected BaseEvent(EventType type, int duration, Identifier worldKey) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.duration = duration;
        this.worldKey = worldKey;
        this.finished = false;
    }
    
    @Override
    public UUID getId() {
        return id;
    }
    
    @Override
    public EventType getType() {
        return type;
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public Identifier getWorldKey() {
        return worldKey;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        // 发送通知
        broadcastMessage(world, Text.literal("⚡ 事件开始：")
            .append(Text.literal(type.getDisplayName()).formatted(Formatting.GOLD))
            .append(Text.literal(" 将持续 " + (duration / 20) + " 秒")));
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        // 发送通知
        broadcastMessage(world, Text.literal("⭕ 事件结束：")
            .append(Text.literal(type.getDisplayName()).formatted(Formatting.GRAY)));
        this.finished = true;
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }
    
    /**
     * 向世界所有玩家发送消息
     */
    protected void broadcastMessage(ServerWorld world, Text message) {
        if (world != null) {
            world.getPlayers().forEach(player -> player.sendMessage(message));
        }
    }
    
    /**
     * 获取世界实例
     */
    protected ServerWorld getWorld(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey().getValue().equals(worldKey)) {
                return world;
            }
        }
        return null;
    }
}
