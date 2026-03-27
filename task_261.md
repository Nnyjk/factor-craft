# Issue #261: 研究系统完善 - 科技树 UI 与研究点机制

## 任务概述
完善研究系统，实现可视化科技树 UI 和研究点货币机制，让玩家能够直观地查看和管理研究进度。

## 当前状态
- ✅ 核心逻辑已实现 (Research, ResearchManager, ResearchProgress, ResearchModule)
- ✅ 17 个默认研究节点已定义
- ✅ 命令支持已实现
- ❌ 缺少科技树 UI
- ❌ 缺少研究点货币系统
- ❌ 缺少研究点获取机制

## 实现目标

### 1. 研究点 (Research Point) 系统
- 新增研究点货币类型
- 玩家存储研究点数据
- 研究消耗研究点而非仅 Factor

### 2. 研究点获取机制
- Factor 合成奖励 (主路径)
- 任务完成奖励
- 成就解锁奖励
- 首研奖励 (首次合成新 Factor)

### 3. 科技树 UI
- ResearchTreeScreen - 可视化科技树
- ResearchTreeScreenHandler - 服务端容器
- 节点状态渲染 (锁定/可用/研究中/已完成)
- 点击交互 (开始研究/查看详情)
- 缩放和平移功能

### 4. 网络同步
- ResearchSyncPayload - 研究进度同步
- ResearchPointSyncPayload - 研究点同步
- 客户端缓存

### 5. 研究树扩展
- 扩展至 30+ 研究节点
- 5 个科技分支 (提取、存储、合成、应用、终极)
- 平衡研究成本和时间

## 文件清单

### 新增文件
```
src/main/java/com/factorcraft/module/research/
├── ResearchPointManager.java          # 研究点管理
├── ResearchPointStorage.java          # 玩家研究点存储
├── screen/
│   ├── ResearchTreeScreen.java        # 科技树 UI
│   ├── ResearchTreeScreenHandler.java # 科技树容器
│   └── ResearchNodeWidget.java        # 研究节点 UI 组件
└── network/
    ├── ResearchSyncPayload.java       # 研究进度同步包
    └── ResearchPointSyncPayload.java  # 研究点同步包
```

### 修改文件
```
src/main/java/com/factorcraft/module/research/
├── Research.java                      # 添加研究点成本字段
├── ResearchManager.java               # 集成研究点逻辑
├── ResearchProgress.java              # 添加研究点存储
├── ResearchModule.java                # 注册 UI 和网络包
└── ResearchCommands.java              # 添加研究点命令

src/main/resources/data/factorcraft/research/
└── default.json                       # 扩展研究节点至 30+
```

## 验收标准
- [ ] 玩家可通过命令 `/research points` 查看研究点
- [ ] 玩家可通过 GUI 打开科技树 (建议绑定键或物品)
- [ ] 科技树显示所有研究节点和连接关系
- [ ] 节点正确显示状态 (锁定/可用/研究中/已完成)
- [ ] 点击节点可开始研究 (消耗研究点)
- [ ] 研究完成后解锁对应效果
- [ ] 研究点通过 Factor 合成等途径获取
- [ ] 网络同步正常 (多人游戏)
- [ ] 构建通过 (`./gradlew build`)
- [ ] 无编译错误和警告

## 技术要点
1. 使用 Fabric Screen API v1
2. 使用 ExtendedScreenHandlerType 进行数据同步
3. 研究点存储使用 PlayerComponent 或 NBT
4. 科技树渲染使用 Minecraft GUI 系统
5. 遵循项目现有代码风格

## 优先级
P0 - 高优先级 (Phase O 核心玩法)

## 预计工作量
5-7 天
