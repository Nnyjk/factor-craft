# 06. 技术实现与数据驱动建议

## 6.1 存储结构

* `FactorWorldState`：维度实时 Factor。
* `DayTierSnapshot`：当日均值、趋势、迟滞参数、次日 Tier。
* `PollutionHeatmap`：区块污染强度。
* `EventCooldownState`：事件触发与冷却。
* `StructureState`：多方块完整度、模块配置、功率预算。

## 6.2 事件系统

* `FactorChangeEvent`（实时）
* `FactorTierChangeEvent`（日切）
* `FactorThresholdEvent`（日切结算）
* `FactorDisasterEvent`
* `GearAbilityEvent`（飞行/瞬移等装备能力）
* `TrinketProcEvent`（饰品触发）

## 6.3 API 建议

* `FactorAPI.getFactor(World)`
* `FactorAPI.getTier(World)`
* `FactorAPI.predictCrossing(World, target)`
* `FactorAPI.addFactorOffset(World, offset, duration)`
* `GearAPI.getConductivity(Player)`
* `GearAPI.tryActivateAbility(Player, abilityId)`
* `StructureAPI.getPowerBudget(BlockPos)`

## 6.4 数据驱动配置（JSON）

建议数据化：

* 维度潮汐参数与日切规则
* 材料等级、词条池、附魔池
* BUFF/DEBUFF 效果与叠加规则
* 多方块结构蓝图与模块预算
* 建材/家具/装饰块属性
* 装备能力（飞行、位移、护盾）与消耗

## 6.5 开发里程碑

* **M1**：Factor + HUD + 日切 Tier 判定
* **M2**：材料分级、词条、附魔、状态系统
* **M3**：多方块机器 + 模块预算
* **M4**：武器防具饰品 + 特殊能力（含飞行）
* **M5**：建筑家具装饰 + 服务器社交玩法
