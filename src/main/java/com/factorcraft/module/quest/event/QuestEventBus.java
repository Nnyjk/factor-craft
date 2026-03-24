package com.factorcraft.module.quest.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 任务事件总线
 * 
 * 管理任务相关事件的发布和订阅
 */
public class QuestEventBus {
    
    private final List<Consumer<QuestAcceptEvent.Pre>> acceptPreListeners = new ArrayList<>();
    private final List<Consumer<QuestAcceptEvent.Post>> acceptPostListeners = new ArrayList<>();
    private final List<Consumer<QuestProgressEvent>> progressListeners = new ArrayList<>();
    private final List<Consumer<QuestCompleteEvent.Pre>> completePreListeners = new ArrayList<>();
    private final List<Consumer<QuestCompleteEvent.Post>> completePostListeners = new ArrayList<>();
    
    // ==================== 订阅方法 ====================
    
    /**
     * 订阅任务接取前置事件
     */
    public void onQuestAcceptPre(Consumer<QuestAcceptEvent.Pre> listener) {
        acceptPreListeners.add(listener);
    }
    
    /**
     * 订阅任务接取后置事件
     */
    public void onQuestAcceptPost(Consumer<QuestAcceptEvent.Post> listener) {
        acceptPostListeners.add(listener);
    }
    
    /**
     * 订阅任务进度事件
     */
    public void onQuestProgress(Consumer<QuestProgressEvent> listener) {
        progressListeners.add(listener);
    }
    
    /**
     * 订阅任务完成前置事件
     */
    public void onQuestCompletePre(Consumer<QuestCompleteEvent.Pre> listener) {
        completePreListeners.add(listener);
    }
    
    /**
     * 订阅任务完成后置事件
     */
    public void onQuestCompletePost(Consumer<QuestCompleteEvent.Post> listener) {
        completePostListeners.add(listener);
    }
    
    // ==================== 发布方法 ====================
    
    /**
     * 发布任务接取前置事件
     */
    public void publishAcceptPre(QuestAcceptEvent.Pre event) {
        for (Consumer<QuestAcceptEvent.Pre> listener : acceptPreListeners) {
            listener.accept(event);
            if (event.isCancelled()) {
                break;
            }
        }
    }
    
    /**
     * 发布任务接取后置事件
     */
    public void publishAcceptPost(QuestAcceptEvent.Post event) {
        for (Consumer<QuestAcceptEvent.Post> listener : acceptPostListeners) {
            listener.accept(event);
        }
    }
    
    /**
     * 发布任务进度事件
     */
    public void publishProgress(QuestProgressEvent event) {
        for (Consumer<QuestProgressEvent> listener : progressListeners) {
            listener.accept(event);
        }
    }
    
    /**
     * 发布任务完成前置事件
     */
    public void publishCompletePre(QuestCompleteEvent.Pre event) {
        for (Consumer<QuestCompleteEvent.Pre> listener : completePreListeners) {
            listener.accept(event);
        }
    }
    
    /**
     * 发布任务完成后置事件
     */
    public void publishCompletePost(QuestCompleteEvent.Post event) {
        for (Consumer<QuestCompleteEvent.Post> listener : completePostListeners) {
            listener.accept(event);
        }
    }
    
    /**
     * 清空所有监听器
     */
    public void clear() {
        acceptPreListeners.clear();
        acceptPostListeners.clear();
        progressListeners.clear();
        completePreListeners.clear();
        completePostListeners.clear();
    }
}