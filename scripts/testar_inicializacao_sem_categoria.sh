#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant -noinput -q clean jar >/dev/null
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -cp build/classes -d "$TMP" scripts/testes/TesteInicializacaoSemCategoria.java
xvfb-run -a java -cp "build/classes:$TMP" TesteInicializacaoSemCategoria
python3 - <<'PY'
from pathlib import Path
main = Path('src/Main.java').read_text(encoding='utf-8')
assert 'categoriaSelecionadaParaAtividade = false' in main
assert 'inicializarTelaSemCategoria()' in main
assert 'botaoSortear.setEnabled(categoriaSelecionadaParaAtividade)' in main
assert 'if (!categoriaSelecionadaParaAtividade)' in main
print('OK: proteções estruturais da inicialização vazia presentes.')
PY
