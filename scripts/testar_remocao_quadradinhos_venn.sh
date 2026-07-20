#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAIN="$ROOT/src/Main.java"
CTRL="$ROOT/src/gerard/Scaffolding/venn/ControleRemoverQuadradinhoVenn.java"
CONTRATO="$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesRemoviveis.java"
I18N="$ROOT/src/gerard/i18n/mensagens_pt.properties"

test -f "$CTRL"
test -f "$CONTRATO"
grep -q 'desenharControlesRemoverQuadradinhoVenn' "$MAIN"
grep -q 'RepresentacaoComUnidadesRemoviveis' "$MAIN"
grep -q 'representacaoRemover.removerUnidade()' "$MAIN"
grep -q 'podeRemoverUnidade()' "$MAIN"
grep -q 'sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar' "$MAIN"
grep -q 'ui.tooltip.venn.removeSquare' "$I18N"
grep -q 'drawLine(cx - raio, cy, cx + raio, cy)' "$CTRL"
! grep -q 'drawLine(cx, cy - raio, cx, cy + raio)' "$CTRL"
echo 'Teste de remoção de quadradinhos por contrato polimórfico: OK'
