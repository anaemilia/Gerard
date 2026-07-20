#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$BASE/build/teste-bloqueio-representacoes"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -source 8 -target 8 -encoding UTF-8 -d "$TMP" \
  "$BASE/src/gerard/campoaditivo/sincronizacao/representacoes/EstadoPrimeiroPosicionamento.java" \
  "$BASE/src/gerard/campoaditivo/sincronizacao/representacoes/PoliticaInteracaoRepresentacoes.java" \
  "$BASE/scripts/testes/TestePoliticaInteracaoRepresentacoes.java"
java -Djava.awt.headless=true -cp "$TMP" TestePoliticaInteracaoRepresentacoes

grep -q 'NaturezaInteracao.VALOR_SEMANTICO' "$BASE/src/Main.java"
grep -q 'Controle do gráfico de barras' "$BASE/src/Main.java"
grep -q 'Unidade da representação complementar' "$BASE/src/Main.java"
grep -q 'interacaoRepresentacoesLiberadaPelaModelagem' "$BASE/src/Main.java"
grep -q 'COMPONENTE_TELA' "$BASE/src/gerard/Scaffolding/grafico/ScaffoldingGraficoInteiros.java"
echo "Teste estrutural: valores semânticos bloqueados; componente flutuante preservado: OK"
