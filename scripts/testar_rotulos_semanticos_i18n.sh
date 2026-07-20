#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes \
  -d build/test-classes scripts/testes/TesteRotulosSemanticosI18n.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.semantica.TesteRotulosSemanticosI18n

! grep -R -q 'etapa = "Entraram"\|etapa = "Saíram"' \
  src/gerard/campoaditivo/transformacao/processo

grep -q 'NormalizadorRotulosSemanticosDiagrama' \
  src/gerard/campoaditivo/diagrama/servico/GeradorCenaDiagramaAditivo.java
grep -q 'NormalizadorRotulosSemanticosDiagrama' \
  src/gerard/campoaditivo/venn/servico/GeradorCenaDiagramaVenn.java

echo "Teste estrutural de rótulos semânticos e i18n: OK"
