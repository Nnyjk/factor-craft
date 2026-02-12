# 08. 非核心模块接入标准（扩展包/第三方包）

## 目标

为工具、武器、防具、家具等“非核心内容”提供统一接入协议，降低后续扩展包与第三方包适配成本。

## 接入入口

- `NonCoreContentProvider`：第三方包实现该接口。
- `NonCoreRegistrar`：主模组提供注册器，暴露 `registerTool/registerWeapon/registerArmor/registerFurniture`。
- `NonCoreIntegrationRegistry`：统一注册中心，按类型保存数据并提供只读视图。

## 统一标准字段

所有接入对象均实现 `NonCoreContentSpec`，包含：

- `contentId`：统一 `namespace:path` 标识。
- `sourcePackId`：来源包 ID。
- `displayName`：展示名称。
- `category`：内容类型（TOOL/WEAPON/ARMOR/FURNITURE/DECOR）。
- `tierWindow`：适配阶段窗口（minTier~maxTier）。
- `conductivityCost`：导因消耗基准。
- `tags`：扩展标签。

## 子类型规范

- `ToolSpec`：`harvestSpeed/durability/harvestTier`
- `WeaponSpec`：`attackDamage/attackSpeed/armorPierce`
- `ArmorSpec`：四件套护甲值 + `knockbackResistance`
- `FurnitureSpec`：`zoneTag/comfortBonus/utilityBonus`

## 校验规则

统一走 `NonCoreSpecValidator`：

- `contentId` 必须是 `namespace:path`
- `sourcePackId` / `displayName` 非空
- `tierWindow` 非空且区间合法
- `conductivityCost >= 0`
- 注册中心禁止重复 `contentId`

## 后续建议

- 可在数据包层增加 JSON 映射，自动转 `*Spec`。
- 为 `tags` 增加白名单词典（如 `flight`, `overload`, `stability`）。
- 在事件总线中为接入内容补充统一生命周期事件。
