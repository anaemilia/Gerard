package gerard.dominio.atividade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Fatos instrumentais entregues pela interação ao proprietário semântico.
 * Não contém C/E nem regra matemática.
 */
public final class ContextoAcaoInstrumental {
    private final String tarefa;
    private final String instrumentoOrganizacao;
    private final String instrumentoArtefato;
    private final String funcaoArtefato;
    private final String objeto;
    private final String origemEvento;
    private final String detalhes;
    private final String mudancaObservavel;
    private final List<String> participantesSemanticos;

    public ContextoAcaoInstrumental(
            String tarefa,
            String instrumentoOrganizacao,
            String instrumentoArtefato,
            String funcaoArtefato,
            String objeto,
            String origemEvento,
            String detalhes,
            String mudancaObservavel,
            List<String> participantesSemanticos) {
        this.tarefa = limpar(tarefa);
        this.instrumentoOrganizacao = limpar(instrumentoOrganizacao);
        this.instrumentoArtefato = limpar(instrumentoArtefato);
        this.funcaoArtefato = limpar(funcaoArtefato);
        this.objeto = limpar(objeto);
        this.origemEvento = limpar(origemEvento);
        this.detalhes = limpar(detalhes);
        this.mudancaObservavel = limpar(mudancaObservavel);
        Set<String> unicos = new LinkedHashSet<String>();
        if (participantesSemanticos != null) {
            for (String participante : participantesSemanticos) {
                String normalizado = limpar(participante);
                if (normalizado.length() > 0) {
                    unicos.add(normalizado);
                }
            }
        }
        this.participantesSemanticos = Collections.unmodifiableList(
                new ArrayList<String>(unicos));
    }

    public String getTarefa() { return tarefa; }
    public String getInstrumentoOrganizacao() { return instrumentoOrganizacao; }
    public String getInstrumentoArtefato() { return instrumentoArtefato; }
    public String getFuncaoArtefato() { return funcaoArtefato; }
    public String getObjeto() { return objeto; }
    public String getOrigemEvento() { return origemEvento; }
    public String getDetalhes() { return detalhes; }
    public String getMudancaObservavel() { return mudancaObservavel; }
    public List<String> getParticipantesSemanticos() { return participantesSemanticos; }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
