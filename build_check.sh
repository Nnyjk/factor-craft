#!/bin/bash
cd /root/workspace/factor-craft
./gradlew build --no-daemon 2>&1 | grep -E "BUILD|error:" | head -20
