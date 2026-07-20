#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes \
  -d build/test-classes scripts/testes/TestePerfisCategoriasAditivas.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.categoria.TestePerfisCategoriasAditivas

grep -q 'CatalogoPerfisCategoriasAditivas' \
  src/gerard/campoaditivo/semantica/PoliticaValoresAditivos.java
grep -q 'GrupoCategoriaAditiva' \
  src/gerard/campoaditivo/categoria/PerfilInteracaoCategoria.java

echo "Teste estrutural dos perfis das categorias: OK"
