# Factor Craft（Fabric Mod 开发环境）

已基于 **Fabric + Minecraft** 的标准工程结构搭建完成，并实现了对 **MOD 资源/配置/贴图/模型/国际化** 的统一动态加载设计。

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

## 4. 统一动态加载设计

### 4.1 统一数据入口

运行后会在 `config/factor-craft/` 下自动生成并读取：

- `configs.json`：业务配置
- `materials_m2.json`：M2 材料/词条/附魔/状态动态配置
- `textures.json`：贴图资源映射
- `models.json`：模型资源映射
- `lang.json`：国际化词条

### 4.2 热更新机制

- 启动时自动加载以上 JSON。
- 运行中通过 `WatchService` 监听目录变更（create/modify/delete）。
- 监听到 JSON 变更后自动重载，统一更新内存快照。

### 4.3 核心类

- `DynamicContentLoader`：负责 JSON 解析、默认文件生成
- `DynamicContentManager`：统一管理当前快照并监听热重载
- `DynamicBundle`：统一聚合对象（configs/textures/models/languages）

## 5. 常用命令

```bash
gradle genSources
gradle runClient
gradle build
```

## 6. 工程结构

- `src/main/java/com/factorcraft/FactorCraftMod.java`：服务端/公共初始化入口
- `src/client/java/com/factorcraft/FactorCraftClient.java`：客户端初始化入口
- `src/main/java/com/factorcraft/dynamic/*`：动态加载核心实现
- `src/main/resources/fabric.mod.json`：Mod 元信息与依赖声明
- `src/main/resources/factorcraft/dynamic/*.json`：动态加载示例模板
- `scripts/sync_fabric_versions.sh`：自动同步 Fabric 版本脚本

## 7. 注意事项

如果网络环境无法访问 Fabric/Maven 仓库，Gradle 依赖下载会失败。请优先检查网络代理或镜像源配置。
