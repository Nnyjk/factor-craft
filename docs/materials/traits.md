# 材料特性系统

> **版本**: 1.0  
> **最后更新**: 2026-03-17  
> **模块**: `materials_traits_enchants_buffs`

---

## 📋 概述

材料特性系统为 Factor Craft 中的物品提供可配置的特性（Traits）、附魔（Enchants）和状态效果（Status Effects）支持。

### 核心功能

- **特性系统**: 物品可以拥有多个特性，提供被动加成
- **附魔系统**: 类似原版附魔，但基于材料等级
- **状态效果**: 特性触发的临时增益/减益效果
- **共振机制**: 相同特性叠加产生协同效果
- **浓度关联**: 特性效果受 Factor 浓度影响

---

## 🎯 特性类别

### 1. 通用特性 (GENERAL)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:resonant` | 共振 | 提升所有特性效果 | +15% 特性持续时间 |
| `factorcraft:lagged_core` | 延迟核心 | 减少服务器延迟影响 | +20% 稳定性 |
| `factorcraft:conductor` | 导体 | 优化能量传输 | -20% 传输损耗 |
| `factorcraft:anchored` | 锚定 | 防止维度跳跃丢失 | +30% 维度稳定性 |

### 2. 提取特性 (EXTRACTION)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:conductive` | 导电 | 提升 Factor 提取速度 | +20% 提取速度/级 |
| `factorcraft:stable` | 稳定 | 减少区块浓度波动 | -30% 波动幅度/级 |
| `factorcraft:factor_sensitive` | Factor 敏感 | 低浓度区域效率提升 | +30% 低浓度效率 |

### 3. 传输特性 (TRANSFER)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:conductive_transfer` | 传导 | 加速区块间 Factor 扩散 | +50% 扩散速度/级 |
| `factorcraft:storage` | 存储 | 临时存储更多 Factor | +50% 缓冲容量/级 |

### 4. 生产特性 (PRODUCTION)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:catalyst` | 催化 | 加速合成速度 | +30% 合成速度/级 |

### 5. 维度特性 (DIMENSION)

| 特性 ID | 名称 | 描述 | 维度 | 效果 |
|---------|------|------|------|------|
| `factorcraft:heat_resistant` | 耐热 | 下界效率提升 | 下界 | +50% 效率/级 |
| `factorcraft:void_adapted` | 虚空适应 | 末地效率提升 | 末地 | +50% 效率/级 |
| `factorcraft:nether_surge` | 下界涌动 | 下界 Factor 活性增强 | 下界 | +10% 持续时间 |
| `factorcraft:end_silence` | 末地静默 | 末地稳定性提升 | 末地 | -15% 波动 |
| `factorcraft:overworld_bloom` | 主世界绽放 | 主世界自然恢复 | 主世界 | +10% 恢复速度 |

### 6. 负面特性 (NEGATIVE)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:fragile` | 易损 | 耐久消耗加快 | +30% 耐久消耗/级 |
| `factorcraft:unstable` | 不稳定 | 区块浓度波动增加 | +50% 波动幅度/级 |
| `factorcraft:energy_hungry` | 耗能 | 额外 Factor 消耗 | +20% 消耗/级 |
| `factorcraft:noisy` | 噪音 | 降低相邻槽位特性效果 | -30% 相邻效果 |

### 7. 终局特性 (ENDGAME)

| 特性 ID | 名称 | 描述 | 效果 |
|---------|------|------|------|
| `factorcraft:arbiter_mark` | 仲裁者印记 | 终局材料专属 | 解锁特殊能力 |
| `factorcraft:chaos_gift` | 混沌恩赐 | 高风险高回报 | +25% 效果，+25% 副作用 |

---

## 🔧 材料等级

### 等级划分

| 等级 | 名称 | 特性槽位 | 典型材料 |
|------|------|----------|----------|
| T1 | LV1_CRUDE (粗糙) | 1 | 铜核心 |
| T2 | LV2_INDUSTRIAL (工业) | 2 | 下界钢 |
| T3 | LV3_DIMENSIONAL (维度) | 3 | 钴矩阵 |
| T4 | LV4_ANCIENT (古代) | 4 | 古代合金 |
| T5 | LV5_ARBITER (仲裁者) | 5 | 仲裁者碎片 |

### 等级获取

材料等级通过以下方式提升：
1. **自然生成**: 世界生成时随机分配
2. **材料升级**: 使用升级配方（`material_upgrade_t1.json` 等）
3. **特性注入**: 通过 CultivationCore 注入特性

---

## 🎲 特性生成规则

### 权重系统

每个特性有权重值，影响生成概率：

```json
{
  "trait_id": "factorcraft:conductive",
  "weight": 1.2,  // 权重越高，越容易生成
  "tier_range": [1, 5]  // 可生成的等级范围
}
```

### 等级限制

特性只能在指定材料等级范围内生成：

- **T1 材料**: 只能生成 `tier_range[0] <= 1` 的特性
- **T5 材料**: 可以生成所有特性

### 兼容性规则

某些特性互斥，不能同时存在：

```java
// 示例：稳定 vs 不稳定
"incompatible": ["unstable"]  // 导电特性与绝缘特性不兼容
```

---

## 💫 共振机制

### 共振加成

当物品拥有多个相同特性时，触发共振加成：

- **2 个相同特性**: ×1.5 倍效果
- **3 个相同特性**: ×2.5 倍效果

### 计算公式

```java
double resonanceBonus = 1.0;
for (每个特性类型) {
    if (count >= 2) bonus *= 1.5;
    if (count >= 3) bonus *= (2.5 / 1.5);
}
```

---

## 🌊 Factor 浓度关联

### 浓度区间

| 状态 | 浓度范围 | 效果倍率 |
|------|----------|----------|
| DEPLETED (枯竭) | 0-20% | 0.4-0.6× |
| LOW_ENERGY (低能) | 20-40% | 0.7-0.85× |
| STABLE (稳定) | 40-60% | 1.0-1.2× |
| HIGH_ENERGY (高能) | 60-80% | 1.1-1.3× |
| OVERLOAD (过载) | 80-100% | 1.4-1.5× |

### 类别倍率

不同特性类别受浓度影响不同：

- **提取类**: 高浓度时效果最佳 (1.5× @ 80-100%)
- **传输类**: 稳定浓度时最佳 (1.2× @ 40-60%)
- **生产类**: 需要高能量 (1.4× @ 80-100%)
- **环境类**: 不受浓度影响 (1.0×)
- **负面类**: 高浓度时更强 (1.3× @ 80-100%)

---

## 📦 配置格式

### traits.json

```json
{
  "traits": [
    {
      "id": "conductive",
      "name": "导电",
      "type": "positive",
      "category": "extraction",
      "description": "提升 Factor 提取速度",
      "effects": [
        {
          "target": "extraction_speed",
          "operation": "multiply",
          "value": 0.2
        }
      ],
      "max_level": 3,
      "level_scaling": 0.1,
      "resonance": {
        "same_trait": {
          "threshold": 2,
          "effect": "multiply",
          "value": 1.5
        }
      },
      "incompatible": ["insulating"],
      "weight": 100,
      "tier_range": [1, 5]
    }
  ]
}
```

### materials_m2.json

```json
{
  "materialsM2": {
    "materials": [
      {
        "material_id": "factorcraft:copper_core",
        "level": "LV1_CRUDE",
        "trait_slots": 1,
        "extraction_efficiency": 0.8,
        "stability": 0.45,
        "tier": 1,
        "dimensions": ["minecraft:overworld"]
      }
    ],
    "traits": [...],
    "enchants": [...],
    "statuses": [...]
  }
}
```

---

## 🔌 API 使用

### 添加特性

```java
ItemStack stack = ...;
boolean success = TraitService.addTrait(stack, "factorcraft:conductive", 2);
```

### 获取特性列表

```java
List<TraitInstance> traits = TraitService.getTraits(stack);
for (TraitInstance trait : traits) {
    LOGGER.info("Trait: {} (Level {})", trait.traitId(), trait.level());
}
```

### 应用效果

```java
LivingEntity entity = ...;
double concentration = 75.0;  // 75% 浓度
TraitEffectApplier.applyTraitEffects(stack, entity, concentration);
```

### 移除特性

```java
boolean removed = TraitService.removeTrait(stack, "factorcraft:conductive");
```

### 清除所有特性

```java
TraitService.clearTraits(stack);
```

---

## 📊 状态效果

### 增益效果 (BUFF)

| 效果 ID | 名称 | 持续时间 | 最大堆叠 | 堆叠规则 |
|---------|------|----------|----------|----------|
| `factorcraft:tidal_focus` | 潮汐专注 | 120s | 3 | 强度堆叠 |
| `factorcraft:steady_blessing` | 稳定祝福 | 180s | 2 | 刷新持续时间 |
| `factorcraft:dimension_sync` | 维度同步 | 90s | 2 | 刷新持续时间 |
| `factorcraft:arbiter_wing` | 仲裁者之翼 | 60s | 1 | 强效覆盖 |

### 减益效果 (DEBUFF)

| 效果 ID | 名称 | 持续时间 | 最大堆叠 | 堆叠规则 |
|---------|------|----------|----------|----------|
| `factorcraft:mana_suppression` | 能量抑制 | 80s | 3 | 强度堆叠 |
| `factorcraft:rift_burn` | 裂隙灼烧 | 75s | 4 | 强度堆叠 |
| `factorcraft:structure_fatigue` | 结构疲劳 | 120s | 2 | 刷新持续时间 |
| `factorcraft:void_vertigo` | 虚空眩晕 | 45s | 1 | 强效覆盖 |

### 堆叠规则

- **STACK_INTENSITY**: 堆叠增加效果强度
- **REFRESH_DURATION**: 堆叠刷新持续时间
- **OVERRIDE_BY_STRONGER**: 强效效果覆盖弱效

---

## 🧪 测试与调试

### 生成测试

```java
MaterialsM2Service service = new MaterialsM2Service(config);
MaterialRollResult result = service.roll(seed, "minecraft:overworld", 3);

LOGGER.info("Material: {}", result.materialId());
LOGGER.info("Traits: {}", result.traitIds());
LOGGER.info("Enchants: {}", result.enchantIds());
LOGGER.info("Statuses: {}", result.statuses());
```

### 控制台命令

```bash
# 查看物品特性
/trait list <player>

# 添加特性
/trait add <player> <trait_id> [level]

# 移除特性
/trait remove <player> <trait_id>

# 清除所有特性
/trait clear <player>
```

---

## ⚠️ 注意事项

1. **特性兼容性**: 添加特性前检查 `incompatible` 列表
2. **等级限制**: 特性只能在 `tier_range` 范围内生成
3. **浓度影响**: 效果会根据 Factor 浓度动态调整
4. **共振计算**: 多个相同特性会触发共振加成
5. **负面特性**: 谨慎添加负面特性，可能影响游戏体验

---

## 📚 相关文件

- `src/main/resources/config/traits.json` - 特性定义
- `src/main/resources/factorcraft/dynamic/materials_m2.json` - 材料配置
- `src/main/java/com/factorcraft/module/material/` - 材料模块代码
- `docs/materials/` - 材料系统文档

---

> **维护者**: Factor Craft 开发团队  
> **Issue**: #148
