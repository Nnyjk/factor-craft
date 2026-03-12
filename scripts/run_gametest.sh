#!/bin/bash
# 后台运行 GameTest

cd /root/workspace/factor-craft

export HTTP_PROXY=http://127.0.0.1:7890
export HTTPS_PROXY=http://127.0.0.1:7890

mkdir -p logs

echo "开始运行 GameTest... ($(date))" > logs/gametest.log

nohup ./gradlew runGametest >> logs/gametest.log 2>&1 &

echo "GameTest PID: $!"
echo $! > logs/gametest.pid
echo "日志文件: logs/gametest.log"
echo "使用 'tail -f logs/gametest.log' 查看进度"