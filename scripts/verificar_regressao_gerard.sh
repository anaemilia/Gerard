#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")" && pwd)"
python3 "$DIR/verificar_regressao_gerard.py"

bash "$DIR/testar_modelo_semantico_explicito.sh"
bash "$DIR/testar_pickup_incognita_gesto_estrutural.sh"
bash "$DIR/testar_rotulos_semanticos_i18n.sh"
bash "$DIR/testar_sincronizacao_decimais_transformacao.sh"
bash "$DIR/testar_grandezas_quantitativas_contextuais.sh"
