package com.factorcraft.module.cycle;

import com.factorcraft.module.cycle.CycleModule.CyclePhase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CycleModule 单元测试
 * 
 * 测试潮汐周期系统功能
 */
@DisplayName("CycleModule Tests")
public class CycleModuleTest {
    
    @Test
    @DisplayName("单例模式 - 实例一致性")
    public void testSingletonPattern() {
        CycleModule instance1 = CycleModule.getInstance();
        CycleModule instance2 = CycleModule.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("周期长度 - 默认值验证")
    public void testDefaultCycleLength() {
        CycleModule module = CycleModule.getInstance();
        
        assertEquals(24000, module.getCycleLength(), "默认周期应该是 24000 ticks (1 个 Minecraft 日)");
    }
    
    @Test
    @DisplayName("周期长度 - 自定义设置")
    public void testCustomCycleLength() {
        CycleModule module = CycleModule.getInstance();
        
        module.setCycleLength(12000); // 半小时
        assertEquals(12000, module.getCycleLength());
        
        // 恢复默认
        module.setCycleLength(24000);
    }
    
    @Test
    @DisplayName("振幅 - 默认值验证")
    public void testDefaultAmplitude() {
        CycleModule module = CycleModule.getInstance();
        
        assertEquals(0.3, module.getAmplitude(), 0.001, "默认振幅应该是 0.3 (30%)");
    }
    
    @Test
    @DisplayName("振幅 - 自定义设置")
    public void testCustomAmplitude() {
        CycleModule module = CycleModule.getInstance();
        
        module.setAmplitude(0.5);
        assertEquals(0.5, module.getAmplitude(), 0.001);
        
        // 验证范围限制
        module.setAmplitude(1.5); // 超过 1.0
        assertTrue(module.getAmplitude() <= 1.0, "振幅不应该超过 1.0");
        
        // 恢复默认
        module.setAmplitude(0.3);
    }
    
    @Test
    @DisplayName("Factor 倍率 - 范围验证")
    public void testFactorMultiplierRange() {
        CycleModule module = CycleModule.getInstance();
        
        // 测试多个时间点的倍率
        for (long tick = 0; tick < 24000; tick += 1000) {
            module.tick(tick);
            double multiplier = module.getFactorMultiplier();
            
            assertTrue(multiplier >= 0.7 && multiplier <= 1.3,
                "Factor 倍率应该在 0.7-1.3 范围内：" + multiplier);
        }
    }
    
    @Test
    @DisplayName("Factor 倍率 - 峰值计算")
    public void testFactorMultiplierAtPeak() {
        CycleModule module = CycleModule.getInstance();
        
        // 峰值在 1/4 周期处 (6000 ticks)
        module.tick(6000);
        double peakMultiplier = module.getFactorMultiplier();
        
        assertTrue(peakMultiplier > 1.2, "峰值倍率应该接近最大值：" + peakMultiplier);
    }
    
    @Test
    @DisplayName("Factor 倍率 - 谷值计算")
    public void testFactorMultiplierAtTrough() {
        CycleModule module = CycleModule.getInstance();
        
        // 谷值在 3/4 周期处 (18000 ticks)
        module.tick(18000);
        double troughMultiplier = module.getFactorMultiplier();
        
        assertTrue(troughMultiplier < 0.8, "谷值倍率应该接近最小值：" + troughMultiplier);
    }
    
    @Test
    @DisplayName("周期阶段 - 上升期检测")
    public void testCyclePhaseRising() {
        CycleModule module = CycleModule.getInstance();
        
        // 0-6000 ticks 应该是上升期
        module.tick(3000);
        assertEquals(CyclePhase.RISING, module.getCurrentPhase());
    }
    
    @Test
    @DisplayName("周期阶段 - 峰值期检测")
    public void testCyclePhasePeak() {
        CycleModule module = CycleModule.getInstance();
        
        // 6000 ticks 附近应该是峰值期
        module.tick(6000);
        assertEquals(CyclePhase.PEAK, module.getCurrentPhase());
    }
    
    @Test
    @DisplayName("周期阶段 - 下降期检测")
    public void testCyclePhaseFalling() {
        CycleModule module = CycleModule.getInstance();
        
        // 6000-12000 ticks 应该是下降期
        module.tick(9000);
        assertEquals(CyclePhase.FALLING, module.getCurrentPhase());
    }
    
    @Test
    @DisplayName("周期阶段 - 谷值期检测")
    public void testCyclePhaseTrough() {
        CycleModule module = CycleModule.getInstance();
        
        // 18000 ticks 附近应该是谷值期
        module.tick(18000);
        assertEquals(CyclePhase.TROUGH, module.getCurrentPhase());
    }
    
    @Test
    @DisplayName("峰值检测 - isPeakTick 方法")
    public void testIsPeakTick() {
        CycleModule module = CycleModule.getInstance();
        
        // 6000 ticks 应该是峰值
        assertTrue(module.isPeakTick(6000));
        
        // 远离峰值的位置不应该被识别
        assertFalse(module.isPeakTick(0));
        assertFalse(module.isPeakTick(12000));
        assertFalse(module.isPeakTick(18000));
    }
    
    @Test
    @DisplayName("谷值检测 - isTroughTick 方法")
    public void testIsTroughTick() {
        CycleModule module = CycleModule.getInstance();
        
        // 18000 ticks 应该是谷值
        assertTrue(module.isTroughTick(18000));
        
        // 远离谷值的位置不应该被识别
        assertFalse(module.isTroughTick(0));
        assertFalse(module.isTroughTick(6000));
        assertFalse(module.isTroughTick(12000));
    }
    
    @Test
    @DisplayName("距离峰值 - 计算验证")
    public void testTicksUntilNextPeak() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(0);
        long untilPeak = module.getTicksUntilNextPeak();
        
        assertEquals(6000, untilPeak, "从 0 开始应该距离峰值 6000 ticks");
    }
    
    @Test
    @DisplayName("距离谷值 - 计算验证")
    public void testTicksUntilNextTrough() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(0);
        long untilTrough = module.getTicksUntilNextTrough();
        
        assertEquals(18000, untilTrough, "从 0 开始应该距离谷值 18000 ticks");
    }
    
    @Test
    @DisplayName("周期进度 - 计算验证")
    public void testCycleProgress() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(0);
        assertEquals(0.0, module.getCycleProgress(), 0.001);
        
        module.tick(6000);
        assertEquals(0.25, module.getCycleProgress(), 0.001);
        
        module.tick(12000);
        assertEquals(0.5, module.getCycleProgress(), 0.001);
        
        module.tick(18000);
        assertEquals(0.75, module.getCycleProgress(), 0.001);
        
        module.tick(24000);
        assertEquals(0.0, module.getCycleProgress(), 0.001); // 循环
    }
    
    @Test
    @DisplayName("预测功能 - 未来倍率预测")
    public void testPredictFactorMultiplier() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(0);
        
        // 预测 6000 ticks 后的倍率 (应该是峰值)
        double predictedPeak = module.predictFactorMultiplier(6000);
        assertTrue(predictedPeak > 1.2, "预测的峰值倍率应该 > 1.2: " + predictedPeak);
        
        // 预测 18000 ticks 后的倍率 (应该是谷值)
        double predictedTrough = module.predictFactorMultiplier(18000);
        assertTrue(predictedTrough < 0.8, "预测的谷值倍率应该 < 0.8: " + predictedTrough);
    }
    
    @Test
    @DisplayName("变化率 - 计算验证")
    public void testChangeRate() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(0);
        double rateAtStart = module.getChangeRate();
        
        // 在起始点，变化率应该是正的 (上升期)
        assertTrue(rateAtStart > 0, "起始点变化率应该为正：" + rateAtStart);
        
        module.tick(6000);
        double rateAtPeak = module.getChangeRate();
        
        // 在峰值，变化率应该接近 0
        assertTrue(Math.abs(rateAtPeak) < 0.0001, "峰值变化率应该接近 0: " + rateAtPeak);
    }
    
    @Test
    @DisplayName("状态描述 - 格式验证")
    public void testStatusDescription() {
        CycleModule module = CycleModule.getInstance();
        
        module.tick(3000);
        String status = module.getStatus();
        
        assertNotNull(status);
        assertTrue(status.contains("周期状态"));
        assertTrue(status.contains("倍率"));
        assertTrue(status.contains("距峰值"));
        assertTrue(status.contains("距谷值"));
    }
    
    @Test
    @Disabled("需要 Minecraft 注册表环境，在单元测试中无法运行")
    @DisplayName("初始化 - 方法可调用")
    public void testInitializeMethod() {
        CycleModule module = CycleModule.getInstance();
        
        assertDoesNotThrow(() -> module.initialize());
    }
    
    @Test
    @DisplayName("周期循环 - 超过周期长度")
    public void testCycleWrapping() {
        CycleModule module = CycleModule.getInstance();
        
        // 超过一个周期
        module.tick(30000); // 24000 + 6000
        
        // 应该等同于 6000 ticks
        assertEquals(6000, module.getCurrentTick() % 24000);
        assertEquals(CyclePhase.PEAK, module.getCurrentPhase());
    }
}
