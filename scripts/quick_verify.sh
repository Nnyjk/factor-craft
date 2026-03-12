#!/bin/bash
# Factor Craft 快速验证脚本
# 在本地运行前检查常见问题

set -e

echo "=== Factor Craft 快速验证 ==="
echo ""

# 1. 编译检查
echo "[1/4] 编译检查..."
./gradlew compileJava --quiet
echo "  ✓ 编译通过"

# 2. 单元测试
echo "[2/4] 单元测试..."
./gradlew test --quiet
echo "  ✓ 单元测试通过"

# 3. 资源完整性
echo "[3/4] 资源完整性检查..."
./gradlew runQuickTest --quiet
echo "  ✓ 资源检查通过"

# 4. 构建 JAR
echo "[4/4] 构建 JAR..."
./gradlew jar --quiet
echo "  ✓ JAR 构建成功"

echo ""
echo "=== 验证完成 ==="
echo ""
echo "下一步："
echo "  本地测试: 复制 build/libs/*.jar 到 Minecraft mods 目录"
echo "  GameTest:  ./gradlew runGametest (首次需下载资源)"
echo ""