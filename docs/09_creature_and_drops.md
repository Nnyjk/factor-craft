# 09. 怪物生态与凋落物（设定 + 模块骨架）

## 设计目标

补全“怪物与凋落物”层：让怪物刷新、阶段能力、掉落奖励与 Factor Tier（日切）联动，并为扩展包提供统一接入点。

## 设定补全

- 生态刷新：怪物按维度、Tier 区间、权重和群体大小进行刷新。
- 阶段能力：怪物在高能/过载窗口可启用额外能力（位移、抗性、范围技等）。
- 凋落物池：掉落按实体 + Tier 池切换，支持条件条目与概率控制。
- 日切联动：次日 Tier 变化时，刷新规则和掉落池自动切换到新窗口。

## 接口与标准

- `CreatureRegistrar`：注册刷新规则、掉落池、怪物能力。
- `CreatureApi`：运行时查询“某世界某 Tier 的可用规则/掉落池”。
- `SpawnRuleSpec`：`ruleId/entityId/dimensions/tier/weight/groupSize`
- `DropPoolSpec`：`poolId/entityId/tier/entries`
- `LootEntrySpec`：`itemId/chance/countRange/conditionTag`
- `CreatureAbilitySpec`：`abilityId/entityId/tier/effects/cooldown`

## 统一注册中心

`CreatureDropRegistry` 提供：

- 去重注册（防重复 ID）
- 按 Tier 与维度过滤查询
- 规则、掉落、能力的只读视图

## 事件骨架

- `CreatureSpawnRollEvent`
- `CreatureDropRollEvent`

用于后续挂接日志、统计、奖励修正与其他模块联动（饰品、词条、阵营等）。
