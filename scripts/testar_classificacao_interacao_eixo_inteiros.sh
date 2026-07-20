#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$BASE/build/teste-classificacao-eixo"
cd "$BASE"
ant -q compile
rm -rf "$TMP"
mkdir -p "$TMP"
javac -source 8 -target 8 -encoding UTF-8 \
  -cp "$BASE/build/classes" \
  -d "$TMP" \
  "$BASE/scripts/testes/TesteClassificacaoInteracaoEixoInteiros.java"
java -Djava.awt.headless=true -cp "$BASE/build/classes:$TMP" TesteClassificacaoInteracaoEixoInteiros
