package gerard.aplicacao.portabilidade;

import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.ResultadoRegistroTentativaPapel;

/**
 * Texto mostrado quando a incógnita é rejeitada, replicando o desfecho real
 * de confirmarValorIncognitaAceito em Main.java para uma sessão sem Modelo
 * do Usuário/Agente Modelador (nenhuma ajuda adaptativa chega a materializar
 * nesse caso, então o desktop sempre cai num destes dois textos): aviso de
 * limite de tentativas (rejeicoesConsecutivas atingiu
 * PapelQuantitativo.LIMITE_TENTATIVAS_REJEITADAS_CONSECUTIVAS, ou o papel já
 * estava bloqueado por isso) ou a pergunta genérica de confirmação
 * (ui.question.valueMismatch, AG_EMLQ).
 */
final class MensagemFeedbackIncognitaWeb {
    private MensagemFeedbackIncognitaWeb() {
    }

    static String resolver(PapelQuantitativo papel, ResultadoRegistroTentativaPapel registro) {
        String nomePapel = gerard.i18n.ServicoLocalizacao.getInstancia().texto(papel.getChave());
        boolean bloqueado = registro.isLimiteAtingidoAgora()
                || registro.isRejeitadaSemAvaliacaoPorBloqueio();
        String chave = bloqueado ? "ui.notice.attemptLimitReached" : "ui.question.valueMismatch";
        return gerard.i18n.ServicoLocalizacao.getInstancia().formatar(chave, nomePapel);
    }

    static boolean limiteAtingido(ResultadoRegistroTentativaPapel registro) {
        return registro.isLimiteAtingidoAgora() || registro.isRejeitadaSemAvaliacaoPorBloqueio();
    }
}
