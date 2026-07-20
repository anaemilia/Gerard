package gerard.campoaditivo.venn.mapeamento;

/**
 * Herança compartilhada para mapeamentos de três papéis. A subclasse declara
 * apenas a correspondência visual -> semântica; a correspondência inversa é
 * obtida de forma uniforme e validada em um único ponto.
 */
public abstract class MapeamentoPapeisRepresentacaoComplementarAbstrato
        implements MapeamentoPapeisRepresentacaoComplementar {

    private final int[] visualParaSemantico;
    private final int[] semanticoParaVisual;

    protected MapeamentoPapeisRepresentacaoComplementarAbstrato(
            int primeiro, int segundo, int terceiro) {
        visualParaSemantico = new int[] { primeiro, segundo, terceiro };
        semanticoParaVisual = new int[] { -1, -1, -1 };
        for (int visual = 0; visual < visualParaSemantico.length; visual++) {
            int semantico = visualParaSemantico[visual];
            if (semantico < 0 || semantico >= semanticoParaVisual.length
                    || semanticoParaVisual[semantico] >= 0) {
                throw new IllegalArgumentException(
                        "O mapeamento deve ser uma permutação dos índices 0, 1 e 2.");
            }
            semanticoParaVisual[semantico] = visual;
        }
    }

    public final int paraIndiceSemantico(int indiceVisual) {
        return indiceVisual >= 0 && indiceVisual < visualParaSemantico.length
                ? visualParaSemantico[indiceVisual] : -1;
    }

    public final int paraIndiceVisual(int indiceSemantico) {
        return indiceSemantico >= 0 && indiceSemantico < semanticoParaVisual.length
                ? semanticoParaVisual[indiceSemantico] : -1;
    }
}
