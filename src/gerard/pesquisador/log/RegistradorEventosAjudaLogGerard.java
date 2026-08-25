package gerard.pesquisador.log;

import gerard.adaptacao.DecisaoAjuda;
import gerard.adaptacao.ItemRepertorioAjuda;
import gerard.aplicacao.adaptacao.ConfirmacaoMaterializacaoAjuda;
import gerard.aplicacao.adaptacao.ContextoRegistroAjuda;
import gerard.aplicacao.adaptacao.RegistradorEventosAjuda;

/**
 * Traduz os fatos da ajuda adaptativa para o log de producao. A decisao tem
 * origem INFERENCIA_COMPUTACIONAL; a materializacao confirmada, SISTEMA.
 */
public final class RegistradorEventosAjudaLogGerard
        implements RegistradorEventosAjuda {

    private final LoggerInteracaoGerard logger;

    public RegistradorEventosAjudaLogGerard(LoggerInteracaoGerard logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger de producao e obrigatorio");
        }
        this.logger = logger;
    }

    @Override
    public void registrarDecisao(
            DecisaoAjuda decisao,
            ContextoRegistroAjuda contexto) {
        validar(decisao, contexto);
        ItemRepertorioAjuda ajuda = decisao.getAjuda();
        logger.registrarComputadorComIdentidade(
                "Decidir ajuda adaptativa",
                "Aplicacao de regra publicada pelo proprietario semantico",
                decisao.deveAplicarAjuda()
                        ? ajuda.getCodigo() : "SEM_REGRA_APLICAVEL",
                "Selecionar apoio no repertorio local sem materializa-lo",
                decisao.deveAplicarAjuda()
                        ? "regra=" + decisao.getRegraId()
                                + "@" + decisao.getRegraVersao()
                        : "nenhuma regra publicada aplicavel",
                "AJUDA_ADAPTATIVA_DECIDIDA",
                "origem_acao=INFERENCIA_COMPUTACIONAL"
                        + ";versao_modelo=" + decisao.getVersaoModelo()
                        + ";regra_id=" + valor(decisao.getRegraId())
                        + ";regra_versao=" + valor(decisao.getRegraVersao())
                        + ";regra_algoritmo_origem="
                                + valor(decisao.getRegraAlgoritmoOrigem())
                        + ";regra_proveniencia_casos="
                                + valor(decisao.getRegraProvenienciaCasos())
                        + ";proprietario_semantico="
                                + decisao.getProprietarioSemantico()
                        + ";diagnostico_factual="
                                + decisao.getDiagnosticoFactual()
                        + ";apoio=" + (ajuda == null ? "" : ajuda.getCodigo())
                        + correlacoes(contexto),
                contexto.getActionId(), contexto.getRejectionSequenceId());
    }

    @Override
    public void registrarFeedbackExibido(
            DecisaoAjuda decisao,
            ConfirmacaoMaterializacaoAjuda confirmacao,
            ContextoRegistroAjuda contexto) {
        validar(decisao, contexto);
        if (confirmacao == null || decisao.getAjuda() == null
                || !decisao.getAjuda().getCodigo().equals(
                        confirmacao.getCodigoAjuda())) {
            throw new IllegalArgumentException(
                    "a confirmacao deve pertencer a ajuda decidida");
        }
        logger.registrarComputadorComIdentidade(
                "Exibir apoio pedagogico (Scaffolding)",
                "Materializacao da DecisaoAjuda pela representacao",
                confirmacao.getCodigoAjuda(),
                "Apresentar apoio pedagogico ao participante",
                "Evento nao afirma percepcao ou compreensao do participante",
                "FEEDBACK_EXIBIDO",
                "origem_acao=SISTEMA"
                        + ";estilo=" + confirmacao.getCodigoAjuda()
                        + ";modalidade=" + confirmacao.getModalidade()
                        + ";criterio=" + confirmacao.getCriterioConfirmacao()
                        + ";detalhe=" + confirmacao.getDetalhe()
                        + ";versao_modelo=" + decisao.getVersaoModelo()
                        + ";regra_id=" + decisao.getRegraId()
                        + ";regra_versao=" + decisao.getRegraVersao()
                        + ";regra_algoritmo_origem="
                                + decisao.getRegraAlgoritmoOrigem()
                        + ";regra_proveniencia_casos="
                                + decisao.getRegraProvenienciaCasos()
                        + ";proprietario_semantico="
                                + decisao.getProprietarioSemantico()
                        + correlacoes(contexto),
                contexto.getActionId(), contexto.getRejectionSequenceId());
    }

    private void validar(
            DecisaoAjuda decisao,
            ContextoRegistroAjuda contexto) {
        if (decisao == null || contexto == null) {
            throw new IllegalArgumentException(
                    "decisao e contexto de registro sao obrigatorios");
        }
    }

    private String correlacoes(ContextoRegistroAjuda contexto) {
        return ";action_id=" + contexto.getActionId()
                + ";rejection_sequence_id="
                        + contexto.getRejectionSequenceId();
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
