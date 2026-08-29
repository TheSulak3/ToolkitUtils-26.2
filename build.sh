#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${JAVA_HOME:=/opt/jdk25}"
export PATH="$JAVA_HOME/bin:$PATH"
echo "Building with $JAVA_HOME" > build-log.txt
./gradlew build --no-daemon --console=plain >> build-log.txt 2>&1
echo "DONE_EXIT_CODE=$?" >> build-log.txt
echo "Build finished. See build-log.txt."
