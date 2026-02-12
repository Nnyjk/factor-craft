# 07. 模块划分与骨架（基于 00~06 设定）

## 模块边界

- `command_mvp`：负责命令配置、注册、校验与受控执行框架。
- `factor_system`：负责因子计算、日切 Tier、阈值广播与世界状态（M1）。
- `creature_drop`：负责怪物生态刷新、阶段能力、凋落物池与日切联动。
- `materials_traits_enchants_buffs`：负责材料等级、词条、附魔与状态池（M2）。
- `technology_multiblock`：负责科技树、多方块结构与功率预算（M3）。
- `combat_gear_trinkets`：负责武器、防具、饰品与能力触发（M4）。
- `building_furniture_decor`：负责建材、家具、装饰与区域玩法联动（M5）。
- `non_core_integration`：负责扩展包/第三方包的工具、武器、防具、家具接入标准。

## 代码组织

```text
com.factorcraft.module
├── FactorCraftModule / ModuleBootstrap
├── command (spec/handler/registry/validator)
├── factor
│   ├── api (FactorApi)
│   └── state (FactorWorldState / DayTierSnapshot / PollutionHeatmap / EventCooldownState)
├── creature (spawn/drop/ability/api/registry)
├── material
├── technology
│   ├── api (StructureApi)
│   └── state (StructureState)
├── gear
│   └── api (GearApi)
├── building
├── integration (provider/registrar/registry/spec-validator)
└── event (Command* / Factor* / Creature* / GearAbilityEvent / TrinketProcEvent ...)
```

## 初始化流程

1. 保留既有 `dynamic` 动态配置系统。
2. `FactorCraftMod` 启动时统一调用 `ModuleBootstrap.initializeDefaults()`。
3. `ModuleBootstrap` 提供 `reloadDefaults()` 与 `shutdownDefaults()`，并进行模块依赖校验。
4. 当前阶段模块以骨架和标准为主，后续按里程碑逐步填充逻辑。
