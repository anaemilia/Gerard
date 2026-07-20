package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado imutável da preparação de uma atividade de construção. */
public final class ConjuntoBlocosMontagem {
    private final SituacaoProblemaAditiva situacao;
    private final List<BlocoTextoMontagem> blocosDisponiveis;
    private final List<String> idsCorretosOrdenados;
    private final String[] valoresDiagrama;

    public ConjuntoBlocosMontagem(SituacaoProblemaAditiva situacao,
                                  List<BlocoTextoMontagem> blocosDisponiveis,
                                  List<String> idsCorretosOrdenados,
                                  String[] valoresDiagrama) {
        this.situacao = situacao;
        this.blocosDisponiveis = Collections.unmodifiableList(
                new ArrayList<BlocoTextoMontagem>(blocosDisponiveis));
        this.idsCorretosOrdenados = Collections.unmodifiableList(
                new ArrayList<String>(idsCorretosOrdenados));
        this.valoresDiagrama = valoresDiagrama == null ? new String[0] : valoresDiagrama.clone();
    }

    public SituacaoProblemaAditiva getSituacao() { return situacao; }
    public List<BlocoTextoMontagem> getBlocosDisponiveis() { return blocosDisponiveis; }
    public List<String> getIdsCorretosOrdenados() { return idsCorretosOrdenados; }
    public String[] getValoresDiagrama() { return valoresDiagrama.clone(); }
}
