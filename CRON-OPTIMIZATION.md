# Factor Craft 定时任务优化方案

## 问题诊断

### 1. PR 积压（9 个 DIRTY）
- **原因**: PR 分支落后 main 太多，需要 rebase
- **当前任务**: `fc-PR 合并` 只处理 mergeable=true 的 PR
- **解决**: 需要自动 rebase 或批量清理

### 2. 分支积压（14+ 个）
- **原因**: 已合并的分支未自动删除
- **当前任务**: 无专门清理任务
- **解决**: 添加分支清理任务

### 3. Issue 积压（29 个，26 个 low）
- **原因**: `fc-需求规划` 持续创建新 Issue
- **当前任务**: `fc-实现推进` 只处理 priority:high
- **解决**: 调整优先级或修改任务逻辑

### 4. 任务频率过高
- **问题**: 每小时 5-6 个任务运行
- **影响**: 资源消耗，但产出有限

---

## 优化建议

### 方案 A: 添加清理任务

```bash
# 创建 PR Rebase/清理任务
copaw cron create \
  --type agent \
  --name "fc-PR 清理" \
  --cron "30 */4 * * *" \
  --channel console \
  --target-user default \
  --target-session fc-cleanup \
  --text "你是 Factor Craft 的 PR 清理 Agent。执行流程：

【DIRTY PR 处理】
1. gh pr list --state open --json number,title,headRefName,mergeStateStatus
2. 对每个 DIRTY PR：
   - 尝试自动 rebase：gh pr update-branch <number>
   - 如果失败，检查是否超过 30 天未更新
   - 超过 30 天的 PR → 评论提醒 + 添加 stale 标签
   - 超过 60 天的 PR → 关闭并评论

【分支清理】
3. git fetch --prune 清理远程引用
4. 删除已合并的本地分支：git branch --merged main | grep -v main | xargs git branch -d
5. 删除已合并的远程分支：gh pr list --state merged --json headRefName --jq '.[].headRefName' | xargs -I {} git push origin --delete {}

【报告】
6. 输出：rebase 成功数 + 关闭的 PR + 删除的分支

工作目录：/root/workspace/factor-craft
注意：谨慎关闭 PR，保留有用的开发记录。"
```

### 方案 B: 调整现有任务

**修改 fc-实现推进**（处理 medium priority）：

```bash
copaw cron delete <当前job_id>

copaw cron create \
  --type agent \
  --name "fc-实现推进" \
  --cron "25,35 * * * *" \
  --channel console \
  --target-user default \
  --target-session fc-impl \
  --text "你是 Factor Craft 的实现推进 Agent。执行流程：

【优先级队列】
1. 获取任务优先级：
   - 优先：status:in-progress Issue
   - 其次：priority:high Issue
   - 再次：priority:medium Issue（按创建时间排序，最老的优先）
   
【执行】
2. 如果没有 in-progress Issue，选择最高优先级的开始
3. 添加 status:in-progress 标签
4. 实现并提交
5. 关闭 Issue 或创建 PR

工作目录：/root/workspace/factor-craft"
```

**修改 fc-需求规划**（限制创建频率）：

```bash
copaw cron delete <当前job_id>

copaw cron create \
  --type agent \
  --name "fc-需求规划" \
  --cron "0 */6 * * *" \
  --channel console \
  --target-user default \
  --target-session fc-planning \
  --text "你是 Factor Craft 的需求规划 Agent。执行流程：

【需求检查】
1. 只有在以下情况才创建新 Issue：
   - ROADMAP 有明确的缺失模块
   - 发现严重 bug 或技术债务
   - 用户明确提出需求
   
【Issue 清理】
2. 检查是否有重复或过时的 Issue
3. 对 low priority Issue 进行归档（关闭并添加 wontfix 标签）

【优先级调整】
4. 对超过 30 天未处理的 low priority Issue：
   - 评估是否仍需要
   - 不需要的 → 关闭
   - 需要的 → 调整为 medium priority

工作目录：/root/workspace/factor-craft"
```

### 方案 C: 减少任务频率

| 任务 | 当前频率 | 建议频率 |
|------|----------|----------|
| fc-记忆同步 | 每小时 | 每 4 小时 |
| fc-实现推进 | 每小时 2 次 | 每 2 小时 |
| fc-需求规划 | 每小时 | 每 6 小时 |
| fc-代码审查 | 每小时 | 每 4 小时 |
| fc-PR 合并 | 每小时 | 每 2 小时 |
| fc-PR 清理 | 无 | 每 4 小时 |

---

## 立即可执行的清理命令

### 清理已合并的分支
```bash
# 清理远程引用
git fetch --prune

# 删除本地已合并分支
git branch --merged main | grep -v '^\*\|main' | xargs -r git branch -d

# 查看可删除的远程分支
gh pr list --state merged --limit 50 --json headRefName --jq '.[].headRefName'
```

### 批量处理 DIRTY PR
```bash
# 尝试自动 rebase 所有 DIRTY PR
gh pr list --state open --json number,mergeStateStatus --jq '.[] | select(.mergeStateStatus == "DIRTY") | .number' | \
  xargs -I {} gh pr update-branch {}

# 查看超过 30 天的 PR
gh pr list --state open --json number,updatedAt --jq '.[] | select(.updatedAt < "2025-02-15") | .number'
```

### Issue 优先级批量调整
```bash
# 查看所有 low priority Issue
gh issue list --state open --label priority:low --limit 50

# 批量关闭长期未处理的 low priority Issue（谨慎）
# gh issue close <number> --comment "长期未处理，关闭以减少积压"
```

---

## 推荐执行顺序

1. **立即执行**: 手动清理分支 + 处理 DIRTY PR
2. **创建任务**: 添加 `fc-PR 清理` 任务
3. **调整频率**: 修改现有任务的 cron 表达式
4. **调整逻辑**: 修改 `fc-实现推进` 处理 medium priority

---

*创建时间: 2026-03-18*