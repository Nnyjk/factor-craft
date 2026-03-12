package com.factorcraft.module.social.api;

import com.factorcraft.module.social.config.SocialConfig;

public interface SocialApi {
    boolean isEnabled();
    SocialConfig getConfig();
}