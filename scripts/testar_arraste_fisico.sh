#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$BASE/build/teste-arraste-fisico"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -source 8 -target 8 -encoding UTF-8 -d "$TMP" \
  "$BASE/src/gerard/Scaffolding/arraste/OuvinteArrasteElastico.java" \
  "$BASE/src/gerard/Scaffolding/arraste/ControladorArrasteElastico.java" \
  "$BASE/src/gerard/Scaffolding/arraste/ControladorArrasteElasticoAbstrato.java" \
  "$BASE/src/gerard/Scaffolding/arraste/ControladorArrasteElasticoMola.java" \
  "$BASE/src/gerard/Scaffolding/arraste/ControladorArrasteMolaMomento.java" \
  "$BASE/src/gerard/Scaffolding/arraste/DesenhavelFantasmaOrigem.java" \
  "$BASE/src/gerard/Scaffolding/arraste/MarcadorOrigemArraste.java" \
  "$BASE/src/gerard/Scaffolding/arraste/MarcadorOrigemArrasteAbstrato.java" \
  "$BASE/src/gerard/Scaffolding/arraste/MarcadorOrigemArrasteTracejado.java" \
  "$BASE/scripts/testes/TesteArrasteFisico.java"
java -Djava.awt.headless=true -cp "$TMP" TesteArrasteFisico

grep -q 'marcadorOrigemArraste.desenhar(g2)' "$BASE/src/Main.java"
grep -q 'controladorArrasteElastico.atualizarAlvo' "$BASE/src/Main.java"
grep -q 'controladorArrasteElastico.concluir' "$BASE/src/Main.java"
grep -q 'new ControladorArrasteMolaMomento' "$BASE/src/Main.java"
grep -q 'processarMovimentoArraste' "$BASE/src/Main.java"
echo "Teste estrutural de arraste físico: OK"
