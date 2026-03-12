package com.factorcraft.module.social.service;

import com.factorcraft.module.social.config.SocialConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatService {
    private final SocialConfig config;
    private final Map<String, Deque<ChatMessage>> channels = new ConcurrentHashMap<>();
    
    public ChatService(SocialConfig config) { this.config = config; }
    
    public void sendMessage(String channel, UUID sender, String content) {
        Deque<ChatMessage> history = channels.computeIfAbsent(channel, k -> new ConcurrentLinkedDeque<>());
        if (history.size() >= config.chatHistorySize()) history.removeFirst();
        history.addLast(new ChatMessage(sender, content, System.currentTimeMillis()));
    }
    
    public List<ChatMessage> getHistory(String channel) {
        return new ArrayList<>(channels.getOrDefault(channel, new ConcurrentLinkedDeque<>()));
    }
    
    public void createChannel(String name) { channels.putIfAbsent(name, new ConcurrentLinkedDeque<>()); }
    public Set<String> getChannels() { return channels.keySet(); }
    
    public record ChatMessage(UUID sender, String content, long timestamp) {}
}