package gerard.campoaditivo.diagrama.modelo;

import gerard.interpretacao.modelo.SegmentoTextoSemantico;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CenaDiagramaAditivo {
    private final String titulo;
    private final String descricao;
    private final List<FiguraDiagrama> figuras;
    private final List<ConectorDiagrama> conectores;
    private final EstadoFeedbackDiagrama estadoFeedback;
    // Palavras do enunciado, não figuras do diagrama — ver
    // GeradorCenaDiagramaAditivo.comElementosTextoNarrativa. Diferente de
    // FiguraDiagrama (retângulos/quadradinhos fixos), estas são editáveis
    // pela usuária num rascunho efêmero (ver PainelEditorNarrativa), por
    // isso vivem numa lista própria, nunca misturadas a "figuras".
    private final List<SegmentoTextoSemantico> elementosTexto;
    private final VocabularioTextoNarrativo vocabularioTexto;
    // Decisão de domínio (modelagem concluída), não uma regra de
    // apresentação — ver GeradorCenaDiagramaAditivo.permiteEditarNarrativa.
    // Fica na cena para que desktop e web leiam o mesmo sinal do mesmo
    // gerador, em vez de cada um recalcular "posso mostrar o botão de
    // editar?" por conta própria.
    private final boolean permiteEditarNarrativa;

    public CenaDiagramaAditivo(String titulo, String descricao, List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores) {
        this(titulo, descricao, figuras, conectores, EstadoFeedbackDiagrama.NEUTRO);
    }

    public CenaDiagramaAditivo(String titulo, String descricao,
            List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores,
            EstadoFeedbackDiagrama estadoFeedback) {
        this(titulo, descricao, figuras, conectores, estadoFeedback,
                Collections.<SegmentoTextoSemantico>emptyList(), null, false);
    }

    private CenaDiagramaAditivo(String titulo, String descricao,
            List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores,
            EstadoFeedbackDiagrama estadoFeedback,
            List<SegmentoTextoSemantico> elementosTexto,
            VocabularioTextoNarrativo vocabularioTexto,
            boolean permiteEditarNarrativa) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.figuras = new ArrayList<FiguraDiagrama>(figuras);
        this.conectores = new ArrayList<ConectorDiagrama>(conectores);
        this.estadoFeedback = estadoFeedback == null
                ? EstadoFeedbackDiagrama.NEUTRO : estadoFeedback;
        this.elementosTexto = new ArrayList<SegmentoTextoSemantico>(
                elementosTexto == null ? Collections.<SegmentoTextoSemantico>emptyList() : elementosTexto);
        this.vocabularioTexto = vocabularioTexto;
        this.permiteEditarNarrativa = permiteEditarNarrativa;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public List<FiguraDiagrama> getFiguras() { return Collections.unmodifiableList(figuras); }
    public List<ConectorDiagrama> getConectores() { return Collections.unmodifiableList(conectores); }
    public EstadoFeedbackDiagrama getEstadoFeedback() { return estadoFeedback; }
    public List<SegmentoTextoSemantico> getElementosTexto() { return Collections.unmodifiableList(elementosTexto); }
    public VocabularioTextoNarrativo getVocabularioTexto() { return vocabularioTexto; }
    public boolean isPermiteEditarNarrativa() { return permiteEditarNarrativa; }

    public CenaDiagramaAditivo comEstadoFeedback(EstadoFeedbackDiagrama estado) {
        return new CenaDiagramaAditivo(titulo, descricao, figuras, conectores, estado,
                elementosTexto, vocabularioTexto, permiteEditarNarrativa);
    }

    /**
     * Nova cena com as palavras do enunciado anexadas — ver
     * GeradorCenaDiagramaAditivo.gerarCenaNarrativa. {@code permiteEditarNarrativa}
     * é uma decisão de domínio (modelagem concluída) que o chamador já
     * possui e só repassa; o gerador não decide isso por conta própria.
     */
    public CenaDiagramaAditivo comElementosTexto(List<SegmentoTextoSemantico> elementosTexto,
            VocabularioTextoNarrativo vocabularioTexto, boolean permiteEditarNarrativa) {
        return new CenaDiagramaAditivo(titulo, descricao, figuras, conectores, estadoFeedback,
                elementosTexto, vocabularioTexto, permiteEditarNarrativa);
    }
}
