# 分支保护配置指南

**配置日期:** 2026-03-10  
**配置状态:** ⚠️ **需要仓库所有者手动配置**

---

## ✅ 已完成

### Java 21 安装
- ✅ Temurin OpenJDK 21.0.10 已安装
- ✅ 设置为默认 Java 版本
- ✅ 构建验证通过 (BUILD SUCCESSFUL)

### 代码修复
- ✅ TechnologyModule 改为实现 FactorCraftModule 接口
- ✅ FactorCraftMod 改为使用 ModuleBootstrap 初始化
- ✅ 编译错误已修复

---

## ⚠️ 需要手动配置

### GitHub 分支保护规则

**原因:** 当前 GitHub token (Y-Bot-N) 没有仓库管理员权限

**仓库:** https://github.com/Nnyjk/factor-craft

**配置步骤:**

#### 方法 1: GitHub Web 界面 (推荐)

1. **打开仓库设置**
   - 访问：https://github.com/Nnyjk/factor-craft/settings/branches

2. **添加分支保护规则**
   - 点击 "Add branch protection rule"

3. **配置 develop 分支保护**
   ```
   Branch name pattern: develop
   ```

4. **勾选以下选项:**
   - ✅ **Require a pull request before merging**
     - ✅ Required approvals: `1`
     - ✅ Dismiss stale reviews when new commits are pushed
   - ✅ **Require status checks to pass before merging**
     - ✅ build (等 CI/CD 配置完成后)
     - ✅ test (等 CI/CD 配置完成后)
   - ✅ **Include administrators** (可选，建议勾选)
   - ✅ **Do not allow bypassing the above settings** (可选)

5. **保存规则**
   - 点击 "Create" 或 "Save changes"

#### 方法 2: GitHub CLI (需要管理员权限)

```bash
# 使用仓库所有者账号登录
gh auth login --switch-user

# 设置 develop 分支保护
gh api repos/Nnyjk/factor-craft/branches/develop/protection \
  -X PUT \
  -f required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true}' \
  -f required_status_checks='{"strict":false,"contexts":[]}' \
  -f enforce_admins=true \
  -f restrictions=null
```

---

## 📋 推荐配置

### develop 分支保护规则

**Branch name pattern:** `develop`

**保护选项:**

| 选项 | 设置 | 说明 |
|------|------|------|
| Require PR | ✅ Yes | 禁止直接 push |
| Required approvals | 1 | 至少 1 人审查 |
| Dismiss stale reviews | ✅ Yes | 新提交后审查失效 |
| Require status checks | ⚠️ 待配置 | 等 CI/CD 配置完成 |
| Include administrators | ✅ Yes | 管理员也要遵守 |

### main 分支保护规则

**Branch name pattern:** `main`

**保护选项:**

| 选项 | 设置 | 说明 |
|------|------|------|
| Require PR | ✅ Yes | 禁止直接 push |
| Required approvals | 2 | 至少 2 人审查 |
| Require branches up to date | ✅ Yes | 必须是最新 develop |
| Require status checks | ⚠️ 待配置 | 等 CI/CD 配置完成 |
| Include administrators | ✅ Yes | 管理员也要遵守 |

---

## 🚀 CI/CD 配置 (可选但推荐)

### GitHub Actions Workflow

创建 `.github/workflows/build.yml`:

```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Build with Gradle
        run: ./gradlew build
      
      - name: Run tests
        run: ./gradlew test
      
      - name: Upload build artifacts
        uses: actions/upload-artifact@v4
        with:
          name: mod-jar
          path: build/libs/*.jar
```

### 配置步骤

1. **创建 workflow 文件**
   ```bash
   mkdir -p .github/workflows
   # 创建 build.yml 文件
   ```

2. **提交并推送**
   ```bash
   git add .github/workflows/build.yml
   git commit -m "ci: add GitHub Actions workflow"
   git push origin develop
   ```

3. **验证 Actions 运行**
   - 访问：https://github.com/Nnyjk/factor-craft/actions
   - 确认 build 和 test 任务成功运行

4. **更新分支保护规则**
   - 添加 status checks: `build`, `test`

---

## 📊 当前状态总结

| 项目 | 状态 | 备注 |
|------|------|------|
| Java 21 | ✅ 已安装 | Temurin 21.0.10 |
| 构建验证 | ✅ 通过 | BUILD SUCCESSFUL |
| 代码修复 | ✅ 完成 | TechnologyModule 修复 |
| 分支保护 | ⚠️ 待配置 | 需要仓库所有者 |
| CI/CD | ⚠️ 待配置 | 需要创建 workflow |

---

## 🔗 相关链接

- **仓库设置:** https://github.com/Nnyjk/factor-craft/settings/branches
- **Actions 配置:** https://github.com/Nnyjk/factor-craft/actions
- **GitHub 分支保护文档:** https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches

---

*配置指南创建时间：2026-03-10*
