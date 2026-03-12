package com.factorcraft.module.social;

import com.factorcraft.module.social.api.SocialApi;
import com.factorcraft.module.social.config.SocialConfig;
import com.factorcraft.module.social.service.FriendService;
import com.factorcraft.module.social.service.GuildService;
import com.factorcraft.module.social.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 14: 社交功能模块
 */
public class SocialModule implements SocialApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(SocialModule.class);
    private static SocialModule INSTANCE;
    
    private final SocialConfig config;
    private FriendService friendService;
    private GuildService guildService;
    private ChatService chatService;
    
    private SocialModule() {
        this.config = SocialConfig.load();
    }
    
    public static synchronized SocialModule getInstance() {
        if (INSTANCE == null) INSTANCE = new SocialModule();
        return INSTANCE;
    }
    
    public void initialize() {
        if (!config.enabled()) {
            LOGGER.info("Social module disabled");
            return;
        }
        LOGGER.info("Initializing social module...");
        this.friendService = new FriendService(config);
        this.guildService = new GuildService(config);
        this.chatService = new ChatService(config);
        LOGGER.info("Social module initialized");
    }
    
    public void shutdown() { LOGGER.info("Social module shut down"); }
    
    @Override public boolean isEnabled() { return config.enabled(); }
    @Override public SocialConfig getConfig() { return config; }
    
    public FriendService getFriendService() { return friendService; }
    public GuildService getGuildService() { return guildService; }
    public ChatService getChatService() { return chatService; }
}