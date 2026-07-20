#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE"
ant -q jar
mkdir -p build/testes
javac -encoding UTF-8 -source 8 -target 8 \
  -cp build/classes \
  -d build/testes \
  scripts/testes/TesteElementosMatematicosImersosTexto.java
java -Djava.awt.headless=true -cp build/classes:build/testes \
  TesteElementosMatematicosImersosTexto
