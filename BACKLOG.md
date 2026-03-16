# Factor Craft 需求积压

> 由需求规划 Agent 维护，实现推进 Agent 从中拉取任务

## 核心主线

**不可偏离的核心玩法：**
- Factor 系统（因子收集、合成、扩散）
- 机器系统（提取器、合成器、传递器）
- 玩家进度（引导书、任务系统）

---

## 🔴 高优先级

### 机器逻辑完善
- [ ] `CultivatorCoreBlockEntity` - 实现特性注入逻辑
- [ ] `BreederCoreBlockEntity` - 产出物品到库存
- [ ] `SynthesizerCoreBlockEntity` - 物品槽位系统
- [ ] `TransmitterBlockEntity` - 目标位置添加 Factor

### GUI 完善
- [ ] 测试游戏中 GUI 显示
- [ ] 添加配方系统 UI 交互

---

## 🟡 中优先级

### 世界生成
- [ ] `FactorOreGenerator` - 接入世界生成系统
- [ ] `FactorAltarGenerator` - 接入结构生成系统

### 性能优化
- [ ] `OptimizedDiffusion` - 接入 Factor 系统

---

## 🟢 低优先级

### 任务系统
- [ ] `QuestTrackerScreen` - 服务端数据同步

### 文档完善
- [ ] 更新 README 功能说明
- [ ] 添加开发指南文档

---

## 💡 功能扩充建议

> 由需求规划 Agent 定期更新

### 候选方向（与主线相关）
1. **新 Factor 类型** - 添加更多因子类型（火焰、冰霜、雷电）
2. **机器升级系统** - 机器可升级提升效率
3. **视觉反馈** - Factor 流动粒子效果
4. **多方块结构变体** - 不同规模的提取器/合成器

### 暂不考虑（偏离主线）
- 多人同步优化（优先级低）
- 独立模组整合（非核心）

---

## 完成记录

| 日期 | 任务 | PR |
|------|------|-----|
| 2026-03-16 | GUI 界面修复 | #63 |
| 2026-03-16 | 物品命名修复 | #62 |
| 2026-03-16 | Chunk Factor Events | #61 |
| 2026-03-15 | Phase B/C/D 完成 | #59 |