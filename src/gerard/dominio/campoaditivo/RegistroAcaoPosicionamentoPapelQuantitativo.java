package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.semantica.papel.DescritorPapelQuantitativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Fato produzido pelo papel quantitativo quando um elemento textual é
 * posicionado sobre um papel do diagrama.
 */
public final class RegistroAcaoPosicionamentoPapelQuantitativo
        implements RegistroFactualAcaoInstrumental {

    public static final String DIAGNOSTICO_INCOMPATIBILIDADE =
            "PAPEL_INCOMPATIVEL_COM_DESTINO";
    public static final String REGRA_SEMANTICA =
            "regra.papel.compatibilidadePosicionamento";

    private final String actionId;
    private final TipoSituacaoAditiva categoria;
    private final DescritorPapelQuantitativo papelOrigem;
    private final DescritorPapelQuantitativo papelDestino;
    private final ResultadoAvaliacaoAcaoInstrumental resultado;
    private final ContextoAcaoInstrumental contexto;
    private final List<String> participantesSemanticos;

    public RegistroAcaoPosicionamentoPapelQuantitativo(
            TipoSituacaoAditiva categoria,
            DescritorPapelQuantitativo papelOrigem,
            DescritorPapelQuantitativo papelDestino,
            boolean compativel,
            ContextoAcaoInstrumental contexto) {
        if (categoria == null || papelOrigem == null || papelDestino == null
                || contexto == null) {
            throw new IllegalArgumentException(
                    "categoria, papéis e contexto são obrigatórios");
        }
        this.actionId = UUID.randomUUID().toString();
        this.categoria = categoria;
        this.papelOrigem = papelOrigem;
        this.papelDestino = papelDestino;
        this.resultado = compativel
                ? ResultadoAvaliacaoAcaoInstrumental.CORRETA
                : ResultadoAvaliacaoAcaoInstrumental.ERRADA;
        this.contexto = contexto;

        Set<String> participantes = new LinkedHashSet<String>();
        participantes.add(papelOrigem.getChave());
        participantes.add(papelDestino.getChave());
        participantes.addAll(contexto.getParticipantesSemanticos());
        this.participantesSemanticos = Collections.unmodifiableList(
                new ArrayList<String>(participantes));
    }

    public String getActionId() { return actionId; }
    public OrigemAcao getOrigemAcao() { return OrigemAcao.ORIGEM_USUARIO; }
    public TarefaInteracao getTarefaInteracao() { return TarefaInteracao.POSICIONAR; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public String getProprietarioSemantico() { return papelOrigem.getChave(); }
    public String getAlvoSemantico() { return papelDestino.getChave(); }
    public ResultadoAvaliacaoAcaoInstrumental getResultado() { return resultado; }
    public String getTipoDiagnosticoFactual() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA
                ? DIAGNOSTICO_INCOMPATIBILIDADE : "";
    }
    public String getValorPropostoFactual() { return papelOrigem.getChave(); }
    public String getValorEsperadoFactual() { return papelDestino.getChave(); }
    public String getRegraSemantica() { return REGRA_SEMANTICA; }
    public ContextoAcaoInstrumental getContexto() { return contexto; }
    public List<String> getParticipantesSemanticos() {
        return participantesSemanticos;
    }
    public String getRejectionSequenceId() { return null; }
    public boolean foiCorreta() {
        return resultado == ResultadoAvaliacaoAcaoInstrumental.CORRETA;
    }
}
