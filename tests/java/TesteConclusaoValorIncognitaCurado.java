package gerard.campoaditivo.conclusao;

import java.util.Arrays;
import java.util.List;

/**
 * Cobre a checagem do valor curado da incógnita (getValorCorrespondeAoCurado):
 * um número qualquer informado pelo protocolo mouse/texto não pode mais
 * concluir a modelagem — só o valor que bate com o curado da situação.
 */
public final class TesteConclusaoValorIncognitaCurado {
    private static int verificacoes;

    public static void main(String[] args) {
        testarValorDivergenteNaoConclui();
        testarValorCorretoConclui();
        testarSemValorCuradoMantemComportamentoAnterior();
        System.out.println("Teste do valor curado da incógnita aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarValorDivergenteNaoConclui() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        List<EstadoPosicionamentoModelagem> posicionamentos = Arrays.asList(
                estado("papel.parte1", "papel.parte1", "3"),
                estado("papel.parte2", "papel.parte2", "4"),
                incognitaComCurado("papel.todo", "papel.todo", "99", false));
        confirmar(avaliador.avaliar(esperados, posicionamentos)
                        == FaseConclusaoModelagem.INCOMPLETA,
                "um número que diverge do valor curado não pode concluir a modelagem");
    }

    private static void testarValorCorretoConclui() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        List<EstadoPosicionamentoModelagem> posicionamentos = Arrays.asList(
                estado("papel.parte1", "papel.parte1", "3"),
                estado("papel.parte2", "papel.parte2", "4"),
                incognitaComCurado("papel.todo", "papel.todo", "7", true));
        confirmar(avaliador.avaliar(esperados, posicionamentos)
                        == FaseConclusaoModelagem.CONCLUIDA,
                "o valor que bate com o curado deve concluir a modelagem normalmente");
    }

    private static void testarSemValorCuradoMantemComportamentoAnterior() {
        AvaliadorConclusaoModelagem avaliador = new AvaliadorConclusaoModelagem();
        List<String> esperados = Arrays.asList(
                "papel.parte1", "papel.parte2", "papel.todo");
        List<EstadoPosicionamentoModelagem> posicionamentos = Arrays.asList(
                estado("papel.parte1", "papel.parte1", "3"),
                estado("papel.parte2", "papel.parte2", "4"),
                incognita("papel.todo", "papel.todo", "999", true));
        confirmar(avaliador.avaliar(esperados, posicionamentos)
                        == FaseConclusaoModelagem.CONCLUIDA,
                "sem valor curado disponível para conferir (situação digitada livremente), "
                        + "qualquer número pelo protocolo continua concluindo, como antes desta checagem");
    }

    private static EstadoPosicionamentoModelagem estado(
            String item, String alvo, String valor) {
        return new EstadoPosicionamentoModelagem(
                item, alvo, valor, true, false, false);
    }

    /** Incógnita sem valor curado disponível (comportamento anterior a esta checagem). */
    private static EstadoPosicionamentoModelagem incognita(
            String item, String alvo, String valor, boolean preenchida) {
        return new EstadoPosicionamentoModelagem(
                item, alvo, valor, true, true, preenchida);
    }

    /** Incógnita com valor curado disponível para conferir. */
    private static EstadoPosicionamentoModelagem incognitaComCurado(
            String item, String alvo, String valor, boolean correspondeAoCurado) {
        return new EstadoPosicionamentoModelagem(
                item, alvo, valor, true, true, true, Boolean.valueOf(correspondeAoCurado));
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
