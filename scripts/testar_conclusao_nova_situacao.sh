#!/usr/bin/env bash
set -euo pipefail
# Valida o avanço da conclusão pelo fluxo consolidado de nova situação.
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteConclusaoAcionaNovaSituacao.java
xvfb-run -a java -cp build/classes:build/test-classes \
  TesteConclusaoAcionaNovaSituacao
