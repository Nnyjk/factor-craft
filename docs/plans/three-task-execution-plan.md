# Phase 3 三任务并行执行计划

> **创建日期：** 2026-03-09  
> **主控：** Y 同学  
> **工作区：** `.worktrees/phase3-alpha`  
> **分支：** `phase3-alpha-release`

---

## 📋 任务总览

| 任务 | 描述 | 优先级 | 状态 |
|------|------|--------|------|
| **任务一** | 修复编译报错，产出完整 mod 包 | 🔴 高 | 🔄 进行中 |
| **任务二** | 重构系统，确保对外开放性 | 🔴 高 | ⏳ 待开始 |
| **任务三** | 武器、怪物、掉落物设定 | 🟡 中 | ⏳ 待开始 |

---

## 🎯 任务一：编译修复与 Mod 包产出

### 目标
- 修复所有编译错误
- 通过 `gradle build`
- 产出可安装的 JAR 文件

### 问题清单

#### 1.1 API 兼容性错误
```
错误：cannot find symbol - ClientboundPacketListener
位置：net.minecraft.network.listener
影响：3 个 BlockEntity 文件
```

**解决方案：** Minecraft 1.21.4 移除了 `ClientboundPacketListener`，需改用通用 `Packet<?>`

#### 1.2 循环依赖
```
CycleBlocks 引用 CycleBlockEntities
CycleBlockEntities 引用 CycleBlocks
```

**解决方案：** 分离注册逻辑，使用延迟初始化

#### 1.3 缺失的 Block 定义
```
CycleBlockEntities.FACTOR_SINK 等方块未正确定义
```

**解决方案：** 先注册 Block，再创建 BlockEntityType

### 执行步骤

1. **修复 BlockEntity 网络包 API** (30 分钟)
   - 移除 `ClientboundPacketListener` 导入
   - 改用 `Packet<?>` 或 `ClientPlayPacketListener`
   
2. **重构注册系统** (45 分钟)
   - 创建 `CycleContentRegistrar` 统一管理
   - 分离 Block 和 BlockEntity 注册
   
3. **验证构建** (15 分钟)
   - 运行 `gradle build`
   - 修复剩余错误
   
4. **测试 Mod 包** (15 分钟)
   - 检查 JAR 文件
   - 验证 fabric.mod.json

### 验收标准
- [ ] `gradle build` 成功
- [ ] 生成 `build/libs/factor-craft-0.1.0.jar`
- [ ] 无编译错误
- [ ] 无编译警告
- [ ] Git 提交：`fix: resolve compilation errors for Minecraft 1.21.4`

---

## 🔧 任务二：系统重构与对外开放性

### 目标
- 科技系统 API 化
- 机械系统模块化
- 多方块结构可扩展
- 第三方接入友好

### 工作流

#### 工作流 2A：架构设计专家

**职责：**
- 设计 API 接口
- 定义扩展点
- 编写架构文档

**交付物：**
- `docs/api/architecture.md`
- `docs/api/extension-points.md`
- API 接口定义

#### 工作流 2B：重构实施专家

**职责：**
- 重构核心系统
- 实现 API 接口
- 编写示例代码

**交付物：**
- `src/main/java/com/factorcraft/api/` (公共 API)
- `src/main/java/com/factorcraft/spi/` (SPI 扩展点)
- 示例 Mod 代码

#### 工作流 2C：文档专家

**职责：**
- 编写 API 文档
- 编写集成指南
- 编写示例教程

**交付物：**
- Javadoc 完整
- `docs/guides/integration.md`
- `examples/` 示例项目

### 重构范围

#### 2.1 科技系统 API
```java
// 公共 API
public interface TechnologyApi {
    void registerStructure(StructureSpec spec);
    void registerMaterial(MaterialSpec spec);
    TechnologyRegistry getRegistry();
}

// SPI 扩展点
public interface StructureProvider {
    Collection<StructureSpec> getStructures();
}
```

#### 2.2 机械系统 API
```java
// 公共 API
public interface MachineApi {
    void registerMachineType(MachineType type);
    void registerRecipe(RecipeSpec spec);
    MachineRegistry getRegistry();
}

// SPI 扩展点
public interface MachineComponentProvider {
    Collection<MachineComponent> getComponents();
}
```

#### 2.3 多方块结构 API
```java
// 公共 API
public interface MultiblockApi {
    void registerPattern(MultiblockPattern pattern);
    void registerController(ControllerSpec spec);
    MultiblockRegistry getRegistry();
}

// SPI 扩展点
public interface MultiblockProvider {
    Collection<MultiblockPattern> getPatterns();
}
```

### 执行步骤

1. **架构设计** (Day 1)
   - 定义 API 边界
   - 设计扩展机制
   - 审查设计方案

2. **核心重构** (Day 2-3)
   - 提取接口
   - 实现注册系统
   - 迁移现有代码

3. **文档编写** (Day 3-4)
   - Javadoc 完整
   - 集成指南
   - 示例代码

4. **测试验证** (Day 4-5)
   - 编写 API 测试
   - 验证扩展性
   - 第三方模拟接入

### 验收标准
- [ ] API 包完整 (`com.factorcraft.api.*`)
- [ ] SPI 包完整 (`com.factorcraft.spi.*`)
- [ ] Javadoc 覆盖率 ≥ 90%
- [ ] 示例 Mod 可编译运行
- [ ] Git 提交：`feat: extract public API for third-party integration`

---

## ⚔️ 任务三：武器、怪物、掉落物设定

### 目标
- 设计武器系统
- 设计怪物系统
- 设计掉落物系统
- 平衡数值

### 工作流

#### 工作流 3A：游戏设计专家

**职责：**
- 设计武器属性
- 设计怪物行为
- 设计掉落表

**交付物：**
- `docs/designs/weapons.md`
- `docs/designs/creatures.md`
- `docs/designs/loot_tables.md`

#### 工作流 3B：数值平衡专家

**职责：**
- 数值计算
- 平衡性测试
- 难度曲线设计

**交付物：**
- 数值表
- 平衡性报告
- 难度曲线图

#### 工作流 3C：内容实施专家

**职责：**
- 实现武器代码
- 实现怪物代码
- 实现掉落系统

**交付物：**
- `src/main/java/com/factorcraft/module/combat/`
- `src/main/java/com/factorcraft/module/creature/`
- `src/main/java/com/factorcraft/module/loot/`

### 设计框架

#### 3.1 武器系统

**武器类型：**
| 类型 | 伤害 | 速度 | 特殊效果 |
|------|------|------|---------|
| 因子剑 | 中等 | 快 | Factor 伤害加成 |
| 维度锤 | 高 | 慢 | 破甲效果 |
| 共振弓 | 中等 | 中 | 远程 Factor 冲击 |
| 仲裁刃 | 极高 | 中 | 维度穿透 |

**材质等级：**
- T1: 粗坯级 (铜、青铜)
- T2: 工业级 (铁、钢)
- T3: 维度级 (钴、阿迪特)
- T4: 远古级 (远古合金)
- T5: 仲裁级 (零熵结晶)

#### 3.2 怪物系统

**怪物分类：**
| 类型 | 维度 | Factor 等级 | 行为 |
|------|------|------------|------|
| 因子畸变体 | 主世界 | T1-T2 | 近战攻击 |
| 维度扭曲者 | 下界 | T2-T3 | 远程 + 传送 |
| 虚空侵蚀者 | 末地 | T3-T4 | 高伤害 + 隐身 |
| 仲裁守卫 | 所有 | T5 | Boss 级 |

**掉落物设计：**
- 普通掉落：材料碎片
- 稀有掉落：核心组件
- Boss 掉落：仲裁残片

#### 3.3 掉落物系统

**掉落表结构：**
```json
{
  "entity": "factor_distortion",
  "loot_table": [
    {
      "item": "factor_shard",
      "count": {"min": 1, "max": 3},
      "chance": 1.0
    },
    {
      "item": "resonance_core",
      "count": 1,
      "chance": 0.15,
      "looting_bonus": 0.05
    }
  ]
}
```

### 执行步骤

1. **设计文档** (Day 1-2)
   - 武器设计
   - 怪物设计
   - 掉落表设计

2. **代码实现** (Day 2-4)
   - 武器系统
   - 怪物 AI
   - 掉落逻辑

3. **数值平衡** (Day 4-5)
   - 数值测试
   - 难度调整
   - 平衡性验证

4. **整合测试** (Day 5)
   - 战斗测试
   - 掉落测试
   - Bug 修复

### 验收标准
- [ ] 设计文档完整
- [ ] 代码实现完成
- [ ] 数值平衡合理
- [ ] 战斗体验流畅
- [ ] Git 提交：`feat: add combat system with weapons and creatures`

---

## 📅 执行时间表

### Day 1 (今天)
```
上午：
- 任务一：修复编译错误 (完成)
- 任务一：验证构建通过 (完成)
- 任务二：架构设计 (开始)

下午：
- 任务二：API 接口定义 (进行中)
- 任务三：武器设计文档 (开始)

晚上：
- 代码提交 PR #1
- 进度汇报
```

### Day 2-3
```
任务一：✅ 完成
任务二：重构实施 (核心)
任务三：设计文档完成
```

### Day 4-5
```
任务二：文档与测试
任务三：代码实现
```

---

## 🔄 并行执行策略

```
主控协调
├── 任务一 (编译修复)
│   └── 实施专家：修复 API 兼容性
│
├── 任务二 (系统重构)
│   ├── 架构专家：设计 API
│   ├── 实施专家：重构代码
│   └── 文档专家：编写文档
│
└── 任务三 (战斗系统)
    ├── 设计专家：武器/怪物设定
    ├── 数值专家：平衡性
    └── 实施专家：代码实现
```

---

## 📊 质量检查点

### 每日检查
- [ ] 代码编译通过
- [ ] 测试通过
- [ ] Git 提交
- [ ] 进度更新

### 里程碑检查
- [ ] 任务一完成：Mod 包可安装
- [ ] 任务二完成：API 文档完整
- [ ] 任务三完成：战斗系统可玩

---

## 🚨 风险管理

| 风险 | 影响 | 概率 | 应对 |
|------|------|------|------|
| API 设计不合理 | 高 | 中 | 早期审查 + 迭代 |
| 数值不平衡 | 中 | 高 | 多次测试调整 |
| 时间不足 | 中 | 中 | 优先级排序 |
| 第三方接入困难 | 高 | 中 | 示例代码验证 |

---

> **状态：** 执行中  
> **下次更新：** 每日站会  
> **PR 策略：** 小步快跑，每日合并
