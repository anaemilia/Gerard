#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$BASE/build/teste-proxy-arraste-texto"
rm -rf "$TMP"
mkdir -p "$TMP"
javac -source 8 -target 8 -encoding UTF-8 -d "$TMP" \
  "$BASE/src/gerard/ui/UITemaGerard.java" \
  "$BASE/src/gerard/campoaditivo/sincronizacao/texto/ElementoSemanticoTexto.java" \
  "$BASE/src/gerard/campoaditivo/diagrama/elementos/ItemTextoArrastavel.java" \
  "$BASE/src/gerard/campoaditivo/diagrama/elementos/MarcadorTexto.java" \
  "$BASE/src/gerard/interpretacao/simbolo/SimboloDesconhecido.java" \
  "$BASE/src/gerard/interacao/texto/TipoElementoMatematicoTexto.java" \
  "$BASE/src/gerard/interacao/texto/ElementoMatematicoImersoTexto.java" \
  "$BASE/src/gerard/interacao/arraste/SessaoArrasteTextoParaDiagrama.java" \
  "$BASE/scripts/testes/TesteProxyArrasteTextoSemantico.java"
java -Djava.awt.headless=true -cp "$TMP" TesteProxyArrasteTextoSemantico

python - "$BASE/src/Main.java" <<'PY'
from pathlib import Path
import sys
text = Path(sys.argv[1]).read_text(encoding='utf-8')
marcador = text.find('MarcadorTexto marcador = encontrarMarcadorFixoTexto(x, y);')
elemento = text.find('ElementoTextoMovel elementoTexto = encontrarElementoTextoMovel(x, y);')
assert marcador >= 0 and elemento >= 0, 'Blocos de seleção não encontrados.'
assert marcador < elemento, 'O marcador semântico deve ter prioridade sobre o elemento textual móvel.'
bloco = text[marcador:elemento]
assert 'sessaoArrasteTextoParaDiagrama.iniciarPorMarcador' in bloco
assert 'itensArrastaveis.add(novo)' not in bloco, 'O proxy não pode persistir antes da soltura válida.'
assert 'finalizarProxyTextoSolto(itemSolto, !posicionamentoIncorreto)' in text
print('Teste estrutural do proxy textual: OK')
PY
