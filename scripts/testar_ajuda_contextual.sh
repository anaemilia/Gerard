#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$DIR"
ant -noinput -q clean jar
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -cp build/classes -d "$TMP" scripts/testes/TesteAjudaContextual.java
java -Djava.awt.headless=true -cp "build/classes:$TMP" TesteAjudaContextual
python3 - <<'PY2'
from pathlib import Path
main=Path('src/Main.java').read_text(encoding='utf-8')
assert 'botaoAjudaTexto' in main
assert 'botaoAjudaVergnaud' in main
assert 'botaoAjudaComplementar' in main
assert 'mostrarMenuAjudaContextual' in main
assert 'botao.setToolTipText(null)' in main
assert 'mouseEntered(MouseEvent e)' in main
assert 'Tenho uma dúvida' in Path('src/gerard/i18n/mensagens_pt.properties').read_text(encoding='utf-8')
assert 'Qual é o próximo passo?' in Path('src/gerard/i18n/mensagens_pt.properties').read_text(encoding='utf-8')
print('OK: três interrogações e menu contextual por mouseover/clique presentes.')
PY2
