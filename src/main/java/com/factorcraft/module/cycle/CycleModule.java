package com.factorcraft.module.cycle;

/**
 * Factor 潮汐周期模块
 * 
 * 负责管理 Factor 能量的周期性波动
 * 
 * 功能:
 * - 潮汐周期计算
 * - 峰值/谷值检测
 * - 周期事件触发
 * - 周期配置系统
 */
public class CycleModule {
    
    private static CycleModule instance;
    
    /** 默认周期长度 (ticks) */
    private int cycleLength;
    
    /** 当前周期位置 (0 到 cycleLength-1) */
    private long currentTick;
    
    /** 周期振幅系数 */
    private double amplitude;
    
    /** 周期事件监听器 */
    private CycleEventListener eventListener;
    
    private CycleModule() {
        this.cycleLength = 24000; // 默认 1 个 Minecraft 日 (20 分钟)
        this.currentTick = 0;
        this.amplitude = 0.3; // 默认振幅 30%
    }
    
    public static CycleModule getInstance() {
        if (instance == null) {
            instance = new CycleModule();
        }
        return instance;
    }
    
    /**
     * 更新周期状态
     * 
     * @param worldTick 世界时间 (ticks)
     */
    public void tick(long worldTick) {
        this.currentTick = worldTick % cycleLength;
        
        // 检测峰值/谷值
        if (isPeakTick(currentTick)) {
            onPeakReached();
        } else if (isTroughTick(currentTick)) {
            onTroughReached();
        }
        
        // 触发周期事件
        if (eventListener != null) {
            eventListener.onCycleTick(currentTick, getFactorMultiplier());
        }
    }
    
    /**
     * 获取当前 Factor 倍率
     * 
     * 基于正弦波计算：1.0 + amplitude * sin(2π * tick / cycleLength)
     * 
     * @return Factor 倍率 (0.7 - 1.3)
     */
    public double getFactorMultiplier() {
        double progress = (double) currentTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return 1.0 + amplitude * Math.sin(angle);
    }
    
    /**
     * 判断是否为峰值时刻
     */
    public boolean isPeakTick(long tick) {
        long quarterCycle = cycleLength / 4;
        return Math.abs(tick - quarterCycle) < 100; // 峰值在 1/4 周期处
    }
    
    /**
     * 判断是否为谷值时刻
     */
    public boolean isTroughTick(long tick) {
        long threeQuarterCycle = cycleLength * 3 / 4;
        return Math.abs(tick - threeQuarterCycle) < 100; // 谷值在 3/4 周期处
    }
    
    /**
     * 获取当前周期阶段
     */
    public CyclePhase getCurrentPhase() {
        long quarterCycle = cycleLength / 4;
        long position = currentTick % cycleLength;
        
        if (position < quarterCycle - 100) {
            return CyclePhase.RISING; // 上升期 (0-5900)
        } else if (position < quarterCycle + 100) {
            return CyclePhase.PEAK; // 峰值期 (5900-6100)
        } else if (position < quarterCycle * 3 - 100) {
            return CyclePhase.FALLING; // 下降期 (6100-17900)
        } else if (position < quarterCycle * 3 + 100) {
            return CyclePhase.TROUGH; // 谷值期 (17900-18100)
        } else {
            return CyclePhase.RISING; // 回到上升期
        }
    }
    
    /**
     * 获取距离下一个峰值的 ticks
     */
    public long getTicksUntilNextPeak() {
        long quarterCycle = cycleLength / 4;
        long position = currentTick % cycleLength;
        
        if (position < quarterCycle) {
            return quarterCycle - position;
        } else {
            return cycleLength + quarterCycle - position;
        }
    }
    
    /**
     * 获取距离下一个谷值的 ticks
     */
    public long getTicksUntilNextTrough() {
        long threeQuarterCycle = cycleLength * 3 / 4;
        long position = currentTick % cycleLength;
        
        if (position < threeQuarterCycle) {
            return threeQuarterCycle - position;
        } else {
            return cycleLength + threeQuarterCycle - position;
        }
    }
    
    /**
     * 设置周期长度
     */
    public void setCycleLength(int ticks) {
        this.cycleLength = ticks;
        System.out.println("[CycleModule] 周期长度设置为 " + ticks + " ticks (" + (ticks / 1200.0) + " 小时)");
    }
    
    /**
     * 获取周期长度
     */
    public int getCycleLength() {
        return cycleLength;
    }
    
    /**
     * 设置振幅
     */
    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0.0, Math.min(1.0, amplitude));
        System.out.println("[CycleModule] 振幅设置为 " + (amplitude * 100) + "%");
    }
    
    /**
     * 获取振幅
     */
    public double getAmplitude() {
        return amplitude;
    }
    
    /**
     * 设置周期事件监听器
     */
    public void setEventListener(CycleEventListener listener) {
        this.eventListener = listener;
    }
    
    /**
     * 峰值到达回调
     */
    private void onPeakReached() {
        System.out.println("[CycleModule] ⚡ 峰值时刻 - Factor 活性最高");
        if (eventListener != null) {
            eventListener.onPeakReached();
        }
    }
    
    /**
     * 谷值到达回调
     */
    private void onTroughReached() {
        System.out.println("[CycleModule] 🌑 谷值时刻 - Factor 活性最低");
        if (eventListener != null) {
            eventListener.onTroughReached();
        }
    }
    
    /**
     * 获取当前周期进度 (0.0 - 1.0)
     */
    public double getCycleProgress() {
        return (double) currentTick / cycleLength;
    }
    
    /**
     * 获取当前 ticks 位置
     */
    public long getCurrentTick() {
        return currentTick;
    }
    
    /**
     * 预测未来某个时刻的 Factor 倍率
     */
    public double predictFactorMultiplier(long ticksAhead) {
        long futureTick = (currentTick + ticksAhead) % cycleLength;
        double progress = (double) futureTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return 1.0 + amplitude * Math.sin(angle);
    }
    
    /**
     * 计算 Factor 变化率 (导数)
     */
    public double getChangeRate() {
        double progress = (double) currentTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return amplitude * 2 * Math.PI * Math.cos(angle) / cycleLength;
    }
    
    /**
     * 获取周期状态描述
     */
    public String getStatus() {
        CyclePhase phase = getCurrentPhase();
        double multiplier = getFactorMultiplier();
        long untilPeak = getTicksUntilNextPeak();
        long untilTrough = getTicksUntilNextTrough();
        
        return String.format("周期状态：%s | 倍率：%.2f | 距峰值：%d ticks | 距谷值：%d ticks",
            phase.getDisplayName(), multiplier, untilPeak, untilTrough);
    }
    
    /**
     * 初始化周期模块
     * 同时初始化能源模块
     */
    public void initialize() {
        // 初始化能源模块
        com.factorcraft.module.cycle.energy.FactorEnergyModule.init();
        
        System.out.println("[CycleModule] 潮汐周期模块已初始化");
        System.out.println("[CycleModule] 周期长度：" + cycleLength + " ticks");
        System.out.println("[CycleModule] 振幅：" + (amplitude * 100) + "%");
    }
    
    /**
     * 周期阶段枚举
     */
    public enum CyclePhase {
        RISING("上升期"),
        PEAK("峰值期"),
        FALLING("下降期"),
        TROUGH("谷值期");
        
        private final String displayName;
        
        CyclePhase(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 周期事件监听器接口
     */
    public interface CycleEventListener {
        void onCycleTick(long tick, double factorMultiplier);
        void onPeakReached();
        void onTroughReached();
    }
}
