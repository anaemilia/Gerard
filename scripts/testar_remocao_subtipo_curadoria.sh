#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ARQ="$ROOT/src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java"

if grep -Fq 'adicionarCampo(formulario, gbc, y, "subtipo"' "$ARQ"; then
  echo "ERRO: o campo subtipo ainda aparece na curadoria" >&2
  exit 1
fi

if ! grep -Fq 'final JTextField campoSubtipo = campoTexto(linha.subtipo);' "$ARQ"; then
  echo "ERRO: compatibilidade interna/legada do subtipo foi removida indevidamente" >&2
  exit 1
fi

echo "TESTE_REMOCAO_SUBTIPO_CURADORIA_OK"
