#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteConclusaoModelagem.java \
  scripts/testes/TesteConclusaoPadronizadaTodasCategorias.java \
  scripts/testes/TesteProtecaoIncognitaEstadoCompartilhado.java \
  scripts/testes/TestePosicionamentoTipConclusao.java \
  scripts/testes/TestePosicionamentoSeloConclusao.java \
  scripts/testes/TesteSequenciadorFeedbackConclusao.java \
  scripts/testes/TesteTextosConclusaoProgressiva.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.conclusao.TesteConclusaoModelagem
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.conclusao.TesteConclusaoPadronizadaTodasCategorias
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.conclusao.TesteProtecaoIncognitaEstadoCompartilhado
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.ui.conclusao.TestePosicionamentoTipConclusao
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.ui.conclusao.TestePosicionamentoSeloConclusao
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.ui.conclusao.TesteSequenciadorFeedbackConclusao
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.ui.conclusao.TesteTextosConclusaoProgressiva
