#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
ARQUIVO="ROTEIRO_TESTE_VISUAL_CONCLUSAO_POR_CATEGORIA.md"
MATRIZ="MATRIZ_REGRESSAO_FUNCIONALIDADES_CONSOLIDADAS.md"
[[ -f "$ARQUIVO" ]] || { echo "Roteiro visual ausente"; exit 1; }
[[ -f "$MATRIZ" ]] || { echo "Matriz de regressão ausente"; exit 1; }
for id in \
  C139-VIS-COMP-01 C139-VIS-COMP-02 C139-VIS-COMP-03 \
  C139-VIS-COMPAr-01 C139-VIS-COMPAr-02 C139-VIS-COMPAr-03 \
  C139-VIS-TRANS-01 C139-VIS-TRANS-02 C139-VIS-TRANS-03 \
  C139-VIS-CONT-01 C139-VIS-CONT-02 \
  C155-VIS-CM C155-VIS-TM C155-VIS-CMT C155-VIS-COP \
  C155-VIS-CT C155-VIS-TCP C155-VIS-TR C155-VIS-CR; do
  grep -q "$id" "$ARQUIVO" || { echo "Caso ausente no roteiro: $id"; exit 1; }
  grep -q "$id" "$MATRIZ" || { echo "Caso ausente na matriz: $id"; exit 1; }
done
echo "Roteiro visual de conclusão por categoria: OK"
