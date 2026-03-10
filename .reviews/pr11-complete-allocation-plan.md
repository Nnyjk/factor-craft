# PR #11 完全分配方案

**分析日期:** 2026-03-10  
**确认状态:** ✅ Phase 1/2/3 内容均 **未合并** 到 develop

---

## 📊 完整文件对比

### PR #11 独有文件 (develop 缺少)

**总览:**
- **核心代码:** 31 个文件
- **测试文件:** 2 个文件
- **文档文件:** 27 个文件
- **构建文件:** 4 个文件
- **总计:** 64 个文件

---

## 🎯 分配方案

### Phase 1: 核心框架 (创建 PR #19)

**文件列表:**
```
src/main/java/com/factorcraft/module/factor/DimensionType.java
src/main/java/com/factorcraft/module/factor/TideSystem.java
src/main/java/com/factorcraft/module/factor/DimensionManager.java
src/main/java/com/factorcraft/module/factor/FactorService.java
src/main/java/com/factorcraft/module/factor/FactorSystemModule.java
src/main/java/com/factorcraft/module/cycle/CycleModule.java
src/test/java/com/factorcraft/module/factor/DimensionTypeTest.java
src/test/java/com/factorcraft/module/factor/TideSystemTest.java
```

**代码量:** ~800 行  
**PR 标题:** `Phase 1: Core Framework (DimensionType, TideSystem, CycleModule)`

---

### Phase 2: BlockEntity 实现 (创建 PR #20)

**文件列表:**
```
src/main/java/com/factorcraft/module/cycle/block/CycleBlocks.java
src/main/java/com/factorcraft/module/cycle/block/entity/CycleBlockEntities.java
src/main/java/com/factorcraft/module/cycle/block/entity/FactorSinkBlockEntity.java
src/main/java/com/factorcraft/module/cycle/block/entity/FactorSourceBlockEntity.java
src/main/java/com/factorcraft/module/cycle/block/entity/FactorTransmitterBlockEntity.java
```

**代码量:** ~800 行  
**PR 标题:** `Phase 2: BlockEntity Implementations (Sink/Source/Transmitter)`

---

### Phase 2: API 接口 (创建 PR #21)

**文件列表:**
```
src/main/java/com/factorcraft/api/CombatApi.java
src/main/java/com/factorcraft/api/FactorApi.java
src/main/java/com/factorcraft/api/TechnologyApi.java
```

**代码量:** ~250 行  
**PR 标题:** `Phase 2: API Interfaces (Combat/Factor/Technology)`

---

### Phase 3: 战斗系统 (PR #12 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/combat/CombatModule.java
src/main/java/com/factorcraft/module/combat/item/FactorSwordItem.java
src/main/java/com/factorcraft/module/combat/item/DimensionHammerItem.java
src/main/java/com/factorcraft/module/combat/item/ResonanceBowItem.java
```

**状态:** ✅ 已审查通过

---

### Phase 3: 多方块系统 (PR #13 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/technology/MultiblockDetector.java
src/main/java/com/factorcraft/module/technology/MultiblockBlueprints.java
src/main/java/com/factorcraft/module/technology/TechnologyModule.java
```

**状态:** ⚠️ 需修复 matchesBlock() 方法

---

### Phase 3: 网络系统 (PR #14 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/network/FactorNetworkManager.java
src/main/java/com/factorcraft/module/network/NetworkModule.java
```

**状态:** ✅ 已审查通过

---

### Phase 3: 掉落物系统 (PR #15 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/loot/FactorShardItem.java
src/main/java/com/factorcraft/module/loot/LootModule.java
src/main/java/com/factorcraft/module/loot/ResonanceCoreItem.java
src/main/resources/factorcraft/loot_tables/entities/factor_distortion.json
```

**状态:** ✅ 已审查通过

---

### Phase 3: UI 框架 (PR #16 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/ui/UIModule.java
```

**状态:** ⚠️ 框架占位，待完善

---

### Phase 3: 任务系统 (PR #17 - 已存在)

**文件列表:**
```
src/main/java/com/factorcraft/module/quest/QuestModule.java
```

**状态:** ⚠️ 框架占位，待完善

---

### 文档集合 (创建 PR #22)

**文件列表:**
```
docs/00_world_and_loop.md
docs/16_dimensions_and_biomes.md
docs/17_factor_cycle_structures.md
docs/18_disasters_and_events.md
docs/19_economy_and_balance.md
docs/20_main_questline.md
docs/designs/combat_system.md
docs/designs/multiblock_diagrams.md
docs/designs/technology_tree.md
docs/guides/installation.md
docs/plans/FINAL_REPORT.md
docs/plans/TASK_BOARD.md
docs/plans/TASK_BOARD_PHASE3.md
docs/plans/code-quality-reviewer-prompt.md
docs/plans/implementation-progress.md
docs/plans/implementer-prompt.md
docs/plans/multi-expert-plan.md
docs/plans/mvp-implementation-plan.md
docs/plans/phase2-detailed-implementation.md
docs/plans/phase3-alpha-release.md
docs/plans/spec-reviewer-prompt.md
docs/plans/three-task-execution-plan.md
docs/reports/accelerated-execution-report.md
docs/reports/blockentity-solution-analysis.md
docs/reports/day1-execution-report.md
docs/reports/day2-execution-report.md
docs/reports/multi-expert-execution-report.md
```

**代码量:** ~5000 行  
**PR 标题:** `docs: Complete Phase 1-3 Documentation`

---

### 构建配置 (创建 PR #23)

**文件列表:**
```
build.gradle
.gitignore
gradlew
gradle/wrapper/gradle-wrapper.properties
```

**PR 标题:** `chore: Build configuration and Gradle wrapper`

---

### 主类和模块整合 (创建 PR #24)

**文件列表:**
```
src/main/java/com/factorcraft/FactorCraftMod.java
src/main/java/com/factorcraft/module/ModuleBootstrap.java
src/main/java/com/factorcraft/module/creature/CreatureModule.java
```

**代码量:** ~100 行  
**PR 标题:** `feat: Main mod class and module integration`

---

## 📋 执行步骤

### 步骤 1: 创建 Phase 1 PR

```bash
git checkout develop
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/module/factor/
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/module/cycle/CycleModule.java
git checkout phase3-alpha-release -- src/test/java/com/factorcraft/module/factor/

git checkout -b phase1/core-framework
git add src/main/java/com/factorcraft/module/factor/
git add src/main/java/com/factorcraft/module/cycle/CycleModule.java
git add src/test/java/com/factorcraft/module/factor/

git commit -m "feat(phase1): core framework (DimensionType, TideSystem, CycleModule)

- DimensionType: dimension base values (0.5/1.5/3.0)
- TideSystem: Factor cycle calculation
- DimensionManager: dimension state management
- 18 unit tests (100% passing)

Refs: #11"

git push -u origin phase1/core-framework

gh pr create \
  --title "Phase 1: Core Framework (DimensionType, TideSystem, CycleModule)" \
  --body "Implements core Factor system with dimension base values and tide cycles. Includes 18 unit tests." \
  --base develop
```

### 步骤 2: 创建 Phase 2 PRs

```bash
# PR #20: BlockEntity
git checkout develop
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/module/cycle/block/
git checkout -b phase2/blockentity
git add src/main/java/com/factorcraft/module/cycle/block/
git commit -m "feat(phase2): BlockEntity implementations

- FactorSinkBlockEntity: absorb Factor
- FactorSourceBlockEntity: release Factor  
- FactorTransmitterBlockEntity: cross-dimension transfer
- Uses FabricBlockEntityTypeBuilder (1.21.4 API)

Refs: #11"
git push -u origin phase2/blockentity

gh pr create \
  --title "Phase 2: BlockEntity Implementations" \
  --body "Complete BlockEntity implementations for Factor cycle system." \
  --base develop

# PR #21: API Interfaces
git checkout develop
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/api/
git checkout -b phase2/api-interfaces
git add src/main/java/com/factorcraft/api/
git commit -m "feat(phase2): API interfaces

- CombatApi: weapon system interface
- FactorApi: Factor system interface
- TechnologyApi: multiblock/technology interface

Refs: #11"
git push -u origin phase2/api-interfaces

gh pr create \
  --title "Phase 2: API Interfaces (Combat/Factor/Technology)" \
  --body "Module API definitions for extensibility." \
  --base develop
```

### 步骤 3: 创建文档 PR

```bash
git checkout develop
git checkout phase3-alpha-release -- docs/
git checkout -b docs/phase1-3-collection
git add docs/
git commit -m "docs: complete Phase 1-3 documentation

Design Docs:
- World and loop design
- Dimension and biome design
- Factor cycle structures
- Disaster and event system
- Economy and balance
- Main questline (5 chapters)
- Combat system design
- Multiblock diagrams
- Technology tree

Plans & Reports:
- Phase 1-3 implementation plans
- Multi-expert execution plan
- Day 1-2 execution reports
- Accelerated execution report
- BlockEntity solution analysis

Refs: #11"
git push -u origin docs/phase1-3-collection

gh pr create \
  --title "docs: Complete Phase 1-3 Documentation" \
  --body "Comprehensive design documentation and execution reports." \
  --base develop
```

### 步骤 4: 创建构建配置 PR

```bash
git checkout develop
git checkout phase3-alpha-release -- build.gradle .gitignore gradlew gradle/wrapper/
git checkout -b chore/build-config
git add build.gradle .gitignore gradlew gradle/wrapper/
git commit -m "chore: build configuration

- build.gradle: JUnit 5, Fabric Loom
- gradle wrapper: 8.5
- .gitignore: Java/Gradle/Ideal patterns

Refs: #11"
git push -u origin chore/build-config

gh pr create \
  --title "chore: Build configuration and Gradle wrapper" \
  --body "Standard Fabric 1.21.4 build setup." \
  --base develop
```

### 步骤 5: 创建主类 PR

```bash
git checkout develop
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/FactorCraftMod.java
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/module/ModuleBootstrap.java
git checkout phase3-alpha-release -- src/main/java/com/factorcraft/module/creature/CreatureModule.java
git checkout -b feat/mod-integration
git add src/main/java/com/factorcraft/FactorCraftMod.java
git add src/main/java/com/factorcraft/module/ModuleBootstrap.java
git add src/main/java/com/factorcraft/module/creature/CreatureModule.java
git commit -m "feat: Main mod class and module integration

- FactorCraftMod: mod entry point
- ModuleBootstrap: module initialization
- CreatureModule: creature system placeholder

Refs: #11"
git push -u origin feat/mod-integration

gh pr create \
  --title "feat: Main mod class and module integration" \
  --body "Mod entry point and 9 module integration." \
  --base develop
```

### 步骤 6: 关闭并删除 PR #11

```bash
# 关闭 PR #11
gh pr close 11

# 删除远程分支
git checkout develop
git push origin --delete phase3-alpha-release

# 删除本地分支
git branch -d phase3-alpha-release
```

---

## 📊 最终 PR 列表

| PR # | 分支 | 标题 | 文件数 | 代码量 | 优先级 |
|------|------|------|--------|--------|--------|
| #19 | phase1/core-framework | Phase 1: Core Framework | 8 | ~800 行 | P0 |
| #20 | phase2/blockentity | Phase 2: BlockEntity | 5 | ~800 行 | P0 |
| #21 | phase2/api-interfaces | Phase 2: API Interfaces | 3 | ~250 行 | P1 |
| #12 | feature/combat-weapons | Combat Weapons | 4 | +198 行 | P0 |
| #13 | feature/multiblock | Multiblock Blueprints | 3 | +368 行 | P0 |
| #14 | feature/factor-network | Factor Network | 2 | +67 行 | P0 |
| #15 | feature/loot-system | Loot System | 4 | +141 行 | P1 |
| #16 | feature/ui-framework | UI Framework | 2 | +14 行 | P2 |
| #17 | feature/quest-system | Quest System | 2 | +14 行 | P2 |
| #18 | docs/installation-guide | Installation Guide | 1 | +90 行 | P1 |
| #22 | docs/phase1-3-collection | Complete Documentation | 27 | ~5000 行 | P1 |
| #23 | chore/build-config | Build Configuration | 4 | ~260 行 | P1 |
| #24 | feat/mod-integration | Mod Integration | 3 | ~100 行 | P0 |

**总计:** 13 个 PR (替代 1 个 9998 行大 PR)

---

## 🎯 合并顺序

```bash
# 第一批次：核心框架 (P0)
gh pr merge 19 --squash --delete-branch  # Phase 1 框架
gh pr merge 20 --squash --delete-branch  # BlockEntity
gh pr merge 21 --squash --delete-branch  # API 接口

# 第二批次：Phase 3 功能 (P0)
gh pr merge 12 --squash --delete-branch  # 战斗武器
gh pr merge 14 --squash --delete-branch  # 网络传输
gh pr merge 15 --squash --delete-branch  # 掉落物
# 等待 PR #13 修复 matchesBlock() 后再合并

# 第三批次：整合与文档 (P1)
gh pr merge 23 --squash --delete-branch  # 构建配置
gh pr merge 24 --squash --delete-branch  # 主类整合
gh pr merge 22 --squash --delete-branch  # 文档集合
gh pr merge 18 --squash --delete-branch  # 安装指南

# 第四批次：框架完善 (P2)
gh pr merge 16 --squash --delete-branch  # UI 框架
gh pr merge 17 --squash --delete-branch  # 任务系统

# 最后：修复后的多方块
gh pr merge 13 --squash --delete-branch  # 多方块 (修复后)
```

---

## ✅ 检查清单

- [ ] 创建 PR #19 (Phase 1 核心框架)
- [ ] 创建 PR #20 (BlockEntity)
- [ ] 创建 PR #21 (API 接口)
- [ ] 创建 PR #22 (文档集合)
- [ ] 创建 PR #23 (构建配置)
- [ ] 创建 PR #24 (主类整合)
- [ ] 关闭 PR #11
- [ ] 删除 phase3-alpha-release 分支
- [ ] 审查并合并所有新 PR
- [ ] 更新 develop 分支

---

*方案制定完成时间：2026-03-10*
