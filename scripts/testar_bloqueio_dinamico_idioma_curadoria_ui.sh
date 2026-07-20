#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
ant -noinput -q jar >/dev/null
mkdir -p build/test-classes
javac -encoding UTF-8 -cp build/classes -d build/test-classes scripts/testes/TesteBloqueioDinamicoIdiomaCuradoria.java
xvfb-run -a java -cp build/classes:build/test-classes TesteBloqueioDinamicoIdiomaCuradoria
