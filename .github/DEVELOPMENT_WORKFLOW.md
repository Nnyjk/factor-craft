# Factor Craft 开发流程规范

**版本:** 1.1.0  
**创建日期:** 2026-03-10  
**最后更新:** 2026-03-13

---

## 📋 目录

1. [开发流程概述](#开发流程概述)
2. [分支管理](#分支管理)
3. [Issue 管理](#issue-管理)
4. [PR 流程](#pr-流程)
5. [里程碑管理](#里程碑管理)
6. [发布流程](#发布流程)
7. [代码规范](#代码规范)

---

## 开发流程概述

### 核心原则

1. **基于 Milestone 规划** - 所有工作围绕里程碑目标展开
2. **基于 Issue 追踪** - 每个功能点都有对应的 Issue
3. **基于 PR 推进** - 所有代码变更通过 PR 审查合并

### 工作流

```
Milestone 规划
    ↓
Issue 创建与分配
    ↓
Branch 创建 (feature/xxx)
    ↓
开发与提交
    ↓
PR 创建与审查
    ↓
合并到 develop
    ↓
发布到 main
```

---

## 分支管理

### 分支结构

```
main (生产分支，受保护)
  ↑
develop (开发分支，受保护)
  ↑
  ├── feature/* (功能分支)
  ├── bugfix/* (修复分支)
  ├── hotfix/* (紧急修复)
  └── release/* (发布分支)
```

### 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|----------|---------|------|
| 功能分支 | `feature/<issue-id>-<description>` | `feature/123-add-t6-weapon` |
| Bug 修复 | `bugfix/<issue-id>-<description>` | `bugfix/124-fix-crash` |
| 紧急修复 | `hotfix/<description>` | `hotfix/critical-save-bug` |
| 发布分支 | `release/v<version>` | `release/v0.2.0` |
| 文档分支 | `docs/<description>` | `docs/add-install-guide` |

### 分支保护规则

**main 分支:**
- ✅ 需要 PR 才能合并
- ✅ 至少 2 人审查
- ✅ 必须与 develop 同步
- ✅ 包含管理员

**develop 分支:**
- ✅ 需要 PR 才能合并
- ✅ 至少 1 人审查
- ✅ 包含管理员

---

## Issue 管理

### Issue 类型

使用 Issue 模板创建：
- ✨ **功能需求** - 新功能开发
- 🐛 **Bug 报告** - 问题修复
- 📋 **开发任务** - 内部任务

### 标签系统

**类型标签:**
- `type:feature` - 新功能
- `type:bug` - Bug 修复
- `type:enhancement` - 功能增强
- `type:docs` - 文档
- `type:refactor` - 重构
- `type:test` - 测试

**优先级标签:**
- `priority:critical` - 紧急
- `priority:high` - 高
- `priority:medium` - 中
- `priority:low` - 低

**状态标签:**
- `status:todo` - 待办
- `status:in-progress` - 进行中
- `status:review-needed` - 待审查
- `status:blocked` - 被阻塞
- `status:done` - 已完成

### Issue 生命周期

```
新建 → 分配 → 进行中 → 审查 → 完成
        ↓
      被阻塞 → 解除阻塞
```

---

## PR 流程

### 创建 PR 前

- [ ] Issue 已创建并分配
- [ ] 分支从 develop 创建
- [ ] 本地构建通过
- [ ] 测试通过

### PR 审查流程

```
PR 创建
    ↓
自动检查 (构建/测试)
    ↓
分配审查者 (1-2 人)
    ↓
代码审查
    ↓
[有修改意见] → 开发者修改 → 重新审查
    ↓
[审查通过]
    ↓
合并到 develop
    ↓
删除功能分支
```

### PR 合并标准

**小 PR (<200 行):**
- 1 人审查
- 快速合并

**标准 PR (200-400 行):**
- 1-2 人审查
- 正常审查流程

**大 PR (>400 行):**
- 2 人审查
- 详细审查
- 考虑拆分

### Commit Message 规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type:**
| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档 |
| `style` | 格式（不影响代码运行） |
| `refactor` | 重构 |
| `test` | 测试 |
| `chore` | 维护 |
| `perf` | 性能优化 |
| `ci` | CI 配置 |
| `build` | 构建系统 |

**Scope（可选）:**
| Scope | 模块 |
|-------|------|
| `combat` | 战斗系统 |
| `cycle` | 潮汐周期 |
| `factor` | Factor 能量 |
| `material` | 材料系统 |
| `technology` | 科技树 |
| `quest` | 任务系统 |
| `creature` | 生物系统 |
| `loot` | 战利品 |
| `cultivation` | 培养系统 |
| `ui` | 用户界面 |
| `network` | Factor 网络 |
| `multiblock` | 多方块结构 |
| `command` | 命令系统 |
| `config` | 配置系统 |
| `api` | API 接口 |
| `core` | 核心系统 |

**示例:**
```
feat(combat): add T6 weapon system

- Add FactorSwordT6Item
- Add DimensionHammerT6Item
- Update CombatApi

Refs: #123
```

**Git Hooks 分层检查:**

项目提供三层检查机制：

| Hook | 触发时机 | 检查内容 | 耗时 |
|------|----------|----------|------|
| `pre-commit` | 每次 commit | 编译检查 | ~5s |
| `pre-push` | 每次 push | 编译 + 快速测试 | ~35s |
| `commit-msg` | 每次 commit | Commit 格式 | 即时 |

克隆仓库后运行：

```bash
./scripts/install-hooks.sh
```

**跳过检查：**
```bash
git commit --no-verify  # 跳过 pre-commit 和 commit-msg
git push --no-verify    # 跳过 pre-push
```

CI 会在 PR 时运行完整测试（编译 + QuickTest + GameTest）作为最终保障。

---

## 里程碑管理

### Milestone 结构

| Milestone | 目标 | 时间 | 状态 |
|-----------|------|------|------|
| `v0.1.0-Alpha` | 核心功能可玩 | Day 14 | ✅ 完成 |
| `v0.2.0-Alpha` | 多方块 + 战斗完整 | Day 21 | 🟡 进行中 |
| `v0.3.0-Beta` | 完整科技树 | Day 30 | ⬜ 未开始 |
| `v1.0.0-Release` | 正式版发布 | Day 45 | ⬜ 未开始 |

### Milestone 要求

每个 Milestone 应包含:
- ✅ 明确的目标 (可验证)
- ✅ 截止日期
- ✅ 关联 Issue
- ✅ 负责人
- ✅ 优先级

---

## 发布流程

### Alpha/Beta 发布

```bash
# 1. 创建发布分支
git checkout develop
git checkout -b release/v0.2.0

# 2. 更新版本号
# 3. 更新 CHANGELOG.md
# 4. 最终测试
./gradlew build test

# 5. 提交
git commit -m "chore: prepare release v0.2.0"

# 6. 创建 PR 到 main
gh pr create --title "Release v0.2.0" --base main

# 7. 2 人审查后合并
gh pr merge <number> --squash

# 8. 打标签
git tag -a v0.2.0 -m "Release v0.2.0"
git push origin v0.2.0

# 9. 同步回 develop
git checkout develop
git merge main
git push origin develop
```

### 版本号规范

```
v{major}.{minor}.{patch}

示例:
v0.1.0  - Alpha 发布
v0.2.0  - Alpha 更新
v1.0.0  - 正式版发布
v1.0.1  - Bug 修复
```

---

## 代码规范

### Java 代码规范

- 遵循 Google Java Style
- 使用 `./gradlew spotlessCheck` 检查格式
- 类名：PascalCase
- 方法/变量：camelCase
- 常量：UPPER_SNAKE_CASE

### 测试规范

- 单元测试覆盖率 > 80%
- 测试类名：`<被测试类>Test`
- 测试方法：`should_<预期行为>_when_<条件>`

### 文档规范

- 公共 API 必须有 Javadoc
- 复杂逻辑必须有注释
- README 保持更新

---

## 工具与资源

### 本地开发

```bash
# 构建
./gradlew build

# 测试
./gradlew test

# 代码格式化
./gradlew spotlessApply

# 运行验证任务
./gradlew verifyCommandM0
./gradlew verifyFactorM1
```

### GitHub 命令

```bash
# 查看 Issue
gh issue list
gh issue view <number>

# 创建 PR
gh pr create --title "Title" --body "Description"

# 审查 PR
gh pr review <number> --approve

# 合并 PR
gh pr merge <number> --squash --delete-branch
```

---

## 最佳实践

### ✅ 应该做的

- 小步提交，频繁推送
- 一个 PR 只做一件事
- 及时更新 Issue 状态
- 编写清晰的 Commit Message
- 代码审查时给出建设性意见

### ❌ 不应该做的

- 不要直接 push 到 develop/main
- 不要创建超大 PR (>800 行)
- 不要跳过测试
- 不要忽略审查意见
- 不要合并未审查的 PR

---

## 持续改进

每 2 周进行一次流程回顾:
- 什么做得好？
- 什么需要改进？
- 下周期目标是什么？

---

*本文档会随着项目发展持续更新*
