package gerard.aplicacao.portabilidade;

import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;

/**
 * Texto mostrado quando a incógnita é rejeitada, replicando o desfecho real
 * de confirmarValorIncognitaAceito em Main.java para uma sessão sem Modelo
 * do Usuário/Agente Modelador (nenhuma ajuda adaptativa chega a materializar
 * nesse caso, então o desktop sempre cai num destes dois textos): aviso de
 * limite de tentativas (sem texto: o aviso foi retirado) ou a pergunta genérica
 * de confirmação (ui.question.valueMismatch, AG_EMLQ).
 */
final class MensagemFeedbackIncognitaWeb {
    private MensagemFeedbackIncognitaWeb() {
    }

    /**
     * No limite de rejeições não há mensagem: o aviso "ui.notice.attemptLimitReached" foi retirado
     * da interação (decisão da pesquisadora; no desktop desde 2026-09-05, e agora também na web).
     * O que aparece é a escalada (historinha), decidida pelo backend. Abaixo do limite, a pergunta
     * de confirmação do valor.
     */
    static String resolver(PapelQuantitativo papel, ResultadoRegistroTentativaPapel registro) {
        if (limiteAtingido(registro)) {
            return null;
        }
        String nomePapel = gerard.i18n.ServicoLocalizacao.getInstancia().texto(papel.getChave());
        return gerard.i18n.ServicoLocalizacao.getInstancia().formatar("ui.question.valueMismatch", nomePapel);
    }

    static boolean limiteAtingido(ResultadoRegistroTentativaPapel registro) {
        return registro.isLimiteAtingidoAgora() || registro.isRejeitadaSemAvaliacaoPorBloqueio();
    }
}
