package com.factorcraft.update;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 更新检查配置
 */
public class UpdateConfig {
    
    private static final String CONFIG_FILE = "factorcraft-update.properties";
    
    // 配置项
    private boolean enabled = true;
    private boolean checkOnStartup = true;
    private boolean checkPreReleases = false;
    private long checkIntervalMinutes = 60; // 60分钟
    private boolean silentCheck = false;
    
    private static UpdateConfig instance;
    
    public static UpdateConfig getInstance() {
        if (instance == null) {
            instance = new UpdateConfig();
            instance.load();
        }
        return instance;
    }
    
    // Getters
    public boolean isEnabled() { return enabled; }
    public boolean checkOnStartup() { return checkOnStartup; }
    public boolean checkPreReleases() { return checkPreReleases; }
    public long getCheckIntervalMinutes() { return checkIntervalMinutes; }
    public boolean isSilentCheck() { return silentCheck; }
    
    // Setters
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setCheckOnStartup(boolean checkOnStartup) { this.checkOnStartup = checkOnStartup; }
    public void setCheckPreReleases(boolean checkPreReleases) { this.checkPreReleases = checkPreReleases; }
    public void setCheckIntervalMinutes(long minutes) { this.checkIntervalMinutes = minutes; }
    public void setSilentCheck(boolean silent) { this.silentCheck = silent; }
    
    /**
     * 加载配置
     */
    public void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        if (!Files.exists(configPath)) {
            save();
            return;
        }
        
        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(configPath)) {
            props.load(is);
            
            enabled = Boolean.parseBoolean(props.getProperty("enabled", "true"));
            checkOnStartup = Boolean.parseBoolean(props.getProperty("checkOnStartup", "true"));
            checkPreReleases = Boolean.parseBoolean(props.getProperty("checkPreReleases", "false"));
            checkIntervalMinutes = Long.parseLong(props.getProperty("checkIntervalMinutes", "60"));
            silentCheck = Boolean.parseBoolean(props.getProperty("silentCheck", "false"));
            
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[UpdateConfig] 加载配置失败: {}", e.getMessage());
        }
    }
    
    /**
     * 保存配置
     */
    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        Properties props = new Properties();
        props.setProperty("enabled", String.valueOf(enabled));
        props.setProperty("checkOnStartup", String.valueOf(checkOnStartup));
        props.setProperty("checkPreReleases", String.valueOf(checkPreReleases));
        props.setProperty("checkIntervalMinutes", String.valueOf(checkIntervalMinutes));
        props.setProperty("silentCheck", String.valueOf(silentCheck));
        
        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream os = Files.newOutputStream(configPath)) {
                props.store(os, "FactorCraft Update Checker Configuration");
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.warn("[UpdateConfig] 保存配置失败: {}", e.getMessage());
        }
    }
    
    /**
     * 重置为默认值
     */
    public void reset() {
        enabled = true;
        checkOnStartup = true;
        checkPreReleases = false;
        checkIntervalMinutes = 60;
        silentCheck = false;
        save();
    }
}