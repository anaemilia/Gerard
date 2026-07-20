#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant -noinput -q clean jar >/dev/null
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
javac -encoding UTF-8 -cp build/classes -d "$TMP" scripts/testes/TesteAffordancePickupUI.java
xvfb-run -a java -cp "build/classes:$TMP" TesteAffordancePickupUI
