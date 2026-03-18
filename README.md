# Factor Craft

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.16.10-0075CA.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **v0.2.0 BETA** - 一个为 Minecraft 添加 Factor 能量系统和科技元素的 Fabric Mod

**Factor Craft** 引入了一个全新的能量维度：**Factor（因子）**。这是一种弥漫在世界中的神秘能量，影响着机器运行、生物变异和玩家能力。通过掌握 Factor，你可以建立自动化工厂、培育特殊生物、并完成宏大的科技任务链。

---

## 🌟 核心特性

### ⚡ Factor 能量系统
- **动态潮汐系统**：Factor 浓度随时间周期性波动（枯竭→低能→稳定→高能→过载）
- **多维度支持**：主世界、下界、末地拥有不同的 Factor 基准值和波动周期
- **浓度扩散**：Factor 在区块间自然扩散，形成动态平衡网络
- **玩家效果**：不同潮汐状态提供不同的玩家增益/减益效果
  - 🟣 **枯竭**：缓慢 I（移动速度降低）
  - ⚪ **低能**：无明显效果
  - ⚪ **稳定**：无效果（理想工作状态）
  - 🔵 **高能**：生命恢复 I
  - 🟣 **过载**：力量 I + 凋零 I（高风险高回报）

### 🏭 机器系统
- **提取器核心**：从环境中提取 Factor 能量
- **合成器核心**：消耗 Factor 合成高级物品
- **消耗器核心**：将物品转化为 Factor
- **培育器核心**：加速生物培育和进化
- **发射器核心**：跨维度传输 Factor
- **多方块结构**：支持大型多方块机器构建
- **配方系统**：数据驱动配方（JSON 格式），支持热重载

### 🧬 培育系统
- **生物培育**：在培育器中加速生物生长和进化
- **特性继承**：子代继承父母的优良特性
- **Factor 影响**：高浓度 Factor 环境提高变异率
- **自动化培育**：支持全自动培育流水线

### 📜 任务系统
- **66 个主线任务**：涵盖教程、收集、建造、探索、挑战五大类
- **3 条任务链**：
  - 📖 **主线剧情**（10 个任务）：从新手到 Factor 大师的成长之路
  - ⚙️ **自动化**（5 个任务）：建立自动化工厂
  - ⚔️ **战斗挑战**（5 个任务）：在高 Factor 环境中生存
- **多样化目标**：合成、收集、建造、探索、维度旅行等
- **丰富奖励**：物品、Factor、任务 XP、成就解锁
- **服务端同步**：多人游戏中任务进度实时同步

### 🌍 世界生成
- **Factor 矿石**：在主世界、下界、末地生成 Factor 矿石
- **祭坛结构**：自然生成的 Factor 祭坛，提供初始资源
- **维度差异**：不同维度的矿石生成率和浓度不同

### 🎮 游戏体验优化
- **HUD 显示**：实时显示当前区域的 Factor 浓度和潮汐状态
- **机器动画**：机器工作时显示粒子效果和动画
- **进度系统**：与 Minecraft 成就系统深度集成
- **配置系统**：支持自定义 Factor 生成率、机器效率等参数

---

## 📦 安装指南

### 前置要求

| 组件 | 版本 | 下载地址 |
|------|------|----------|
| Minecraft | 1.21.4 | [官网](https://www.minecraft.net/) |
| Fabric Loader | 0.16.10+ | [下载](https://fabricmc.net/use/installer/) |
| Fabric API | 0.119.2+ | [下载](https://modrinth.com/mod/fabric-api) |
| Java | 21+ | [下载](https://adoptium.net/) |

### 安装步骤

1. **安装 Fabric Loader**
   - 下载 Fabric 安装器
   - 选择 Minecraft 1.21.4 版本
   - 点击"安装客户端"（或"安装服务器"）

2. **安装依赖模组**
   - 下载 **Fabric API**
   - 放入 `.minecraft/mods` 文件夹

3. **安装 Factor Craft**
   - 下载最新版本的 Factor Craft
   - 放入 `.minecraft/mods` 文件夹

4. **启动游戏**
   - 在 Minecraft 启动器中选择 "Fabric 1.21.4" 配置
   - 启动游戏，在主菜单检查模组列表确认 Factor Craft 已加载

### 版本兼容性

| Factor Craft 版本 | Minecraft | Fabric Loader | Fabric API | 状态 |
|-------------------|-----------|---------------|------------|------|
| v0.2.0-beta | 1.21.4 | 0.16.10+ | 0.119.2+ | 🟢 当前版本 |
| v0.1.x | 1.21.4 | 0.16.x | 0.110.x+ | 🟡 稳定版本 |

---

## 🎮 入门指南

### 第一步：获取初始 Factor

1. 在世界中寻找 **Factor 矿石**（发出淡蓝色光芒的矿石）
2. 用任意镐开采，获得 **Factor 晶体**
3. 或使用 **简易提取器** 从环境中直接提取 Factor

### 第二步：建立基础工厂

1. 制作 **提取器核心** 和 **基础机器外壳**
2. 建造第一台 **Factor 提取器**
3. 连接 **能量导管** 传输 Factor

### 第三步：推进科技

1. 打开任务书（默认键 `U`）查看当前任务
2. 按照任务指引解锁新配方和机器
3. 逐步建立自动化生产线

### 潮汐状态提示

- ⚪ **稳定状态**（浓度 40-60%）：机器工作效率正常，适合长期运行
- 🔵 **高能状态**（浓度 60-80%）：机器效率 +25%，适合加速生产
- 🟣 **过载状态**（浓度 80-100%）：机器效率 +50%，但有损坏风险

---

## 🖼️ 截图展示

### 机器系统
![提取器工作](docs/screenshots/extractor.png)
*Factor 提取器在高能潮汐状态下工作*

### 多方块结构
![多方块机器](docs/screenshots/multiblock.png)
*大型多方块 Factor 合成阵列*

### 任务界面
![任务书](docs/screenshots/quest_book.png)
*任务书界面显示当前任务进度*

### HUD 显示
![HUD](docs/screenshots/hud.png)
*左上角显示当前区域的 Factor 浓度和潮汐状态*

---

## ⌨️ 控制

| 操作 | 默认键位 | 说明 |
|------|----------|------|
| 打开任务书 | `U` | 查看任务列表和进度 |
| 打开机器 GUI | 右键点击机器 | 访问机器界面 |
| 扫描 Factor 浓度 | 手持扫描仪右键 | 显示当前区块的 Factor 信息 |

---

## ⚙️ 配置

配置文件位于 `config/factorcraft.json`，支持以下选项：

```json
{
  "factor": {
    "generationRate": 1.0,
    "diffusionRate": 0.5,
    "tideCycleMultiplier": 1.0
  },
  "machines": {
    "efficiencyMultiplier": 1.0,
    "powerConsumptionMultiplier": 1.0
  },
  "world": {
    "oreSpawnRate": 1.0,
    "structureSpawnRate": 1.0
  }
}
```

---

## 🔧 开发

### 环境设置

```bash
# 克隆仓库
git clone https://github.com/Nnyjk/factor-craft.git
cd factor-craft

# 设置开发环境
./gradlew setupDecompWorkspace

# 在 IDE 中打开项目（推荐 IntelliJ IDEA）
# 选择 build.gradle 文件导入
```

### 构建

```bash
# 构建模组
./gradlew build

# 运行测试
./gradlew test

# 运行游戏测试
./gradlew runGametest

# 启动开发环境
./gradlew runClient
```

### 项目结构

```
src/main/java/com/factorcraft/
├── FactorCraftMod.java          # 主模组类
├── config/                      # 配置系统
├── module/                      # 功能模块
│   ├── factor/                  # Factor 核心系统
│   │   ├── FactorService.java   # Factor 管理服务
│   │   ├── TideSystem.java      # 潮汐系统
│   │   ├── TideStatus.java      # 潮汐状态枚举
│   │   └── TideEffectManager.java # 效果管理器
│   ├── technology/              # 科技模块
│   │   ├── machines/            # 机器系统
│   │   └── recipes/             # 配方系统
│   ├── cultivation/             # 培育系统
│   └── quest/                   # 任务系统
├── registry/                    # 游戏内容注册
├── world/                       # 世界生成
└── network/                     # 网络同步
```

### 数据驱动内容

Factor Craft 使用 JSON 数据文件定义游戏内容，支持热重载：

- **配方**：`src/main/resources/data/factorcraft/recipes/`
- **任务**：`src/main/resources/data/factorcraft/quests/`

使用 `/reload` 命令重新加载数据文件，无需重启游戏。

---

## 📋 已知问题

### v0.2.0 BETA 已知问题

1. **性能问题**：当区域内有 100+ 台机器同时运行时，可能出现 TPS 下降
   - 临时解决方案：减少同时运行的机器数量，或调整配置中的 `diffusionRate`

2. **多方块检测**：某些复杂多方块结构可能无法正确识别
   - 临时解决方案：确保多方块结构完全对齐，无方块间隙

3. **任务同步延迟**：多人游戏中任务进度同步可能有 1-2 秒延迟
   - 计划修复：v0.2.1

4. **兼容性**：尚未测试与所有科技模组的兼容性
   - 已确认兼容：JEI/REI, JourneyMap
   - 测试中：Create, Thermal Series

如遇到问题，请在 [GitHub Issues](https://github.com/Nnyjk/factor-craft/issues) 报告。

---

## 🔗 链接

- **GitHub 仓库**: [https://github.com/Nnyjk/factor-craft](https://github.com/Nnyjk/factor-craft)
- **Issue 追踪**: [https://github.com/Nnyjk/factor-craft/issues](https://github.com/Nnyjk/factor-craft/issues)
- **Discord**: [加入服务器](https://discord.gg/xxxxx) (即将开放)
- **QQ 群**: 群号待定 (即将开放)

### 下载

- **CurseForge**: [即将发布](https://curseforge.com/minecraft/mc-mods/factor-craft)
- **Modrinth**: [即将发布](https://modrinth.com/mod/factor-craft)
- **GitHub Releases**: [https://github.com/Nnyjk/factor-craft/releases](https://github.com/Nnyjk/factor-craft/releases)

---

## 📜 更新日志

详见 [CHANGELOG.md](CHANGELOG.md)

**v0.2.0-beta (最新)**
- ✨ 新增：5 种潮汐状态效果系统
- ✨ 新增：66 个任务内容（教程/收集/建造/探索/挑战/任务链）
- ✨ 新增：55 个机器配方（提取/合成/消耗/培育）
- ✨ 新增：数据驱动配方系统（JSON 格式，支持热重载）
- 🔧 优化：Factor 扩散算法性能提升
- 🐛 修复：多个机器逻辑问题

---

## 👥 贡献

欢迎贡献代码、报告问题或提出建议！

### 贡献方式

1. **提交 Issue**：报告 Bug 或提出新功能建议
2. **提交 PR**：修复问题或实现新功能
3. **文档改进**：帮助完善 Wiki 和指南
4. **社区帮助**：在 Discord/QQ 群帮助其他玩家

### 开发规范

- 遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范
- 代码需通过所有测试 (`./gradlew test`)
- PR 需关联对应的 Issue

---

## 📄 许可证

本项目采用 **MIT 许可证** - 详见 [LICENSE](LICENSE) 文件

---

## 🙏 致谢

- [FabricMC](https://fabricmc.net/) - 优秀的 Mod 加载框架
- [Minecraft](https://www.minecraft.net/) - Mojang Studios
- 所有贡献者和社区成员

---

<div align="center">

**Factor Craft** - 掌握因子之力，建造科技帝国

[![Stars](https://img.shields.io/github/stars/Nnyjk/factor-craft?style=social)](https://github.com/Nnyjk/factor-craft/stargazers)
[![Forks](https://img.shields.io/github/forks/Nnyjk/factor-craft?style=social)](https://github.com/Nnyjk/factor-craft/network/members)

</div>
