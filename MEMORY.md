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

**Phase A-I** - ✅ 全部完成！(2026-03-20)
**Phase J: Beta 准备** - 收尾中 (95%)

### 阶段里程碑

| 阶段 | 状态 | 完成日期 |
|------|------|----------|
| Phase A: 基础架构 | ✅ 完成 | 2026-02 |
| Phase B: 多方结构 | ✅ 完成 | 2026-02 |
| Phase C: 传递系统 | ✅ 完成 | 2026-03 |
| Phase D: 引导书 | ✅ 完成 | 2026-03 |
| Phase E: 机器系统完善 | ✅ 完成 | 2026-03-17 |
| Phase F: 用户体验和 BETA 准备 | ✅ 完成 | 2026-03-17 |
| Phase G: Factor 系统深化 | ✅ 完成 | 2026-03-19 |
| Phase H: 任务系统扩展 | ✅ 完成 | 2026-03-19 |
| Phase I: 多人同步 | ✅ 完成 | 2026-03-19 |
| Phase J: Beta 准备 | 🔄 95% | 收尾中 |

---

## Git 状态

```
Branch: main (up to date)
Status: clean (工作区干净)
Open PRs: 0
Open Issues: 1
```

---

## 最近进展

| 日期 | 事件 | 详情 |
|------|------|------|
| 2026-03-20 | PR 合并 | #193 记忆更新 ✅ |
| 2026-03-20 | PR 合并 | #192 记忆同步 ✅ |
| 2026-03-20 | PR 合并 | #191 记忆更新 ✅ |
| 2026-03-20 | PR 合并 | #189 REI integration ✅ |
| 2026-03-19 | PR 合并 | #188 Recipe Provider 系统 ✅ |

---

## 活跃 Issue (1 个)

| Issue | 描述 | 阶段 | 优先级 |
|-------|------|------|--------|
| #78 | 贴图制作 | Phase J | low |

---

## 核心功能完成状态

### ✅ Phase G: Factor 系统深化

| 功能 | Issue | 状态 |
|------|-------|------|
| 世界事件系统 | #140 | ✅ CLOSED |
| 生物系统 | #138 | ✅ CLOSED |
| 装备系统 | #139 | ✅ CLOSED |
| 装饰方块 | #141 | ✅ CLOSED |
| 战利品系统 | #149 | ✅ CLOSED |
| 研究系统 | #131 | ✅ CLOSED |

### ✅ Phase H: 任务系统扩展

| 功能 | Issue | 状态 |
|------|-------|------|
| 成就扩展 | #135 | ✅ CLOSED |
| 任务多样性 | #126 | ✅ CLOSED |
| 任务成就关联 | #111 | ✅ MERGED |
| 任务奖励 | #110 | ✅ CLOSED |
| 任务同步 | #116 | ✅ CLOSED |

### ✅ Phase I: 用户体验优化

| 功能 | Issue | 状态 |
|------|-------|------|
| 性能优化 | #122 | ✅ CLOSED |
| GUI优化 | #121 | ✅ CLOSED |
| 音效系统 | #113 | ✅ CLOSED |
| 错误处理 | #124 | ✅ CLOSED |
| 更新检查 | #125 | ✅ CLOSED |

### 🔄 Phase J: Beta 准备

| 功能 | Issue | 状态 |
|------|-------|------|
| 权限管理 | #145 | ✅ CLOSED |
| 多人同步 | #112 | ✅ CLOSED |
| REI/JEI 支持 | #119 | ✅ CLOSED |
| 本地化 | #117 | ✅ CLOSED |
| 游戏平衡配置 | #115 | ✅ CLOSED |
| **贴图制作** | **#78** | ⏳ 开放 |

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

1. **Phase J 收尾** - #78 贴图制作
2. **Beta 发布准备** - 最终测试和发布
3. **更新 ROADMAP** - 反映实际进度

---

*最后更新*: 2026-03-20 10:00