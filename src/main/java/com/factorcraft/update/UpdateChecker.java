package com.factorcraft.update;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 更新检查器
 * 
 * 从 GitHub Releases 检查模组更新
 */
public class UpdateChecker {
    
    private static final String GITHUB_API = "https://api.github.com/repos/Nnyjk/factor-craft/releases/latest";
    private static final String USER_AGENT = "FactorCraft-Mod/%s";
    private static final int TIMEOUT_MS = 5000;
    
    private static final Gson GSON = new Gson();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "FactorCraft-UpdateChecker");
        t.setDaemon(true);
        return t;
    });
    
    // 缓存
    private static UpdateInfo cachedUpdate = null;
    private static long lastCheckTime = 0;
    private static final long CACHE_DURATION = 1000 * 60 * 60; // 1小时
    
    /**
     * 异步检查更新
     */
    public static CompletableFuture<UpdateInfo> checkForUpdate(String currentVersion) {
        // 检查缓存
        if (cachedUpdate != null && System.currentTimeMillis() - lastCheckTime < CACHE_DURATION) {
            return CompletableFuture.completedFuture(cachedUpdate);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return doCheck(currentVersion);
            } catch (Exception e) {
                FactorCraftMod.LOGGER.debug("[UpdateChecker] 检查更新失败: {}", e.getMessage());
                return new UpdateInfo(false, currentVersion, currentVersion, null, null);
            }
        }, executor);
    }
    
    /**
     * 执行更新检查
     */
    private static UpdateInfo doCheck(String currentVersion) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(GITHUB_API).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", String.format(USER_AGENT, currentVersion));
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HTTP " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JsonObject response = GSON.fromJson(reader, JsonObject.class);
        reader.close();
        
        String latestVersion = response.get("tag_name").getAsString();
        // 移除 'v' 前缀
        if (latestVersion.startsWith("v")) {
            latestVersion = latestVersion.substring(1);
        }
        
        String changelog = response.has("body") ? response.get("body").getAsString() : "";
        String downloadUrl = null;
        
        // 获取下载链接
        if (response.has("assets") && response.getAsJsonArray("assets").size() > 0) {
            JsonObject asset = response.getAsJsonArray("assets").get(0).getAsJsonObject();
            downloadUrl = asset.get("browser_download_url").getAsString();
        }
        
        String htmlUrl = response.has("html_url") ? response.get("html_url").getAsString() : 
            "https://github.com/Nnyjk/factor-craft/releases";
        
        boolean hasUpdate = isNewerVersion(currentVersion, latestVersion);
        
        UpdateInfo info = new UpdateInfo(hasUpdate, currentVersion, latestVersion, changelog, downloadUrl);
        info.setReleaseUrl(htmlUrl);
        
        // 更新缓存
        cachedUpdate = info;
        lastCheckTime = System.currentTimeMillis();
        
        if (hasUpdate) {
            FactorCraftMod.LOGGER.info("[UpdateChecker] 发现新版本: {} (当前: {})", latestVersion, currentVersion);
        } else {
            FactorCraftMod.LOGGER.debug("[UpdateChecker] 已是最新版本: {}", currentVersion);
        }
        
        return info;
    }
    
    /**
     * 比较版本号
     */
    private static boolean isNewerVersion(String current, String latest) {
        try {
            String[] currentParts = current.replaceAll("[^0-9.]", "").split("\\.");
            String[] latestParts = latest.replaceAll("[^0-9.]", "").split("\\.");
            
            int maxLen = Math.max(currentParts.length, latestParts.length);
            for (int i = 0; i < maxLen; i++) {
                int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int l = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                if (l > c) return true;
                if (l < c) return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取缓存的更新信息
     */
    public static UpdateInfo getCachedUpdate() {
        return cachedUpdate;
    }
    
    /**
     * 清除缓存
     */
    public static void clearCache() {
        cachedUpdate = null;
        lastCheckTime = 0;
    }
    
    /**
     * 关闭线程池
     */
    public static void shutdown() {
        executor.shutdown();
    }
}