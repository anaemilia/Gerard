#!/usr/bin/env bash
# Baixa as dependências externas do projeto para lib/ (não versionadas —
# .gitignore tem "*.jar" de propósito, ver decisão em 2026-07-22). Rodar
# este script sempre que lib/ estiver vazio ou faltando algum jar, por
# exemplo em um checkout novo do repositório.
#
# Para atualizar uma versão (ex.: novo pacote do Weka lançado depois deste
# versionamento), edite WEKA_VERSAO/WEKA_SHA256 (ou BOUNCE_VERSAO/
# BOUNCE_SHA256) abaixo e rode de novo — o script detecta a mudança porque
# o checksum não vai bater com o jar antigo em lib/, baixa a nova versão e
# imprime o novo SHA-256 pra você colar aqui.
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)"
LIB="$DIR/lib"
mkdir -p "$LIB"

WEKA_VERSAO="3.8.6"
WEKA_URL="https://repo1.maven.org/maven2/nz/ac/waikato/cms/weka/weka-stable/${WEKA_VERSAO}/weka-stable-${WEKA_VERSAO}.jar"
WEKA_ARQUIVO="weka-stable-${WEKA_VERSAO}.jar"
WEKA_SHA256="932ea2f342b58fe45736389e9c426d5b5955e610d1f5f6485117f532068915e9"

# bounce não é dependência direta do Gérard: é exigida em tempo de execução
# pela inicialização estática de weka.core.Capabilities (mesmo sem usar o
# gerenciador de pacotes do Weka) — descoberto rodando o teste de
# desempenho do Agente Modelador em 2026-07-22. Sem ela, PART/Apriori
# lançam NoClassDefFoundError: org/bounce/net/DefaultAuthenticator.
BOUNCE_VERSAO="0.18"
BOUNCE_URL="https://repo1.maven.org/maven2/nz/ac/waikato/cms/weka/thirdparty/bounce/${BOUNCE_VERSAO}/bounce-${BOUNCE_VERSAO}.jar"
BOUNCE_ARQUIVO="bounce-${BOUNCE_VERSAO}.jar"
BOUNCE_SHA256="bffff1505335c02256b7ab2ccffbe4aa4d3ac9fe14c17557809b7c9d99d666ca"

sha256_arquivo() {
    if command -v sha256sum >/dev/null 2>&1; then
        sha256sum "$1" | awk '{print $1}'
    else
        certutil -hashfile "$1" SHA256 | sed -n '2p' | tr -d '\r\n[:space:]'
    fi
}

baixar_e_verificar() {
    local url="$1" arquivo="$2" sha_esperado="$3"
    local destino="$LIB/$arquivo"

    if [ -f "$destino" ]; then
        local sha_atual
        sha_atual="$(sha256_arquivo "$destino")"
        if [ "$sha_atual" = "$sha_esperado" ]; then
            echo "OK (já presente, checksum confere): $arquivo"
            return 0
        fi
        echo "Checksum não confere para $arquivo — baixando de novo."
    fi

    echo "Baixando $arquivo..."
    curl -sL --fail --max-time 120 -o "$destino" "$url"

    local sha_baixado
    sha_baixado="$(sha256_arquivo "$destino")"
    if [ "$sha_baixado" != "$sha_esperado" ]; then
        echo "FALHOU: checksum de $arquivo não confere." >&2
        echo "  esperado: $sha_esperado" >&2
        echo "  obtido:   $sha_baixado" >&2
        echo "Se você trocou a versão de propósito, atualize o SHA256 esperado neste script com o valor 'obtido' acima." >&2
        rm -f "$destino"
        exit 1
    fi
    echo "OK: $arquivo (SHA256 $sha_baixado)"
}

baixar_e_verificar "$WEKA_URL" "$WEKA_ARQUIVO" "$WEKA_SHA256"
baixar_e_verificar "$BOUNCE_URL" "$BOUNCE_ARQUIVO" "$BOUNCE_SHA256"

echo "Dependências prontas em $LIB"
