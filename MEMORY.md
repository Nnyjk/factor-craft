# Factor Craft 项目记忆

> 由 FC Agent 维护的长期记忆

---

## 项目概况

- **项目名称**: Factor Craft
- **版本**: v0.2.0-beta (已发布)
- **技术栈**: Fabric 1.21.4, Java 21, Gradle
- **仓库**: Nnyjk/factor-craft
- **工作目录**: /root/workspace/factor-craft

---

## 当前阶段

**Phase A-I** - ✅ 全部完成！(2026-03-19)
**Phase J: Beta 准备** - 进行中 (95%)

### 阶段里程碑

| 阶段 | 状态 | 完成日期 |
|------|------|----------|
| Phase A: 基础架构 | ✅ 完成 | 2026-02 |
| Phase B: 多方结构 | ✅ 完成 | 2026-02 |
| Phase C: 传递系统 | ✅ 完成 | 2026-03 |
| Phase D: 引导书 | ✅ 完成 | 2026-03 |
| Phase E: 任务系统基础 | ✅ 完成 | 2026-03 |
| Phase F: Factor 浓度 | ✅ 完成 | 2026-03 |
| Phase G: 世界交互 | ✅ 完成 | 2026-03 |
| Phase H: 任务系统扩展 | ✅ 完成 | 2026-03-19 |
| Phase I: 多人同步 | ✅ 完成 | 2026-03-19 |
| Phase J: Beta 准备 | 🔄 95% | 进行中 |

---

## Git 状态

```
Branch: main (up to date)
Status: clean (工作区干净)
Open PRs: 0
```

---

## 最近进展

| 日期 | 事件 | 详情 |
|------|------|------|
| 2026-03-20 | PR 合并 | #189 REI integration ✅ |
| 2026-03-20 | Issue 关闭 | #119 REI/JEI 支持 ✅ |
| 2026-03-20 | Issue 关闭 | #190 REI Mixin 兼容性 ✅ |
| 2026-03-19 | PR 合并 | #188 Recipe Provider 系统 ✅ |
| 2026-03-19 | PR 合并 | #187 游戏平衡配置 ✅ |

---

## 活跃 Issue (1 个)

### ⚪ 低优先级 (1 个)

| Issue | 描述 | 阶段 |
|-------|------|------|
| #78 | 贴图制作 | Phase J |

---

## 关键技术要点

### BlockEntity 注册 (1.21.4)

```java
// 使用 MapCodec 方式注册
public static final MapCodec<ExtractorCoreBlockEntity> CODEC =
    MapCodec.unit(ExtractorCoreBlockEntity::new);

// Inventory 需实现 canPlayerUse
@Override
public boolean canPlayerUse(PlayerEntity player) {
    return pos.isWithinDistance(player.getPos(), 4.5);
}
```

### 网络包

- PacketCodec 超过 8 字段需手动实现
- 使用 `factorcraft:` 前缀注册

### Data Components (1.20.5+)

- 物品数据存储使用 DataComponentType
- 注册时指定 `packetCodec`

---

## 下次行动

1. **Phase J 收尾** - #78 贴图制作 (美术任务，需要 Nn 处理)
2. **Beta 发布准备** - 最终测试和发布

---

## 执行日志

### 2026-03-20 04:25 fc-impl

**状态**: 无开发任务可执行

**检查结果**:
- 当前分支: main
- 工作: 干净 (已提交记忆文件)
- 开放 PR: 0
- 开放 Issue: 1 个 (#78 贴图制作)

**结论**: 唯一开放的 Issue #78 是美术任务（贴图制作），不适合开发 Agent 处理。需要等待用户创建新的开发任务或处理贴图制作。

### 2026-03-20 04:35 fc-impl

**状态**: 无开发任务可执行

**检查结果**:
- 当前分支: main
- 工作区: 干净
- 开放 PR: 0
- 开放 Issue: 1 (#78 贴图制作 - 美术任务)

**结论**: Phase J 已完成 95%，唯一开放 Issue 是贴图制作（美术任务）。项目等待 Beta 发布或新开发任务。

---

*最后更新*: 2026-03-20 04:35