# 配置系统完善设计文档

**创建日期**: 2026-03-17  
**Issue**: #150  
**状态**: 阶段 A/B 已完成，阶段 C 进行中

---

## 1. 概述

完善 Factor Craft 配置系统，将所有游戏数值和机制外部化为 JSON 配置文件，支持热重载、验证、版本控制和客户端同步。

---

## 2. 当前状态

### 已有组件
- ✅ `ConfigManager` - 基础配置加载（内部/外部配置）
- ✅ `ConfigHotReloader` - 文件监听和热重载
- ✅ `TraitsConfigParser` / `MaterialsM2ConfigParser` - 配置解析器
- ✅ 10 个配置文件（traits.json, materials.json, weapons.json 等）
- ✅ `ConfigValidator` - 配置验证器（版本/必填字段/数值范围/类型检查）
- ✅ `ConfigVersionChecker` - 版本兼容性检查（语义化版本比较/升级建议）
- ✅ `ConfigDefaults` - 默认值管理器（内嵌默认值/合并回退机制）
- ✅ 5 个机器配置文件（extractor/synthesizer/transmitter/consumer/cultivator）

### 缺失功能
- ❌ 服务端 - 客户端配置同步（阶段 C）
- ❌ 配置文档和注释

---

## 3. 设计方案

### 3.1 配置验证框架

**新增类**: `ConfigValidator`

```java
public class ConfigValidator {
    // 验证 JSON 结构
    public ValidationResult validate(JsonObject config, JsonSchema schema);
    
    // 验证必填字段
    public boolean validateRequiredFields(JsonObject config, List<String> requiredFields);
    
    // 验证数值范围
    public boolean validateRange(JsonObject config, String field, double min, double max);
}
```

**配置 Schema 示例**:
```json
{
  "version": "1.0.0",
  "schema": "factorcraft:traits/1.0",
  "traits": [...]
}
```

### 3.2 版本控制系统

**新增类**: `ConfigVersionChecker`

```java
public class ConfigVersionChecker {
    // 检查版本兼容性
    public CompatibilityResult checkCompatibility(String configVersion, String requiredVersion);
    
    // 获取配置版本
    public String getVersion(JsonObject config);
    
    // 版本比较
    public int compareVersions(String v1, String v2);
}
```

**版本格式**: `major.minor.patch` (如 `1.0.0`)

### 3.3 机器配置外部化

**目标**: 将 Java 配置类转为 JSON

| 当前 Java 类 | 目标 JSON 文件 |
|-------------|---------------|
| `ExtractionConfig` | `config/machines/extractor.json` |
| `SynthesisConfig` | `config/machines/synthesizer.json` |
| `TransmitterConfig` | `config/machines/transmitter.json` |
| `ConsumerConfig` | `config/machines/consumer.json` |
| `BreedingConfig` | `config/machines/cultivator.json` |

**JSON 结构示例** (`extractor.json`):
```json
{
  "version": "1.0.0",
  "machines": {
    "extractor_t1": {
      "speed": 1.0,
      "capacity": 1000,
      "energy_consumption": 10,
      "range": 3
    },
    "extractor_t2": {
      "speed": 2.0,
      "capacity": 5000,
      "energy_consumption": 25,
      "range": 5
    }
  },
  "_comments": {
    "speed": "提取速度倍率，1.0 = 基础速度",
    "capacity": "内部 Factor 存储容量",
    "energy_consumption": "每次工作的 Factor 消耗",
    "range": "影响范围（区块半径）"
  }
}
```

### 3.4 服务端 - 客户端同步

**新增网络包**: `ConfigSyncPayload`

```java
public record ConfigSyncPayload(String configName, JsonObject configData) implements CustomPayload {
    public static final Id<ConfigSyncPayload> ID = new Id<>(Identifier.of("factorcraft", "config_sync"));
}
```

**同步流程**:
1. 服务器启动时加载所有配置
2. 玩家加入时，服务器发送 `ConfigSyncPayload`
3. 客户端接收并缓存配置
4. 配置热重载时，重新同步到客户端

### 3.5 默认值回退机制

**新增类**: `ConfigDefaults`

```java
public class ConfigDefaults {
    private static final Map<String, JsonObject> DEFAULTS = new HashMap<>();
    
    public static JsonObject getDefaults(String configName);
    public static void registerDefaults(String configName, JsonObject defaults);
}
```

**回退逻辑**:
```
加载配置 → 验证失败 → 使用默认值 → 记录警告日志
```

---

## 4. 实施计划

### 阶段 A：配置验证与版本控制（核心基础）✅ 已完成
- [x] 创建 `ConfigValidator` 类
- [x] 创建 `ConfigVersionChecker` 类
- [x] 为所有配置文件添加 `version` 字段
- [x] 创建 `ConfigDefaults` 默认值系统
- [x] 集成到 `ConfigManager`

### 阶段 B：机器配置外部化（功能完善）✅ 已完成
- [x] 创建 `config/machines/` 目录
- [x] 创建 5 个机器配置文件
- [x] 创建 `MachineConfigLoader` 统一加载
- [ ] 修改机器 BlockEntity 使用 JSON 配置
- [ ] 移除旧的 Java 配置类

### 阶段 C：服务端 - 客户端同步（多人游戏支持）✅ 已完成
- [x] 创建 `ConfigSyncPayload` 网络包
- [x] 实现服务器端同步逻辑
- [x] 实现客户端接收逻辑
- [ ] 配置热重载时重新同步（可选）

### 阶段 D：文档完善
- [ ] 编写配置文档 `docs/configuration.md`
- [ ] 为每个配置文件添加注释
- [ ] 更新 README 配置说明

---

## 5. 验收标准

- [ ] 所有配置文件包含 `version` 字段
- [ ] 无效配置自动回退到默认值
- [ ] 版本不匹配时给出警告
- [ ] 5 个机器配置外部化为 JSON
- [ ] 服务端配置同步到客户端
- [ ] 配置文档完整

---

## 6. 技术细节

### 6.1 配置文件目录结构

```
src/main/resources/config/
├── traits.json
├── materials.json
├── weapons.json
├── dimensions.json
├── biome_concentrations.json
├── resonance_rules.json
├── cultivation.json
├── structure_unlocks.json
├── extraction.json
├── material_production.json
└── machines/           # 新增
    ├── extractor.json
    ├── synthesizer.json
    ├── transmitter.json
    ├── consumer.json
    └── cultivator.json
```

### 6.2 配置加载顺序

1. 加载默认配置（resources 内嵌）
2. 加载外部配置（config 目录）
3. 验证配置
4. 应用默认值回退
5. 同步到客户端

### 6.3 热重载触发

- 文件修改事件（WatchService）
- `/reload` 命令
- 服务器启动时

---

## 7. 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 配置格式错误导致崩溃 | 高 | 验证失败时使用默认值 |
| 客户端 - 服务器配置不同步 | 高 | 强制同步，版本检查 |
| 性能问题（大配置文件） | 中 | 异步加载，缓存解析结果 |
| 向后兼容性问题 | 中 | 版本号管理，迁移指南 |

---

## 8. 依赖关系

- #115 游戏平衡配置外部化
- #107 任务配置外部化
- #130 模组兼容性测试

---

**设计批准**: 待用户确认  
**实施状态**: 未开始
