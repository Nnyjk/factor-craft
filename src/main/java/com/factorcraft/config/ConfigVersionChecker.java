package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.JsonObject;

/**
 * 配置版本检查器
 * 
 * 检查配置文件的版本兼容性
 */
public class ConfigVersionChecker {
    
    /**
     * 兼容性结果
     */
    public enum CompatibilityStatus {
        COMPATIBLE,      // 完全兼容
        COMPATIBLE_WARN, // 兼容但有警告
        INCOMPATIBLE     // 不兼容
    }
    
    /**
     * 兼容性检查结果
     */
    public static class CompatibilityResult {
        private final CompatibilityStatus status;
        private final String message;
        private final String configVersion;
        private final String requiredVersion;
        
        public CompatibilityResult(CompatibilityStatus status, String message, 
                                 String configVersion, String requiredVersion) {
            this.status = status;
            this.message = message;
            this.configVersion = configVersion;
            this.requiredVersion = requiredVersion;
        }
        
        public CompatibilityStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public String getConfigVersion() { return configVersion; }
        public String getRequiredVersion() { return requiredVersion; }
        public boolean isCompatible() { return status != CompatibilityStatus.INCOMPATIBLE; }
    }
    
    /**
     * 从配置中获取版本号
     */
    public String getVersion(JsonObject config) {
        if (config == null || !config.has("version")) {
            return "0.0.0"; // 无版本号视为 0.0.0
        }
        return config.get("version").getAsString();
    }
    
    /**
     * 检查版本兼容性
     * 
     * @param configVersion 配置文件版本
     * @param requiredVersion 需要的版本
     * @return 兼容性结果
     */
    public CompatibilityResult checkCompatibility(String configVersion, String requiredVersion) {
        if (configVersion == null || configVersion.isEmpty()) {
            return new CompatibilityResult(
                CompatibilityStatus.INCOMPATIBLE,
                "配置文件缺少版本号",
                "0.0.0",
                requiredVersion
            );
        }
        
        int comparison = compareVersions(configVersion, requiredVersion);
        
        if (comparison >= 0) {
            // 配置版本 >= 需要版本
            if (isMajorVersionMatch(configVersion, requiredVersion)) {
                return new CompatibilityResult(
                    CompatibilityStatus.COMPATIBLE,
                    "版本兼容",
                    configVersion,
                    requiredVersion
                );
            } else {
                return new CompatibilityResult(
                    CompatibilityStatus.COMPATIBLE_WARN,
                    "主版本不匹配，可能存在兼容性问题",
                    configVersion,
                    requiredVersion
                );
            }
        } else {
            // 配置版本 < 需要版本
            if (canAutoUpgrade(configVersion, requiredVersion)) {
                return new CompatibilityResult(
                    CompatibilityStatus.COMPATIBLE_WARN,
                    "配置版本过旧，建议更新（可自动升级）",
                    configVersion,
                    requiredVersion
                );
            } else {
                return new CompatibilityResult(
                    CompatibilityStatus.INCOMPATIBLE,
                    "配置版本过旧，无法自动升级",
                    configVersion,
                    requiredVersion
                );
            }
        }
    }
    
    /**
     * 比较两个版本号
     * 
     * @param v1 版本 1
     * @param v2 版本 2
     * @return v1 > v2 返回 1, v1 < v2 返回 -1, 相等返回 0
     */
    public int compareVersions(String v1, String v2) {
        if (v1 == null) v1 = "0.0.0";
        if (v2 == null) v2 = "0.0.0";
        
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int maxLen = Math.max(parts1.length, parts2.length);
        
        for (int i = 0; i < maxLen; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            
            if (num1 > num2) return 1;
            if (num1 < num2) return -1;
        }
        
        return 0;
    }
    
    /**
     * 检查主版本是否匹配
     */
    private boolean isMajorVersionMatch(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        if (parts1.length == 0 || parts2.length == 0) {
            return true;
        }
        
        int major1 = Integer.parseInt(parts1[0]);
        int major2 = Integer.parseInt(parts2[0]);
        
        return major1 == major2;
    }
    
    /**
     * 检查是否可以自动升级
     * 
     * 规则：
     * - 主版本相同可以自动升级
     * - 次版本差异 <= 1 可以自动升级
     * - 主版本不同不能自动升级
     */
    private boolean canAutoUpgrade(String configVersion, String requiredVersion) {
        String[] configParts = configVersion.split("\\.");
        String[] requiredParts = requiredVersion.split("\\.");
        
        if (configParts.length < 2 || requiredParts.length < 2) {
            return false;
        }
        
        int configMajor = Integer.parseInt(configParts[0]);
        int requiredMajor = Integer.parseInt(requiredParts[0]);
        
        // 主版本不同，不能自动升级
        if (configMajor != requiredMajor) {
            return false;
        }
        
        int configMinor = Integer.parseInt(configParts[1]);
        int requiredMinor = Integer.parseInt(requiredParts[1]);
        
        // 次版本差异 <= 1 可以自动升级
        return Math.abs(requiredMinor - configMinor) <= 1;
    }
    
    /**
     * 获取版本升级建议
     */
    public String getUpgradeAdvice(String configVersion, String requiredVersion) {
        CompatibilityResult result = checkCompatibility(configVersion, requiredVersion);
        
        switch (result.getStatus()) {
            case COMPATIBLE:
                return "无需升级";
            case COMPATIBLE_WARN:
                return "建议备份后升级到版本 " + requiredVersion;
            case INCOMPATIBLE:
                return "必须手动升级配置，参考迁移指南";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 记录版本检查结果
     */
    public void logCheckResult(String configName, CompatibilityResult result) {
        switch (result.getStatus()) {
            case COMPATIBLE:
                FactorCraftMod.LOGGER.debug("[Config] {} 版本检查通过：{}", configName, result.getConfigVersion());
                break;
            case COMPATIBLE_WARN:
                FactorCraftMod.LOGGER.warn("[Config] {} 版本警告：{} - {}", configName, result.getMessage(), 
                                          getUpgradeAdvice(result.getConfigVersion(), result.getRequiredVersion()));
                break;
            case INCOMPATIBLE:
                FactorCraftMod.LOGGER.error("[Config] {} 版本不兼容：{} - {}", configName, result.getMessage(),
                                           getUpgradeAdvice(result.getConfigVersion(), result.getRequiredVersion()));
                break;
        }
    }
}
