#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$ROOT/build/teste_limite_quantidade_venn"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -encoding UTF-8 -d "$TMP" \
  "$ROOT/src/gerard/Scaffolding/venn/ScaffoldingLimiteQuantidadeVenn.java" \
  "$ROOT/scripts/testes/TesteLimiteQuantidadeVenn.java"
java -cp "$TMP" TesteLimiteQuantidadeVenn
