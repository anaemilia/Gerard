#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$BASE/build/teste-pickup"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -source 8 -target 8 -encoding UTF-8 -d "$TMP" \
  "$BASE/src/gerard/Scaffolding/pickup/DesenhavelPickup.java" \
  "$BASE/src/gerard/Scaffolding/pickup/RenderizadorPickup.java" \
  "$BASE/src/gerard/Scaffolding/pickup/RenderizadorPickupAbstrato.java" \
  "$BASE/src/gerard/Scaffolding/pickup/RenderizadorPickupElevado.java" \
  "$BASE/src/gerard/Scaffolding/pickup/FornecedorCursoresPickup.java" \
  "$BASE/src/gerard/Scaffolding/pickup/FornecedorCursoresPickupAbstrato.java" \
  "$BASE/src/gerard/Scaffolding/pickup/FornecedorCursoresPickupSwing.java" \
  "$BASE/scripts/testes/TesteAffordancePickup.java"
java -Djava.awt.headless=true -cp "$TMP" TesteAffordancePickup

grep -q 'desenharPickupEmPrimeiroPlano(g2)' "$BASE/src/Main.java"
grep -q 'item != itemSelecionado' "$BASE/src/Main.java"
grep -q 'quadradinho != quadradinhoVennSelecionado' "$BASE/src/Main.java"
grep -q 'definirCursorMaoAberta' "$BASE/src/Main.java"
grep -q 'definirCursorMaoFechada' "$BASE/src/Main.java"
echo "Teste estrutural de pickup: OK"
