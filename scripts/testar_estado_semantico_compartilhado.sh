#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TMP="${TMPDIR:-/tmp}/gerard_estado_semantico_c67"
rm -rf "$TMP"
mkdir -p "$TMP"
cd "$RAIZ"
ant -noinput -q compile
cat > "$TMP/TesteEstadoSemantico.java" <<'JAVA'
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

public class TesteEstadoSemantico {
    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    public static void main(String[] args) {
        EstadoSemanticoCompartilhado e = new EstadoSemanticoCompartilhado();
        e.limpar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        EstadoSemanticoCompartilhado.Snapshot s = e.atualizar(
            TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
            new Integer[]{8, 6, null}, new boolean[]{true, true, false},
            1, EstadoSemanticoCompartilhado.Origem.VERGNAUD);
        conferir(s.valorOuZero(2) == 14, "8 + 6 deve produzir 14");
        s = e.atualizar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
            new Integer[]{8, 6, 20}, new boolean[]{true, true, true},
            2, EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);
        conferir(s.valorOuZero(1) == 12, "Todo 20 com Parte 1 igual a 8 deve produzir Parte 2 igual a 12");
        e.limpar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);
        s = e.atualizar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
            new Integer[]{6, 8, null}, new boolean[]{true, true, false},
            1, EstadoSemanticoCompartilhado.Origem.EIXO_VERTICAL);
        conferir(s.valorOuZero(2) == 14, "6 + 8 deve produzir 14");
        e.limpar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        s = e.atualizar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
            new Integer[]{10, null, 7}, new boolean[]{true, false, true},
            2, EstadoSemanticoCompartilhado.Origem.DIAGRAMA_COMPLEMENTAR);
        conferir(s.valorOuZero(1) == -3, "10 + (-3) deve produzir 7");
        System.out.println("TESTE_ESTADO_SEMANTICO_COMPARTILHADO_OK");
    }
}
JAVA
javac -cp build/classes -d "$TMP" "$TMP/TesteEstadoSemantico.java"
java -cp "build/classes:$TMP" TesteEstadoSemantico
