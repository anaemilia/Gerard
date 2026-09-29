package gerard.pesquisador.log;

import gerard.aplicacao.portabilidade.PortaRegistroAtividadeWeb;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.ControladorContextoSituacao;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;

/** Adaptador técnico entre a atividade web e o log existente do Gérard. */
public final class RegistradorAtividadeWebLogGerard
        implements PortaRegistroAtividadeWeb {

    private final LoggerInteracaoGerard logger;
    private final ControladorContextoSituacao contexto;
    private final PersistidorAcaoInstrumentalLogGerard persistidor;
    private final PersistidorRestauracaoModelagemLogGerard persistidorRestauracao;

    public RegistradorAtividadeWebLogGerard() {
        this(LoggerInteracaoGerard.getInstancia());
    }

    RegistradorAtividadeWebLogGerard(LoggerInteracaoGerard logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger é obrigatório");
        }
        this.logger = logger;
        this.contexto = new ControladorContextoSituacao(logger);
        this.persistidor = new PersistidorAcaoInstrumentalLogGerard(logger);
        this.persistidorRestauracao = new PersistidorRestauracaoModelagemLogGerard(logger);
    }

    public void registrarNovaSituacao(SituacaoProblemaAditiva situacao,
            String categoriaFallback, String enunciado) {
        contexto.registrarNovaSituacao(situacao, categoriaFallback, enunciado);
    }

    public void registrarAcaoGranularUsuario(String tipoAcao, String tarefa,
            String instrumentoOrganizacao, String instrumentoArtefato,
            String funcaoArtefato, String objeto, String origemEvento,
            String detalhes, String mudancaObservavel) {
        logger.registrarAcaoGranularUsuario(tipoAcao, tarefa,
                instrumentoOrganizacao, instrumentoArtefato, funcaoArtefato,
                objeto, origemEvento, detalhes, mudancaObservavel);
    }

    /** Mesma persistência do desktop para o registro constituído pelo proprietário. */
    public void persistir(RegistroFactualAcaoInstrumental registro) {
        persistidor.persistir(registro);
    }

    public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem registro) {
        persistidorRestauracao.persistir(registro,
                "Restaurar a área do diagrama", "Botão Reiniciar",
                "Botão Reiniciar da modelagem (web)",
                "Limpar a modelagem e permitir nova tentativa", "OBJ8",
                "O sujeito pode recomeçar a construção do diagrama.",
                "WEB_REINICIAR_MODELAGEM");
    }
}
