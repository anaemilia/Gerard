package gerard.pesquisador.log;

import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;

/** Adaptador que somente encaminha ao logger o registro já produzido. */
public final class PersistidorAcaoInstrumentalLogGerard
        implements PortaPersistenciaAcaoInstrumental {

    private final LoggerInteracaoGerard logger;

    public PersistidorAcaoInstrumentalLogGerard(LoggerInteracaoGerard logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger obrigatorio");
        }
        this.logger = logger;
    }

    @Override
    public void persistir(RegistroFactualAcaoInstrumental registro) {
        logger.registrarAcaoInstrumentalUsuario(registro);
    }
}
