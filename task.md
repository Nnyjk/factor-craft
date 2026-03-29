# R4 模组兼容性实现进度

## R4.1: JEI/REI 配方支持 🔄 进行中

**Issue**: #316
**分支**: `feature/r4.1-jei-support`
**Worktree**: `/home/fc-runner/worktrees/r4.1`

### 当前状态

REI 已有部分实现：
- ✅ 3 个配方类别 (Extractor, Consumer, Synthesizer)
- ✅ 3 个配方显示类
- ✅ REI 插件入口点
- ❌ 缺少配方注册 (registerRecipes)
- ❌ 缺少新机器类别 (Breeder, Cultivator, Endgame)
- ❌ build.gradle 中 REI 为 compileOnly，需要 runtime

### 实现计划

#### Step 1: 完成 REI 支持

1. **添加 REI 运行时依赖** - 修改 build.gradle
2. **实现配方注册** - 在 FactorCraftREIClientPlugin 中添加 registerRecipes
3. **添加缺失类别** - Breeder, Cultivator, Factor 合成台，终局自动化
4. **更新 fabric.mod.json** - 确保 REI 入口点正确

#### Step 2: 添加 JEI 支持 (可选)

如果时间允许，添加 JEI 作为替代方案。

### 交付文件

**新增/修改**:
- build.gradle (添加 REI runtime)
- FactorCraftREIClientPlugin.java (添加 registerRecipes)
- category/BreederCategory.java (新增)
- category/CultivatorCategory.java (新增)
- display/BreederDisplay.java (新增)
- display/CultivatorDisplay.java (新增)
- 语言文件 (添加新类别翻译)

### 验收标准

- REI 中显示所有 Factor Craft 配方
- 配方按类别正确分组
- 点击配方可查看所需材料
- 编译和 CI 通过
