#!/bin/bash
# 批量生成物品资源文件
# 使用原版贴图作为临时占位

cd /root/workspace/factor-craft/src/main/resources/assets/factorcraft

# 物品列表：名称和临时贴图
declare -A ITEMS=(
  ["sharp_crystal"]="amethyst_shard"
  ["sturdy_crystal"]="diamond"
  ["protective_crystal"]="emerald"
  ["energetic_crystal"]="glowstone_dust"
  ["catalytic_crystal"]="lapis_lazuli"
  ["extraction_coil_t1"]="iron_ingot"
  ["extraction_coil_t2"]="iron_ingot"
  ["extraction_coil_t3"]="gold_ingot"
  ["extraction_coil_t4"]="gold_ingot"
  ["extraction_coil_t5"]="diamond"
  ["basic_circuit"]="redstone"
  ["advanced_circuit"]="repeater"
  ["elite_circuit"]="comparator"
)

# 生成 item models
for item in "${!ITEMS[@]}"; do
  texture="${ITEMS[$item]}"
  
  # item model
  echo "{\"parent\":\"minecraft:item/generated\",\"textures\":{\"layer0\":\"minecraft:item/${texture}\"}}" > "models/item/${item}.json"
done

echo "Generated resources for ${#ITEMS[@]} items"