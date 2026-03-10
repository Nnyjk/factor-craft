package com.factorcraft.module.cycle;

/**
 * Factor 潮汐周期模块
 * 
 * 负责管理 Factor 能量的周期性波动
 */
public class CycleModule {
    
    private static CycleModule instance;
    
    /** 默认周期长度 (ticks) */
    private int cycleLength;
    
    /** 当前周期位置 */
    private long currentTick;
    
    /** 周期振幅系数 */
    private double amplitude;
    
    private CycleModule() {
        this.cycleLength = 24000;
        this.currentTick = 0;
        this.amplitude = 0.3;
    }
    
    public static CycleModule getInstance() {
        if (instance == null) {
            instance = new CycleModule();
        }
        return instance;
    }
    
    public void tick(long worldTick) {
        this.currentTick = worldTick % cycleLength;
    }
    
    public double getFactorMultiplier() {
        double progress = (double) currentTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return 1.0 + amplitude * Math.sin(angle);
    }
    
    public boolean isPeakTick(long tick) {
        long quarterCycle = cycleLength / 4;
        return Math.abs(tick - quarterCycle) < 100;
    }
    
    public boolean isTroughTick(long tick) {
        long threeQuarterCycle = cycleLength * 3 / 4;
        return Math.abs(tick - threeQuarterCycle) < 100;
    }
    
    public CyclePhase getCurrentPhase() {
        long quarterCycle = cycleLength / 4;
        long position = currentTick % cycleLength;
        
        if (position < quarterCycle - 100) {
            return CyclePhase.RISING;
        } else if (position < quarterCycle + 100) {
            return CyclePhase.PEAK;
        } else if (position < quarterCycle * 3 - 100) {
            return CyclePhase.FALLING;
        } else if (position < quarterCycle * 3 + 100) {
            return CyclePhase.TROUGH;
        } else {
            return CyclePhase.RISING;
        }
    }
    
    public long getTicksUntilNextPeak() {
        long quarterCycle = cycleLength / 4;
        long position = currentTick % cycleLength;
        
        if (position < quarterCycle) {
            return quarterCycle - position;
        } else {
            return cycleLength + quarterCycle - position;
        }
    }
    
    public long getTicksUntilNextTrough() {
        long threeQuarterCycle = cycleLength * 3 / 4;
        long position = currentTick % cycleLength;
        
        if (position < threeQuarterCycle) {
            return threeQuarterCycle - position;
        } else {
            return cycleLength + threeQuarterCycle - position;
        }
    }
    
    public void setCycleLength(int ticks) {
        this.cycleLength = ticks;
    }
    
    public int getCycleLength() {
        return cycleLength;
    }
    
    public void setAmplitude(double amplitude) {
        this.amplitude = Math.max(0.0, Math.min(1.0, amplitude));
    }
    
    public double getAmplitude() {
        return amplitude;
    }
    
    public double getCycleProgress() {
        return (double) currentTick / cycleLength;
    }
    
    public long getCurrentTick() {
        return currentTick;
    }
    
    public double predictFactorMultiplier(long ticksAhead) {
        long futureTick = (currentTick + ticksAhead) % cycleLength;
        double progress = (double) futureTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return 1.0 + amplitude * Math.sin(angle);
    }
    
    public double getChangeRate() {
        double progress = (double) currentTick / cycleLength;
        double angle = 2 * Math.PI * progress;
        return amplitude * 2 * Math.PI * Math.cos(angle) / cycleLength;
    }
    
    public String getStatus() {
        CyclePhase phase = getCurrentPhase();
        double multiplier = getFactorMultiplier();
        long untilPeak = getTicksUntilNextPeak();
        long untilTrough = getTicksUntilNextTrough();
        
        return String.format("周期状态：%s | 倍率：%.2f | 距峰值：%d ticks | 距谷值：%d ticks",
            phase.getDisplayName(), multiplier, untilPeak, untilTrough);
    }
    
    public void initialize() {
        System.out.println("[CycleModule] 潮汐周期模块已初始化");
        System.out.println("[CycleModule] 周期长度：" + cycleLength + " ticks");
        System.out.println("[CycleModule] 振幅：" + (amplitude * 100) + "%");
    }
    
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
}
