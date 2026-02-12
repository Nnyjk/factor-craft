package com.factorcraft.module.command.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令限流器：按 "执行者+命令" 维度统计 1 分钟窗口。
 */
public final class CommandRateLimiter {
    private final Map<String, Deque<Long>> usage = new HashMap<>();

    public synchronized boolean allow(String key, int rateLimitPerMinute, long nowMillis) {
        if (rateLimitPerMinute <= 0) {
            return true;
        }
        Deque<Long> hits = usage.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        long min = nowMillis - 60_000L;
        while (!hits.isEmpty() && hits.peekFirst() < min) {
            hits.removeFirst();
        }
        if (hits.size() >= rateLimitPerMinute) {
            return false;
        }
        hits.addLast(nowMillis);
        return true;
    }
}
