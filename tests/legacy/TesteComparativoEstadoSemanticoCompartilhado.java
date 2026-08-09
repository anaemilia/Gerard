import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Verificação comparativa ANTES/DEPOIS da delegação (Fase B, opção B1) em
 * EstadoSemanticoCompartilhado. Roda a mesma sequência de chamadas
 * atualizar(...) na classe NOVA (modificada, pacote real) e na classe
 * ANTIGA (cópia exata pré-mudança, EstadoSemanticoCompartilhadoOriginal,
 * pacote default) e compara os Snapshots resultantes campo a campo.
 * Ferramenta de verificação, não faz parte do código de produção.
 */
public class TesteComparativoEstadoSemanticoCompartilhado {

    static int totalCenarios = 0;
    static int falhas = 0;

    public static void main(String[] args) {
        // ================= COMPOSIÇÃO DE MEDIDAS =================
        // índices: 0=parte1, 1=parte2, 2=todo
        System.out.println("=== COMPOSICAO_MEDIDAS ===");
        cenario("preencher todo (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher parte2 (2-0->1), indiceAlterado=0",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, null, 14}, new boolean[]{true, false, true}, 0);
        cenario("preencher parte1 (2-1->0), indiceAlterado=1",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{null, 6, 14}, new boolean[]{false, true, true}, 1);
        cenario("preencher parte1 (2-1->0), indiceAlterado=2",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{null, 6, 14}, new boolean[]{false, true, true}, 2);
        cenario("fallback indiceAlterado=-1, exatamente 1 incognita (todo)",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, -1);
        cenario("fallback indiceAlterado=-1, 0 incognitas (nao faz nada)",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, 14}, new boolean[]{true, true, true}, -1);
        cenario("fase 2: consistencia, todos preenchidos, edita indice0 -> sobrescreve indice2",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{10, 6, 14}, new boolean[]{true, true, true}, 0);
        cenario("fase 2: consistencia, todos preenchidos, edita indice2 -> sobrescreve indice1",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, 20}, new boolean[]{true, true, true}, 2);
        cenario("incognita na propria posicao editada (indice0 limpo), nao deve auto-preencher",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{null, 6, 14}, new boolean[]{false, true, true}, 0);
        cenario("rejeicao de negativo: parte1 = todo(5) - parte2(8) = -3, NATURAIS rejeita",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{null, 8, 5}, new boolean[]{false, true, true}, 1);
        cenario("2 incognitas (nao resolvivel)",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, null, null}, new boolean[]{true, false, false}, 0);
        cenario("3 incognitas (nao resolvivel)",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{null, null, null}, new boolean[]{false, false, false}, -1);

        // ================= TRANSFORMAÇÃO DE MEDIDAS =================
        // índices: 0=estadoInicial, 1=transformacao(INTEIROS), 2=estadoFinal
        System.out.println();
        System.out.println("=== TRANSFORMACAO_MEDIDAS ===");
        cenario("preencher estadoFinal (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher transformacao negativa (2-0->1), indiceAlterado=2",
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[]{8, null, 5}, new boolean[]{true, false, true}, 2);
        cenario("fase 2: consistencia, todos preenchidos, edita transformacao(indice1) -> sobrescreve estadoFinal",
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[]{8, -3, 14}, new boolean[]{true, true, true}, 1);
        cenario("rejeicao de negativo: estadoInicial = estadoFinal(5) - transformacao(20) = -15",
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[]{null, 20, 5}, new boolean[]{false, true, true}, 1);

        // ================= COMPARAÇÃO DE MEDIDAS =================
        // índices: 0=referido, 1=diferenca/valorRelativo(INTEIROS), 2=referendo
        System.out.println();
        System.out.println("=== COMPARACAO_MEDIDAS ===");
        cenario("preencher referendo (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[]{6, 8, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher valorRelativo negativo (2-0->1), indiceAlterado=2",
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[]{10, null, 4}, new boolean[]{true, false, true}, 2);
        cenario("fase 2: consistencia, todos preenchidos, edita referido(indice0) -> sobrescreve referendo",
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[]{7, 8, 14}, new boolean[]{true, true, true}, 0);
        cenario("rejeicao de negativo: referendo = referido(3) + valorRelativo(-10) = -7",
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[]{3, -10, null}, new boolean[]{true, true, false}, 1);

        // ================= COMPOSIÇÃO DE TRANSFORMAÇÕES (Relações) =================
        // índices: 0=transformacao1, 1=transformacao2, 2=transformacaoFinal — todos INTEIROS
        System.out.println();
        System.out.println("=== COMPOSICAO_TRANSFORMACOES ===");
        cenario("preencher transformacaoFinal (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{8, -3, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher transformacao1 (2-1->0), indiceAlterado=2",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{null, -3, 5}, new boolean[]{false, true, true}, 2);
        cenario("fase 2: consistencia, todos preenchidos, edita indice0 -> sobrescreve indice2",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{10, -3, 5}, new boolean[]{true, true, true}, 0);
        cenario("incognita na propria posicao editada, nao deve auto-preencher",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{null, -3, 5}, new boolean[]{false, true, true}, 0);
        cenario("valor negativo sempre aceito (INTEIROS nos tres, sem rejeicao)",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{null, 20, -15}, new boolean[]{false, true, true}, 1);

        // ================= TRANSFORMAÇÃO DE RELAÇÃO (Relações) =================
        // índices: 0=relacaoInicial, 1=transformacao, 2=relacaoFinal — todos INTEIROS
        System.out.println();
        System.out.println("=== TRANSFORMACAO_RELACAO ===");
        cenario("preencher relacaoFinal (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                new Integer[]{-4, 9, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher relacaoInicial (2-1->0), indiceAlterado=2",
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                new Integer[]{null, 9, 5}, new boolean[]{false, true, true}, 2);
        cenario("fase 2: consistencia, todos preenchidos, edita transformacao(indice1) -> sobrescreve relacaoFinal",
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                new Integer[]{-4, 20, 5}, new boolean[]{true, true, true}, 1);

        // ================= COMPOSIÇÃO DE RELAÇÕES (Relações) =================
        // índices: 0=relacao1, 1=relacao2, 2=relacaoFinal — todos INTEIROS
        System.out.println();
        System.out.println("=== COMPOSICAO_RELACOES ===");
        cenario("preencher relacaoFinal (0+1->2), indiceAlterado=0",
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[]{-6, 11, null}, new boolean[]{true, true, false}, 0);
        cenario("preencher relacao2 (2-0->1), indiceAlterado=2",
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[]{-6, null, 5}, new boolean[]{true, false, true}, 2);
        cenario("fase 2: consistencia, todos preenchidos, edita relacao1(indice0) -> sobrescreve relacaoFinal",
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[]{7, 8, 14}, new boolean[]{true, true, true}, 0);
        cenario("2 incognitas (nao resolvivel)",
                TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                new Integer[]{-6, null, null}, new boolean[]{true, false, false}, 0);

        // ================= incognitaProtegida =================
        System.out.println();
        System.out.println("=== indiceIncognitaProtegida (assinatura de 7 argumentos) ===");
        cenarioProtegido("protegido: nao preenche indice2 mesmo com 1 incognita, permitir=false",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, -1,
                2, false);
        cenarioProtegido("protegido: preenche indice2 quando permitir=true",
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, -1,
                2, true);

        // ================= tipo NÃO coberto pelo piloto (controle) =================
        System.out.println();
        System.out.println("=== COMPOSICAO_TRANSFORMACOES (controle) ===");
        cenario("tipo composto: preencher indice2, indiceAlterado=0",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{8, 6, null}, new boolean[]{true, true, false}, 0);
        cenario("tipo composto: fase 2 consistencia",
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                new Integer[]{10, 6, 14}, new boolean[]{true, true, true}, 0);

        // ================= sequência realista (drag simulado) =================
        System.out.println();
        System.out.println("=== Sequência multi-passo (simulando arraste real) ===");
        sequenciaRealista();

        System.out.println();
        System.out.println(totalCenarios + " cenários, " + falhas + " falhas.");
        if (falhas > 0) {
            throw new AssertionError(falhas + " cenário(s) com divergência entre antes/depois.");
        }
        System.out.println("TODOS OS CENARIOS COMPARATIVOS BATEM ENTRE ANTES E DEPOIS.");
    }

    static void sequenciaRealista() {
        EstadoSemanticoCompartilhado novo = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhadoOriginal antigo = new EstadoSemanticoCompartilhadoOriginal();
        TipoSituacaoAditiva tipo = TipoSituacaoAditiva.COMPOSICAO_MEDIDAS;

        // passo 1: usuário preenche parte1=8 (parte2 e todo ainda vazios)
        passo(novo, antigo, tipo, new Integer[]{8, null, null}, new boolean[]{true, false, false}, 0, "passo1: so parte1=8");
        // passo 2: usuário preenche parte2=6 -> sistema deve calcular todo=14 (primeiro preenchimento)
        passo(novo, antigo, tipo, new Integer[]{8, 6, null}, new boolean[]{true, true, false}, 1, "passo2: parte2=6, esperado todo=14 calculado");
        // passo 3: usuário arrasta parte1 para 10 (parte2=6, todo ainda o que a tela mostra=14 antes do arraste) -> fase 2, sobrescreve todo=16
        passo(novo, antigo, tipo, new Integer[]{10, 6, 14}, new boolean[]{true, true, true}, 0, "passo3: parte1->10, esperado todo sobrescrito para 16");
        // passo 4: usuário apaga parte2 -> nao deve auto-preencher parte2 (posicao editada)
        passo(novo, antigo, tipo, new Integer[]{10, null, 16}, new boolean[]{true, false, true}, 1, "passo4: parte2 apagada, nao deve auto-preencher");
    }

    static void passo(EstadoSemanticoCompartilhado novo, EstadoSemanticoCompartilhadoOriginal antigo,
            TipoSituacaoAditiva tipo, Integer[] valores, boolean[] conhecidos, int indiceAlterado, String rotulo) {
        EstadoSemanticoCompartilhado.Snapshot sNovo = novo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhado.Origem.ARRASTE);
        EstadoSemanticoCompartilhadoOriginal.Snapshot sAntigo = antigo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhadoOriginal.Origem.ARRASTE);
        compararEexibir(rotulo, sNovo, sAntigo);
    }

    static void cenario(String rotulo, TipoSituacaoAditiva tipo, Integer[] valores, boolean[] conhecidos, int indiceAlterado) {
        EstadoSemanticoCompartilhado novo = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhadoOriginal antigo = new EstadoSemanticoCompartilhadoOriginal();
        EstadoSemanticoCompartilhado.Snapshot sNovo = novo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhado.Origem.ARRASTE);
        EstadoSemanticoCompartilhadoOriginal.Snapshot sAntigo = antigo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhadoOriginal.Origem.ARRASTE);
        compararEexibir(rotulo, sNovo, sAntigo);
    }

    static void cenarioProtegido(String rotulo, TipoSituacaoAditiva tipo, Integer[] valores, boolean[] conhecidos,
            int indiceAlterado, int indiceIncognitaProtegida, boolean permitir) {
        EstadoSemanticoCompartilhado novo = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhadoOriginal antigo = new EstadoSemanticoCompartilhadoOriginal();
        EstadoSemanticoCompartilhado.Snapshot sNovo = novo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO, indiceIncognitaProtegida, permitir);
        EstadoSemanticoCompartilhadoOriginal.Snapshot sAntigo = antigo.atualizar(tipo, valores, conhecidos, indiceAlterado,
                EstadoSemanticoCompartilhadoOriginal.Origem.PROTOCOLO, indiceIncognitaProtegida, permitir);
        compararEexibir(rotulo, sNovo, sAntigo);
    }

    static void compararEexibir(String rotulo, EstadoSemanticoCompartilhado.Snapshot sNovo,
            EstadoSemanticoCompartilhadoOriginal.Snapshot sAntigo) {
        totalCenarios++;
        StringBuilder divergencias = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            boolean conhNovo = sNovo.isConhecido(i);
            boolean conhAntigo = sAntigo.isConhecido(i);
            Integer valNovo = sNovo.getValor(i);
            Integer valAntigo = sAntigo.getValor(i);
            boolean ok = conhNovo == conhAntigo
                    && (valNovo == null ? valAntigo == null : valNovo.equals(valAntigo));
            if (!ok) {
                divergencias.append(" [indice ").append(i).append(": novo=")
                        .append(conhNovo ? valNovo : "?").append(" antigo=")
                        .append(conhAntigo ? valAntigo : "?").append("]");
            }
        }
        String estadoNovo = "[" + repr(sNovo) + "]";
        String estadoAntigo = "[" + repr(sAntigo) + "]";
        if (divergencias.length() == 0) {
            System.out.println("OK - " + rotulo + " -> " + estadoNovo);
        } else {
            falhas++;
            System.out.println("FALHA - " + rotulo + " -> novo=" + estadoNovo + " antigo=" + estadoAntigo
                    + " divergencias:" + divergencias);
        }
    }

    static String repr(EstadoSemanticoCompartilhado.Snapshot s) {
        return fmt(s, 0) + ", " + fmt(s, 1) + ", " + fmt(s, 2);
    }
    static String repr(EstadoSemanticoCompartilhadoOriginal.Snapshot s) {
        return fmt(s, 0) + ", " + fmt(s, 1) + ", " + fmt(s, 2);
    }
    static String fmt(EstadoSemanticoCompartilhado.Snapshot s, int i) {
        return s.isConhecido(i) ? String.valueOf(s.getValor(i)) : "?";
    }
    static String fmt(EstadoSemanticoCompartilhadoOriginal.Snapshot s, int i) {
        return s.isConhecido(i) ? String.valueOf(s.getValor(i)) : "?";
    }
}
