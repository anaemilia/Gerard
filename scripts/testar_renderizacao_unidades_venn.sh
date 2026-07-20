#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$ROOT/build/teste_renderizacao_unidades_venn"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -encoding UTF-8 -d "$TMP" \
  "$ROOT/src/gerard/ui/UITemaGerard.java" \
  "$ROOT/src/gerard/campoaditivo/diagrama/elementos/QuadradinhoVenn.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/EstadoVisualUnidadeVenn.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeVenn.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeVennAbstrato.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeComposicao.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeComparacaoCorrespondente.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeComparacaoExcedente.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeTransformacaoPositiva.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/RenderizadorUnidadeTransformacaoNegativa.java" \
  "$ROOT/src/gerard/campoaditivo/venn/apresentacao/FabricaRenderizadoresUnidadeVenn.java" \
  "$ROOT/scripts/testes/TesteRenderizacaoUnidadesVenn.java"
java -Djava.awt.headless=true -cp "$TMP" TesteRenderizacaoUnidadesVenn
