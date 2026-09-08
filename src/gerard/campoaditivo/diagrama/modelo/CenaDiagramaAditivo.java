package gerard.campoaditivo.diagrama.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenaDiagramaAditivo {
    private final String titulo;
    private final String descricao;
    private final List<FiguraDiagrama> figuras;
    private final List<ConectorDiagrama> conectores;
    private final EstadoFeedbackDiagrama estadoFeedback;

    public CenaDiagramaAditivo(String titulo, String descricao, List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores) {
        this(titulo, descricao, figuras, conectores, EstadoFeedbackDiagrama.NEUTRO);
    }

    public CenaDiagramaAditivo(String titulo, String descricao,
            List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores,
            EstadoFeedbackDiagrama estadoFeedback) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.figuras = new ArrayList<FiguraDiagrama>(figuras);
        this.conectores = new ArrayList<ConectorDiagrama>(conectores);
        this.estadoFeedback = estadoFeedback == null
                ? EstadoFeedbackDiagrama.NEUTRO : estadoFeedback;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public List<FiguraDiagrama> getFiguras() { return Collections.unmodifiableList(figuras); }
    public List<ConectorDiagrama> getConectores() { return Collections.unmodifiableList(conectores); }
    public EstadoFeedbackDiagrama getEstadoFeedback() { return estadoFeedback; }

    public CenaDiagramaAditivo comEstadoFeedback(EstadoFeedbackDiagrama estado) {
        return new CenaDiagramaAditivo(titulo, descricao, figuras, conectores, estado);
    }
}
