# Factor Craft

一个 Minecraft Fabric Mod，为游戏添加 Factor 能量系统和科技元素。

## 功能特性

- **Factor 系统**: 核心能量管理机制
- **培育系统**: 生物培育与进化
- **科技模块**: 多层次科技树
- **任务系统**: 可追踪的任务链
- **多人优化**: 高效的多人游戏支持

## 环境要求

- Minecraft 1.21.4
- Fabric Loader 0.16.x
- Java 21+

## 安装

1. 下载最新版本从 [Releases](https://github.com/Nnyjk/factor-craft/releases)
2. 放入 Minecraft 的 `mods` 文件夹
3. 启动游戏

## 开发

```bash
# 克隆仓库
git clone https://github.com/Nnyjk/factor-craft.git
cd factor-craft

# 构建
./gradlew build

# 运行测试
./gradlew test

# 运行游戏测试
./gradlew runGametest
```

## 项目结构

```
src/main/java/com/factorcraft/
├── config/          # 配置系统
├── trait/           # 特性系统
├── module/          # 功能模块
│   ├── factor/      # Factor 管理
│   ├── cultivation/ # 培育系统
│   └── technology/  # 科技模块
├── registry/        # 游戏注册
├── world/           # 世界生成
└── quest/           # 任务系统
```

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！