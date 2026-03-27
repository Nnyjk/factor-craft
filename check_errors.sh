#!/bin/bash
cd /root/workspace/factor-craft
./gradlew compileJava --no-daemon 2>&1 | grep -E "error:|SUCCESS|FAILED" | head -30
