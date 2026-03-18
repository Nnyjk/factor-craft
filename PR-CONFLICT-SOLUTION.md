# Factor Craft PR 冲突问题解决方案

## 问题根因

### 1. 开发模式冲突
- **定时任务** `fc-实现推进` 直接在本地 main 上 commit
- **分支保护** 要求必须通过 PR + 签名
- **结果**：本地 main 与远程分叉，无法同步

### 2. PR 过期机制缺失
- PR 分支基于旧的 main
- 本地 main 持续更新（直接 commit）
- 无自动 rebase 机制 → PR 变成 CONFLICTING

### 3. 签名缺失
- 本地 commit 无 GPG 签名
- 分支保护要求 verified signatures
- 即使创建 PR 也无法合并

---

## 解决方案

### 方案 A: 修改 fc-实现推进 任务（推荐）

**当前流程（有问题）：**
```
1. 检查 Issue
2. 直接在 main 上实现
3. 直接 commit 到 main
4. 无法 push（分支保护）
```

**修改后流程：**
```
1. 检查 Issue
2. 创建特性分支：git checkout -b feat/issue-{number}
3. 在特性分支上实现
4. commit 到特性分支
5. 推送特性分支
6. 创建 PR
7. 等待 fc-PR 合并清理 任务合并
```

**具体修改：**

```bash
copaw cron delete aa76803c-8ebd-43b4-b876-07b9f05315b1

copaw cron create \
  --type agent \
  --name "fc-实现推进" \
  --cron "25,35 * * * *" \
  --channel console \
  --target-user default \
  --target-session fc-impl \
  --text "你是 Factor Craft 的实现推进 Agent。执行流程：

【准备工作】
1. 读取 skills：github-issues, fabric-best-practices, git-commit

【任务选择】
2. gh issue list --state open --json number,title,labels
3. 优先级：status:in-progress > priority:high > priority:medium
4. 选择一个 Issue 开始

【分支管理】⭐ 关键修改
5. 检查当前分支：git branch --show-current
6. 如果在 main 分支：
   - 创建特性分支：git checkout -b feat/issue-{number}-{简短描述}
   - 例如：git checkout -b feat/issue-127-vfx
7. 如果已在特性分支，继续使用

【实现】
8. 按 fabric-best-practices 实现功能
9. 遵循 git-commit skill 提交

【创建 PR】⭐ 关键修改
10. 推送分支：git push origin {branch-name}
11. 创建 PR：gh pr create --title '...' --body 'Closes #{number}'
12. 添加标签：gh pr edit {number} --add-label 'status:in-progress'

【报告】
13. 输出：Issue # + 分支名 + PR #

工作目录：/root/workspace/factor-craft
注意：永远不要直接 commit 到 main 分支！"
```

### 方案 B: 配置 GPG 签名

```bash
# 1. 生成 GPG 密钥
gpg --full-generate-key

# 2. 获取密钥 ID
gpg --list-secret-keys --keyid-format=long

# 3. 配置 Git 使用签名
git config --global user.signingkey {KEY_ID}
git config --global commit.gpgsign true

# 4. 添加公钥到 GitHub
gpg --armor --export {KEY_ID}
# 复制输出，添加到 GitHub Settings > SSH and GPG keys
```

### 方案 C: 调整分支保护规则

在 GitHub 仓库 Settings > Branches > Branch protection rules 中：

| 规则 | 当前 | 建议 |
|------|------|------|
| Require PR | ✅ | 保持 |
| Require signed commits | ✅ | ⚠️ 可选关闭（简化流程） |
| Require linear history | ✅ | 保持 |
| Allow force pushes | ❌ | 保持 |

---

## 推荐执行步骤

### 立即执行（修改定时任务）

```bash
# 1. 删除旧任务
copaw cron delete aa76803c-8ebd-43b4-b876-07b9f05315b1

# 2. 创建新任务（见方案 A）
```

### 后续执行（配置签名）

```bash
# 配置 GPG 签名（见方案 B）
```

---

## 验证清单

- [ ] fc-实现推进 任务已修改为创建分支+PR
- [ ] 不再直接 commit 到 main
- [ ] GPG 签名已配置（可选）
- [ ] 新 PR 不再频繁冲突

---

*创建时间: 2026-03-18*