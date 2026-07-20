#!/usr/bin/env bash
set -euo pipefail
BASE="$(cd "$(dirname "$0")/.." && pwd)"
ARQ="$BASE/src/gerard/Scaffolding/pickup/FornecedorCursoresPickupSwing.java"
grep -q 'Cursor.HAND_CURSOR' "$ARQ"
grep -q 'Cursor.MOVE_CURSOR' "$ARQ"
grep -q 'definirCursorMaoAberta' "$BASE/src/Main.java"
grep -q 'definirCursorMaoFechada' "$BASE/src/Main.java"
echo "TesteCursoresPickupEstados: OK"
