# 14. M2 整体设计与实现说明（材料/词条/附魔/状态）

## 目标

基于 `docs/02`、`docs/06`、`docs/12`，完成 M2 的首版可运行闭环：

1. 材料等级模型（Lv1~Lv5）与核心属性。
2. 词条池（通用/维度/终局）与 Tier 窗口。
3. 附魔池（武器/工具/防具）。
4. BUFF/DEBUFF 状态池与叠加策略。
5. 与 Tier 的日切联动。
6. 提供可复现的生成逻辑与 JVM 验证入口。

## 架构设计

### 数据模型层（model）

- `MaterialLevel`：Lv1~Lv5 等级枚举。
- `MaterialSpec`：材料基础属性（harvest/toughness/conductivity/slots/dimensions）。
- `TraitSpec`：词条类别、Tier 窗口、权重、维度限制、联动状态。
- `EnchantSpec`：附魔类别、Tier 窗口、效能。
- `StatusEffectSpec`：状态类型、持续、叠加规则、Tier 窗口。

### 配置层（config）

- `MaterialsM2Config`：聚合材料/词条/附魔/状态四大池。
- `MaterialsM2Defaults`：内置首版默认数据（对齐文档定义的概念名）。

### 校验层（validation）

- `MaterialsM2SpecValidator`：统一校验 ID、Tier 窗口、重复项、数值范围、空池等。

### 服务层（service）

- `MaterialsM2Service.roll(seed, dimension, tier)`：
  - 按维度+Tier 筛选材料池并抽取。
  - 按权重抽取词条（数量与材料槽位挂钩）。
  - 每类附魔各抽取一个。
  - 根据词条联动映射状态，计算持续时间与层数。
- `settleDailyStatusPool(previousTier, nextTier)`：
  - 根据日切前后 Tier 结算全局状态池。

### 模块接入

- `MaterialsModule.initialize()`：
  - 装载默认配置。
  - 运行校验器。
  - 初始化服务并输出演示链路日志。

### 验证层（verification）

- `MaterialsM2Verifier`：纯 JVM 校验，覆盖：
  - 配置合法性。
  - 同 seed 条件下可复现。
  - “材料->词条->附魔->状态”链路非空。
  - 日切状态池结算结果非空。


## M2 后续推进（本次增量）

- 接入 `materials_m2.json` 动态入口：通过 `DynamicContentLoader/DynamicBundle` 暴露 M2 配置对象。
- 新增 `MaterialsM2ConfigParser`：优先解析动态配置，解析失败或结构不完整时自动回退默认池。
- `MaterialsModule` 初始化改为“动态配置优先 + 默认回退”，为后续平衡性在线调参提供基础。

- 新增 `MaterialTierSyncService` + `MaterialStatusState`：消费 `FactorTierChangeEvent`，执行日切状态池结算并记录世界级快照。
- 日切快照结构 `MaterialDayStatusSnapshot` 包含 world/day/tier/statuses，便于后续 HUD 与审计落地。
- 补充 `MaterialsM2TierSyncVerifier`，验证“日切结算 -> 状态快照写入”链路。
- 新增 `MaterialApi/MaterialApiProvider/MaterialRuntimeApi`：向后续模块提供“按 M1 当前 Tier 生成材料结果 + 查询最新日切快照”的统一接口。

## 里程碑结论

M2 首版已具备：

- 可配置、可校验、可演示、可复现的基础实现。
- 与 Tier 的基本联动能力（日切状态池结算 + 词条窗口筛选）。
- 独立 JVM 验证入口，支持后续 CI 集成。

## 后续建议（M2.1）

1. 将默认池迁移为动态 JSON 文件并纳入 `DynamicBundle`。
2. 引入实际装备位（主手/副手/防具槽）驱动附魔选择策略。
3. 状态叠加规则从“离线计算”扩展到 tick 级状态机。
4. 增加跨模块联调：对接 `GearApi`、`FactorApi` 与事件总线。
