# Issue #264 - 社交系统实现计划

## 目标
实现玩家交易市场、Factor 交易所和排行榜系统。

## 模块结构

### 1. 玩家交易市场 (market)
- `MarketManager.java` - 市场数据管理
- `TradeListing.java` - 交易挂单数据结构
- `MarketScreen.java` - 市场 UI 界面
- `MarketScreenHandler.java` - 市场 ScreenHandler
- `MarketCommands.java` - 市场命令支持
- `MarketConfig.java` - 市场配置（税费、最大挂单数等）

### 2. Factor 交易所 (exchange)
- `ExchangeManager.java` - Factor 交易所管理
- `FactorPrice.java` - Factor 价格数据
- `OrderBook.java` - 订单簿（买单/卖单）
- `TradeOrder.java` - 交易订单（限价/市价）
- `ExchangeScreen.java` - 交易所 UI
- `ExchangeScreenHandler.java` - 交易所 ScreenHandler

### 3. 排行榜系统 (leaderboard)
- `LeaderboardManager.java` - 排行榜管理
- `LeaderboardEntry.java` - 排行榜条目
- `LeaderboardType.java` - 排行榜类型枚举
- `LeaderboardScreen.java` - 排行榜 UI
- `LeaderboardCommands.java` - 排行榜命令

### 4. 数据持久化
- `SocialStorage.java` - 社交数据持久化
- `MarketData.java` - 市场数据快照
- `LeaderboardData.java` - 排行榜数据

### 5. 网络包
- `MarketSyncPacket.java` - 市场数据同步
- `ExchangeSyncPacket.java` - 交易所数据同步
- `LeaderboardSyncPacket.java` - 排行榜数据同步

## 实现步骤

### Step 1: 核心数据结构
1. `TradeListing.java` - 交易挂单
2. `TradeOrder.java` - 交易订单
3. `LeaderboardEntry.java` - 排行榜条目
4. `LeaderboardType.java` - 排行榜类型

### Step 2: 管理器实现
1. `MarketManager.java` - 市场管理（单例）
2. `ExchangeManager.java` - 交易所管理（单例）
3. `LeaderboardManager.java` - 排行榜管理（单例）

### Step 3: 数据持久化
1. `SocialStorage.java` - 数据存储
2. 配置文件格式设计

### Step 4: UI 实现
1. `MarketScreen.java` + `MarketScreenHandler.java`
2. `ExchangeScreen.java` + `ExchangeScreenHandler.java`
3. `LeaderboardScreen.java`

### Step 5: 网络同步
1. 定义网络包
2. 注册到 NetworkModule

### Step 6: 命令支持
1. `MarketCommands.java`
2. `LeaderboardCommands.java`

### Step 7: 配置与平衡
1. `MarketConfig.java`
2. 税费、价格波动等参数

## 文件列表

```
src/main/java/com/factorcraft/module/social/
├── SocialModule.java (更新)
├── market/
│   ├── MarketManager.java
│   ├── TradeListing.java
│   ├── MarketConfig.java
│   ├── MarketScreen.java
│   ├── MarketScreenHandler.java
│   └── MarketCommands.java
├── exchange/
│   ├── ExchangeManager.java
│   ├── FactorPrice.java
│   ├── OrderBook.java
│   ├── TradeOrder.java
│   ├── ExchangeScreen.java
│   └── ExchangeScreenHandler.java
├── leaderboard/
│   ├── LeaderboardManager.java
│   ├── LeaderboardEntry.java
│   ├── LeaderboardType.java
│   ├── LeaderboardScreen.java
│   └── LeaderboardCommands.java
├── storage/
│   ├── SocialStorage.java
│   ├── MarketData.java
│   └── LeaderboardData.java
└── network/
    ├── MarketSyncPacket.java
    ├── ExchangeSyncPacket.java
    └── LeaderboardSyncPacket.java
```

## 验收标准
- [ ] 玩家可上架/购买物品
- [ ] Factor 价格动态调整
- [ ] 排行榜数据准确显示
- [ ] 编译通过 `./gradlew build`
- [ ] 无运行时错误

## 注意事项
- 使用 fc-runner 用户执行
- Git 身份：Y-Bot-N <214893859@qq.com>
- 分支名：feature/social-system
- Commit scope: core, ui, network, config
