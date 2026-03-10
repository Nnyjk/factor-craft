# Factor Craft 开发操作链路

**文档日期:** 2026-03-10  
**当前状态:** ✅ Phase 1-3 代码已合并到 develop

---

## 📊 仓库配置

### 远程仓库
- **URL:** https://github.com/Nnyjk/factor-craft.git
- **协议:** HTTPS
- **认证:** GitHub CLI (gh auth)

### 分支结构

```
main (未保护)
  └── develop (默认开发分支，未保护)
        ├── feature/* (功能分支，已合并)
        ├── phase1/* (Phase 1 分支，已合并)
        ├── phase2/* (Phase 2 分支，已合并)
        ├── docs/* (文档分支，已合并)
        └── chore/* (配置分支，已合并)
```

### 当前状态

**本地分支:**
- ✅ `develop` (当前)
- ✅ `main`
- ✅ `feature/factor-cycle-implementation`

**远程分支:** 20+ 个 (大部分已合并，待清理)

---

## 🔧 开发环境

### 技术栈
- **Minecraft:** 1.21.4
- **Fabric Loader:** 0.16.10
- **Fabric API:** 0.119.2+1.21.4
- **Java:** 21
- **Gradle:** 8.5 (wrapper 缺失，需修复)

### 构建配置

**build.gradle:**
- Fabric Loom 1.8-SNAPSHOT
- Java 21 编译
- Maven 发布配置

**gradle.properties:**
```properties
minecraft_version=1.21.4
yarn_mappings=1.21.4+build.8
loader_version=0.16.10
mod_version=0.1.0
```

---

## 🚀 标准开发流程

### 1. 功能开发流程

```bash
# 1. 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name

# 2. 开发功能
# ... 编写代码 ...

# 3. 提交更改
git add .
git commit -m "feat: your feature description

Detailed description here.

Refs: #issue-number"

# 4. 推送到远程
git push -u origin feature/your-feature-name

# 5. 创建 PR
gh pr create \
  --title "feat: Your Feature Name" \
  --body "## What\n\nDescription here.\n\n## Why\n\nReason here.\n\n## Testing\n\n- [ ] Tested locally" \
  --base develop
```

### 2. 代码审查流程

```bash
# 审查 PR
gh pr view <number>
gh pr diff <number>

# 本地测试 PR
gh pr checkout <number>
./gradlew build
./gradlew test

# 批准 PR
gh pr review <number> --approve

# 合并 PR (squash)
gh pr merge <number> --squash --delete-branch
```

### 3. 修复流程

```bash
# 切换到功能分支
git checkout feature/your-feature-name

# 修复问题
# ... 修改代码 ...

git add .
git commit -m "fix: description of fix

Refs: #pr-number"

git push origin feature/your-feature-name
```

---

## 📝 Git 操作规范

### Commit Message 规范

**格式:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type:**
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具

**示例:**
```
feat(combat): add Factor weapons (T1-T5)

- FactorSwordItem: 5 tier levels
- DimensionHammerItem: 5 tier levels
- ResonanceBowItem: 5 tier levels

Refs: #11
```

### 分支命名规范

**功能分支:**
- `feature/<feature-name>`
- 示例：`feature/combat-weapons`

**修复分支:**
- `fix/<issue-description>`
- 示例：`fix/multiblock-detection`

**文档分支:**
- `docs/<doc-description>`
- 示例：`docs/installation-guide`

**配置分支:**
- `chore/<config-description>`
- 示例：`chore/build-config`

---

## 🔍 当前问题与待办

### ⚠️ 紧急问题

1. **gradlew 脚本缺失**
   ```bash
   # 解决方案：重新生成 wrapper
   gradle wrapper --gradle-version 8.5
   # 或从 PR #23 恢复
   git checkout origin/chore/build-config -- gradlew
   ```

2. **远程分支未清理**
   ```bash
   # 已合并的分支列表
   git fetch --prune
   git branch -r --merged origin/develop
   
   # 删除远程分支 (示例)
   git push origin --delete feature/combat-weapons
   git push origin --delete phase1/core-framework
   # ... 等等
   ```

### 📋 待办事项

1. **构建验证**
   ```bash
   # 修复 gradlew
   gradle wrapper --gradle-version 8.5
   
   # 构建项目
   ./gradlew build
   
   # 运行测试
   ./gradlew test
   ```

2. **代码格式化**
   ```bash
   # 安装 spotless (如果未安装)
   # 在 build.gradle 中添加 spotless 插件
   
   # 格式化代码
   ./gradlew spotlessApply
   ```

3. **CI/CD 配置**
   - [ ] 创建 GitHub Actions workflow
   - [ ] 配置自动构建
   - [ ] 配置自动测试
   - [ ] 配置分支保护规则

4. **分支保护**
   ```bash
   # 设置 develop 分支保护 (需要 GitHub 权限)
   gh api repos/Nnyjk/factor-craft/branches/develop/protection \
     -X PUT \
     -f required_status_checks='{"strict":true,"contexts":["build"]}' \
     -f required_pull_request_reviews='{"required_approving_review_count":1}'
   ```

---

## 🛠️ 常用命令速查

### Git 操作

```bash
# 查看状态
git status
git log --oneline -10

# 分支管理
git branch -a
git checkout -b <branch-name>
git branch -d <branch-name>

# 推送/拉取
git push -u origin <branch-name>
git pull origin develop

# 清理已合并分支
git fetch --prune
git branch --merged | grep -v "\*" | xargs -n 1 git branch -d
```

### GitHub CLI

```bash
# PR 管理
gh pr list
gh pr view <number>
gh pr diff <number>
gh pr create
gh pr merge <number> --squash --delete-branch
gh pr close <number>

# 审查
gh pr review <number> --approve
gh pr review <number> --request-changes
gh pr comment <number> --body "Comment text"

# 分支清理
gh pr list --state closed --json headRefName --jq '.[].headRefName'
```

### Gradle 构建

```bash
# 构建
./gradlew build

# 测试
./gradlew test

# 清理
./gradlew clean

# 打包
./gradlew jar

# 运行验证任务
./gradlew verifyCommandM0
./gradlew verifyFactorM1
```

---

## 📊 当前项目状态

### Phase 完成度

| Phase | 状态 | 完成度 |
|-------|------|--------|
| Phase 1: MVP 设计 | ✅ 完成 | 100% |
| Phase 2: 详细实现 | ✅ 完成 | 100% |
| Phase 3: Alpha 核心 | ✅ 完成 | 100% |

### 代码统计

- **总 PR:** 13 个 (全部合并)
- **总代码量:** ~10028 行
- **测试文件:** 18 个单元测试
- **文档文件:** 27 个

### 模块列表

**核心模块:**
- ✅ `factor` - Factor 系统 (DimensionType, TideSystem)
- ✅ `cycle` - Factor 循环 (BlockEntity)
- ✅ `combat` - 战斗系统 (15 种武器)
- ✅ `technology` - 科技系统 (12 种多方块)
- ✅ `network` - Factor 网络
- ✅ `loot` - 掉落物系统
- ✅ `ui` - UI 框架
- ✅ `quest` - 任务系统

**支持模块:**
- ✅ `api` - 公共 API
- ✅ `event` - 事件系统
- ✅ `shared` - 共享工具

---

## 🎯 下一步计划

### Day 11-12: 环境修复

1. **修复 gradlew**
   ```bash
   gradle wrapper --gradle-version 8.5
   ```

2. **构建验证**
   ```bash
   ./gradlew build
   ./gradlew test
   ```

3. **分支清理**
   ```bash
   git fetch --prune
   # 删除已合并的远程分支
   ```

### Day 13-14: 测试补充

1. **补充单元测试**
   - CombatModule 测试
   - MultiblockDetector 测试
   - FactorNetworkManager 测试

2. **集成测试**
   - 多方块检测测试
   - Factor 传输测试

### Day 15: Alpha 发布

1. **最终测试**
2. **Bug 修复**
3. **打包发布**
4. **发布说明**

---

## 🔗 相关链接

- **GitHub Repo:** https://github.com/Nnyjk/factor-craft
- **开发分支:** https://github.com/Nnyjk/factor-craft/tree/develop
- **Issue Tracker:** https://github.com/Nnyjk/factor-craft/issues

---

*文档更新时间：2026-03-10*
