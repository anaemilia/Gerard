package gerard.ui.vergnaud;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Planeja a projeção do snapshot nos elementos visuais do Vergnaud.
 * Não altera componentes e não decide regras do domínio.
 */
public final class PlanejadorAplicacaoEstadoVergnaud {

    public List<AtualizacaoElementoVergnaud> planejar(
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            int[] indicesElementos,
            List<ElementoVergnaud> elementos) {
        if (snapshot == null || indicesElementos == null
                || elementos == null || elementos.size() < 3) {
            return Collections.emptyList();
        }
        List<AtualizacaoElementoVergnaud> atualizacoes =
                new ArrayList<AtualizacaoElementoVergnaud>();
        for (int indiceSemantico = 0;
                indiceSemantico < 3 && indiceSemantico < indicesElementos.length;
                indiceSemantico++) {
            if (!snapshot.isConhecido(indiceSemantico)) {
                continue;
            }
            int indiceReal = indicesElementos[indiceSemantico];
            if (indiceReal < 0 || indiceReal >= elementos.size()) {
                continue;
            }
            ElementoVergnaud elemento = elementos.get(indiceReal);
            AtualizacaoElementoVergnaud.NaturezaVisual natureza =
                    elemento != null && elemento.tipo == TipoFiguraDiagrama.ELIPSE
                    ? AtualizacaoElementoVergnaud.NaturezaVisual.NUMERO_RELATIVO
                    : AtualizacaoElementoVergnaud.NaturezaVisual.MEDIDA;
            atualizacoes.add(new AtualizacaoElementoVergnaud(
                    indiceReal, snapshot.valorOuZero(indiceSemantico), natureza));
        }
        return Collections.unmodifiableList(atualizacoes);
    }
}
