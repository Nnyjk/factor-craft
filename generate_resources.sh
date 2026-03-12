#!/bin/bash
# 批量生成方块资源文件
# 使用原版贴图作为临时占位

cd /root/workspace/factor-craft/src/main/resources/assets/factorcraft

# 方块列表：名称和临时贴图
declare -A BLOCKS=(
  ["factor_conduit_t1"]="iron_block"
  ["factor_conduit_t2"]="iron_block"
  ["factor_conduit_t3"]="iron_block"
  ["factor_conduit_t4"]="iron_block"
  ["factor_conduit_t5"]="iron_block"
  ["factor_tank"]="cauldron"
  ["factor_pump"]="piston"
  ["sharp_block"]="redstone_block"
  ["sturdy_block"]="iron_block"
  ["protective_block"]="obsidian"
  ["energetic_block"]="glowstone"
  ["catalytic_block"]="lapis_block"
  ["stabilizing_block"]="quartz_block_side"
  ["building_block_t1"]="cobblestone"
  ["building_block_t2"]="stone"
  ["building_block_t3"]="stone_bricks"
  ["building_block_t4"]="polished_andesite"
  ["building_block_t5"]="smooth_stone"
  ["cultivation_core"]="beacon"
  ["factor_anchor"]="lodestone"
  ["factor_extractor"]="blast_furnace"
)

# 生成 blockstates、models
for block in "${!BLOCKS[@]}"; do
  texture="${BLOCKS[$block]}"
  
  # blockstates
  echo "{\"variants\":{\"\":{\"model\":\"factorcraft:block/${block}\"}}}" > "blockstates/${block}.json"
  
  # model
  echo "{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"minecraft:block/${texture}\"}}" > "models/block/${block}.json"
  
  # item model
  echo "{\"parent\":\"factorcraft:block/${block}\"}" > "models/item/${block}.json"
done

echo "Generated resources for ${#BLOCKS[@]} blocks"