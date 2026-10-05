package gerard.aplicacao;

import gerard.aplicacao.adaptacao.ResultadoExecucaoAjudaIncognita;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;

/**
 * Sequência da confirmação do valor informado para a incógnita, escrita UMA vez para os dois protocolos
 * ({@link ProtocoloValorIncognita}). Decisão do usuário (2026-07-22): a modelagem só volta a propagar/concluir
 * depois que o valor correto for inserido; "Sim" (a pessoa insiste) não libera a propagação, "Não" só permite
 * tentar de novo.
 *
 * <p>Quem avalia é o proprietário da incógnita (a tentativa do papel); esta classe só garante a ORDEM e os
 * desvios, sem conhecer Swing:</p>
 * <ol>
 *   <li>fora do alvo (item que não é a incógnita preenchida pelo protocolo, ou sem incógnita): nada a conferir;</li>
 *   <li>o proprietário avalia a ação e o registro é persistido;</li>
 *   <li>erro com critério aplicável: feedback visual de erro; erro com diagnóstico: executa a ajuda do repertório;</li>
 *   <li>sem critério ou correto: limpa o feedback de erro e libera;</li>
 *   <li>ajuda materializada: não pergunta; limite de tentativas atingido: reposiciona, sem diálogo;</li>
 *   <li>caso contrário: pergunta de confirmação e registro da resposta.</li>
 * </ol>
 *
 * @return true quando não há divergência a conferir; false sempre que houver, seja qual for a resposta.
 */
public final class FluxoConfirmacaoValorIncognita {

    /** Os passos que dependem da tela e da sessão; cada um é uma ação já decidida por quem a possui. */
    public interface Alvo {
        /** O item é a incógnita original preenchida pelo protocolo de mouse+texto. */
        boolean ehIncognitaPreenchidaPeloProtocolo();

        /** Garante a tentativa e o contexto adaptativo do papel; devolve a chave do papel da incógnita. */
        String prepararPapelDaIncognita();

        IncognitaQuantitativa incognitaAtual();

        IdentidadeAcaoInstrumentalPapel iniciarAcaoDoUsuario();

        void registrarPapeisDadoModificadosSeHouver();

        RegistroAcaoInstrumental avaliar(ProtocoloValorIncognita protocolo, IncognitaQuantitativa incognita,
                IdentidadeAcaoInstrumentalPapel identidade, String papelAlvo);

        void persistir(RegistroAcaoInstrumental registro);

        void aplicarFeedbackVisualErro();

        void limparFeedbackVisualErro();

        ResultadoExecucaoAjudaIncognita executarAjuda(RegistroAcaoInstrumental registro,
                ResultadoRegistroTentativaPapel resultadoTentativa);

        void processarLimiteTentativasAtingido(ResultadoRegistroTentativaPapel resultadoTentativa);

        /** Pergunta de confirmação do valor divergente, exibição do apoio e registro da resposta. */
        void perguntarConfirmacao(ProtocoloValorIncognita protocolo, RegistroAcaoInstrumental registro,
                IncognitaQuantitativa incognita, String papelAlvo);
    }

    private FluxoConfirmacaoValorIncognita() {
    }

    public static boolean executar(Alvo alvo, ProtocoloValorIncognita protocolo,
            IdentidadeAcaoInstrumentalPapel identidadeAcao) {
        if (!alvo.ehIncognitaPreenchidaPeloProtocolo()) {
            return true;
        }
        String papelAlvo = alvo.prepararPapelDaIncognita();
        IncognitaQuantitativa incognita = alvo.incognitaAtual();
        if (incognita == null) {
            return true;
        }
        IdentidadeAcaoInstrumentalPapel identidade =
                identidadeAcao != null ? identidadeAcao : alvo.iniciarAcaoDoUsuario();

        alvo.registrarPapeisDadoModificadosSeHouver();
        RegistroAcaoInstrumental registro = alvo.avaliar(protocolo, incognita, identidade, papelAlvo);
        alvo.persistir(registro);

        ResultadoRegistroTentativaPapel resultadoTentativa = registro.getResultadoTentativa().isPresent()
                ? registro.getResultadoTentativa().get() : null;
        if (registro.foiErrada() && registro.possuiCriterioAplicavel()) {
            alvo.aplicarFeedbackVisualErro();
        }
        ResultadoExecucaoAjudaIncognita resultadoAjuda = null;
        if (registro.foiErrada() && resultadoTentativa != null && registro.getDiagnostico().isPresent()) {
            resultadoAjuda = alvo.executarAjuda(registro, resultadoTentativa);
        }
        if (!registro.possuiCriterioAplicavel() || registro.foiCorreta()) {
            alvo.limparFeedbackVisualErro();
            return true;
        }
        if (resultadoAjuda != null && resultadoAjuda.foiMaterializada()) {
            return false;
        }
        if (resultadoTentativa != null && resultadoTentativa.isLimiteAtingidoAgora()) {
            // 3ª rejeição consecutiva do mesmo item: encerra a ação e bloqueia novas tentativas, sem diálogo.
            alvo.processarLimiteTentativasAtingido(resultadoTentativa);
            return false;
        }
        alvo.perguntarConfirmacao(protocolo, registro, incognita, papelAlvo);
        return false;
    }
}
