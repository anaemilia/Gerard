#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant -noinput -q jar >/dev/null
mkdir -p build/test-classes
javac -encoding UTF-8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteSinalObrigatorioCuradoria.java
java -Dfile.encoding=UTF-8 -Djava.awt.headless=true \
  -cp build/classes:build/test-classes TesteSinalObrigatorioCuradoria
