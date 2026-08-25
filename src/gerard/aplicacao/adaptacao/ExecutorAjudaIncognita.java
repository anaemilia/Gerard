package gerard.aplicacao.adaptacao;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.DecisaoAjuda;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FatosSelecaoAjudaIncognita;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import java.util.List;

/**
 * Orquestra uma decisao ja localizada no proprietario semantico: nao conhece
 * regras, codigos de ajuda, Swing ou formato de persistencia.
 */
public final class ExecutorAjudaIncognita {

    private final MaterializadorDecisaoAjuda materializador;
    private final RegistradorEventosAjuda registrador;

    public ExecutorAjudaIncognita(
            MaterializadorDecisaoAjuda materializador,
            RegistradorEventosAjuda registrador) {
        if (materializador == null || registrador == null) {
            throw new IllegalArgumentException(
                    "materializador e registrador de ajuda sao obrigatorios");
        }
        this.materializador = materializador;
        this.registrador = registrador;
    }

    public ResultadoExecucaoAjudaIncognita executar(
            ResultadoContextualizacaoIncognita contextualizacao,
            DiagnosticoErroPapel diagnostico,
            int ordinalRejeicao,
            ContextoRegistroAjuda contextoRegistro) {
        if (contextualizacao == null || !contextualizacao.estaDisponivel()) {
            return ResultadoExecucaoAjudaIncognita.contextoIndisponivel();
        }
        if (diagnostico == null || contextoRegistro == null) {
            throw new IllegalArgumentException(
                    "diagnostico e contexto de registro sao obrigatorios");
        }

        IncognitaQuantitativa incognita = contextualizacao.getIncognita().get();
        ContextoAdaptativoUsuario contexto = contextualizacao.getContexto().get();
        DecisaoAjuda decisao = incognita.selecionarAjuda(
                new FatosSelecaoAjudaIncognita(diagnostico, ordinalRejeicao),
                contexto);

        registrador.registrarDecisao(decisao, contextoRegistro);
        if (!decisao.deveAplicarAjuda()) {
            return ResultadoExecucaoAjudaIncognita.semRegra(decisao);
        }

        List<ConfirmacaoMaterializacaoAjuda> confirmacoes =
                materializador.materializar(
                        decisao, incognita.getChavePapelDesignado());
        if (confirmacoes == null || confirmacoes.isEmpty()) {
            throw new IllegalStateException(
                    "a representacao nao confirmou a ajuda que recebeu para materializar");
        }
        for (ConfirmacaoMaterializacaoAjuda confirmacao : confirmacoes) {
            if (confirmacao == null
                    || !decisao.getAjuda().getCodigo().equals(
                            confirmacao.getCodigoAjuda())) {
                throw new IllegalStateException(
                        "a representacao confirmou outra ajuda ou uma confirmacao nula");
            }
            registrador.registrarFeedbackExibido(
                    decisao, confirmacao, contextoRegistro);
        }
        return ResultadoExecucaoAjudaIncognita.materializada(
                decisao, confirmacoes);
    }
}
