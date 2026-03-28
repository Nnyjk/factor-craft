# Q1: Factor 物流系统实现任务

## 任务描述
实现完整的 Factor 流体式物流网络，支持智能路由、仓储管理和自动请求。

## 项目位置
/root/workspace/factor-craft

## 技术栈
- Fabric 1.21.4
- Java 21
- Gradle

## 实现内容

### 1. 创建新模块结构
在 `src/main/java/com/factorcraft/module/logistics/` 下创建：
- `LogisticsModule.java` - 模块入口
- `LogisticsModuleClient.java` - 客户端入口

### 2. 管道系统 (`src/main/java/com/factorcraft/module/logistics/pipe/`)
- `AdvancedFactorPipeBlock.java` - 智能管道方块
- `AdvancedFactorPipeBlockEntity.java` - 智能管道 BlockEntity
- `PriorityPipeBlock.java` - 优先级管道方块
- `PriorityPipeBlockEntity.java` - 优先级管道 BlockEntity
- `FilterPipeBlock.java` - 过滤管道方块
- `FilterPipeBlockEntity.java` - 过滤管道 BlockEntity
- `OneWayPipeBlock.java` - 单向管道方块
- `OneWayPipeBlockEntity.java` - 单向管道 BlockEntity
- `LogisticsPipes.java` - 方块注册表

### 3. 仓储系统 (`src/main/java/com/factorcraft/module/logistics/storage/`)
- `FactorStorageUnitBlock.java` - 大型存储单元方块
- `FactorStorageUnitBlockEntity.java` - 大型存储单元 BlockEntity
- `StorageMonitorBlock.java` - 存储监控方块
- `StorageMonitorScreenHandler.java` - 监控屏幕处理器
- `StorageMonitorScreen.java` - 监控 UI 界面
- `StorageBusBlock.java` - 存储总线方块
- `StorageBusBlockEntity.java` - 存储总线 BlockEntity
- `LogisticsStorage.java` - 仓储注册表

### 4. 物流网络 (`src/main/java/com/factorcraft/module/logistics/network/`)
- `LogisticsNetwork.java` - 物流网络管理器（单例）
- `NetworkChannel.java` - 网络频道
- `RouteCalculator.java` - 路由计算器（BFS/A*算法）
- `TrafficMonitor.java` - 流量监控
- `LogisticsConfig.java` - 物流配置

### 5. 自动请求系统 (`src/main/java/com/factorcraft/module/logistics/request/`)
- `AutoRequesterBlock.java` - 自动请求器方块
- `AutoRequesterBlockEntity.java` - 自动请求器 BlockEntity
- `RequestTerminalBlock.java` - 请求终端方块
- `RequestTerminalScreenHandler.java` - 终端屏幕处理器
- `RequestTerminalScreen.java` - 终端 UI 界面
- `RequestNetwork.java` - 请求网络

### 6. 网络包 (`src/main/java/com/factorcraft/module/logistics/network/packet/`)
- `LogisticsSyncPayload.java` - 物流同步包 S2C
- `RequestSyncPayload.java` - 请求同步包 S2C
- `LogisticsRequestPayload.java` - 物流请求包 C2S
- `LogisticsNetworkHandler.java` - 网络包处理

### 7. 集成
- 更新 `src/main/java/com/factorcraft/module/Modules.java` 注册新模块
- 更新 `src/main/java/com/factorcraft/FactorCraftMod.java` 注册网络包

## 技术要求
1. 遵循 Fabric 1.21.4 API 规范
2. 使用现有 FactorNetworkManager 架构扩展
3. 网络包使用 PacketByteBuf 序列化
4. BlockEntity 使用 PersistentState 持久化
5. 客户端 UI 使用 ScreenHandler + Screen 模式
6. Git commit scope 使用 `core` 或 `ui`

## 验收标准
1. 编译通过：`./gradlew build`
2. 所有 CI 检查通过
3. 代码符合项目规范
4. 创建 PR 提交

## Git 配置
- user.name: Y-Bot-N
- user.email: 214893859@qq.com
- 分支名：feature/logistics-system
- Commit 格式：`feat(core): <description>` 或 `feat(ui): <description>`

## 执行步骤
1. 创建模块目录结构
2. 实现管道系统
3. 实现仓储系统
4. 实现物流网络
5. 实现自动请求系统
6. 实现网络包
7. 集成到主模块
8. 编译验证
9. Git 提交并创建 PR
