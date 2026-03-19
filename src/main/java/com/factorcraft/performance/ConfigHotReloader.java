package com.factorcraft.performance;

import com.factorcraft.config.ConfigManager;
import com.factorcraft.module.factor.TideEffectsConfig;
import com.factorcraft.module.factor.TideEffectManager;
import com.factorcraft.module.loot.MobDropsConfig;
import com.factorcraft.module.loot.handler.EntityDropHandler;
import com.factorcraft.module.material.trait.TraitRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 配置热重载系统
 * 监听配置文件变化，自动重载
 */
public class ConfigHotReloader {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static WatchService watchService;
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static Path configPath;
    
    public static void initialize(Path configDir) {
        configPath = configDir;
        
        try {
            watchService = FileSystems.getDefault().newWatchService();
            configPath.register(watchService, 
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE
            );
            
            startWatcher();
            
            // 服务器停止时关闭
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> shutdown());
            
        } catch (Exception e) {
            System.err.println("Failed to initialize config watcher: " + e.getMessage());
        }
    }
    
    private static void startWatcher() {
        if (running.compareAndSet(false, true)) {
            EXECUTOR.submit(() -> {
                while (running.get()) {
                    try {
                        WatchKey key = watchService.take();
                        
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path changedFile = (Path) event.context();
                            
                            if (changedFile.toString().endsWith(".json")) {
                                reloadConfig(changedFile.toString());
                            }
                        }
                        
                        key.reset();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        System.err.println("Error watching config: " + e.getMessage());
                    }
                }
            });
        }
    }
    
    private static void reloadConfig(String fileName) {
        System.out.println("[FactorCraft] Reloading config: " + fileName);
        
        try {
            // 根据文件名重载对应配置
            if (fileName.contains("tide_effects")) {
                reloadTideEffects();
            } else if (fileName.contains("mob_drops")) {
                reloadMobDrops();
            } else if (fileName.contains("traits")) {
                reloadTraits();
            } else if (fileName.contains("biome")) {
                reloadBiomeConcentrations();
            } else if (fileName.contains("resonance")) {
                reloadResonanceRules();
            }
            
            System.out.println("[FactorCraft] Config reloaded: " + fileName);
        } catch (Exception e) {
            System.err.println("[FactorCraft] Failed to reload config: " + e.getMessage());
        }
    }
    
    private static void reloadTraits() {
        ConfigManager.reload();
    }
    
    private static void reloadBiomeConcentrations() {
        ConfigManager.reload();
    }
    
    private static void reloadResonanceRules() {
        ConfigManager.reload();
    }
    
    public static void shutdown() {
        running.set(false);
        EXECUTOR.shutdown();
        
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing watch service: " + e.getMessage());
        }
    }
    
    /**
     * 手动重载所有配置
     */
    public static void reloadAll() {
        System.out.println("[FactorCraft] Reloading all configs...");
        
        try {
            reloadTraits();
            reloadBiomeConcentrations();
            reloadResonanceRules();
            reloadTideEffects();
            reloadMobDrops();
            
            System.out.println("[FactorCraft] All configs reloaded successfully");
        } catch (Exception e) {
            System.err.println("[FactorCraft] Failed to reload configs: " + e.getMessage());
        }
    }
    
    /**
     * 重载潮汐效果配置
     */
    private static void reloadTideEffects() {
        TideEffectsConfig.reload();
        TideEffectManager.getInstance().reloadConfig();
    }
    
    /**
     * 重载怪物掉落配置
     */
    private static void reloadMobDrops() {
        MobDropsConfig.reload();
        EntityDropHandler.reloadConfig();
    }
}