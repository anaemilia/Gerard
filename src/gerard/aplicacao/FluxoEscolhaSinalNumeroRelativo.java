package gerard.aplicacao;

import gerard.dominio.campoaditivo.RegistroAcaoEscolhaSinalPapelQuantitativo;

/**
 * Sequência do que acontece quando a pessoa escolhe o sinal de um número relativo, escrita UMA vez para os
 * dois pontos de partida (o texto do próprio círculo/retângulo e o item arrastado até ele). Quem decide cada
 * passo continua sendo o proprietário (a tentativa do papel avalia a escolha, a política protege as quantidades
 * não negativas); esta classe só garante a ORDEM e os desvios: não conhece Swing nem a tela.
 *
 * <ol>
 *   <li>limpa o aviso de sinal divergente anterior;</li>
 *   <li>se o valor relativo resultante tornaria alguma quantidade negativa: restaura o positivo seguro, informa o
 *       bloqueio e encerra;</li>
 *   <li>aplica o sinal ao alvo;</li>
 *   <li>pede a avaliação do proprietário: sinal errado convida a reconferir (sem bloquear); sem critério curado
 *       registra a escolha como compatibilidade;</li>
 *   <li>sincroniza as representações, verifica a conclusão e repinta.</li>
 * </ol>
 */
public final class FluxoEscolhaSinalNumeroRelativo {

    /** Os passos que dependem de onde o sinal foi escolhido (texto do elemento ou item arrastado). */
    public interface Alvo {
        void limparAvisoDivergente();

        int valorRelativoCandidato(String base, String sinal);

        boolean preservaQuantidadesNaoNegativas(int valorRelativoCandidato);

        void restaurarPositivoSeguro(String base);

        void informarBloqueioQuantidadeNegativa();

        void aplicarSinal(String base, String sinal);

        /** Avaliação do proprietário do papel; nulo quando a situação/papel não tem critério curado. */
        RegistroAcaoEscolhaSinalPapelQuantitativo avaliarEscolha(String base, String sinal);

        void informarSuspeitaSinalIncorreto(String sinal);

        void registrarEscolhaSemCriterio(String base, String sinal);

        void sincronizarRepresentacoes(String sinal);

        void verificarConclusao();

        void repintar();
    }

    private FluxoEscolhaSinalNumeroRelativo() {
    }

    public static void executar(Alvo alvo, String base, String sinal) {
        alvo.limparAvisoDivergente();
        int candidato = alvo.valorRelativoCandidato(base, sinal);
        if (!alvo.preservaQuantidadesNaoNegativas(candidato)) {
            alvo.restaurarPositivoSeguro(base);
            alvo.informarBloqueioQuantidadeNegativa();
            alvo.repintar();
            return;
        }
        alvo.aplicarSinal(base, sinal);
        RegistroAcaoEscolhaSinalPapelQuantitativo registro = alvo.avaliarEscolha(base, sinal);
        if (registro != null && registro.foiErrada()) {
            alvo.informarSuspeitaSinalIncorreto(sinal);
        }
        if (registro == null) {
            alvo.registrarEscolhaSemCriterio(base, sinal);
        }
        alvo.sincronizarRepresentacoes(sinal);
        alvo.verificarConclusao();
        alvo.repintar();
    }
}
