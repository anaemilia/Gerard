#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ant -q compile
mkdir -p build/test-classes
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes \
  -d build/test-classes scripts/testes/TesteProcessoTransformacao.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes \
  gerard.campoaditivo.transformacao.processo.TesteProcessoTransformacao

grep -q 'PROCESSO_TRANSFORMACAO' \
  src/gerard/campoaditivo/representacao/SeletorRepresentacaoComplementar.java
grep -q 'LayoutProcessoTransformacao' \
  src/gerard/campoaditivo/venn/servico/GeradorCenaDiagramaVenn.java
grep -q 'RenderizadorProcessoTransformacao' src/Main.java
grep -q 'LayoutUnidadesProcessoTransformacao' src/Main.java
grep -q 'desenharEstrutura' src/Main.java


if find src/gerard/campoaditivo/transformacao -type f \
  \( -name 'RenderizadorTabuleiroTransformacao.java' \
     -o -name 'LayoutTabuleiroTransformacao.java' \
     -o -name 'EstadoTabuleiroTransformacao.java' \) | grep -q .; then
  echo "ERRO: view legada da transformação ainda existe" >&2
  exit 1
fi

if grep -R -q 'Máquina de transformação' src; then
  echo "ERRO: rótulo legado da máquina ainda existe" >&2
  exit 1
fi

grep -q 'ehProcessoTransformacaoMedidas' src/Main.java
! grep -q 'ehTabuleiroTransformacaoMedidas' src/Main.java

echo "Teste estrutural do processo de transformação sem legado: OK"
grep -q 'ControleSinalProcessoTransformacao' src/Main.java
grep -q 'funil é parte estrutural' \
  src/gerard/campoaditivo/transformacao/processo/RenderizadorProcessoTransformacao.java
