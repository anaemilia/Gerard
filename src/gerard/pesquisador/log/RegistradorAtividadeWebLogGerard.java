package gerard.pesquisador.log;

import gerard.aplicacao.portabilidade.PortaRegistroAtividade;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.ControladorContextoSituacao;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;

/**
 * Adaptador técnico entre a atividade web e o log existente do Gérard. Há um por visitante: o arquivo de log é um só
 * (o do processo), mas o contexto de cada registro — usuário, problema, situação e tentativa — é o do visitante a
 * quem o registrador pertence. Cada gravação restaura esse contexto no logger, grava e guarda de volta o que mudou, sob
 * o monitor do logger, de modo que visitantes simultâneos não misturam o contexto uns dos outros.
 */
public final class RegistradorAtividadeWebLogGerard
        implements PortaRegistroAtividade {
    private static final String USUARIO_PADRAO = "usuario_local";

    private final LoggerInteracaoGerard logger;
    private final ControladorContextoSituacao contexto;
    private final PersistidorAcaoInstrumentalLogGerard persistidor;
    private final PersistidorRestauracaoModelagemLogGerard persistidorRestauracao;
    private LoggerInteracaoGerard.ContextoInteracao contextoDoVisitante = LoggerInteracaoGerard.contextoInicial();
    private volatile String usuarioId = USUARIO_PADRAO;

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

    /** O usuário que entrou neste visitante; aparece na coluna de usuário de todos os registros dele. */
    public void definirUsuario(String id) {
        this.usuarioId = id == null || id.trim().isEmpty() ? USUARIO_PADRAO : id.trim();
    }

    private void comContextoDoVisitante(Runnable gravacao) {
        synchronized (logger) {
            logger.restaurarContexto(contextoDoVisitante);
            logger.definirUsuario(usuarioId);
            try {
                gravacao.run();
            } finally {
                contextoDoVisitante = logger.capturarContextoAtual();
            }
        }
    }

    public void registrarNovaSituacao(final SituacaoProblemaAditiva situacao,
            final String categoriaFallback, final String enunciado) {
        comContextoDoVisitante(new Runnable() {
            public void run() {
                contexto.registrarNovaSituacao(situacao, categoriaFallback, enunciado);
            }
        });
    }

    public void registrarAcaoGranularUsuario(final String tipoAcao, final String tarefa,
            final String instrumentoOrganizacao, final String instrumentoArtefato,
            final String funcaoArtefato, final String objeto, final String origemEvento,
            final String detalhes, final String mudancaObservavel) {
        comContextoDoVisitante(new Runnable() {
            public void run() {
                logger.registrarAcaoGranularUsuario(tipoAcao, tarefa,
                        instrumentoOrganizacao, instrumentoArtefato, funcaoArtefato,
                        objeto, origemEvento, detalhes, mudancaObservavel);
            }
        });
    }

    /** Mesma persistência do desktop para o registro constituído pelo proprietário. */
    public void persistir(final RegistroFactualAcaoInstrumental registro) {
        comContextoDoVisitante(new Runnable() {
            public void run() {
                persistidor.persistir(registro);
            }
        });
    }

    public void registrarRestauracaoModelagem(final RegistroAcaoRestauracaoModelagem registro) {
        comContextoDoVisitante(new Runnable() {
            public void run() {
                persistidorRestauracao.persistir(registro,
                        "Restaurar a área do diagrama", "Botão Reiniciar",
                        "Botão Reiniciar da modelagem (web)",
                        "Limpar a modelagem e permitir nova tentativa", "OBJ8",
                        "O sujeito pode recomeçar a construção do diagrama.",
                        "WEB_REINICIAR_MODELAGEM");
            }
        });
    }
}
