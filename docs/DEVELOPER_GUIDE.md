# Factor Craft 开发者文档

## 模块系统

### ModuleBootstrap

所有模块的统一入口点。

```java
// 初始化所有默认模块
ModuleBootstrap.initializeDefaults();

// 手动注册模块
ModuleBootstrap.registerModule(new CombatModule());
ModuleBootstrap.registerModule(new CycleModule());
```

### 核心模块

#### 1. CombatModule (战斗系统)

**功能**: 武器系统、怪物系统、掉落物

```java
CombatModule module = CombatModule.getInstance();
module.initialize();
```

**武器系统**:
- **FactorSwordItem**: 平衡型近战武器 (T1-T5)
  - 伤害：6/7/8/9/10
  - 攻速：-2.4 ~ -2.2
  - Factor 加成：0.2 ~ 1.0

- **DimensionHammerItem**: 重型破甲武器 (T1-T5)
  - 伤害：8/10/12/14/16
  - 攻速：-3.2 ~ -2.8 (慢)
  - 破甲：20% ~ 60%
  - 维度穿透：T2+

- **ResonanceBowItem**: 远程能量武器 (T1-T5)
  - 伤害：5/6/7/8/9
  - 蓄力：20 ~ 16 ticks
  - 射程加成：0% ~ 40%
  - 穿透等级：T3+

**武器属性配置**:
```java
// 访问武器属性
float damage = WeaponAttributes.Sword.DAMAGE[tier - 1];
int durability = WeaponAttributes.Sword.DURABILITY[tier - 1];
double factorBonus = WeaponAttributes.Sword.FACTOR_BONUS[tier - 1];
```

#### 2. CycleModule (潮汐周期)

**功能**: Factor 能量周期性波动管理

```java
CycleModule module = CycleModule.getInstance();
module.initialize();

// 每 tick 更新
module.tick(worldTime);

// 获取当前 Factor 倍率
double multiplier = module.getFactorMultiplier(); // 0.7 ~ 1.3

// 获取周期阶段
CyclePhase phase = module.getCurrentPhase();
// RISING(上升期) | PEAK(峰值期) | FALLING(下降期) | TROUGH(谷值期)
```

**配置**:
```java
// 设置周期长度 (默认 24000 ticks = 1 Minecraft 日)
module.setCycleLength(24000);

// 设置振幅 (默认 0.3 = 30%)
module.setAmplitude(0.3);
```

**预测系统**:
```java
// 预测未来 tick 的 Factor 倍率
double futureMultiplier = module.predictFactorMultiplier(1200); // 1 分钟后

// 获取距离下一个峰值的时间
long ticksUntilPeak = module.getTicksUntilNextPeak();

// 获取距离下一个谷值的时间
long ticksUntilTrough = module.getTicksUntilNextTrough();
```

#### 3. FactorNetworkManager (Factor 网络)

**功能**: 跨维度 Factor 传输和同步

```java
FactorNetworkManager manager = FactorNetworkManager.getInstance();
manager.initialize();
```

**维度基准值**:
```java
// 默认配置
double overworld = manager.getDimensionBase("minecraft:overworld"); // 0.5
double nether = manager.getDimensionBase("minecraft:the_nether");   // 1.5
double end = manager.getDimensionBase("minecraft:the_end");         // 3.0

// 自定义维度基准值
manager.setDimensionBase("custom_dimension", 2.0);
```

**传输倍率计算**:
```java
// 主世界 -> 下界：0.5 / 1.5 = 0.333 (损失 67%)
// 下界 -> 主世界：1.5 / 0.5 = 3.0 (增益 3 倍)
double multiplier = manager.calculateTransferMultiplier(fromWorld, toWorld);
```

**性能监控**:
```java
// 获取统计
NetworkStats stats = manager.getStats();
System.out.println("总传输：" + stats.totalTransfers);
System.out.println("平均损失：" + stats.avgLoss);

// 重置统计
manager.resetStats();
```

#### 4. MultiblockDetector (多方块检测)

**功能**: 12 种结构蓝图检测

```java
// 获取所有蓝图
List<MultiblockPattern> patterns = MultiblockDetector.getAllPatterns();

// 创建特定蓝图
MultiblockPattern t1Furnace = MultiblockDetector.createBasicResonanceFurnace();
MultiblockPattern t5Gate = MultiblockDetector.createDimensionalGate();
```

**结构列表**:
- **T1**: BasicResonanceFurnace, FactorConverter, FactorStorage, ResonanceWorkbench
- **T2**: AdvancedResonanceFurnace, FactorConverterT2, ResonanceCoilArray, DimensionalForge
- **T3**: DimensionalFurnace, FactorConverterT3, DimensionStabilizer, FactorStorageT3, FactorInjector
- **T4**: ResonanceAltar
- **T5**: DimensionalGate

**蓝图属性**:
```java
String id = pattern.getId();           // 唯一标识符
String name = pattern.getName();       // 显示名称
int tier = pattern.getTier();          // 等级 (1-5)
Map<BlockPos, String> structure = pattern.getStructure(); // 结构映射
List<String> materials = pattern.getMaterials(); // 所需材料
```

## 测试系统

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行特定模块测试
./gradlew test --tests "com.factorcraft.module.cycle.CycleModuleTest"

# 运行性能测试
./gradlew test --tests "com.factorcraft.performance.*"
```

### 测试覆盖率

当前测试统计：
- **总测试数**: 81
- **通过率**: 100%
- **覆盖模块**: Combat, Cycle, Network, Multiblock, Factor

### 编写测试

```java
@DisplayName("模块功能测试")
public class MyModuleTest {
    
    @Test
    @DisplayName("功能验证")
    public void testFeature() {
        //  Arrange
        MyModule module = MyModule.getInstance();
        
        // Act
        module.doSomething();
        int result = module.getResult();
        
        // Assert
        assertEquals(expected, result);
    }
}
```

## 性能指标

### 目标性能

| 系统 | 目标 | 当前 |
|------|------|------|
| 多方块检测 | <10ms | ✅ <2ms |
| T1 结构检测 | <2ms | ✅ <1ms |
| 周期计算 (1000 次) | <2ms | ✅ <1ms |
| 周期预测 (100 次) | <1ms | ✅ <0.5ms |
| 武器属性访问 (10000 次) | <0.5ms | ✅ <0.2ms |
| 综合性能 (100 次迭代) | <20ms | ✅ <10ms |

### 性能优化建议

1. **避免频繁创建对象** - 使用缓存和单例
2. **减少 Map 查找** - 使用数组或直接访问
3. **批量处理** - 合并多个小操作
4. **懒加载** - 只在需要时初始化

## 配置系统

### 维度基准值

配置文件：`config/factorcraft/dimensions.json`

```json
{
  "dimensions": {
    "minecraft:overworld": 0.5,
    "minecraft:the_nether": 1.5,
    "minecraft:the_end": 3.0,
    "custom_dimension": 2.0
  }
}
```

### 周期配置

配置文件：`config/factorcraft/cycle.json`

```json
{
  "cycleLength": 24000,
  "amplitude": 0.3,
  "enablePeakEvents": true,
  "enableTroughEvents": true
}
```

## 调试工具

### 日志级别

```java
// 启用详细日志
System.setProperty("factorcraft.debug", "true");

// 查看传输日志
List<TransferRecord> log = manager.getTransferLog();
for (TransferRecord record : log) {
    System.out.println(record);
}
```

### 状态查询

```java
// 周期状态
String status = cycleModule.getStatus();
System.out.println(status);

// 网络统计
NetworkStats stats = networkManager.getStats();
System.out.println(stats);
```

## 最佳实践

### 1. 模块使用

```java
// ✅ 推荐：使用 getInstance() 获取单例
CycleModule module = CycleModule.getInstance();

// ❌ 避免：创建新实例
CycleModule module = new CycleModule();
```

### 2. 性能敏感代码

```java
// ✅ 推荐：缓存计算结果
double multiplier = module.getFactorMultiplier();
for (int i = 0; i < 100; i++) {
    use(multiplier); // 使用缓存值
}

// ❌ 避免：重复计算
for (int i = 0; i < 100; i++) {
    use(module.getFactorMultiplier()); // 每次都重新计算
}
```

### 3. 多方块检测

```java
// ✅ 推荐：缓存蓝图
MultiblockPattern pattern = MultiblockDetector.createBasicResonanceFurnace();
// 重复使用 pattern

// ❌ 避免：每次检测都创建新蓝图
for (BlockPos pos : positions) {
    MultiblockDetector.createBasicResonanceFurnace(); // 重复创建
}
```

## 常见问题

### Q: 如何添加新维度？

A: 使用 `setDimensionBase()` 方法：
```java
manager.setDimensionBase("modname:new_dimension", 2.5);
```

### Q: 如何自定义周期长度？

A: 使用 `setCycleLength()` 方法：
```java
module.setCycleLength(48000); // 2 个 Minecraft 日
```

### Q: 如何创建新武器？

A: 扩展 WeaponAttributes 并创建新 Item 类：
```java
// 1. 在 WeaponAttributes 中添加属性
public static class CustomWeapon {
    public static final float[] DAMAGE = {5.0f, 6.0f, ...};
}

// 2. 创建武器类
public class CustomWeaponItem extends Item {
    // 实现武器逻辑
}
```

## 贡献指南

### 提交 PR

1. 创建 feature 分支：`git checkout -b feature/my-feature`
2. 实现功能并编写测试
3. 确保所有测试通过：`./gradlew test`
4. 提交 PR (<400 行)
5. 等待审查和合并

### 代码规范

- 使用 Java 21 特性
- 遵循 Minecraft 命名规范
- 添加完整的 Javadoc
- 编写单元测试
- PR 大小 <400 行

---

**最后更新**: Day 11  
**文档版本**: 1.0  
**维护者**: Factor Craft Team
