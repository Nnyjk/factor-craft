# Q3: 自动化机器系统 - 任务规范

## 目标
实现完整的自动化生产机器，支持配方编程、批量生产和智能管理。

## 实现内容

### 1. 自动合成器 (automation/crafter)
- `AutoCrafterBlock.java` - 自动合成器（9 格合成）
- `AutoCrafterBlockEntity.java` - 合成器逻辑
- `PatternEncoderBlock.java` - 样板编码器（保存配方）
- `PatternEncoderBlockEntity.java` - 编码器逻辑
- `CraftingMonitorBlock.java` - 合成监控（进度显示）
- `AutoCrafterScreenHandler.java` - 合成器 UI

### 2. 自动采集器 (automation/harvester)
- `AutoHarvesterBlock.java` - 自动收割机（作物采集）
- `AutoHarvesterBlockEntity.java` - 收割机逻辑
- `AutoMinerBlock.java` - 自动采矿机（矿物采集）
- `AutoMinerBlockEntity.java` - 采矿机逻辑
- `ItemFilterBlock.java` - 物品过滤器（筛选输出）
- `CollectionFunnelBlock.java` - 收集漏斗（聚合物品）

### 3. 自动分配器 (automation/distributor)
- `AutoDistributorBlock.java` - 自动分配器（均匀分配）
- `AutoDistributorBlockEntity.java` - 分配器逻辑
- `ItemSorterBlock.java` - 物品分拣器（分类存储）
- `ItemSorterBlockEntity.java` - 分拣器逻辑
- `PriorityAllocatorBlock.java` - 优先级分配器（按需分配）
- `OverflowExporterBlock.java` - 溢出导出器（多余输出）

### 4. 中央控制器 (automation/controller)
- `SystemControllerBlock.java` - 系统控制器（中央管理）
- `SystemControllerBlockEntity.java` - 控制器逻辑
- `MonitorScreenBlock.java` - 监控屏幕（可视化界面）
- `ProgramModuleBlock.java` - 程序模块（逻辑编程）

## 技术要点

### Fabric 1.21.4 API 要求
- `Block.Settings.create()` 需要 `.registryKey(key)` 设置 registryKey
- `BlockWithEntity` 需要实现 `getCodec()` 方法
- `FabricBlockEntityTypeBuilder.build()` 不需要 null 参数
- `writeNbt`/`readNbt` 需要 `RegistryWrapper.WrapperLookup` 参数
- `Item.Settings` 需要 `.registryKey(key)` 设置 registryKey

### 通用要求
- 所有 BlockEntity 继承 `FactorMachineBlockEntity` 基类
- 支持红石信号控制启停
- 使用 Q2 能源系统的 FactorConsumerComponent 接口
- 使用 Q1 物流系统的物品传输接口
- 客户端 UI 显示生产队列和进度
- 服务器端处理自动化逻辑

## 目录结构
```
src/main/java/nnyjk/factor/craft/cycle/automation/
├── block/
│   ├── AutomationBlocks.java          # 方块注册表
│   ├── crafter/                       # 合成器
│   ├── harvester/                     # 采集器
│   ├── distributor/                   # 分配器
│   └── controller/                    # 控制器
├── block/entity/
│   ├── AutomationBlockEntities.java   # BlockEntity 注册表
│   ├── crafter/
│   ├── harvester/
│   ├── distributor/
│   └── controller/
├── screen/
│   ├── AutomationScreens.java         # 屏幕注册表
│   └── handler/                       # ScreenHandler
├── component/
│   ├── RecipePattern.java             # 配方样板
│   └── CraftingJob.java               # 合成任务
├── AutomationModule.java              # 服务端初始化
└── AutomationModuleClient.java        # 客户端初始化
```

## 验收标准
- [ ] 编译通过 (`./gradlew compileJava`)
- [ ] 单元测试通过 (`./gradlew test`)
- [ ] CI 全部通过
- [ ] 自动合成器可按配方批量生产
- [ ] 自动采集器可无人值守采集资源
- [ ] 分配器可按规则分拣物品
- [ ] 控制器可监控整个自动化系统

## 依赖
- Q1 物流系统（物品传输）✅
- Q2 能源系统（Factor 消耗）✅

## Git 规范
- 分支名：`feature/q3-automation-system`
- Commit scope: `core` (自动化机器属于核心玩法)
- PR 标题：`feat: Q3 自动化机器系统核心实现`

## 优先级
P0 - 高优先级
