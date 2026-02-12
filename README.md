# Factor Craft（Fabric Mod 开发环境）

已基于 **Fabric + Minecraft** 的标准工程结构搭建完成，可直接用于开发与调试 Mod。

## 1. 环境要求

- JDK 21
- Git
- 推荐 IDE：IntelliJ IDEA / VS Code

## 2. 当前版本基线

版本位于 `gradle.properties`：

- `minecraft_version`
- `yarn_mappings`
- `loader_version`
- `fabric_version`
- `loom_version`

> 默认已填入一组可作为起点的版本；若你希望严格切换到 Fabric 元数据中的最新稳定版本，执行下面脚本。

## 3. 自动同步到最新 Fabric 版本

```bash
bash scripts/sync_fabric_versions.sh
```

脚本会自动更新 `gradle.properties` 内的 Minecraft/Fabric 相关版本号。

## 4. 常用命令

```bash
gradle genSources
gradle runClient
gradle build
```

## 5. 工程结构

- `src/main/java/com/factorcraft/FactorCraftMod.java`：服务端/公共初始化入口
- `src/client/java/com/factorcraft/FactorCraftClient.java`：客户端初始化入口
- `src/main/resources/fabric.mod.json`：Mod 元信息与依赖声明
- `scripts/sync_fabric_versions.sh`：自动同步 Fabric 版本脚本

## 6. 注意事项

如果网络环境无法访问 Fabric/Maven 仓库，Gradle 依赖下载会失败。请优先检查网络代理或镜像源配置。
