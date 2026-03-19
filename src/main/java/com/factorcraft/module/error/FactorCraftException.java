package com.factorcraft.module.error;

/**
 * Factor Craft 自定义异常基类
 * 
 * 提供错误代码和友好消息支持
 */
public class FactorCraftException extends RuntimeException {
    
    private final String errorCode;
    private final String friendlyMessage;
    private final String suggestion;
    
    public FactorCraftException(String message) {
        super(message);
        this.errorCode = "FC-0000";
        this.friendlyMessage = message;
        this.suggestion = null;
    }
    
    public FactorCraftException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FC-0000";
        this.friendlyMessage = message;
        this.suggestion = null;
    }
    
    public FactorCraftException(String errorCode, String message, String friendlyMessage) {
        super(message);
        this.errorCode = errorCode;
        this.friendlyMessage = friendlyMessage;
        this.suggestion = null;
    }
    
    public FactorCraftException(String errorCode, String message, String friendlyMessage, String suggestion) {
        super(message);
        this.errorCode = errorCode;
        this.friendlyMessage = friendlyMessage;
        this.suggestion = suggestion;
    }
    
    public FactorCraftException(String errorCode, String message, String friendlyMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.friendlyMessage = friendlyMessage;
        this.suggestion = null;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getFriendlyMessage() {
        return friendlyMessage;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    /**
     * 机器异常
     */
    public static class MachineException extends FactorCraftException {
        public MachineException(String machineType, String operation, String details) {
            super("FC-MACH", 
                String.format("Machine error [%s]: %s - %s", machineType, operation, details),
                String.format("机器 %s 在 %s 时发生错误", machineType, operation),
                "请检查机器结构是否完整，能源是否充足");
        }
        
        public MachineException(String machineType, String operation, Throwable cause) {
            super("FC-MACH",
                String.format("Machine error [%s]: %s", machineType, operation),
                String.format("机器 %s 在 %s 时发生错误", machineType, operation),
                cause);
        }
    }
    
    /**
     * Factor 网络异常
     */
    public static class NetworkException extends FactorCraftException {
        public NetworkException(String operation, String details) {
            super("FC-NET",
                String.format("Factor network error: %s - %s", operation, details),
                String.format("Factor 网络 %s 时发生错误", operation),
                "请检查网络连接和传输器状态");
        }
    }
    
    /**
     * 任务异常
     */
    public static class QuestException extends FactorCraftException {
        public QuestException(String questId, String operation, String details) {
            super("FC-QUEST",
                String.format("Quest error [%s]: %s - %s", questId, operation, details),
                String.format("任务 %s %s 时发生错误", questId, operation),
                "请尝试重新接受任务");
        }
    }
    
    /**
     * 配置异常
     */
    public static class ConfigException extends FactorCraftException {
        public ConfigException(String configFile, String details) {
            super("FC-CFG",
                String.format("Config error [%s]: %s", configFile, details),
                "配置文件加载失败",
                "请检查配置文件格式是否正确");
        }
    }
    
    /**
     * 数据异常
     */
    public static class DataException extends FactorCraftException {
        public DataException(String dataType, String details) {
            super("FC-DATA",
                String.format("Data error [%s]: %s", dataType, details),
                "数据加载失败",
                "请尝试重新加载或联系管理员");
        }
    }
}