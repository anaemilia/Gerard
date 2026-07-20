package gerard.campoaditivo.conclusao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

public final class TesteProtecaoIncognitaEstadoCompartilhado {
    private static int verificacoes;

    public static void main(String[] args) {
        testarEstadoFinalProtegido();
        testarTransformacaoProtegida();
        testarLiberacaoAposProtocolo();
        testarPapelNaoProtegidoContinuaReativo();
        testarPoliticaDePapeis();
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

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
