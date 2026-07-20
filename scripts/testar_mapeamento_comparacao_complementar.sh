#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
cd "$RAIZ"
ant -noinput -q clean jar
mkdir -p build/test-classes
javac -source 8 -target 8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteMapeamentoComparacaoComplementar.java
java -cp build/classes:build/test-classes TesteMapeamentoComparacaoComplementar
