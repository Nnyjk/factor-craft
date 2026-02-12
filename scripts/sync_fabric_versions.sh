#!/usr/bin/env bash
set -euo pipefail

if ! command -v jq >/dev/null 2>&1; then
  echo '需要先安装 jq 才能自动同步版本。' >&2
  exit 1
fi

meta_url="https://meta.fabricmc.net/v2/versions"
latest_game=$(curl -fsSL "${meta_url}/game" | jq -r '[.[] | select(.stable == true)][0].version')
latest_loader=$(curl -fsSL "${meta_url}/loader" | jq -r '.[0].version')
latest_yarn=$(curl -fsSL "${meta_url}/yarn/${latest_game}" | jq -r '.[0].version')
latest_api=$(curl -fsSL "${meta_url}/fabric/${latest_game}" | jq -r '.[0].version')

if [[ -z "${latest_game}" || -z "${latest_loader}" || -z "${latest_yarn}" || -z "${latest_api}" ]]; then
  echo '获取 Fabric 元数据失败，请稍后重试。' >&2
  exit 1
fi

sed -i -E "s/^minecraft_version=.*/minecraft_version=${latest_game}/" gradle.properties
sed -i -E "s/^yarn_mappings=.*/yarn_mappings=${latest_yarn}/" gradle.properties
sed -i -E "s/^loader_version=.*/loader_version=${latest_loader}/" gradle.properties
sed -i -E "s/^fabric_version=.*/fabric_version=${latest_api}/" gradle.properties

echo "版本已同步："
echo "  minecraft_version=${latest_game}"
echo "  yarn_mappings=${latest_yarn}"
echo "  loader_version=${latest_loader}"
echo "  fabric_version=${latest_api}"
