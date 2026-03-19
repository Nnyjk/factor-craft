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

**Phase A-J** - ✅ 全部完成！(2026-03-19)
**Beta v0.2.0** - 已发布

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
| Phase J: Beta 准备 | ✅ 完成 | 2026-03-19 |

---

## Git 状态

```
Branch: main (up to date)
Status: clean (工作区干净)
Open PRs: 0
Open Issues: 2 (#119 REI/JEI, #78 贴图)
```

---

## 最近进展

| 日期 | 事件 | 详情 |
|------|------|------|
| 2026-03-19 | PR 合并 | #187 游戏平衡配置系统 ✅ |
| 2026-03-19 | PR 合并 | #186 本地化完善 ✅ |
| 2026-03-19 | PR 合并 | #185 非核心内容集成 ✅ |
| 2026-03-19 | PR 合并 | #184 引导书完善 ✅ |
| 2026-03-19 | PR 合并 | #183 BETA 发布文档 ✅ |
| 2026-03-19 | PR 合并 | #182 GameTest 修复 ✅ |
| 2026-03-19 | PR 合并 | #179 GameTest 覆盖 ✅ |
| 2026-03-19 | PR 合并 | #177 TideStatus 效果 ✅ |
| 2026-03-19 | PR 合并 | #176 怪物掉落表 ✅ |
| 2026-03-19 | PR 合并 | #175 权限管理系统 ✅ |
| 2026-03-19 | PR 合并 | #174 管理命令完善 ✅ |
| 2026-03-19 | PR 合并 | #173 更新检查机制 ✅ |

---

## 活跃 Issue (2 个)

### ⚪ 全部为低优先级 (2 个)

| Issue | 描述 | 状态 |
|-------|------|------|
| #119 | REI/JEI 支持 | OPEN |
| #78 | 贴图制作 | OPEN |

### ✅ Phase J 已完成

| Issue | 描述 | PR |
|-------|------|-----|
| #147 | 非核心内容集成系统 | #185 |
| #117 | 本地化完善 | #186 |
| #115 | 游戏平衡配置 | #187 |
| #106 | 引导书完善 | #184 |
| #109 | BETA 文档 | #183 |
| #108 | GameTest 覆盖 | #179 |
| #75 | 怪物掉落表 | #176 |
| #125 | 更新检查机制 | #173 |
| #120 | 管理命令完善 | #174 |
| #145 | 权限管理系统 | #175 |
| #76 | TideStatus 效果 | #177 |

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

1. **可选功能** - #119 REI/JEI 支持（3-4小时工作量）
2. **美术资源** - #78 贴图制作（需要美术资源）
3. **Beta 发布** - 核心功能已完备，可发布正式版

---

*最后更新*: 2026-03-19 21:30 Asia/Shanghai