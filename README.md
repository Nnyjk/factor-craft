# Factor Craft（Fabric Mod）

一个基于 Fabric 1.21.4 的 Minecraft Mod，引入 Factor 能量系统和特性培育机制。

## 项目状态

✅ **全部 11 个阶段完成，生产就绪！**

| 阶段 | 功能 | 状态 |
|------|------|------|
| P1 | 配置系统 | ✅ |
| P2 | 特性系统 | ✅ |
| P3 | Factor 系统 | ✅ |
| P4 | 培育系统 | ✅ |
| P5 | UI 系统 | ✅ |
| P6 | 测试系统 | ✅ |
| P7 | 性能优化 | ✅ |
| P8 | 游戏内容 | ✅ |
| P9 | 高级特性 | ✅ |
| P10 | 世界生成 & 任务 | ✅ |
| P11 | 多人优化 | ✅ |

## 代码统计

- **Java 类：** 52 个
- **配置文件：** 9 个
- **测试用例：** 18 个
- **总代码：** ~6200 行

## 核心功能

### Factor 系统
- 区块 Factor 浓度管理
- 扩散和潮汐效应
- 群系影响机制

### 特性系统
- 12 种特性类型
- 共振效果（最高 ×2.5）
- 特性注入和培育

### 培育系统
- Factor 注入机制
- 随机特性生成
- 培育核心方块

### 世界生成
- Factor 晶体矿脉
- 祭坛结构
- Boss 实体

### 多人功能
- 区域保护系统
- Factor 物品交易
- 排行榜系统

## 环境要求

- JDK 21
- Git
- 推荐 IDE：IntelliJ IDEA / VS Code

## 构建命令

```bash
# 编译
./gradlew build

# 运行客户端
./gradlew runClient

# 运行测试
./gradlew test

# 同步 Fabric 版本
bash scripts/sync_fabric_versions.sh
```

## 工程结构

```
src/main/java/com/factorcraft/
├── config/          # 配置系统
├── trait/           # 特性系统
├── module/          # 核心模块
│   ├── factor/      # Factor 管理
│   └── cultivation/ # 培育系统
├── ui/              # UI 系统
├── registry/        # 游戏内容注册
├── command/         # 命令系统
├── world/           # 世界生成
│   ├── generation/  # 矿脉生成
│   └── structure/   # 结构生成
├── entity/          # 实体系统
├── quest/           # 任务系统
└── multiplayer/     # 多人优化
```

## 配置文件

运行后会在 `config/factor-craft/` 下自动生成：

- `configs.json` - 业务配置
- `materials_m2.json` - M2 材料/词条/附魔/状态动态配置
- `textures.json` - 贴图资源映射
- `models.json` - 模型资源映射
- `lang.json` - 国际化词条

## 热更新机制

- 启动时自动加载 JSON 配置
- 运行中通过 `WatchService` 监听变更
- 监听到变更后自动重载

## 游戏内容

### 方块
- Factor Extractor - Factor 提取器
- Cultivation Core - 培育核心
- Factor Analyzer - Factor 分析仪

### 物品
- Factor Crystal - Factor 晶体
- Trait Essence - 特性精华
- Factor Meter - Factor 测量仪
- Resonance Catalyst - 共振催化剂
- Blank Trait Scroll - 空白特性卷轴

### 实体
- Factor Guardian - Factor 守护者（Boss）

## 命令

### 玩家命令
- `/factor info` - 查看区块 Factor 信息
- `/trait info` - 查看物品特性

### 管理员命令（OP 2+）
- `/factor set <值>` - 设置 Factor 浓度
- `/factor reset` - 重置 Factor
- `/trait add/remove` - 管理特性
- `/factorcraft stats` - 查看统计

### 超级管理员（OP 3+）
- `/factorcraft reload` - 重载配置

## 可选扩展

- Phase 12: 跨服同步
- Phase 13: 经济系统深化
- Phase 14: 社交功能

## 许可证

MIT License