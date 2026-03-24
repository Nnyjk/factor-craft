package com.factorcraft.module.profession.event;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 经验获取事件
 */
public class ProfessionExperienceGainEvent {
    
    /**
     * 经验获取前事件（可取消、可修改数量）
     */
    public static class Pre implements ProfessionEvent {
        private final ServerPlayerEntity player;
        private final ProfessionType professionType;
        private int amount;
        private final String source;
        private final long timestamp;
        private boolean cancelled = false;
        
        public Pre(ServerPlayerEntity player, int amount, String source) {
            this.player = player;
            this.professionType = null; // 将在事件处理中获取
            this.amount = amount;
            this.source = source;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public ProfessionEventType getType() { return ProfessionEventType.EXPERIENCE_GAIN; }
        @Override
        public ServerPlayerEntity getPlayer() { return player; }
        @Override
        public long getTimestamp() { return timestamp; }
        @Override
        public boolean isCancellable() { return true; }
        @Override
        public boolean isCancelled() { return cancelled; }
        @Override
        public void cancel() { cancelled = true; }
        
        public ProfessionType getProfessionType() { return professionType; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        public String getSource() { return source; }
    }
    
    /**
     * 经验获取后事件
     */
    public static class Post implements ProfessionEvent {
        private final ServerPlayerEntity player;
        private final ProfessionType professionType;
        private final int amount;
        private final String source;
        private final long timestamp;
        
        public Post(ServerPlayerEntity player, int amount, String source) {
            this.player = player;
            this.professionType = null;
            this.amount = amount;
            this.source = source;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public ProfessionEventType getType() { return ProfessionEventType.EXPERIENCE_GAIN; }
        @Override
        public ServerPlayerEntity getPlayer() { return player; }
        @Override
        public long getTimestamp() { return timestamp; }
        @Override
        public boolean isCancellable() { return false; }
        @Override
        public boolean isCancelled() { return false; }
        
        public ProfessionType getProfessionType() { return professionType; }
        public int getAmount() { return amount; }
        public String getSource() { return source; }
    }
}