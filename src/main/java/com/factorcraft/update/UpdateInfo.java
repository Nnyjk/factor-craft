package com.factorcraft.update;

import net.minecraft.text.Text;

/**
 * 更新信息
 * 
 * 包含版本比较结果和下载信息
 */
public class UpdateInfo {
    
    private final boolean hasUpdate;
    private final String currentVersion;
    private final String latestVersion;
    private final String changelog;
    private final String downloadUrl;
    private String releaseUrl;
    
    public UpdateInfo(boolean hasUpdate, String currentVersion, String latestVersion, 
                      String changelog, String downloadUrl) {
        this.hasUpdate = hasUpdate;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
        this.changelog = changelog;
        this.downloadUrl = downloadUrl;
        this.releaseUrl = "https://github.com/Nnyjk/factor-craft/releases";
    }
    
    // Getters
    public boolean hasUpdate() { return hasUpdate; }
    public String getCurrentVersion() { return currentVersion; }
    public String getLatestVersion() { return latestVersion; }
    public String getChangelog() { return changelog; }
    public String getDownloadUrl() { return downloadUrl; }
    public String getReleaseUrl() { return releaseUrl; }
    
    public void setReleaseUrl(String url) { this.releaseUrl = url; }
    
    /**
     * 获取更新提示文本
     */
    public Text getUpdateMessage() {
        if (!hasUpdate) {
            return Text.translatable("factorcraft.update.up_to_date", currentVersion);
        }
        return Text.translatable("factorcraft.update.available", 
            currentVersion, latestVersion);
    }
    
    /**
     * 获取更新通知标题
     */
    public Text getNotificationTitle() {
        return Text.translatable("factorcraft.update.title");
    }
    
    /**
     * 获取更新通知内容
     */
    public Text getNotificationBody() {
        if (!hasUpdate) {
            return Text.translatable("factorcraft.update.up_to_date", currentVersion);
        }
        return Text.translatable("factorcraft.update.notification", 
            currentVersion, latestVersion);
    }
    
    /**
     * 获取下载按钮文本
     */
    public Text getDownloadButtonText() {
        return Text.translatable("factorcraft.update.download");
    }
    
    /**
     * 获取发布页面按钮文本
     */
    public Text getReleasePageButtonText() {
        return Text.translatable("factorcraft.update.release_page");
    }
    
    /**
     * 获取简化版 changelog（前几行）
     */
    public String getShortChangelog(int maxLines) {
        if (changelog == null || changelog.isEmpty()) {
            return "";
        }
        
        String[] lines = changelog.split("\n");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        
        for (String line : lines) {
            if (count >= maxLines) break;
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                sb.append(trimmed).append("\n");
                count++;
            }
        }
        
        return sb.toString().trim();
    }
}