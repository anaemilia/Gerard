#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$DIR"
ant -noinput -q clean jar
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -cp build/classes -d "$TMP" scripts/testes/TesteFeedbackMultissensorialPosicionamento.java
java -Djava.awt.headless=true -cp "build/classes:$TMP" TesteFeedbackMultissensorialPosicionamento
python3 - <<'PY2'
from pathlib import Path
main=Path('src/Main.java').read_text(encoding='utf-8')
feedback=Path('src/gerard/Scaffolding/feedbackerro/ScaffoldingFeedbackMultissensorialErro.java').read_text(encoding='utf-8')
assert 'registrarQuestionamentoPersistente(item, resultado)' in main
assert 'scaffoldingFeedbackMultissensorialErro.sinalizarErro' in main
assert 'itemQuestionadoPersistente.x + itemQuestionadoPersistente.largura' in main
assert 'if (!resultado.isAplicavel() || resultado.isCorreto())' in main
assert 'removerItemDaCenaAposPosicionamentoIncorreto' not in main
assert 'Toolkit.getDefaultToolkit().beep()' in feedback
assert 'DESLOCAMENTOS_X = {-3, 3, -2, 2, -1, 1, 0}' in feedback
print('OK: item mantido, tooltip ancorado no item, tremor, som e remoção da mensagem após correção.')
PY2
