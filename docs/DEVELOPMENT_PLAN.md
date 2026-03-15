# Factor Craft 开发规划

> 更新日期：2026-03-15
> 状态：核心系统完成，游戏闭环待实现

---

## 当前完成度

### ✅ 核心系统 (100%)

| 模块 | 状态 | 说明 |
|------|------|------|
| Factor 服务 | ✅ | FactorService, 潮汐, 扩散 |
| 维度活性 | ✅ | DimensionType, 传输倍率, FactorTier |
| 区块浓度 | ✅ | ChunkFactorState, 扩散系统 |
| 事件系统 | ✅ | SimpleFactorEventBus, 各类事件 |
| 配置系统 | ✅ | 动态加载, 热重载 |

### ✅ 基础设施 (100%)

| 模块 | 状态 | 说明 |
|------|------|------|
| 日志系统 | ✅ | ModuleLoggers, 标准化格式 |
| 命令系统 | ✅ | 10+ 命令, 权限管理 |
| 网络同步 | ✅ | FactorSyncPayload, 客户端处理 |
| 测试覆盖 | ✅ | 18+ 测试用例 |

### 🔄 游戏内容 (40%)

| 模块 | 状态 | 待完成 |
|------|------|--------|
| 多方块结构 | 🔄 框架完成 | 与科技树映射 |
| 机器核心 | 🔄 框架完成 | 实际工作逻辑 |
| 材料系统 | 🔄 配置完成 | 升级配方实现 |
| 战斗系统 | 🔄 武器完成 | 怪物掉落表 |
| 任务系统 | 🔄 框架完成 | 实际任务内容 |
| 世界生成 | ✅ | 晶体矿脉, 祭坛, Boss |

### ❌ 资源 (0%)

| 模块 | 状态 |
|------|------|
| 方块贴图 | ❌ 缺失 |
| 物品贴图 | ❌ 缺失 |
| GUI 背景 | ❌ 缺失 |

---

## 开发阶段规划

### Phase A: 科技树闭环 (优先级最高)

**目标**: 实现 T1 材料到 T2 材料的完整生产链

#### A1. 提取结构实现

**文件**: `ExtractorCoreBlockEntity.java`

**待实现**:
```java
// 1. 接入区块浓度
ChunkFactorState chunkState = ChunkFactorManager.getState(pos).orElse(...);
double concentration = chunkState.getCurrentConcentration();

// 2. 接入维度活性
DimensionType dimType = DimensionType.fromKey(world.getRegistryKey()...);
double activity = dimType.calculateFactor(world.getTime());

// 3. 计算提取效率
double concentrationCoeff = getConcentrationCoefficient(concentration);
double actualExtract = baseRate * activity * concentrationCoeff * structureEfficiency;

// 4. 消耗区块浓度
chunkState.setCurrentConcentration(concentration - drain);
```

**浓度系数**:
```java
private double getConcentrationCoefficient(double concentration) {
    if (concentration > 50) return 1.2;
    if (concentration > 30) return 1.0;
    if (concentration > 10) return 0.8;
    return 0.5;
}
```

#### A2. 消耗结构实现

**文件**: `ConsumerCoreBlockEntity.java`

**待实现**:
1. 物品槽位 (Inventory)
2. 消耗配方配置
3. Factor 产出计算

**配方示例**:
```json
{
  "consumption_recipes": {
    "stone": { "input": "minecraft:stone", "output_factor": 20, "time": 100 },
    "iron_ingot": { "input": "minecraft:iron_ingot", "output_factor": 50, "time": 200 },
    "diamond": { "input": "minecraft:diamond", "output_factor": 300, "time": 400 }
  }
}
```

#### A3. 合成结构实现

**文件**: `SynthesizerCoreBlockEntity.java`

**待实现**:
1. 材料升级配方
2. Factor 消耗逻辑
3. 维度惩罚机制

**升级配置**:
```json
{
  "upgrade_recipes": {
    "t1_to_t2": {
      "input": "factorcraft:dust_copper_ingot",
      "input_count": 64,
      "output": "factorcraft:shadow_steel_ingot",
      "output_count": 32,
      "factor_cost": 1000,
      "time": 1200,
      "recommended_dimension": "minecraft:overworld"
    },
    "t2_to_t3": {
      "input": "factorcraft:shadow_steel_ingot",
      "input_count": 128,
      "output": "factorcraft:stardust_ingot",
      "output_count": 32,
      "factor_cost": 5000,
      "time": 2400,
      "recommended_dimension": "minecraft:the_nether"
    }
  }
}
```

#### A4. 培育结构实现

**文件**: `CultivatorCoreBlockEntity.java`

**待实现**:
1. 特性注入逻辑 (已有 TraitGenerator)
2. Factor 消耗公式
3. 特性槽位管理

---

### Phase B: 多方块结构映射

**问题**: 当前 MultiblockDetector 的蓝图命名与设计文档不一致

**设计文档结构**:
| Tier | 提取 | 消耗 | 合成 | 培育 |
|------|------|------|------|------|
| T1 | 星辰收集器 | 灵魂燃烧器 | 远古合成阵 | 命运织机 |
| T2 | 星辰阵列 | 灵魂熔炉 | 远古锻造台 | 灵魂编织器 |
| T3 | 星云汲取器 | 深渊吞噬者 | 命运铸造炉 | 命运祭坛 |
| T4 | 宇宙共鸣器 | 混沌裂隙 | 创世熔炉 | 命运圣所 |
| T5 | 虚空漩涡 | 永恒炉心 | 本源祭坛 | 轮回之门 |

**当前蓝图**: basic_resonance_furnace, factor_converter, resonance_workbench...

**任务**: 
1. 重命名蓝图 ID 匹配设计文档
2. 调整结构尺寸 (T1: 3×5×3, T2: 5×7×5, T3+: 7×9×7)
3. 配置化蓝图加载

---

### Phase C: 传递器系统

**文件**: `TransmitterBlockEntity.java` (待创建)

**功能**: 跨维度传输 Factor

**实现要点**:
```java
public double transmit(ServerWorld fromWorld, ServerWorld toWorld, double amount) {
    FactorService service = FactorService.getInstance();
    
    // 计算传输倍率
    double multiplier = service.calculateTransferMultiplier(fromWorld, toWorld);
    
    // 传递器效率 (T1: 80%, T2: 85%, T3: 90%, T4: 95%)
    double efficiency = getEfficiencyByTier(tier);
    
    // 距离损耗
    double distanceLoss = calculateDistanceLoss(fromPos, toPos);
    
    // 最终传输
    double received = amount * multiplier * efficiency * (1 - distanceLoss);
    
    return received;
}
```

---

### Phase D: UI 完善

**待实现**:
1. 结构状态屏幕 (当前 Tier, Factor 存储, 进度)
2. 材料信息面板 (Factor 值, 特性槽位, 维度限制)
3. 任务追踪 UI

---

### Phase E: 资源制作

**需要贴图**:
- T1-T5 材料锭 (5 个)
- T1-T5 材料方块 (5 个)
- 4 类结构核心 (提取/消耗/合成/培育)
- 4 类武器 × 5 Tier (20 个)
- UI 背景

---

## 优先级排序

```
1. [A1] 提取结构实际工作 ← 最高优先级，玩家第一个接触的机器
2. [A3] 合成结构实现 ← 材料升级核心
3. [A2] 消耗结构实现 ← 替代提取的 Factor 来源
4. [B]  多方块结构映射 ← 游戏内容完整性
5. [A4] 培育结构实现 ← 特性系统闭环
6. [C]  传递器系统 ← 跨维度物流
7. [D]  UI 完善 ← 用户体验
8. [E]  资源制作 ← 视觉效果
```

---

## 验收标准

### Phase A 完成标准

- [ ] 玩家可以在主世界建造 T1 星辰收集器
- [ ] 星辰收集器能从区块提取 Factor
- [ ] 玩家可以在远古合成阵中升级 T1→T2 材料
- [ ] 材料升级消耗正确的 Factor 量
- [ ] 维度惩罚机制生效 (在错误维度生产效率降低)

### 游戏闭环验证

1. **开局**: 玩家找到 Factor 晶体矿
2. **提取**: 建造星辰收集器，获得 Factor
3. **升级**: 在远古合成阵中制作 T2 材料
4. **进阶**: 在下界建造更高级结构
5. **终局**: 在末地建造 T5 结构

---

## 预计工作量

| 阶段 | 预计时间 | 依赖 |
|------|---------|------|
| A1 | 2-3 小时 | 无 |
| A3 | 2-3 小时 | A1 |
| A2 | 2-3 小时 | 无 |
| B | 3-4 小时 | 无 |
| A4 | 2-3 小时 | 特性系统已有 |
| C | 2-3 小时 | A1 |
| D | 3-4 小时 | A 全部 |
| E | - | 外部资源 |

**总计**: 约 20 小时核心开发

---

## 下一步行动

**立即开始**: A1 - 提取结构实现

1. 创建 `ExtractionConfig.java` 配置类
2. 修改 `ExtractorCoreBlockEntity.tick()` 接入真实系统
3. 添加测试用例验证提取逻辑