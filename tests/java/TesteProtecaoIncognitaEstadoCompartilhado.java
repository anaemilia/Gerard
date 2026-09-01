package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.sincronizacao.ResolvedorRelacoesEstruturaisAditivas;
import gerard.semantica.numero.ValorNumerico;

public final class TesteProtecaoIncognitaEstadoCompartilhado {
    private static int verificacoes;

    public static void main(String[] args) {
        testarEstadoFinalProtegido();
        testarTransformacaoProtegida();
        testarLiberacaoAposProtocolo();
        testarPapelNaoProtegidoContinuaReativo();
        testarPoliticaDePapeis();
        testarValidacaoPreventivaPelosDominios();
        System.out.println("Proteção da incógnita aprovada: "
                + verificacoes + " verificações.");
    }

    private static void testarEstadoFinalProtegido() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {25, -18, null},
                new boolean[] {true, true, false},
                1, EstadoSemanticoCompartilhado.Origem.ARRASTE,
                2, false);
        confirmar(!s.isConhecido(2),
                "o estado final desconhecido não pode ser resolvido automaticamente");
    }

    private static void testarTransformacaoProtegida() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {10, null, 6},
                new boolean[] {true, false, true},
                2, EstadoSemanticoCompartilhado.Origem.ARRASTE,
                1, false);
        confirmar(!s.isConhecido(1),
                "a transformação desconhecida não pode ser resolvida automaticamente");
    }

    private static void testarLiberacaoAposProtocolo() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {25, -18, 7},
                new boolean[] {true, true, true},
                2, EstadoSemanticoCompartilhado.Origem.EDICAO_TEXTO,
                2, true);
        confirmar(s.isConhecido(2) && s.valorOuZero(2) == 7,
                "o protocolo mouse/texto deve liberar a incógnita");
    }

    private static void testarPapelNaoProtegidoContinuaReativo() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot s = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {25, -18, null},
                new boolean[] {true, true, false},
                1, EstadoSemanticoCompartilhado.Origem.ARRASTE,
                -1, false);
        confirmar(s.isConhecido(2) && s.valorOuZero(2) == 7,
                "um papel não desconhecido continua sendo sincronizado");
    }

    private static void testarPoliticaDePapeis() {
        PoliticaPreenchimentoIncognita politica =
                new PoliticaPreenchimentoIncognita();
        confirmar(politica.devePreservarMarcador(
                        "papel.estadoFinal", "papel.estadoFinal", false),
                "o papel desconhecido deve preservar o marcador");
        confirmar(!politica.devePreservarMarcador(
                        "papel.estadoFinal", "papel.estadoFinal", true),
                "o protocolo libera o preenchimento");
        confirmar(!politica.devePreservarMarcador(
                        "papel.estadoInicial", "papel.estadoFinal", false),
                "outros papéis não devem ser bloqueados");
    }

    private static void testarValidacaoPreventivaPelosDominios() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot atual = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] {3, null, null},
                new boolean[] {true, false, false}, 0,
                EstadoSemanticoCompartilhado.Origem.INICIALIZACAO);
        ValorNumerico[] valores = new ValorNumerico[] {
            atual.getValorNumerico(0), atual.getValorNumerico(1),
            atual.getValorNumerico(2)
        };
        ResolvedorRelacoesEstruturaisAditivas resolvedor =
                new ResolvedorRelacoesEstruturaisAditivas();
        confirmar(!resolvedor.tentativaPreservaDominios(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        valores, 1, Integer.valueOf(-10)),
                "resultado negativo deve ser rejeitado");
        confirmar(resolvedor.tentativaPreservaDominios(
                        TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        valores, 1, Integer.valueOf(-2)),
                "resultado natural deve ser aceito");
        confirmar(!resolvedor.tentativaPreservaDominios(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                        valores, 1, Integer.valueOf(-10)),
                "referendo negativo da comparação deve ser rejeitado");
        EstadoSemanticoCompartilhado.Snapshot comparacao = estado.atualizar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new Integer[] {6, 8, 14},
                new boolean[] {true, true, true}, 1,
                EstadoSemanticoCompartilhado.Origem.INICIALIZACAO);
        ValorNumerico[] valoresComparacao = new ValorNumerico[] {
            comparacao.getValorNumerico(0), comparacao.getValorNumerico(1),
            comparacao.getValorNumerico(2)
        };
        confirmar(!resolvedor.tentativaPreservaDominios(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                        valoresComparacao, 1, Integer.valueOf(-8)),
                "comparação completa não pode recalcular Referendo negativo");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
