# 任务：Q2 Factor 浓度驱动系统实现

## 背景
根据 Issue #278 的设计调整，Factor 作为唯一能源形式，不引入传统 FE 发电系统。
本任务实现 Factor 浓度驱动系统，机器直接消耗 Factor 浓度运行。

## 目标
实现 Issue #274 定义的核心功能：
1. Factor 浓度消耗机制
2. Factor 晶体储能系统
3. Factor 泵（加速传输）
4. 机器 Factor 消耗配置框架

## 文件结构
在 `src/main/java/com/factorcraft/module/cycle/` 下创建：

```
cycle/
├── energy/
│   ├── FactorEnergyModule.java          # 模块初始化
│   ├── FactorEnergyModuleClient.java    # 客户端初始化
│   ├── FactorEnergyBlocks.java          # 方块注册表
│   ├── block/
│   │   ├── FactorPumpBlock.java         # Factor 泵方块
│   │   ├── FactorPumpBlockEntity.java   # 泵实体
│   │   ├── FactorCrystalBlock.java      # Factor 晶体方块
│   │   └── FactorCrystalBlockEntity.java # 晶体实体
│   └── component/
│       ├── FactorConsumerComponent.java # 消费者组件
│       └── FactorStorageComponent.java  # 存储组件
└── world/
    └── FactorLevelManager.java          # 浓度管理（扩展现有）
```

## 技术要求

### 1. Factor 浓度消耗
- 机器运行时降低周围区域的 Factor 浓度
- 浓度低于阈值时机器停止
- 浓度恢复后自动重启

### 2. Factor 晶体储能
- 存储 Factor 浓度（类似电池）
- 可插入机器作为备用能源
- 支持充能/放能模式

### 3. Factor 泵
- 加速 Factor 浓度传输
- 消耗 Factor 浓度运行
- 支持红石控制

### 4. 消耗配置框架
- 定义机器 Factor 消耗率
- 支持配置文件调整
- 提供 API 供其他模块使用

## 实现步骤

### Step 1: 创建目录结构
```bash
mkdir -p src/main/java/com/factorcraft/module/cycle/energy/block
mkdir -p src/main/java/com/factorcraft/module/cycle/energy/component
mkdir -p src/main/java/com/factorcraft/module/cycle/world
```

### Step 2: 创建核心组件

#### 2.1 FactorConsumerComponent.java
- 接口定义：机器消耗 Factor 的通用接口
- 方法：`consumeFactor(amount: double): boolean`
- 方法：`getConsumptionRate(): double`
- 方法：`canOperate(concentration: double): boolean`

#### 2.2 FactorStorageComponent.java
- 接口定义：Factor 存储通用接口
- 方法：`insertFactor(amount: double): double`
- 方法：`extractFactor(amount: double): double`
- 方法：`getStoredFactor(): double`
- 方法：`getCapacity(): double`

### Step 3: 创建方块和 BlockEntity

#### 3.1 FactorCrystalBlock.java
- 方块定义：Factor 晶体方块
- 属性：存储容量、当前存储量
- 交互：右键查看存储量

#### 3.2 FactorCrystalBlockEntity.java
- 实现 FactorStorageComponent
- tick 逻辑：自然充能（从环境吸收 Factor）
- 支持机器提取 Factor

#### 3.3 FactorPumpBlock.java
- 方块定义：Factor 泵
- 属性：传输速率、工作范围
- 红石控制：启用/禁用

#### 3.4 FactorPumpBlockEntity.java
- 实现 FactorConsumerComponent
- tick 逻辑：加速周围 Factor 浓度流动
- 消耗：运行时消耗 Factor 浓度

### Step 4: 创建模块初始化

#### 4.1 FactorEnergyBlocks.java
- 注册 FactorCrystalBlock
- 注册 FactorPumpBlock
- 定义 BlockItem

#### 4.2 FactorEnergyBlockEntities.java
- 注册 FactorCrystalBlockEntity
- 注册 FactorPumpBlockEntity

#### 4.3 FactorEnergyModule.java
- 模块初始化入口
- 注册方块、BlockEntity、物品
- 集成到 CycleModule

#### 4.4 FactorEnergyModuleClient.java
- 客户端初始化
- 注册屏幕处理器（如有）

### Step 5: 创建世界管理器

#### 5.1 FactorLevelManager.java
- 管理每个 Level 的 Factor 浓度分布
- 方法：`getConcentration(x, y, z): double`
- 方法：`consumeConcentration(x, y, z, amount): void`
- 方法：`addConcentration(x, y, z, amount): void`
- tick 逻辑：浓度扩散、自然恢复

### Step 6: 更新 CycleModule
- 集成 FactorEnergyModule
- 在 tick 中调用 FactorLevelManager

## 代码规范

### Commit Scope
使用 `cycle` 作为 commit scope（属于 Cycle 模块）

### 命名规范
- 方块：`FactorCrystalBlock`, `FactorPumpBlock`
- BlockEntity: `FactorCrystalBlockEntity`, `FactorPumpBlockEntity`
- 组件接口：`FactorConsumerComponent`, `FactorStorageComponent`

### Fabric 1.21.4 最佳实践
- 使用 `Block.Settings.create()` 创建方块设置
- 使用 `.registryKey(key)` 设置 registryKey
- 使用 `FabricBlockEntityTypeBuilder` 创建 BlockEntityType
- 使用 `RegistryKey` 注册方块和物品

## 验收标准

### 编译检查
- [ ] `./gradlew build` 成功
- [ ] 无编译错误
- [ ] 无警告

### 功能检查
- [ ] Factor 晶体可以放置并存储 Factor
- [ ] Factor 泵可以加速浓度传输
- [ ] 机器可以消耗 Factor 浓度运行
- [ ] 浓度低于阈值时机器停止

### 测试检查
- [ ] `./gradlew runGametest` 通过
- [ ] 有基本的 Game Test 覆盖

## 注意事项

1. **不使用 FE 系统**: 根据 #278 设计调整，不引入 Forge Energy
2. **浓度单位**: 使用 double 类型，范围 0.0 - 1.0
3. **性能优化**: 避免每 tick 全图扫描，使用区块级缓存
4. **配置友好**: 消耗率、容量等参数支持配置调整
