# Factor Craft 配置系统指南

**最后更新**: 2026-03-17  
**版本**: 1.0.0  
**Issue**: #150

---

## 概述

Factor Craft 配置系统允许你自定义游戏数值、机器参数、特性规则等，无需修改代码或重新编译。所有配置文件采用 JSON 格式，支持热重载和版本控制。

---

## 快速开始

### 配置文件位置

配置文件位于 `.minecraft/config/factorcraft/` 目录：

```
.minecraft/
└── config/
    └── factorcraft/
        ├── extractor.json          # 提取器配置
        ├── synthesizer.json        # 合成器配置
        ├── transmitter.json        # 传递器配置
        ├── consumer.json           # 消耗器配置
        ├── cultivator.json         # 培育器配置
        ├── traits.json             # 特性定义
        ├── materials.json          # 材料定义
        ├── weapons.json            # 武器配置
        ├── dimensions.json         # 维度配置
        ├── biome_concentrations.json  # 生物群系浓度
        ├── resonance_rules.json    # 共振规则
        ├── cultivation.json        # 特性注入配置
        ├── structure_unlocks.json  # 结构解锁
        ├── extraction.json         # 提取配置
        └── material_production.json # 材料生产
```

### 修改配置

1. 找到对应的配置文件
2. 使用文本编辑器（如 VSCode、Notepad++）打开
3. 修改数值（保持 JSON 格式）
4. 保存文件
5. 在游戏中执行 `/reload` 命令（热重载）

---

## 配置文件详解

### 机器配置

#### extractor.json - 提取器配置

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:machines/extractor/1.0",
  "machines": {
    "extractor_t1": {
      "speed": 1.0,              // 提取速度倍率（1.0 = 基础速度）
      "capacity": 1000,          // 内部 Factor 存储容量
      "energy_consumption": 10,  // 每次工作的 Factor 消耗
      "range": 3,                // 影响范围（方块半径）
      "work_interval": 20        // 工作间隔（ticks，20 = 1 秒）
    },
    "extractor_t2": {
      "speed": 2.0,
      "capacity": 5000,
      "energy_consumption": 25,
      "range": 5,
      "work_interval": 15
    },
    "extractor_t3": {
      "speed": 4.0,
      "capacity": 20000,
      "energy_consumption": 60,
      "range": 7,
      "work_interval": 10
    }
  }
}
```

**参数说明**：
- `speed`: 提取速度倍率，值越大提取越快
- `capacity`: 内部 Factor 存储上限
- `energy_consumption`: 每次工作消耗的 Factor 量
- `range`: 提取器影响的范围半径
- `work_interval`: 工作冷却时间（ticks）

#### synthesizer.json - 合成器配置

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:machines/synthesizer/1.0",
  "machines": {
    "synthesizer_t1": {
      "speed": 1.0,              // 合成速度倍率
      "capacity": 2000,          // Factor 存储容量
      "energy_consumption": 20,  // 每次合成消耗
      "crafting_time": 100       // 合成时间（ticks，100 = 5 秒）
    },
    "synthesizer_t2": {
      "speed": 2.0,
      "capacity": 10000,
      "energy_consumption": 50,
      "crafting_time": 60
    }
  }
}
```

#### transmitter.json - 传递器配置

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:machines/transmitter/1.0",
  "machines": {
    "transmitter_t1": {
      "transfer_rate": 10,         // 每秒传输量
      "capacity": 500,             // 缓存容量
      "range": 16,                 // 传输范围（方块半径）
      "dimension_transfer": false  // 是否支持跨维度传输
    },
    "transmitter_t2": {
      "transfer_rate": 50,
      "capacity": 2500,
      "range": 32,
      "dimension_transfer": true
    }
  }
}
```

#### consumer.json - 消耗器配置

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:machines/consumer/1.0",
  "machines": {
    "consumer_t1": {
      "consumption_rate": 5,     // 每秒消耗量
      "capacity": 1000,          // 缓存容量
      "output_factor": 1.0       // 输出倍率
    },
    "consumer_t2": {
      "consumption_rate": 25,
      "capacity": 5000,
      "output_factor": 2.5
    }
  }
}
```

#### cultivator.json - 培育器配置

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:machines/cultivator/1.0",
  "machines": {
    "cultivator_t1": {
      "infusion_speed": 1.0,     // 灌注速度倍率
      "capacity": 3000,          // Factor 存储容量
      "energy_consumption": 30,  // 每秒能耗
      "trait_slots": 3           // 特性槽位数量
    },
    "cultivator_t2": {
      "infusion_speed": 2.5,
      "capacity": 15000,
      "energy_consumption": 75,
      "trait_slots": 5
    }
  }
}
```

---

### 世界生成配置

#### biome_concentrations.json - 生物群系浓度

定义不同生物群系的 Factor 浓度基准值。

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:world/biome/1.0",
  "biomes": {
    "minecraft:plains": {
      "base_concentration": 50.0,  // 基础浓度（0-100）
      "variation": 20.0,           // 随机波动范围
      "dimension_multiplier": 1.0  // 维度乘数
    },
    "minecraft:desert": {
      "base_concentration": 30.0,
      "variation": 15.0,
      "dimension_multiplier": 1.0
    }
  }
}
```

#### resonance_rules.json - 共振规则

控制 Factor 浓度共振机制。

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:world/resonance/1.0",
  "config": {
    "resonance_threshold": 75.0,  // 触发共振的浓度阈值
    "resonance_multiplier": 1.5,  // 共振时浓度倍率
    "decay_rate": 0.1             // 浓度衰减速率
  }
}
```

---

### 特性系统配置

#### traits.json - 特性定义

定义材料可以拥有的特性。

```json
{
  "version": "1.0.0",
  "schema": "factorcraft:traits/1.0",
  "config": {
    "max_traits_per_material": 5,    // 每个材料最大特性数
    "trait_rarity_weight": 100.0,    // 稀有度权重
    "allow_negative_traits": true,   // 是否允许负面特性
    "negative_trait_chance": 0.2     // 负面特性出现概率
  },
  "traits": [
    {
      "id": "factorcraft:sharpness",
      "name": "锋利",
      "rarity": "common",
      "effects": {
        "damage_bonus": 1.0
      }
    }
  ]
}
```

---

### 游戏平衡配置

#### extraction.json - 提取配置

控制 Factor 提取的全局参数。

```json
{
  "version": "1.0.0",
  "extraction_rate": 1.0,        // 全局提取速率倍率
  "max_concentration": 100.0,    // 最大浓度上限
  "natural_regeneration": 0.01   // 自然恢复速率
}
```

#### cultivation.json - 特性注入配置

```json
{
  "version": "1.0.0",
  "infusion_base_time": 200,     // 基础灌注时间（ticks）
  "trait_success_rate": 0.8,     // 特性注入成功率
  "failure_penalty": 0.5         // 失败惩罚（材料损失率）
}
```

---

## 配置版本控制

### 版本号格式

所有配置文件包含 `version` 字段，采用语义化版本：

```
major.minor.patch
```

- **major**: 主版本号（不兼容的变更）
- **minor**: 次版本号（向后兼容的功能新增）
- **patch**: 修订号（向后兼容的问题修复）

### 版本兼容性

| 配置版本 | 模组版本 | 兼容性 | 说明 |
|---------|---------|--------|------|
| 1.0.0 | 0.2.0+ | ✅ 完全兼容 | 初始版本 |
| 0.x.x | 0.2.0+ | ⚠️ 可能兼容 | 旧版本，建议更新 |
| 2.0.0 | 0.2.0+ | ❌ 不兼容 | 未来版本 |

### 版本检查

模组启动时会自动检查配置版本：

- **COMPATIBLE**: 版本匹配，正常加载
- **COMPATIBLE_WARN**: 版本不匹配但可兼容，记录警告
- **INCOMPATIBLE**: 版本不兼容，使用默认值

---

## 热重载

### 使用方式

1. 修改配置文件
2. 在游戏中输入 `/reload` 命令
3. 配置立即生效（无需重启）

### 注意事项

- 热重载不会影响已存在的机器/物品
- 新配置仅对新生成的内容生效
- 建议在创造模式下测试配置变更

---

## 默认值回退

如果配置文件：
- 不存在
- 格式错误
- 缺少必填字段
- 版本不兼容

模组会自动使用内嵌的默认值，并在日志中记录警告。

**示例日志**：
```
[FactorCraft:Config] 配置 extractor 版本警告：配置版本过旧，建议更新（可自动升级）
[FactorCraft:Config] 配置 traits 验证失败：缺少必填字段 version，使用默认值
```

---

## 多人游戏配置同步

### 服务端配置

- 服务端配置文件位于 `server.properties` 同级目录的 `config/factorcraft/`
- 服务端配置优先于客户端配置
- 玩家加入时自动同步配置到客户端

### 客户端缓存

- 客户端接收的配置缓存在内存中
- 断开连接时自动清除
- 单人游戏中使用本地配置

---

## 配置示例

### 提高提取器效率（简单模式）

```json
{
  "version": "1.0.0",
  "machines": {
    "extractor_t1": {
      "speed": 2.0,
      "capacity": 2000,
      "energy_consumption": 5,
      "range": 5,
      "work_interval": 10
    }
  }
}
```

### 禁用负面特性（休闲模式）

修改 `traits.json`：
```json
{
  "config": {
    "allow_negative_traits": false,
    "negative_trait_chance": 0.0
  }
}
```

### 提高 Factor 浓度（资源丰富模式）

修改 `biome_concentrations.json`：
```json
{
  "biomes": {
    "minecraft:plains": {
      "base_concentration": 80.0,
      "variation": 10.0
    }
  }
}
```

---

## 故障排除

### 配置不生效

1. 检查 JSON 格式是否正确（使用 JSON 验证工具）
2. 确认配置文件位于正确目录
3. 执行 `/reload` 命令
4. 查看日志是否有错误信息

### 游戏崩溃

1. 删除 `config/factorcraft/` 目录
2. 重新启动游戏（会生成默认配置）
3. 逐个添加自定义配置，定位问题文件

### 版本不匹配警告

日志显示：
```
[Config] extractor 版本警告：配置版本过旧，建议更新
```

**解决方案**：
1. 备份当前配置
2. 删除旧配置文件
3. 重新启动游戏生成新配置
4. 手动迁移自定义数值

---

## 配置模板

所有配置文件模板位于模组 JAR 的 `config/` 目录，可通过解压 JAR 文件获取。

---

## 相关资源

- [Issue #150](https://github.com/Nnyjk/factor-craft/issues/150) - 配置系统完善
- [CHANGELOG.md](../CHANGELOG.md) - 版本变更日志
- [README.md](../README.md) - 模组说明

---

## 反馈与支持

如有配置相关问题，请提交 Issue 或参与讨论：
- GitHub Issues: https://github.com/Nnyjk/factor-craft/issues
