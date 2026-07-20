package gerard.campoaditivo.diagrama.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenaDiagramaAditivo {
    private final String titulo;
    private final String descricao;
    private final List<FiguraDiagrama> figuras;
    private final List<ConectorDiagrama> conectores;

    public CenaDiagramaAditivo(String titulo, String descricao, List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.figuras = new ArrayList<FiguraDiagrama>(figuras);
        this.conectores = new ArrayList<ConectorDiagrama>(conectores);
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public List<FiguraDiagrama> getFiguras() { return Collections.unmodifiableList(figuras); }
    public List<ConectorDiagrama> getConectores() { return Collections.unmodifiableList(conectores); }
}
