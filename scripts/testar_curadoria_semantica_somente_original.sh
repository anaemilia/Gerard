#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
TELA="$BASE/src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java"
VALIDADOR="$BASE/src/gerard/campoaditivo/servico/ValidadorVinculosTraducoes.java"
I18N="$BASE/src/gerard/i18n/mensagens_pt.properties"
grep -q 'versaoTraducaoSomenteTexto' "$TELA"
grep -q 'configurarCampoHerdado(campoEstadoInicial' "$TELA"
grep -q 'configurarComboHerdado(campoTermoDesconhecido' "$TELA"
grep -q 'copiarMetadadosConceituais(original, linha)' "$TELA"
grep -q 'destino.personagem1 = origem.personagem1' "$TELA"
grep -q 'destino.observacoes = origem.observacoes' "$TELA"
grep -q '"personagem_1"' "$VALIDADOR"
grep -q '"observacoes"' "$VALIDADOR"
grep -q 'curadoria.semantica.somenteOriginal' "$I18N"
echo CURADORIA_SEMANTICA_SOMENTE_ORIGINAL_OK
