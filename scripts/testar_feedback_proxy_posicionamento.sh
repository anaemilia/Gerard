#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE"
ant -noinput -q clean jar
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -source 8 -target 8 -encoding UTF-8 -cp build/classes -d "$TMP" \
  scripts/testes/TesteFeedbackProxyPosicionamento.java
java -Djava.awt.headless=true -cp "build/classes:$TMP" TesteFeedbackProxyPosicionamento
python3 - <<'PY'
from pathlib import Path
main = Path('src/Main.java').read_text(encoding='utf-8')
sessao = Path('src/gerard/interacao/arraste/SessaoArrasteTextoParaDiagrama.java').read_text(encoding='utf-8')
feedback = Path('src/gerard/Scaffolding/feedbackerro/ScaffoldingFeedbackMultissensorialErro.java').read_text(encoding='utf-8')
assert 'deveManterNoDiagramaAposErro' in main
assert 'itensArrastaveis.add(itemSolto)' in main
assert 'processarQuestionamentoPosicionamento(itemSolto)' in main
assert 'registrarQuestionamentoPersistente(item, resultado)' in main
assert 'scaffoldingFeedbackMultissensorialErro.sinalizarErro(item' in main
assert 'return ehProxyAtivo(item) && !solturaSobreDiagrama;' in sessao
assert 'sinalizarErro(ItemTextoArrastavel item, Runnable repaint, Runnable aoConcluir)' in feedback
wrong_branch = main[main.find('if (posicionamentoIncorreto) {'):main.find('registrarLogSolturaItem(itemSolto)')]
assert 'descartarProxy(itemSolto)' not in wrong_branch
print('Teste estrutural: tip, som e tremor preservam o item incorreto para novo arraste.')
PY
