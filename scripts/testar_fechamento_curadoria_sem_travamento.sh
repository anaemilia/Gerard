#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ARQ="$ROOT/src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java"
python3 - "$ARQ" <<'PY'
from pathlib import Path
import sys
text=Path(sys.argv[1]).read_text(encoding='utf-8')
start=text.index('final Runnable fecharESalvar')
end=text.index('botaoAdicionarTraducao.addActionListener', start)
block=text[start:end]
assert 'salvarRascunhoTraducaoSilenciosamente(dialogo' not in block, 'fechamento ainda faz persistência intermediária'
assert 'confirmarValidacaoAntesDeFechar(dialogo' not in block, 'fechamento ainda depende de modal de validação pendente'
assert 'validarAlgarismosDaTraducao(null' in block, 'validação numérica silenciosa ausente'
assert 'salvarCuradoriaSemMensagem();' in block, 'persistência final única ausente'
assert 'new JButton("Salvar e fechar")' in text, 'rótulo explícito do botão ausente'
print('TESTE_FECHAMENTO_CURADORIA_SEM_TRAVAMENTO_OK')
PY
