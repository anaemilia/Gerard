package gerard.campoaditivo.venn.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenaDiagramaVenn {
    private final List<NoDiagramaVenn> nos;
    private final List<ConectorDiagramaVenn> conectores;

    public CenaDiagramaVenn(List<NoDiagramaVenn> nos, List<ConectorDiagramaVenn> conectores) {
        this.nos = new ArrayList<NoDiagramaVenn>(nos);
        this.conectores = new ArrayList<ConectorDiagramaVenn>(conectores);
    }

    public List<NoDiagramaVenn> getNos() { return Collections.unmodifiableList(nos); }
    public List<ConectorDiagramaVenn> getConectores() { return Collections.unmodifiableList(conectores); }
}
