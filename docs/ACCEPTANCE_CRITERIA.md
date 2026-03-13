# 验收标准

所有代码变更必须通过以下测试：

## 测试流程

### 1. 编译测试
```bash
./gradlew build
```
- 验证代码编译通过
- 验证单元测试通过

### 2. 资源完整性测试
```bash
./gradlew runQuickTest
```
- 检查 fabric.mod.json 配置
- 检查方块注册与资源文件匹配
- 检查物品注册与资源文件匹配
- 检查语言文件
- 检查配置文件

### 3. GameTest（游戏集成测试）
```bash
./gradlew runGametest
```
- 验证方块在 Registry 中正确注册
- 验证物品在 Registry 中正确注册
- 验证世界生成不崩溃

## 验收标准

```
✅ BUILD SUCCESSFUL
✅ All tests passed
✅ No errors in logs
```

## CI/CD 集成

在 PR 合并前自动运行：
```yaml
- name: Build
  run: ./gradlew build
- name: Quick Test
  run: ./gradlew runQuickTest  
- name: Game Test
  run: ./gradlew runGametest
```

## 快速验证脚本

```bash
./scripts/quick_verify.sh
```

运行所有测试并输出结果摘要。