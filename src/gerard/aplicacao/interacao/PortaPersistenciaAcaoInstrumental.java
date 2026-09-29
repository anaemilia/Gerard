package gerard.aplicacao.interacao;

import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;

/** Persiste um fato já constituído, sem reinterpretá-lo. */
public interface PortaPersistenciaAcaoInstrumental {
    void persistir(RegistroFactualAcaoInstrumental registro);
}
