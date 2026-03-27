#!/bin/bash
cd /root/workspace/factor-craft
./gradlew build --no-daemon 2>&1 | tail -60
