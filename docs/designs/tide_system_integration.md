# TideSystem 接入计划

## 背景

TideSystem 已实现完整的潮汐计算逻辑，但未接入主系统。FactorService 也有独立的潮汐计算，导致：
1. 代码重复
2. 参数不一致
3. 维护成本高

## 目标

统一潮汐系统，让 TideSystem 成为 Factor 核心功能的一部分。

---

## Phase 1: 统一维度参数 ✅ 已完成

### 1.1 标准化 DimensionType 参数

**决策：采用设计文档的参数体系**

| 维度 | 基准值 | 幅度 | 周期(ticks) | Factor 范围 |
|------|--------|------|-------------|-------------|
| 主世界 | 0.5 | 0.2 | 192000 (8天) | 0.3 - 0.7 |
| 下界 | 1.5 | 0.6 | 96000 (4天) | 0.9 - 2.1 |
| 末地 | 3.0 | 1.2 | 288000 (12天) | 1.8 - 4.2 |

### 1.2 DimensionType 实现

```java
public enum DimensionType {
    OVERWORLD("minecraft:overworld", 0.5, 0.2, 192000),
    NETHER("minecraft:the_nether", 1.5, 0.6, 96000),
    END("minecraft:the_end", 3.0, 1.2, 288000);
}
```

---

## Phase 2: 整合 TideSystem 到 FactorService ✅ 已完成

### 2.1 FactorService 重构

FactorService 使用 DimensionType 计算潮汐：

```java
public final class FactorService implements FactorApi {
    
    // 使用 DimensionType 计算潮汐变化
    private double calculateTideDelta(DimensionType type, long tick) {
        double currentTide = type.calculateFactor(tick);
        double nextTide = type.calculateFactor(tick + 1);
        return nextTide - currentTide;
    }
    
    // 获取潮汐状态
    public TideStatus getTideStatus(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(...);
        double deviation = calculateDeviation(currentFactor, type.baseValue());
        return getTideStatus(deviation);
    }
}
```

### 2.2 TideSystem 作为工具类

TideSystem 改为纯静态工具类：

```java
public final class TideSystem {
    public static double calculateDeviation(double current, double base) {}
    public static TideStatus getStatusFromDeviation(double deviation) {}
    public static long findNextPeakTick(DimensionType type, long tick) {}
    public static long findNextTroughTick(DimensionType type, long tick) {}
}
```

---

## Phase 3: 接入游戏循环 ✅ 已完成

### 3.1 FactorSystemModule 中调用潮汐效果

```java
@Override
public void initialize() {
    ServerTickEvents.END_WORLD_TICK.register(world -> {
        SERVICE.tick(world);
        
        // 每 1200 ticks (60秒) 检查潮汐效果
        if (world.getTime() % 1200 == 0) {
            checkTideEffects(world, state, dimensionType);
        }
    });
}
```

---

## Phase 4: 扩展 API ✅ 已完成

### 4.1 FactorApi 接口

```java
public interface FactorApi {
    // 基础查询
    double getFactor(ServerWorld world);
    int getTier(ServerWorld world);
    OptionalLong predictCrossing(ServerWorld world, double target);
    
    // 潮汐相关
    TideStatus getTideStatus(ServerWorld world);
    double getDeviation(ServerWorld world);
    long getNextPeakTick(ServerWorld world);
    long getNextTroughTick(ServerWorld world);
    boolean isOutbreakTime(ServerWorld world);
    double getTideCycleProgress(ServerWorld world);
}
```

---

## Phase 5: FactorTier 偏离度体系 ✅ 已完成

### 5.1 FactorTier 重构

Tier 由偏离度决定，而非绝对值：

```java
public enum FactorTier {
    DEPLETED(0, -1.0, -0.5),    // 偏离 < -50%
    LOW_ENERGY(1, -0.5, -0.2),  // 偏离 -50% ~ -20%
    STABLE(2, -0.2, 0.2),       // 偏离 -20% ~ +20%
    HIGH_ENERGY(3, 0.2, 0.5),   // 偏离 +20% ~ +50%
    OVERLOAD(4, 0.5, INF);      // 偏离 > +50%
    
    public static FactorTier fromFactor(double factor, double baseValue) {
        double deviation = (factor - baseValue) / baseValue;
        return fromDeviation(deviation);
    }
}
```

---

## Phase 6: 区块级扩散接入 ✅ 已完成

### 6.1 DiffusionSystem

```java
// 在 FactorSystemModule 中
if (world.getTime() % 100 == 0) { // 每 5 秒
    DiffusionSystem.processAllDiffusion(world);
}
```

---

## 实施状态

| Phase | 任务 | 状态 |
|-------|------|------|
| 1 | 统一 DimensionType 参数 | ✅ 完成 |
| 2 | 整合 TideSystem 到 FactorService | ✅ 完成 |
| 3 | 接入游戏循环 | ✅ 完成 |
| 4 | 扩展 FactorApi 接口 | ✅ 完成 |
| 5 | FactorTier 偏离度体系 | ✅ 完成 |
| 6 | 区块级扩散接入 | ✅ 完成 |

---

## 传输倍率参考

| 传输方向 | 倍率 |
|----------|------|
| 下界 → 主世界 | 3.0x |
| 末地 → 主世界 | 6.0x |
| 末地 → 下界 | 2.0x |
| 主世界 → 下界 | 0.33x |
| 主世界 → 末地 | 0.17x |
| 下界 → 末地 | 0.5x |

---

## 验收标准

- [x] 维度基准值符合设计文档
- [x] 传输倍率计算正确
- [x] 潮汐周期正确
- [x] FactorTier 基于偏离度
- [x] 所有测试通过