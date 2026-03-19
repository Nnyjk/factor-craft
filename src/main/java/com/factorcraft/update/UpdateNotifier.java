package com.factorcraft.update;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

/**
 * 更新通知客户端
 * 
 * 在客户端显示更新通知
 */
public class UpdateNotifier implements ClientModInitializer {
    
    private static UpdateNotifier instance;
    private UpdateInfo pendingUpdate = null;
    private boolean hasShownToast = false;
    private int tickCounter = 0;
    private static final int CHECK_INTERVAL = 20 * 60 * 60; // 每小时检查一次 (20 ticks/sec * 60 sec * 60 min)
    
    public UpdateNotifier() {
        instance = this;
    }
    
    public static UpdateNotifier getInstance() {
        return instance;
    }
    
    @Override
    public void onInitializeClient() {
        // 启动时检查更新
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        
        FactorCraftMod.LOGGER.info("[UpdateNotifier] 更新通知器已初始化");
    }
    
    /**
     * 客户端 tick 处理
     */
    private void onClientTick(MinecraftClient client) {
        UpdateConfig config = UpdateConfig.getInstance();
        
        if (!config.isEnabled()) {
            return;
        }
        
        tickCounter++;
        
        // 启动时检查
        if (config.checkOnStartup() && tickCounter == 100 && pendingUpdate == null) {
            checkForUpdate();
        }
        
        // 定期检查
        if (tickCounter >= CHECK_INTERVAL * config.getCheckIntervalMinutes()) {
            tickCounter = 0;
            checkForUpdate();
        }
        
        // 显示通知
        if (pendingUpdate != null && pendingUpdate.hasUpdate() && !hasShownToast 
            && client.currentScreen != null && client.player != null) {
            showUpdateToast(client);
            hasShownToast = true;
        }
    }
    
    /**
     * 检查更新
     */
    public void checkForUpdate() {
        String currentVersion = FabricLoader.getInstance()
            .getModContainer("factorcraft")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("0.0.0");
        
        UpdateChecker.checkForUpdate(currentVersion).thenAccept(info -> {
            if (info.hasUpdate()) {
                pendingUpdate = info;
                hasShownToast = false;
            }
        });
    }
    
    /**
     * 显示更新 Toast
     */
    private void showUpdateToast(MinecraftClient client) {
        if (pendingUpdate == null || !pendingUpdate.hasUpdate()) {
            return;
        }
        
        UpdateConfig config = UpdateConfig.getInstance();
        if (config.isSilentCheck()) {
            return;
        }
        
        client.execute(() -> {
            SystemToast.show(
                client.getToastManager(),
                SystemToast.Type.PERIODIC_NOTIFICATION,
                pendingUpdate.getNotificationTitle(),
                pendingUpdate.getNotificationBody()
            );
        });
    }
    
    /**
     * 获取待处理的更新信息
     */
    public UpdateInfo getPendingUpdate() {
        return pendingUpdate;
    }
    
    /**
     * 清除待处理的更新
     */
    public void clearPendingUpdate() {
        pendingUpdate = null;
        hasShownToast = false;
    }
    
    /**
     * 打开下载页面
     */
    public static void openDownloadPage(Screen parent) {
        if (instance != null && instance.pendingUpdate != null) {
            String downloadUrl = instance.pendingUpdate.getDownloadUrl();
            String releaseUrl = instance.pendingUpdate.getReleaseUrl();
            final String url = (downloadUrl != null && !downloadUrl.isEmpty()) ? downloadUrl : releaseUrl;
            
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    client.setScreen(null);
                    try {
                        client.keyboard.setClipboard(url);
                        client.player.sendMessage(
                            Text.translatable("factorcraft.update.link_copied"), 
                            false);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                client.setScreen(parent);
            }, url, true));
        }
    }
}