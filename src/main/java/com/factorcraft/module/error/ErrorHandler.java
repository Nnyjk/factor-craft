package com.factorcraft.module.error;

import com.factorcraft.FactorCraftMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统一错误处理器
 * 
 * 提供错误捕获、日志记录、玩家反馈的一站式处理
 */
public class ErrorHandler {
    
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("FactorCraft:Errors");
    
    // 错误计数器（用于统计）
    private static final ConcurrentHashMap<String, AtomicInteger> ERROR_COUNTERS = new ConcurrentHashMap<>();
    
    // 最近错误记录（用于诊断）
    private static final ConcurrentHashMap<String, Long> LAST_ERROR_TIMES = new ConcurrentHashMap<>();
    
    /**
     * 处理异常（不崩溃）
     * 
     * @param context 错误上下文描述
     * @param throwable 异常对象
     */
    public static void handleError(String context, Throwable throwable) {
        handleError(context, throwable, null);
    }
    
    /**
     * 处理异常并通知玩家
     * 
     * @param context 错误上下文描述
     * @param throwable 异常对象
     * @param player 可选的玩家对象（用于发送反馈）
     */
    public static void handleError(String context, Throwable throwable, ServerPlayerEntity player) {
        String errorType = throwable.getClass().getSimpleName();
        String errorCode = generateErrorCode(context, errorType);
        
        // 记录错误计数
        ERROR_COUNTERS.computeIfAbsent(errorCode, k -> new AtomicInteger(0)).incrementAndGet();
        LAST_ERROR_TIMES.put(errorCode, System.currentTimeMillis());
        
        // 分级日志记录
        if (isCriticalError(throwable)) {
            ERROR_LOGGER.error("[{}] {} - {}", errorCode, context, throwable.getMessage());
            if (DebugConfig.isDebugEnabled()) {
                ERROR_LOGGER.error("Stack trace:", throwable);
            }
        } else {
            ERROR_LOGGER.warn("[{}] {} - {}", errorCode, context, throwable.getMessage());
            if (DebugConfig.isDebugEnabled()) {
                ERROR_LOGGER.debug("Stack trace:", throwable);
            }
        }
        
        // 发送玩家反馈
        if (player != null) {
            sendPlayerFeedback(player, errorCode, context, throwable);
        }
    }
    
    /**
     * 处理关键错误（可能导致功能不可用）
     */
    public static void handleCriticalError(String context, Throwable throwable) {
        ERROR_LOGGER.error("[CRITICAL] {}", context);
        ERROR_LOGGER.error("Exception:", throwable);
        
        // 记录到错误统计
        String errorCode = "CRITICAL-" + throwable.getClass().getSimpleName();
        ERROR_COUNTERS.computeIfAbsent(errorCode, k -> new AtomicInteger(0)).incrementAndGet();
    }
    
    /**
     * 记录警告（非异常情况）
     */
    public static void logWarning(String module, String message) {
        ERROR_LOGGER.warn("[{}] {}", module, message);
        if (DebugConfig.isDebugEnabled()) {
            FactorCraftMod.LOGGER.debug("[Warning][{}] {}", module, message);
        }
    }
    
    /**
     * 记录调试信息
     */
    public static void logDebug(String module, String message) {
        if (DebugConfig.isDebugEnabled()) {
            FactorCraftMod.LOGGER.debug("[{}][Debug] {}", module, message);
        }
    }
    
    /**
     * 记录机器状态变化（用于诊断）
     */
    public static void logMachineState(String machineType, String pos, String state) {
        if (DebugConfig.isMachineDebugEnabled()) {
            FactorCraftMod.LOGGER.debug("[Machine][{}] {} - State: {}", machineType, pos, state);
        }
    }
    
    /**
     * 记录 Factor 网络事件
     */
    public static void logFactorNetwork(String event, String details) {
        if (DebugConfig.isNetworkDebugEnabled()) {
            FactorCraftMod.LOGGER.debug("[FactorNetwork] {} - {}", event, details);
        }
    }
    
    /**
     * 安全执行代码块（捕获所有异常）
     * 
     * @param operation 操作名称
     * @param runnable 要执行的代码
     * @return 是否成功执行
     */
    public static boolean safeExecute(String operation, SafeRunnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (Exception e) {
            handleError(operation, e);
            return false;
        }
    }
    
    /**
     * 安全执行代码块并返回结果
     */
    public static <T> T safeExecute(String operation, SafeSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            handleError(operation, e);
            return defaultValue;
        }
    }
    
    /**
     * 发送玩家反馈
     */
    private static void sendPlayerFeedback(ServerPlayerEntity player, String errorCode, String context, Throwable throwable) {
        // 使用错误代码发送简洁的反馈
        String friendlyMessage = getFriendlyMessage(throwable);
        
        player.sendMessage(Text.literal("§c[FactorCraft] §e" + friendlyMessage), false);
        player.sendMessage(Text.literal("§7错误代码: " + errorCode + " | 详情请查看服务器日志"), false);
        
        // 提供解决建议
        String suggestion = getSuggestion(throwable);
        if (suggestion != null) {
            player.sendMessage(Text.literal("§7建议: " + suggestion), false);
        }
    }
    
    /**
     * 生成错误代码
     */
    private static String generateErrorCode(String context, String errorType) {
        // 简单的错误代码生成
        int hash = (context + errorType).hashCode();
        return "FC-" + Math.abs(hash % 10000);
    }
    
    /**
     * 判断是否为关键错误
     */
    private static boolean isCriticalError(Throwable throwable) {
        return throwable instanceof OutOfMemoryError ||
               throwable instanceof LinkageError ||
               throwable instanceof VirtualMachineError;
    }
    
    /**
     * 获取友好的错误消息
     */
    private static String getFriendlyMessage(Throwable throwable) {
        String message = throwable.getMessage();
        
        if (message == null || message.isEmpty()) {
            return "发生了一个错误";
        }
        
        // 简化常见错误消息
        if (throwable instanceof NullPointerException) {
            return "数据缺失导致操作失败";
        }
        if (throwable instanceof IllegalArgumentException) {
            return "参数无效: " + message;
        }
        if (throwable instanceof IllegalStateException) {
            return "状态异常: " + message;
        }
        
        return message.length() > 50 ? message.substring(0, 50) + "..." : message;
    }
    
    /**
     * 获取解决建议
     */
    private static String getSuggestion(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return "请尝试重新加载区块或重启游戏";
        }
        if (throwable instanceof IllegalArgumentException) {
            return "请检查输入参数是否正确";
        }
        return null;
    }
    
    /**
     * 获取错误统计
     */
    public static String getErrorStats() {
        StringBuilder sb = new StringBuilder("=== 错误统计 ===\n");
        ERROR_COUNTERS.forEach((code, count) -> {
            long lastTime = LAST_ERROR_TIMES.getOrDefault(code, 0L);
            sb.append(String.format("%s: %d 次 (最近: %tT)\n", 
                code, count.get(), lastTime));
        });
        return sb.toString();
    }
    
    /**
     * 清除错误统计
     */
    public static void clearStats() {
        ERROR_COUNTERS.clear();
        LAST_ERROR_TIMES.clear();
    }
    
    // 函数式接口
    @FunctionalInterface
    public interface SafeRunnable {
        void run() throws Exception;
    }
    
    @FunctionalInterface
    public interface SafeSupplier<T> {
        T get() throws Exception;
    }
}