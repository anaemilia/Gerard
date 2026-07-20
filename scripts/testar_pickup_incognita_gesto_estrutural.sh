#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build/test-classes
javac -source 8 -target 8 -cp build/classes -d build/test-classes \
  scripts/testes/TestePickupIncognitaEGestoEstrutural.java
java -Djava.awt.headless=true -cp build/classes:build/test-classes TestePickupIncognitaEGestoEstrutural

grep -q 'politicaUnicidadeElementoMatematicoTexto.jaEstaNoDiagrama' src/Main.java
grep -q 'resolvedorPickupElementoMatematicoTexto.encontrar' src/Main.java
grep -q 'politicaGestoEstrutural.ehPressionamentoDeDuploClique' src/Main.java
grep -q 'controladorLimiarArrasteEstrutural.deveMovimentar' src/Main.java

echo 'Teste estrutural de integração da incógnita e do gesto estrutural: OK'
