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
**Phase J: Beta 准备** - 进行中

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
| Phase J: Beta 准备 | 🔄 进行中 | - |

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
| 2026-03-19 | PR 合并 | #177 TideStatus 游戏效果 ✅ |
| 2026-03-19 | PR 合并 | #176 错误处理与日志系统 ✅ |
| 2026-03-19 | PR 合并 | #175 权限管理系统 ✅ |
| 2026-03-19 | PR 合并 | #174 管理命令完善 ✅ |
| 2026-03-19 | PR 合并 | #173 模组更新检查机制 ✅ |

---

## 活跃 Issue (9 个)

### ⚪ 全部为低优先级 (9 个)

| Issue | 描述 | 阶段 |
|-------|------|------|
| #75 | 怪物掉落表 | Phase G 遗留 |
| #106 | 引导书完善 | Phase J |
| #108 | GameTest 测试 | Phase J |
| #109 | BETA 文档 | Phase J |
| #115 | 游戏平衡配置 | Phase J |
| #117 | 本地化支持 | Phase J |
| #119 | REI/JEI 支持 | Phase J |
| #78 | 贴图制作 | Phase J |
| #147 | 非核心内容集成 | Phase J |

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

1. **Phase G 遗留** - #75 怪物掉落表
2. **Phase J 推进** - #108 GameTest, #109 文档, #106 引导书
3. **Beta 准备** - 完善测试和文档

---

*最后更新*: 2026-03-19 17:00