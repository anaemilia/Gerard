#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MAIN="$ROOT/src/Main.java"
CTRL="$ROOT/src/gerard/Scaffolding/venn/ControleAdicionarQuadradinhoVenn.java"
CONTRATO="$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesAdicionaveis.java"
I18N="$ROOT/src/gerard/i18n/mensagens_pt.properties"

test -f "$CTRL"
test -f "$CONTRATO"
grep -q 'desenharControlesAdicionarQuadradinhoVenn' "$MAIN"
grep -q 'RepresentacaoComUnidadesAdicionaveis' "$MAIN"
grep -q 'representacaoAdicionar.adicionarUnidade()' "$MAIN"
grep -q 'ResultadoOperacaoUnidade' "$MAIN"
grep -q 'sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar' "$MAIN"
grep -q 'ui.tooltip.venn.addSquare' "$I18N"
grep -q 'drawLine(cx - raio, cy, cx + raio, cy)' "$CTRL"
echo 'Teste de adição de quadradinhos por contrato polimórfico: OK'
