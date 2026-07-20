#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
cd "$RAIZ"
ant -noinput -q clean jar
mkdir -p build/test-classes
javac -source 8 -target 8 -cp build/classes -d build/test-classes scripts/testes/TesteComparacaoBarrasCuradoria.java
java -cp build/classes:build/test-classes TesteComparacaoBarrasCuradoria
python3 - <<'PY'
from pathlib import Path
texto = Path('src/Main.java').read_text(encoding='utf-8')
for trecho in [
    'situacaoProblemaAtual.getReferido()',
    'situacaoProblemaAtual.getValorRelativo()',
    'situacaoProblemaAtual.getReferendo()',
    'return referido + operador + modulo + " = " + referendo;'
]:
    if trecho not in texto:
        raise SystemExit('Ausente: ' + trecho)
print('Teste estrutural aprovado: gráfico usa os papéis curados, não a ordem textual.')
PY
