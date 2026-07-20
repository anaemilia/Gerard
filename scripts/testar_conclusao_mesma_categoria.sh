#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteConclusaoAcionaSortearMesmaCategoria.java
xvfb-run -a java -cp build/classes:build/test-classes \
  TesteConclusaoAcionaSortearMesmaCategoria
