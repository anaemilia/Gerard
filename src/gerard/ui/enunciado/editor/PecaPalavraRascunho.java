package gerard.ui.enunciado.editor;

import gerard.interpretacao.modelo.SegmentoTextoSemantico;
import gerard.interpretacao.modelo.TipoSegmentoNarrativo;

/**
 * Uma palavra dentro do rascunho efêmero do editor de enunciado — equivalente
 * de {@code Peca}/{@code ElementoTexto} em EditorNarrativa.tsx (protótipo
 * web), construída a partir de {@link SegmentoTextoSemantico} (o mesmo
 * tokenizador que Main.java e o servidor web já usam — ver
 * GeradorCenaDiagramaAditivo.gerarElementosTexto), nunca reclassificada do
 * zero.
 *
 * Ao contrário da primeira versão desta classe, agora carrega geometria de
 * tela (x, y, largura, altura) igual a
 * {@code gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel} — decisão
 * da usuária: o editor deve viver dentro da própria cena, como um elemento de
 * texto arrastável no mesmo estilo do que já existe, e não como um painel
 * Swing separado. Quem calcula/atualiza essa geometria a cada quadro é
 * {@link GeometriaEditorNarrativa}; esta classe só guarda o último valor.
 *
 * Nenhuma operação deste editor reescreve o valor de uma palavra existente,
 * só reordena, remove para um saco ou adiciona uma peça nova — mesmo escopo
 * do protótipo web ("rascunho narrativo efêmero: nenhuma alteração é
 * persistida ou interpretada").
 */
public final class PecaPalavraRascunho {
    private final String id;
    private final String valor;
    private final String papelId;
    private final boolean incognita;
    private final TipoSegmentoNarrativo tipo;
    private final boolean manipulavel;
    /** "COMUM", "ORGANIZADORES" ou null — para onde esta peça vai se for removida da frase. */
    private final String sacoDestino;

    /** Geometria de tela, recalculada a cada quadro por GeometriaEditorNarrativa. */
    public int x;
    public int y;
    public int largura;
    public int altura;

    public PecaPalavraRascunho(String id, String valor, String papelId, boolean incognita,
            TipoSegmentoNarrativo tipo, boolean manipulavel, String sacoDestino) {
        this.id = id;
        this.valor = valor;
        this.papelId = papelId;
        this.incognita = incognita;
        this.tipo = tipo;
        this.manipulavel = manipulavel;
        this.sacoDestino = sacoDestino;
    }

    /** Portação de um token real do enunciado, na ordem em que aparece no texto. */
    public static PecaPalavraRascunho apartirDeSegmento(SegmentoTextoSemantico segmento, int indice) {
        String destino;
        if (segmento.getTipoNarrativo() == TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO) {
            destino = "ORGANIZADORES";
        } else if (segmento.getTipoNarrativo() == TipoSegmentoNarrativo.COMUM) {
            destino = "COMUM";
        } else {
            destino = null;
        }
        return new PecaPalavraRascunho("texto." + indice, segmento.getValor(),
                segmento.possuiVinculoSemantico() ? segmento.getChavePapelSemantico() : null,
                segmento.representaIncognitaOriginal(), segmento.getTipoNarrativo(),
                segmento.isManipulavelNaNarrativa(), destino);
    }

    /** Candidato a organizador da informação vindo do vocabulário (ex.: "agora"), pronto para arrastar para o texto. */
    public static PecaPalavraRascunho apartirDeOrganizadorCandidato(String expressao, int indice) {
        return new PecaPalavraRascunho("vocabulario.organizador." + indice, expressao, null, false,
                TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO, true, "ORGANIZADORES");
    }

    /** Palavra nova digitada pela usuária no saco de palavras comuns. */
    public static PecaPalavraRascunho apartirDePalavraComumDigitada(String valor, int sequencia) {
        return new PecaPalavraRascunho("rascunho.comum." + sequencia, valor, null, false,
                TipoSegmentoNarrativo.COMUM, true, "COMUM");
    }

    /** Cópia com novo id, usada ao inserir uma peça de um saco dentro da frase (o saco mantém a sua). */
    public PecaPalavraRascunho copiaParaFrase(int sequencia) {
        return new PecaPalavraRascunho("rascunho." + sequencia, valor, papelId, incognita, tipo,
                manipulavel, sacoDestino);
    }

    public String getId() { return id; }
    public String getValor() { return valor; }
    public String getPapelId() { return papelId; }
    public boolean isIncognita() { return incognita; }
    public TipoSegmentoNarrativo getTipo() { return tipo; }
    public boolean isManipulavel() { return manipulavel; }
    public String getSacoDestino() { return sacoDestino; }

    public void atualizarTamanho(java.awt.FontMetrics fm) {
        largura = fm.stringWidth(valor);
        altura = fm.getHeight() - 5;
    }

    public boolean contem(int mx, int my) {
        return mx >= x - 4 && mx <= x + largura + 4
                && my >= y - altura && my <= y + 6;
    }

    @Override
    public String toString() {
        return valor;
    }
}
