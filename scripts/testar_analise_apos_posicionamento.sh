#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant -noinput -q clean jar >/dev/null
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -cp build/classes -d "$TMP" scripts/testes/TesteAnaliseAposPosicionamento.java
xvfb-run -a java -cp "build/classes:$TMP" TesteAnaliseAposPosicionamento
python3 - <<'PY2'
from pathlib import Path
main = Path('src/Main.java').read_text(encoding='utf-8')
assert 'existeAoMenosUmPosicionamentoNoDiagramaVergnaud' in main
assert 'atualizarDisponibilidadeArtefatoExplicativo' in main
assert 'analise.requires.positioning' in main
print('OK: proteções de disponibilidade da análise presentes no código.')
PY2
