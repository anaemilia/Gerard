package gerard.campoaditivo.venn.modelo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenaDiagramaVenn {
    public enum Natureza { COLECOES, BARRAS_COMPARACAO, VENN }

    private final List<NoDiagramaVenn> nos;
    private final List<ConectorDiagramaVenn> conectores;
    private final Natureza natureza;

    public CenaDiagramaVenn(List<NoDiagramaVenn> nos, List<ConectorDiagramaVenn> conectores) {
        this(nos, conectores, Natureza.VENN);
    }

    public CenaDiagramaVenn(List<NoDiagramaVenn> nos,
            List<ConectorDiagramaVenn> conectores, Natureza natureza) {
        this.nos = new ArrayList<NoDiagramaVenn>(nos);
        this.conectores = new ArrayList<ConectorDiagramaVenn>(conectores);
        this.natureza = natureza == null ? Natureza.VENN : natureza;
    }

    public List<NoDiagramaVenn> getNos() { return Collections.unmodifiableList(nos); }
    public List<ConectorDiagramaVenn> getConectores() { return Collections.unmodifiableList(conectores); }
    public Natureza getNatureza() { return natureza; }

    public static Natureza naturezaPara(TipoSituacaoAditiva tipo) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return Natureza.COLECOES;
        }
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            return Natureza.BARRAS_COMPARACAO;
        }
        return Natureza.VENN;
    }
}
