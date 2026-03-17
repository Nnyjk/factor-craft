# 配方系统数据驱动

Factor Craft 的配方系统现已支持数据驱动（datapack），允许通过 JSON 文件定义配方，无需修改代码即可添加新配方。

## 架构设计

配方系统采用**自定义加载器**而非 Minecraft 原生 Recipe 系统，原因：
- 简化实现，避免复杂的 Recipe API
- 更灵活的数据结构
- 与现有 DataPackManager 架构一致

### 核心组件

1. **RecipeData** - 配方数据类（纯数据，无逻辑）
2. **RecipeRegistry** - 配方注册表（内存存储）
3. **RecipeLoader** - 配方加载器（从 JSON 加载）

---

## 配方类型

### 1. Factor 融合配方 (`factorcraft:factor_fusion`)

用于定义使用 Factor 进行物品合成的配方（如材料升级）。

#### JSON 格式

```json
{
  "type": "factorcraft:factor_fusion",
  "group": "material_upgrade",
  "input": {
    "item": "factorcraft:dust_copper_ingot"
  },
  "input_count": 64,
  "output": {
    "item": "factorcraft:shadow_steel_ingot",
    "count": 32
  },
  "factor_cost": 1000.0,
  "craft_time": 1200,
  "category": "material"
}
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `type` | String | ✅ | - | 配方类型，固定为 `factorcraft:factor_fusion` |
| `group` | String | ❌ | `""` | 配方分组，用于 JEI/REI 显示 |
| `input.item` | String | ✅ | - | 输入物品 ID |
| `input_count` | Integer | ❌ | `1` | 输入物品数量 |
| `output.item` | String | ✅ | - | 输出物品 ID |
| `output.count` | Integer | ❌ | `1` | 输出物品数量 |
| `factor_cost` | Double | ✅ | - | Factor 消耗量 |
| `craft_time` | Integer | ❌ | `200` | 合成时间（tick） |
| `category` | String | ❌ | `"misc"` | 配方分类 |

---

### 2. 特性注入配方 (`factorcraft:trait_infusion`)

用于定义培育器的特性注入配方。

#### JSON 格式

```json
{
  "type": "factorcraft:trait_infusion",
  "group": "basic_trait",
  "input": {
    "item": "minecraft:wheat"
  },
  "input_count": 1,
  "trait_item": {
    "item": "factorcraft:factor_crystal"
  },
  "output": {
    "item": "factorcraft:energized_wheat",
    "count": 1
  },
  "factor_cost": 500.0,
  "craft_time": 600,
  "success_rate": 0.3,
  "category": "trait"
}
```

#### 字段说明

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `type` | String | ✅ | - | 配方类型，固定为 `factorcraft:trait_infusion` |
| `group` | String | ❌ | `""` | 配方分组 |
| `input.item` | String | ✅ | - | 输入物品 ID |
| `input_count` | Integer | ❌ | `1` | 输入物品数量 |
| `trait_item.item` | String | ✅ | - | 特性物品 ID |
| `output.item` | String | ✅ | - | 输出物品 ID |
| `output.count` | Integer | ❌ | `1` | 输出物品数量 |
| `factor_cost` | Double | ✅ | - | Factor 消耗量 |
| `craft_time` | Integer | ❌ | `200` | 合成时间（tick） |
| `success_rate` | Double | ❌ | `0.3` | 成功率（0.0-1.0） |
| `category` | String | ❌ | `"misc"` | 配方分类 |

---

## 文件位置

### 内置配方

放在 `src/main/resources/data/factorcraft/recipes/` 目录下。

### Datapack 配方

创建 datapack，结构如下：

```
my_datapack/
├── pack.mcmeta
└── data/
    └── factorcraft/
        └── recipes/
            ├── my_recipe.json
            └── another_recipe.json
```

将 datapack 放入 `.minecraft/saves/<world>/datapacks/` 即可。

---

## 代码中使用

### 查询配方

```java
import com.factorcraft.recipe.RecipeRegistry;
import com.factorcraft.recipe.FactorFusionRecipeData;

// 通过 ID 获取配方
FactorFusionRecipeData recipe = RecipeRegistry.getFactorFusionRecipe("material_upgrade_t1");

if (recipe != null) {
    double factorCost = recipe.getFactorCost();
    int craftTime = recipe.getCraftTime();
    ItemStack output = recipe.getOutput();
}

// 通过输入物品查找配方
FactorFusionRecipeData recipe = RecipeRegistry.findFactorFusionRecipe("factorcraft:dust_copper_ingot", 64);
```

### 获取所有配方

```java
List<FactorFusionRecipeData> recipes = RecipeRegistry.getAllFactorFusionRecipes();
for (FactorFusionRecipeData recipe : recipes) {
    // 处理配方
}
```

---

## 配方 ID 命名

配方 ID 由文件名决定。例如：
- `material_upgrade_t1.json` → `material_upgrade_t1`
- `trait_infusion_wheat.json` → `trait_infusion_wheat`

---

## 示例配方

### 材料升级 T1 → T2

```json
{
  "type": "factorcraft:factor_fusion",
  "group": "material_upgrade",
  "input": {
    "item": "factorcraft:dust_copper_ingot"
  },
  "input_count": 64,
  "output": {
    "item": "factorcraft:shadow_steel_ingot",
    "count": 32
  },
  "factor_cost": 1000.0,
  "craft_time": 1200,
  "category": "material"
}
```

### 特性注入 - 小麦

```json
{
  "type": "factorcraft:trait_infusion",
  "group": "basic_trait",
  "input": {
    "item": "minecraft:wheat"
  },
  "input_count": 1,
  "trait_item": {
    "item": "factorcraft:factor_crystal"
  },
  "output": {
    "item": "factorcraft:energized_wheat",
    "count": 1
  },
  "factor_cost": 500.0,
  "craft_time": 600,
  "success_rate": 0.3,
  "category": "trait"
}
```

---

## 迁移现有配方

原有的 `SynthesisConfig.UPGRADE_RECIPES` 硬编码配方已迁移到 JSON 文件：

- `material_upgrade_t1.json` - T1 → T2
- `material_upgrade_t2.json` - T2 → T3
- `material_upgrade_t3.json` - T3 → T4
- `material_upgrade_t4.json` - T4 → T5

建议逐步将其他硬编码配方迁移到 JSON 格式。

---

## 注意事项

1. **配方加载时机**: 配方在资源重载时加载（服务器启动时自动加载）
2. **热重载**: 使用 `/reload` 命令可以重新加载 datapack 配方（开发环境非常有用）
3. **物品验证**: 加载时会验证物品 ID 是否存在，未知物品会记录警告
4. **标签支持**: 当前版本暂不支持标签（tag）输入，仅支持具体物品 ID
5. **冲突处理**: 同名配方后加载的会覆盖先加载的（datapack 优先级高于内置）

---

## 扩展开发

### 添加新配方类型

1. 创建新的 `*RecipeData` 类
2. 在 `RecipeRegistry` 添加存储和查询方法
3. 在 `RecipeLoader` 添加解析逻辑
4. 在 JSON 中使用新的 `type` 值

### 添加标签支持

修改 `RecipeLoader.parse*Recipe()` 方法，使用 `Ingredient.fromJson()` 解析输入。
