# Changelog

All notable changes to Factor Craft will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0-Beta] - 2026-03-17

### ✨ Added

#### Factor 能量系统
- **潮汐系统**：5 种潮汐状态（枯竭/低能/稳定/高能/过载）
  - 浓度区间：0-20%/20-40%/40-60%/60-80%/80-100%
  - 动态波动：基于正弦波的周期性变化
  - 多维度支持：主世界/下界/末地独立基准值和周期
- **效果管理器** (TideEffectManager)
  - 玩家效果：根据潮汐状态自动应用增益/减益
    - 枯竭：缓慢 I
    - 高能：生命恢复 I
    - 过载：力量 I + 凋零 I
  - 机器效率修正：-50% 到 +50%
  - 生物生成率修正：-25% 到 +50%
- **浓度扩散系统**
  - 区块级 Factor 扩散算法
  - 优化扩散性能（支持 100+ 机器）
  - 自动平衡机制

#### 机器系统
- **提取器核心** (Extractor Core T1-T5)
  - 从环境中提取 Factor 能量
  - 支持多方块结构扩展
  - 工作动画和粒子效果
- **合成器核心** (Synthesizer Core T1-T5)
  - 消耗 Factor 合成高级物品
  - 数据驱动配方系统
  - 自动输出到相邻容器
- **消耗器核心** (Consumer Core T1-T5)
  - 将物品转化为 Factor
  - 支持批量处理
  - 效率随等级提升
- **培育器核心** (Breeder Core T1-T5)
  - 加速生物培育和进化
  - 特性遗传系统
  - Factor 浓度影响变异率
- **传递器核心** (Transmitter Core)
  - 跨维度 Factor 传输
  - 支持网络路由
  - 目标位置自动投放

#### 配方系统
- **数据驱动配方**
  - JSON 格式定义（支持热重载）
  - 55 个预设配方：
    - 15 个提取器配方
    - 10 个合成器配方
    - 10 个消耗器配方
    - 15 个培育器配方
    - 5 个基础配方
  - 按机器类型分目录组织
- **自定义加载器**
  - 独立于 Minecraft 原生 Recipe 系统
  - 支持 datapack 热重载（`/reload` 命令）
  - 简化实现，易于扩展

#### 任务系统
- **66 个任务内容**
  - 10 个教程任务：新手引导
  - 10 个收集任务：资源收集挑战
  - 10 个建造任务：多方块结构建造
  - 8 个探索任务：维度/生物群系探索
  - 8 个挑战任务：高难度挑战
  - 20 个任务链任务：
    - 主线剧情（10 个）
    - 自动化（5 个）
    - 战斗挑战（5 个）
- **9 种任务目标类型**
  - craft_item：合成物品
  - collect_item：收集物品
  - place_block：放置方块
  - build_multiblock：建造多方块
  - extract_factor：提取 Factor
  - infuse_trait：注入特性
  - visit_dimension：访问维度
  - visit_biome：访问生物群系
  - visit_structure：访问结构
- **4 种奖励类型**
  - item：物品奖励
  - factor：Factor 奖励
  - quest_xp：任务经验
  - achievement：成就解锁
- **服务端同步**
  - 多人游戏任务进度同步
  - 客户端通知系统
  - 奖励发放机制

#### 世界生成
- **Factor 矿石生成**
  - 主世界：0.5 基准浓度，中等生成率
  - 下界：1.5 基准浓度，高生成率
  - 末地：3.0 基准浓度，稀有生成
- **Factor 祭坛结构**
  - 自然生成的神秘结构
  - 提供初始 Factor 资源
  - 多方块祭坛蓝图

#### 视觉效果
- **Factor 粒子效果**
  - 高浓度区域粒子密度增加
  - 不同潮汐状态不同颜色
  - 性能优化（粒子数量限制）
- **机器工作动画**
  - 提取器：旋转动画 + 粒子喷射
  - 合成器：内部发光 + 进度条
  - 培育器：生物生长动画
- **GUI 视觉优化**
  - 进度条动画
  - 能量槽动态效果
  - 状态指示器

#### HUD 系统
- **Factor 浓度显示**
  - 实时显示当前区域浓度
  - 潮汐状态图标和颜色
  - 可配置开关

#### 配置系统
- **JSON 配置文件**
  - Factor 生成率调整
  - 机器效率倍率
  - 扩散速率控制
  - 潮汐周期倍率

### 🔧 Changed

#### 技术升级
- **Minecraft 1.21.4**
  - 升级到最新稳定版本
  - 适配新 API 变更
- **Java 21**
  - 使用最新 LTS 版本
  - 性能优化和新特性
- **Fabric Loom 1.8.13**
  - 最新开发工具链
  - 改进的依赖管理

#### API 改进
- **TideStatus 重构**
  - 从 4 种状态改为 5 种（基于浓度）
  - 新增效果管理方法
  - 向后兼容旧 API（标记为 @Deprecated）
- **FactorService 优化**
  - 改进潮汐计算方法
  - 性能优化（减少不必要的计算）
  - 更好的事件系统集成

#### 架构优化
- **模块架构重构**
  - 更清晰的模块边界
  - 改进的依赖注入
  - 降低模块间耦合

### 🐛 Fixed

#### 机器逻辑
- **SynthesizerCoreBlockEntity**
  - 修复产出物品逻辑
  - 正确输出到相邻容器
- **BreederCoreBlockEntity**
  - 修复产出物品到库存逻辑
  - 生物培育进度正确保存
- **TransmitterBlockEntity**
  - 修复在目标位置添加 Factor 逻辑
  - 跨维度传输稳定性

#### 网络同步
- **任务数据同步**
  - 修复多人游戏任务进度不同步
  - 优化网络包大小
- **Factor 浓度同步**
  - 客户端显示与服务端一致
  - 减少网络更新频率

#### 性能优化
- **扩散算法优化**
  - OptimizedDiffusion 实现
  - 减少 tick 计算量
  - 支持 100+ 机器同时运行
- **粒子效果优化**
  - 粒子数量限制
  - 距离剔除
  - 批量渲染

#### 世界生成
- **FactorOreGenerator**
  - 修复矿石生成高度分布
  - 不同维度正确生成
- **FactorAltarGenerator**
  - 修复祭坛结构生成
  - 结构完整性验证

### 📚 Documentation

- **README.md 更新**
  - 完整功能特性介绍
  - 详细安装指南
  - 版本兼容性表
  - 入门指南
  - 配置说明
  - 开发指南
- **已知问题列表**
  - v0.2.0 BETA 已知问题
  - 临时解决方案
  - 计划修复版本

### 🧪 Testing

#### 单元测试
- **测试覆盖率**：80%+
- **核心系统测试**：
  - TideSystem 测试（15 个测试用例）
  - FactorService 测试（20 个测试用例）
  - 机器逻辑测试（25 个测试用例）
  - 任务系统测试（21 个测试用例）
- **总测试数**：350+ 个测试用例

#### 集成测试
- **多人游戏测试**：任务同步、Factor 传输
- **性能测试**：100+ 机器同时运行
- **兼容性测试**：JEI/REI、JourneyMap

### 📊 Statistics

- **代码行数**：~3000+ 行
- **PR 数量**：15+ 个 PR
- **Issue 关闭**：20+ 个 Issue
- **贡献者**：2 人

---

## [0.1.0-Alpha] - 2026-03-10

### Added
- CombatModule: 15 weapons (T1-T5)
- MultiblockDetector: 16 structure blueprints
- FactorNetworkManager: Cross-dimensional transfer
- CycleModule: Tide cycle system
- 81 unit tests (100% pass)
- Performance benchmarks (7 items, all passing)

### Technical
- Java 21, Fabric 1.21.4
- Module-based architecture
- GitHub workflow templates
- 30 labels, 3 milestones

---

## 版本说明

### v0.2.0-BETA

这是 Factor Craft 的第一个 BETA 版本，标志着核心功能的基本完成。

**适合人群**：
- ✅ 愿意尝试新内容的玩家
- ✅ 能够接受少量 Bug 的测试玩家
- ✅ 愿意提供反馈的社区成员

**注意事项**：
- ⚠️ 可能存在未发现的 Bug
- ⚠️ 存档格式可能在未来的 Alpha/BETA 版本中变更
- ⚠️ 部分功能仍在完善中

**下一步计划**：
- v0.2.1：修复 BETA 测试中发现的 Bug
- v0.3.0：添加更多机器和配方
- v1.0.0：正式发布版本

### 下载

- [GitHub Releases - v0.2.0-beta](https://github.com/Nnyjk/factor-craft/releases/tag/v0.2.0-beta)
- CurseForge: 即将发布
- Modrinth: 即将发布

### 反馈

如遇到问题或有改进建议，请在 [GitHub Issues](https://github.com/Nnyjk/factor-craft/issues) 提交。
