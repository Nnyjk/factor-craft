package com.factorcraft.module.profession.event;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业选择事件
 */
public class ProfessionSelectEvent {
    
    /**
     * 职业选择前事件（可取消）
     */
    public static class Pre implements ProfessionEvent {
        private final ServerPlayerEntity player;
        private final ProfessionType professionType;
        private final long timestamp;
        private boolean cancelled = false;
        private boolean changing = false;
        
        public Pre(ServerPlayerEntity player, ProfessionType professionType) {
            this.player = player;
            this.professionType = professionType;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public ProfessionEventType getType() { return ProfessionEventType.PROFESSION_SELECT; }
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
        public boolean isChanging() { return changing; }
        public void setChanging(boolean changing) { this.changing = changing; }
    }
    
    /**
     * 职业选择后事件
     */
    public static class Post implements ProfessionEvent {
        private final ServerPlayerEntity player;
        private final ProfessionType professionType;
        private final long timestamp;
        private boolean changing = false;
        
        public Post(ServerPlayerEntity player, ProfessionType professionType) {
            this.player = player;
            this.professionType = professionType;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public ProfessionEventType getType() { return ProfessionEventType.PROFESSION_SELECT; }
        @Override
        public ServerPlayerEntity getPlayer() { return player; }
        @Override
        public long getTimestamp() { return timestamp; }
        @Override
        public boolean isCancellable() { return false; }
        @Override
        public boolean isCancelled() { return false; }
        
        public ProfessionType getProfessionType() { return professionType; }
        public boolean isChanging() { return changing; }
        public void setChanging(boolean changing) { this.changing = changing; }
    }
}