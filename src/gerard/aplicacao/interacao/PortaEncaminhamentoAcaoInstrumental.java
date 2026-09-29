package gerard.aplicacao.interacao;

import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;

/** Encaminha ao Modelador um fato já constituído, sem reinterpretá-lo. */
public interface PortaEncaminhamentoAcaoInstrumental {
    void encaminhar(RegistroFactualAcaoInstrumental registro);
}
