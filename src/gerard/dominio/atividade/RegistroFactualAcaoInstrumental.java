package gerard.dominio.atividade;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.OrigemAcao;
import java.util.List;

/**
 * Contrato factual comum dos registros produzidos pelos proprietários
 * semânticos de ações instrumentais.
 *
 * <p>O contrato permite que a infraestrutura persista e encaminhe ações de
 * naturezas diferentes sem conhecer a regra que as avaliou. A implementação
 * concreta continua pertencendo ao menor objeto ou agregado que possui esse
 * conhecimento.</p>
 */
public interface RegistroFactualAcaoInstrumental {

    String getActionId();
    OrigemAcao getOrigemAcao();
    TarefaInteracao getTarefaInteracao();
    TipoSituacaoAditiva getCategoria();
    String getProprietarioSemantico();
    String getAlvoSemantico();
    ResultadoAvaliacaoAcaoInstrumental getResultado();
    String getTipoDiagnosticoFactual();
    String getValorPropostoFactual();
    String getValorEsperadoFactual();
    String getRegraSemantica();
    ContextoAcaoInstrumental getContexto();
    List<String> getParticipantesSemanticos();
    String getRejectionSequenceId();
}
