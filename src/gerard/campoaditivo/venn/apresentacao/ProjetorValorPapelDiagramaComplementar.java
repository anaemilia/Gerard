package gerard.campoaditivo.venn.apresentacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Projeta um valor semântico para a sintaxe textual mínima usada por uma
 * representação complementar. Não conhece Swing, SVG, coordenadas, rótulos
 * nem a posição visual do papel.
 */
public final class ProjetorValorPapelDiagramaComplementar {

    public String projetar(EstadoSemanticoCompartilhado.Snapshot estado,
            TipoSituacaoAditiva tipoEsperado, int indiceSemantico,
            int valorVisualDeContingencia) {
        if (estado == null || estado.getTipo() != tipoEsperado
                || indiceSemantico < 0) {
            return String.valueOf(valorVisualDeContingencia);
        }
        if (!estado.isConhecido(indiceSemantico)) {
            return "?";
        }
        return String.valueOf(estado.getValor(indiceSemantico));
    }
}
