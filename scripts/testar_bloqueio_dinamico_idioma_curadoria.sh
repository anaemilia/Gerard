#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TELA="$ROOT/src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java"

grep -q 'atualizarModoEdicaoPorIdioma' "$TELA"
grep -q 'boolean outroIdiomaSelecionado' "$TELA"
grep -q 'boolean semanticaHerdada = versaoTraducaoSomenteTexto || outroIdiomaSelecionado' "$TELA"
grep -q 'areaEnunciado.setEditable(!outroIdiomaSelecionado)' "$TELA"
grep -q 'campoValidada.setEnabled(!outroIdiomaSelecionado)' "$TELA"
grep -q 'campoIdiomaVersao.setEnabled(!semanticaHerdada)' "$TELA"
grep -q 'configurarCampoHerdado(campoEstadoInicial, dicaHerdado, semanticaHerdada)' "$TELA"
grep -q 'configurarComboHerdado(campoTermoDesconhecido, dicaHerdado, semanticaHerdada)' "$TELA"

echo BLOQUEIO_DINAMICO_IDIOMA_CURADORIA_OK
