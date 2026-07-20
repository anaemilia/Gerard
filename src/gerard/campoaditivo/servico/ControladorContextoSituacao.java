package gerard.campoaditivo.servico;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.pesquisador.log.LoggerInteracaoGerard;

/** Separa de Main a preparação do contexto analítico da situação exibida. */
public final class ControladorContextoSituacao {
    private final LoggerInteracaoGerard logger;
    public ControladorContextoSituacao(LoggerInteracaoGerard logger) { this.logger = logger; }

    public void registrarNovaSituacao(SituacaoProblemaAditiva situacao, String categoriaFallback, String enunciado) {
        if (situacao == null) {
            logger.novoProblema(categoriaFallback, enunciado, "", "", "");
            return;
        }
        logger.novoProblema(categoriaFallback, enunciado, situacao.getId(), situacao.getSituacaoGrupoId(), situacao.getCodigoIdioma());
    }
}
