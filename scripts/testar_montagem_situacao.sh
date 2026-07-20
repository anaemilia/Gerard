#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant clean jar >/tmp/gerard_ant_montagem.log
mkdir -p build/test-classes
javac -encoding UTF-8 -source 8 -target 8 -cp build/classes -d build/test-classes \
  scripts/testes/TesteMontagemSituacao.java scripts/testes/TesteAbaMontagem.java \
  scripts/testes/TesteTooltipCategoriaConstrucao.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes gerard.campoaditivo.montagem.TesteMontagemSituacao
java -Djava.awt.headless=true -cp build/classes:build/test-classes gerard.campoaditivo.montagem.TesteTooltipCategoriaConstrucao
xvfb-run -a java -cp build/classes:build/test-classes TesteAbaMontagem
