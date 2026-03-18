package com.factorcraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描历史数据
 * 
 * 用于 Factor 扫描仪存储多次扫描结果，支持趋势分析
 * 
 * @param entries 扫描记录列表（最多保留 10 条）
 * @param lastScanTime 上次扫描时间戳（毫秒）
 */
public record ScanHistory(
    List<ScanEntry> entries,
    long lastScanTime
) {
    
    /**
     * 扫描记录
     * 
     * @param timestamp 扫描时间戳（毫秒）
     * @param concentration Factor 浓度（0.0-1.0）
     */
    public record ScanEntry(long timestamp, double concentration) {}
    
    /**
     * 最大历史记录数量
     */
    public static final int MAX_ENTRIES = 10;
    
    /**
     * 缓存过期时间（5 分钟）
     */
    public static final long CACHE_EXPIRY_MS = 5 * 60 * 1000;
    
    /**
     * 趋势检测阈值（5% 变化）
     */
    public static final double TREND_THRESHOLD = 0.05;
    
    /**
     * ScanEntry Codec
     */
    public static final Codec<ScanEntry> CODEC_ENTRY = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.LONG.fieldOf("timestamp").forGetter(ScanEntry::timestamp),
            Codec.DOUBLE.fieldOf("concentration").forGetter(ScanEntry::concentration)
        ).apply(instance, ScanEntry::new)
    );
    
    /**
     * ScanEntry PacketCodec
     */
    public static final PacketCodec<RegistryByteBuf, ScanEntry> STREAM_CODEC_ENTRY =
        PacketCodec.tuple(
            PacketCodecs.LONG,
            ScanEntry::timestamp,
            PacketCodecs.DOUBLE,
            ScanEntry::concentration,
            ScanEntry::new
        );
    
    /**
     * Codec 用于序列化/反序列化
     */
    public static final Codec<ScanHistory> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance ->
        instance.group(
            CODEC_ENTRY.listOf().fieldOf("entries").forGetter(ScanHistory::entries),
            Codec.LONG.fieldOf("lastScanTime").forGetter(ScanHistory::lastScanTime)
        ).apply(instance, ScanHistory::new)
    );
    
    /**
     * PacketCodec 用于网络同步
     */
    public static final PacketCodec<RegistryByteBuf, ScanHistory> STREAM_CODEC =
        PacketCodec.tuple(
            STREAM_CODEC_ENTRY.collect(PacketCodecs.toList()),
            ScanHistory::entries,
            PacketCodecs.LONG,
            ScanHistory::lastScanTime,
            ScanHistory::new
        );
    
    /**
     * 创建空的扫描历史
     */
    public static ScanHistory empty() {
        return new ScanHistory(new ArrayList<>(), 0);
    }
    
    /**
     * 添加新的扫描记录
     * 
     * @param concentration 当前浓度
     * @return 更新后的 ScanHistory
     */
    public ScanHistory addEntry(double concentration) {
        List<ScanEntry> newEntries = new ArrayList<>(this.entries);
        long currentTime = System.currentTimeMillis();
        
        // 添加新记录
        newEntries.add(new ScanEntry(currentTime, concentration));
        
        // 限制历史记录数量
        while (newEntries.size() > MAX_ENTRIES) {
            newEntries.remove(0);
        }
        
        return new ScanHistory(newEntries, currentTime);
    }
    
    /**
     * 获取趋势描述
     * 
     * @param currentConcentration 当前浓度
     * @return 趋势："上升 ↑" / "下降 ↓" / "稳定" / "首次扫描"
     */
    public String getTrend(double currentConcentration) {
        if (entries.isEmpty()) {
            return "首次扫描";
        }
        
        ScanEntry lastEntry = entries.get(entries.size() - 1);
        double diff = currentConcentration - lastEntry.concentration();
        
        if (diff > TREND_THRESHOLD) {
            return "上升 ↑ (" + String.format("+%.1f", diff * 100) + "%)";
        } else if (diff < -TREND_THRESHOLD) {
            return "下降 ↓ (" + String.format("%.1f", diff * 100) + "%)";
        } else {
            return "稳定";
        }
    }
    
    /**
     * 检查缓存是否过期
     * 
     * @return 是否过期
     */
    public boolean isExpired() {
        if (lastScanTime == 0) {
            return true;
        }
        return System.currentTimeMillis() - lastScanTime > CACHE_EXPIRY_MS;
    }
    
    /**
     * 获取扫描次数
     * 
     * @return 扫描次数
     */
    public int getScanCount() {
        return entries.size();
    }
}
