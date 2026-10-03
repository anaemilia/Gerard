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
    // Apoios visuais (historinhas) já decididos pelo backend; a cena só os carrega.
    private final java.util.List<gerard.dominio.campoaditivo.ajuda.ApoioVisual> apoiosVisuais;

    public CenaDiagramaAditivo(String titulo, String descricao, List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores) {
        this(titulo, descricao, figuras, conectores, EstadoFeedbackDiagrama.NEUTRO);
    }

    public CenaDiagramaAditivo(String titulo, String descricao,
            List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores,
            EstadoFeedbackDiagrama estadoFeedback) {
        this(titulo, descricao, figuras, conectores, estadoFeedback,
                Collections.<SegmentoTextoSemantico>emptyList(), null, false, null);
    }

    private CenaDiagramaAditivo(String titulo, String descricao,
            List<FiguraDiagrama> figuras, List<ConectorDiagrama> conectores,
            EstadoFeedbackDiagrama estadoFeedback,
            List<SegmentoTextoSemantico> elementosTexto,
            VocabularioTextoNarrativo vocabularioTexto,
            boolean permiteEditarNarrativa,
            java.util.List<gerard.dominio.campoaditivo.ajuda.ApoioVisual> apoiosVisuais) {
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
        this.apoiosVisuais = apoiosVisuais == null
                ? java.util.Collections.<gerard.dominio.campoaditivo.ajuda.ApoioVisual>emptyList()
                : java.util.Collections.unmodifiableList(new ArrayList<gerard.dominio.campoaditivo.ajuda.ApoioVisual>(apoiosVisuais));
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public List<FiguraDiagrama> getFiguras() { return Collections.unmodifiableList(figuras); }
    public List<ConectorDiagrama> getConectores() { return Collections.unmodifiableList(conectores); }
    public EstadoFeedbackDiagrama getEstadoFeedback() { return estadoFeedback; }
    public List<SegmentoTextoSemantico> getElementosTexto() { return Collections.unmodifiableList(elementosTexto); }
    public VocabularioTextoNarrativo getVocabularioTexto() { return vocabularioTexto; }
    public boolean isPermiteEditarNarrativa() { return permiteEditarNarrativa; }
    public java.util.List<gerard.dominio.campoaditivo.ajuda.ApoioVisual> getApoiosVisuais() { return apoiosVisuais; }

    /** Nova cena que carrega os apoios visuais que o backend decidiu (possivelmente nenhum). */
    public CenaDiagramaAditivo comApoiosVisuais(java.util.List<gerard.dominio.campoaditivo.ajuda.ApoioVisual> decididos) {
        return new CenaDiagramaAditivo(titulo, descricao, figuras, conectores, estadoFeedback,
                elementosTexto, vocabularioTexto, permiteEditarNarrativa, decididos);
    }

    /**
     * Nova cena em que cada figura traz o subtítulo já decidido por quem o conhece
     * (ex.: o personagem do papel na situação curada). A cena só o carrega.
     */
    public CenaDiagramaAditivo comSubtitulos(java.util.function.Function<String, String> subtituloPorPapel) {
        List<FiguraDiagrama> novas = new ArrayList<FiguraDiagrama>();
        for (FiguraDiagrama figura : figuras) {
            String subtitulo = subtituloPorPapel == null ? ""
                    : subtituloPorPapel.apply(figura.getChavePapelSemantico());
            novas.add(figura.comSubtitulo(subtitulo));
        }
        return new CenaDiagramaAditivo(titulo, descricao, novas, conectores, estadoFeedback,
                elementosTexto, vocabularioTexto, permiteEditarNarrativa, apoiosVisuais);
    }

    public CenaDiagramaAditivo comEstadoFeedback(EstadoFeedbackDiagrama estado) {
        return new CenaDiagramaAditivo(titulo, descricao, figuras, conectores, estado,
                elementosTexto, vocabularioTexto, permiteEditarNarrativa, apoiosVisuais);
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
                elementosTexto, vocabularioTexto, permiteEditarNarrativa, apoiosVisuais);
    }
}
