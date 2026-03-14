# TideSystem 接入计划

## 背景

TideSystem 已实现完整的潮汐计算逻辑，但未接入主系统。FactorService 也有独立的潮汐计算，导致：
1. 代码重复
2. 参数不一致
3. 维护成本高

## 目标

统一潮汐系统，让 TideSystem 成为 Factor 核心功能的一部分。

---

## Phase 1: 统一维度参数

### 1.1 标准化 DimensionType 参数

当前问题：
- FactorService 使用基准值 50/80/20
- DimensionType 使用基准值 0.5/1.5/3.0

**决策：采用 FactorService 的参数体系**（0-100 范围）

| 维度 | 基准值 | 幅度 | 周期(ticks) |
|------|--------|------|-------------|
| 主世界 | 50 | ±12 | 192000 (8天) |
| 下界 | 80 | ±8 | 96000 (4天) |
| 末地 | 20 | ±5 | 288000 (12天) |

### 1.2 更新 DimensionType

```java
public enum DimensionType {
    OVERWORLD("minecraft:overworld", 50, 12, 192000),
    NETHER("minecraft:the_nether", 80, 8, 96000),
    END("minecraft:the_end", 20, 5, 288000);
}
```

---

## Phase 2: 整合 TideSystem 到 FactorService

### 2.1 重构 FactorService

将 TideSystem 的功能作为 FactorService 的方法：

```java
public final class FactorService implements FactorApi {
    
    // 使用 DimensionType 计算潮汐
    public double getTideValue(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(world...);
        return type.calculateFactor(world.getTime());
    }
    
    // Factor 状态判断
    public TideSystem.FactorStatus getStatus(ServerWorld world) {
        double current = getFactor(world);
        double base = DimensionType.fromKey(...).baseValue();
        return TideSystem.getStatusFromDeviation(
            TideSystem.calculateDeviation(current, base)
        );
    }
    
    // 预测功能
    public long getNextPeakTick(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(...);
        return TideSystem.findNextPeakTick(type, world.getTime());
    }
}
```

### 2.2 保留 TideSystem 作为工具类

将 TideSystem 改为纯工具类，被 FactorService 调用：

```java
public final class TideSystem {
    // 私有构造，纯静态工具
    private TideSystem() {}
    
    public static double calculateDeviation(...) {}
    public static FactorStatus getStatusFromDeviation(...) {}
    public static long findNextPeakTick(...) {}
    public static boolean isOutbreakTime(...) {}
}
```

---

## Phase 3: 接入游戏循环

### 3.1 在 FactorSystemModule 中调用潮汐效果

```java
@Override
public void initialize() {
    ServerTickEvents.END_WORLD_TICK.register(world -> {
        SERVICE.tick(world);
        
        // 每 1200 ticks (60秒) 检查潮汐效果
        if (world.getTime() % 1200 == 0) {
            TideSystem.applyTideEffects(world);
        }
    });
}
```

### 3.2 实现 applyTideEffects

根据 FactorStatus 触发效果：

| 状态 | 偏离度 | 效果 |
|------|--------|------|
| STABLE | ±10% | 无 |
| DEVIATED | ±10-30% | 低概率事件 |
| FLUCTUATING | ±30-50% | 中概率事件 + 环境效果 |
| VOLATILE | ±50%+ | 高概率灾害 + 视觉效果 |

---

## Phase 4: 扩展 API

### 4.1 扩展 FactorApi 接口

```java
public interface FactorApi {
    // 现有方法
    double getFactor(ServerWorld world);
    int getTier(ServerWorld world);
    OptionalLong predictCrossing(ServerWorld world, double target);
    void addFactorOffset(ServerWorld world, double offset, long durationTicks);
    
    // 新增潮汐相关
    TideSystem.FactorStatus getStatus(ServerWorld world);
    double getDeviation(ServerWorld world);
    long getNextPeakTick(ServerWorld world);
    long getNextTroughTick(ServerWorld world);
    boolean isOutbreakTime(ServerWorld world);
}
```

---

## Phase 5: 区块级扩散接入

### 5.1 接入 DiffusionSystem

```java
// 在 FactorSystemModule 中
if (world.getTime() % 100 == 0) { // 每 5 秒
    DiffusionSystem.processAllDiffusion(world);
}
```

### 5.2 可选：使用 OptimizedDiffusion

如果区块数量大，切换到高性能版本：

```java
OptimizedDiffusion.processBatch(chunkStates);
```

---

## 实施顺序

1. ✅ 分析现有代码
2. 🔲 统一 DimensionType 参数
3. 🔲 重构 FactorService 使用 DimensionType
4. 🔲 整合 TideSystem 工具方法
5. 🔲 扩展 FactorApi 接口
6. 🔲 接入游戏循环（潮汐效果）
7. 🔲 接入扩散系统
8. 🔲 测试验证

---

## 预期成果

1. **统一潮汐计算** - 一套参数，一个实现
2. **完整 API** - 支持查询、预测、状态判断
3. **游戏效果** - 潮汐周期影响游戏玩法
4. **区块扩散** - Factor 在区块间自然流动
5. **可观测性** - 日志、调试 HUD

## 风险

| 风险 | 缓解措施 |
|------|----------|
| 参数变更影响现有平衡 | 测试后微调 |
| 扩散性能问题 | 使用批处理 |
| 事件过于频繁 | 增加冷却机制 |