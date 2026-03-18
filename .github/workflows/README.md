# CI/CD 使用指南

本目录包含 Factor Craft 项目的 GitHub Actions 工作流配置。

## 📁 文件结构

```
.github/workflows/
├── ci.yml          # CI 流水线 - 编译、测试、代码质量检查
├── release.yml     # CD 流水线 - 自动发布 GitHub Release
├── test.yml        # 遗留测试配置（已整合到 ci.yml）
└── commit-lint.yml # 遗留 commit 检查（已整合到 ci.yml）
```

## 🔄 CI 流水线 (ci.yml)

### 触发条件

- **Pull Request**: 创建或更新 PR 时自动触发
- **分支推送**: `main` 或 `develop` 分支推送时触发

### 工作流程

1. **代码质量检查 (lint)**
   - 检查 commit message 格式是否符合 Conventional Commits
   - 格式：`<type>(<scope>): <subject>`
   - 仅在 PR 时运行

2. **编译和测试 (build-and-test)**
   - 设置 JDK 21 环境
   - 使用 Gradle 缓存加速构建
   - 执行 `./gradlew build`
   - 运行单元测试 `./gradlew test`
   - 运行 GameTest `./gradlew runGametest`
   - 上传测试报告和构建产物

3. **汇总检查 (checks)**
   - 汇总所有检查结果
   - 显示最终 CI 状态

### 缓存策略

- **Gradle 依赖**: 自动缓存
- **Fabric Loom**: 基于 `gradle.properties` 哈希缓存
- **缓存有效期**: 7 天

### 构建产物

- **测试报告**: `build/reports/`, `build/gametest/`, `logs/`
- **JAR 文件**: `build/libs/*.jar`
- **保留期**: 7 天

---

## 🚀 CD 流水线 (release.yml)

### 触发条件

- **Tag 推送**: 推送 `v*` 格式的 tag（如 `v0.2.0`, `v0.2.0-beta`）

### 工作流程

1. **环境设置**
   - JDK 21
   - Gradle 缓存

2. **构建和测试**
   - 执行完整构建 `./gradlew build`
   - 运行所有测试

3. **生成 Changelog**
   - 自动获取上一个 tag 到当前 tag 的 commits
   - 生成更新内容列表

4. **创建 GitHub Release**
   - 自动创建 Release
   - 上传 JAR 文件（主文件和 sources）
   - 自动判断是否为预发布版本（包含 `-beta`, `-alpha`, `-rc`）

### 发布流程

```bash
# 1. 更新版本号（gradle.properties）
# mod_version=0.2.1

# 2. 提交并推送
git add gradle.properties
git commit -m "chore: 更新版本到 0.2.1"
git push

# 3. 创建并推送 tag
git tag v0.2.1
git push origin v0.2.1

# 4. GitHub Actions 自动执行发布流程
# 访问 https://github.com/Nnyjk/factor-craft/releases 查看
```

### 版本命名规范

| 类型 | 格式 | 示例 | 自动标记 |
|------|------|------|----------|
| 正式版 | `v{major}.{minor}.{patch}` | `v1.0.0` | ✅ 正式 |
| 测试版 | `v{major}.{minor}.{patch}-beta.{n}` | `v0.2.0-beta.1` | ⚠️ 预发布 |
| 开发版 | `v{major}.{minor}.{patch}-alpha.{n}` | `v0.3.0-alpha.1` | ⚠️ 预发布 |
| 候选版 | `v{major}.{minor}.{patch}-rc.{n}` | `v1.0.0-rc.1` | ⚠️ 预发布 |

---

## ✅ 验收标准

- [x] PR 自动触发 CI 检查
- [x] CI 状态显示在 PR 页面
- [x] Release tag 自动创建 GitHub Release
- [x] 构建失败时通知（GitHub 通知）
- [x] 自动判断预发布版本
- [x] 自动生成 Changelog

---

## 🔧 自定义配置

### 添加新的测试任务

编辑 `ci.yml`，在 `build-and-test` job 中添加步骤：

```yaml
- name: Custom Test
  run: ./gradlew customTest
```

### 发布到 Modrinth/CurseForge

在 `release.yml` 中添加步骤：

```yaml
- name: Publish to Modrinth
  uses: modrinth/upload-action@v2
  with:
    files: build/libs/factor-craft-${{ steps.get_version.outputs.VERSION }}.jar
    token: ${{ secrets.MODRINTH_TOKEN }}

- name: Publish to CurseForge
  uses: Kir-Antipov/mc-publish@v3.3
  with:
    curseforge-id: YOUR_PROJECT_ID
    curseforge-token: ${{ secrets.CURSEFORGE_TOKEN }}
```

### 发送通知（Discord/Slack）

```yaml
- name: Notify Discord
  if: success()
  uses: Ilshidur/action-discord@master
  with:
    args: '✅ Release v${{ steps.get_version.outputs.VERSION }} published!'
  env:
    DISCORD_WEBHOOK: ${{ secrets.DISCORD_WEBHOOK }}
```

---

## 🐛 故障排查

### CI 失败

1. **检查日志**: 访问 Actions 页面查看详细日志
2. **本地复现**: `./gradlew build test runGametest`
3. **缓存问题**: 在 Actions 页面清除缓存后重试

### Release 失败

1. **Tag 格式**: 确保 tag 以 `v` 开头（如 `v0.2.0`）
2. **权限问题**: 确保 `GITHUB_TOKEN` 有 `contents: write` 权限
3. **构建错误**: 检查本地是否能成功构建

### Commit Lint 失败

确保 commit message 符合格式：

```bash
# ✅ 正确
feat(factor): add Factor extraction system
fix(technology): repair SynthesizerCoreBlockEntity output
docs: update README installation guide

# ❌ 错误
add factor system  # 缺少 type 和 scope
feat: add factor system  # 缺少 scope（可选但推荐）
FEAT(factor): add Factor system  # type 必须小写
```

---

## 📚 相关文档

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Gradle Actions](https://github.com/gradle/actions)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Fabric Loom](https://github.com/FabricMC/fabric-loom)

---

*最后更新：2026-03-17*
