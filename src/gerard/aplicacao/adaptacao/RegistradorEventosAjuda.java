package gerard.aplicacao.adaptacao;

import gerard.adaptacao.DecisaoAjuda;

/** Porta de infraestrutura para os dois fatos distintos da ajuda adaptativa. */
public interface RegistradorEventosAjuda {

    void registrarDecisao(
            DecisaoAjuda decisao,
            ContextoRegistroAjuda contexto);

    void registrarFeedbackExibido(
            DecisaoAjuda decisao,
            ConfirmacaoMaterializacaoAjuda confirmacao,
            ContextoRegistroAjuda contexto);
}
