#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$ROOT/build/teste_contratos_unidades_venn"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -encoding UTF-8 -d "$TMP" \
  "$ROOT/src/gerard/ui/UITemaGerard.java" \
  "$ROOT/src/gerard/campoaditivo/diagrama/elementos/CirculoVenn.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidades.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesAdicionaveis.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesRemoviveis.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/OperacoesUnidadesVenn.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/ResultadoOperacaoUnidade.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesAbstrata.java" \
  "$ROOT/src/gerard/campoaditivo/venn/interacao/RepresentacaoVennEditavel.java" \
  "$ROOT/scripts/testes/TesteContratosUnidadesVenn.java"
java -cp "$TMP" TesteContratosUnidadesVenn
