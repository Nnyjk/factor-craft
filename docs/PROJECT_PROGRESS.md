# Factor Craft - 项目进度文档

## 概述

Factor Craft 是一个基于 Fabric 1.21.4 的 Minecraft Mod，引入 Factor 能量系统和特性培育机制。

**项目状态：** ✅ Alpha 核心完成  
**最后更新：** 2026-03-22

---

## 阶段详情

### Phase 1: 配置系统 ✅

**目标：** 建立灵活的配置系统

**实现内容：**
- 9 个配置文件（configs.json, materials_m2.json 等）
- 热重载机制
- 动态加载系统

**核心文件：**
- `DynamicContentLoader.java`
- `DynamicContentManager.java`
- `DynamicBundle.java`

---

### Phase 2: 特性系统 ✅

**目标：** 实现 Factor 特性机制

**实现内容：**
- 12 种特性类型
- 特性注入机制
- 共振效果系统

**核心文件：**
- `TraitType.java`
- `TraitManager.java`
- `TraitInjector.java`

---

### Phase 3: Factor 系统 ✅

**目标：** 实现 Factor 能量系统

**实现内容：**
- 区块 Factor 浓度管理
- 扩散和潮汐效应
- 群系影响机制

**核心文件：**
- `ChunkFactorState.java`
- `ChunkFactorManager.java`
- `FactorDiffusion.java`

---

### Phase 4: 培育系统 ✅

**目标：** 实现特性培育机制

**实现内容：**
- Factor 注入机制
- 随机特性生成
- 培育核心方块

**核心文件：**
- `CultivationCoreBlockEntity.java`
- `FactorInjectionHandler.java`
- `TraitGenerator.java`

---

### Phase 5: UI 系统 ✅

**目标：** 实现用户界面

**实现内容：**
- Factor 浓度显示
- 特性信息显示
- 网络同步

---

### Phase 6: 机器系统 ✅

**目标：** 实现多方块机器

**实现内容：**
- 提取器 T1-T5
- 合成器 T1-T5
- 消耗器 T1-T5
- 培育器 T1-T5
- 传递器 T1-T4

**核心文件：**
- `ExtractorCoreBlockEntity.java`
- `SynthesizerCoreBlockEntity.java`
- `ConsumerCoreBlockEntity.java`
- `BreederCoreBlockEntity.java`
- `TransmitterCoreBlockEntity.java`

---

### Phase 7: 任务系统 ✅

**目标：** 实现任务引导

**实现内容：**
- 任务管理器
- 进度追踪
- 奖励系统

**核心文件：**
- `QuestManager.java`
- `Quest.java`
- `QuestReward.java`

---

### Phase 8: 世界生成 ✅

**目标：** 实现 Factor 相关世界生成

**实现内容：**
- Factor 晶体矿脉
- Factor 祭坛结构
- Boss 守护者

**核心文件：**
- `FactorCrystalOreFeature.java`
- `FactorAltarGenerator.java`
- `FactorGuardianEntity.java`

---

### Phase 9-10: 多人与优化 ✅

**目标：** 多人游戏支持和性能优化

**实现内容：**
- 权限管理系统
- 网络同步优化
- 性能分析工具

---

### Phase 11: 职业系统 🔄

**目标：** 实现三大职业体系

**设计完成：**
- 创生者 (Genesis)
- 湮灭者 (Oblivion)
- 锻铸师 (Forge)

**待实现：**
- 技能树
- 职业特性
- 专属机制

详见: `docs/designs/class_system.md`

---

## 代码统计

| 指标 | 数量 |
|------|------|
| Java 类 | 80+ |
| 配置文件 | 9 |
| 测试用例 | 18+ |
| 总代码行 | ~8000+ |

---

## 相关文档

- [路线图](../ROADMAP.md)
- [开发规划](DEVELOPMENT_PLAN.md)
- [科技树设计](designs/technology_tree.md)
- [职业系统设计](designs/class_system.md)