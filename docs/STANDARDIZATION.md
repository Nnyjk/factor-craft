# Factor Craft 命名标准化规范

> 版本: 1.1  
> 日期: 2026-03-14  
> 状态: 已批准

---

## 一、命名格式

**标准格式**: `factor` + `_` + `{group}` + `_` + `{name}` + `_` + `{tier}`

| 组成部分 | 说明 |
|----------|------|
| `factor` | 前缀，所有标签统一 |
| `{group}` | 分组：`machine` / `block` / `item` |
| `{name}` | 功能名称 |
| `{tier}` | 科技等级 T1-T5（可选，跨 tier 存在时必填）|

---

## 二、Group 定义

| Group | 定义 | 特征 |
|-------|------|------|
| `machine` | 机器方块 | 有 BlockEntity + tick 逻辑 |
| `block` | 静态方块 | 无 BlockEntity |
| `item` | 物品 | 可堆叠物品 |

---

## 三、Machine 命名

### 四大结构核心 (T1-T5)

| Tier | 提取核心 | 消耗核心 | 合成核心 | 培育核心 |
|------|----------|----------|----------|----------|
| T1 | `factor_machine_extractor_core_t1` | `factor_machine_consumer_core_t1` | `factor_machine_synthesizer_core_t1` | `factor_machine_cultivator_core_t1` |
| T2 | `factor_machine_extractor_core_t2` | `factor_machine_consumer_core_t2` | `factor_machine_synthesizer_core_t2` | `factor_machine_cultivator_core_t2` |
| T3 | `factor_machine_extractor_core_t3` | `factor_machine_consumer_core_t3` | `factor_machine_synthesizer_core_t3` | `factor_machine_cultivator_core_t3` |
| T4 | `factor_machine_extractor_core_t4` | `factor_machine_consumer_core_t4` | `factor_machine_synthesizer_core_t4` | `factor_machine_cultivator_core_t4` |
| T5 | `factor_machine_extractor_core_t5` | `factor_machine_consumer_core_t5` | `factor_machine_synthesizer_core_t5` | `factor_machine_cultivator_core_t5` |

### 传输系统 (T1-T5)

| Tier | 传递器 | 储罐 | 泵 |
|------|--------|------|-----|
| T1 | `factor_machine_conduit_t1` | `factor_machine_tank` | `factor_machine_pump` |
| T2 | `factor_machine_conduit_t2` | — | — |
| T3 | `factor_machine_conduit_t3` | — | — |
| T4 | `factor_machine_conduit_t4` | — | — |
| T5 | `factor_machine_conduit_t5` | — | — |

### 四大结构名称对照

| Tier | 提取结构 | 消耗结构 | 合成结构 | 培育结构 |
|------|----------|----------|----------|----------|
| T1 | 星辰收集器 | 灵魂燃烧器 | 远古合成阵 | 命运织机 |
| T2 | 星辰阵列 | 灵魂熔炉 | 远古锻造台 | 灵魂编织器 |
| T3 | 星云汲取器 | 深渊吞噬者 | 命运铸造炉 | 命运祭坛 |
| T4 | 宇宙共鸣器 | 混沌裂隙 | 创世熔炉 | 命运圣所 |
| T5 | 虚空漩涡 | 永恒炉心 | 本源祭坛 | 轮回之门 |

---

## 四、Block 命名

### 特性方块 (无 tier)

| 修改前 | 修改后 | 中文名 |
|--------|--------|--------|
| `sharp_block` | `factor_block_trait_sharp` | 锐利方块 |
| `sturdy_block` | `factor_block_trait_sturdy` | 坚固方块 |
| `protective_block` | `factor_block_trait_protective` | 防护方块 |
| `energetic_block` | `factor_block_trait_energetic` | 充能方块 |
| `catalytic_block` | `factor_block_trait_catalytic` | 催化方块 |
| `stabilizing_block` | `factor_block_trait_stabilizing` | 稳定方块 |

### 建筑方块 (T1-T5)

| Tier | 命名 | 中文名 |
|------|------|--------|
| T1 | `factor_block_building_t1` | 建筑方块 T1 |
| T2 | `factor_block_building_t2` | 建筑方块 T2 |
| T3 | `factor_block_building_t3` | 建筑方块 T3 |
| T4 | `factor_block_building_t4` | 建筑方块 T4 |
| T5 | `factor_block_building_t5` | 建筑方块 T5 |

### 其他方块 (无 tier)

| 修改前 | 修改后 | 中文名 |
|--------|--------|--------|
| `factor_anchor` | `factor_block_anchor` | Factor 锚点 |

---

## 五、Item 命名

### 特性水晶 (无 tier)

| 修改前 | 修改后 | 中文名 |
|--------|--------|--------|
| `sharp_crystal` | `factor_item_crystal_sharp` | 锐利水晶 |
| `sturdy_crystal` | `factor_item_crystal_sturdy` | 坚固水晶 |
| `protective_crystal` | `factor_item_crystal_protective` | 防护水晶 |
| `energetic_crystal` | `factor_item_crystal_energetic` | 充能水晶 |
| `catalytic_crystal` | `factor_item_crystal_catalytic` | 催化水晶 |

### 线圈 (T1-T5)

| Tier | 命名 | 中文名 |
|------|------|--------|
| T1 | `factor_item_coil_t1` | 提取线圈 T1 |
| T2 | `factor_item_coil_t2` | 提取线圈 T2 |
| T3 | `factor_item_coil_t3` | 提取线圈 T3 |
| T4 | `factor_item_coil_t4` | 提取线圈 T4 |
| T5 | `factor_item_coil_t5` | 提取线圈 T5 |

### 电路 (无 tier)

| 修改前 | 修改后 | 中文名 |
|--------|--------|--------|
| `basic_circuit` | `factor_item_circuit_basic` | 基础电路 |
| `advanced_circuit` | `factor_item_circuit_advanced` | 进阶电路 |
| `elite_circuit` | `factor_item_circuit_elite` | 精英电路 |

---

## 六、语言文件 Key

**格式**: `{type}.factorcraft.{id}`

示例：
- `block.factorcraft.factor_machine_extractor_core_t1`
- `block.factorcraft.factor_block_building_t1`
- `item.factorcraft.factor_item_crystal_sharp`

---

## 七、需删除的旧注册

| 类别 | 删除项 |
|------|--------|
| 方块 | `factor_extractor_core`, `factor_emitter_core`, `factor_utilizer_core` |
| 方块 | `cultivation_core`, `factor_extractor` |

---

## 八、迁移执行顺序

1. [x] 更新 ModBlocks.java
2. [x] 更新 ModItems.java
3. [x] 更新 ModMachines.java
4. [x] 更新模型文件 (models/block/, models/item/)
5. [x] 更新 blockstate 文件
6. [x] 更新语言文件 (en_us.json, 新增 zh_cn.json)
7. [x] 更新结构配置 (altar_structures/, structures/)
   - altar_structures: extractor_t1, extractor_t2, consumer_t1, synthesizer_t1, cultivator_t1
   - structures: extractor (T1-T5), consumer (T1-T5), synthesizer (T1-T5), cultivator (T1-T5)
8. [x] 更新创造模式标签页
9. [x] 编译测试通过

---

> **批准人:** Nn  
> **批准日期:** 2026-03-14