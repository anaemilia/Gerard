package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import java.util.List;
import java.util.function.Function;

/**
 * Traduz uma janela de elementos do Vergnaud para a ordem de três papéis do
 * estado compartilhado. A leitura concreta do valor permanece no adaptador.
 */
public final class CapturadorValoresVergnaud {

    public ValoresCapturadosVergnaud capturar(
            List<ElementoVergnaud> elementos, int[] indicesReais,
            int indiceAlteradoReal,
            Function<ElementoVergnaud, Integer> leitorValor) {
        Integer[] valores = new Integer[] {null, null, null};
        boolean[] conhecidos = new boolean[] {false, false, false};
        int indiceAlteradoSemantico = -1;
        if (elementos == null || indicesReais == null || leitorValor == null) {
            return new ValoresCapturadosVergnaud(
                    valores, conhecidos, indiceAlteradoSemantico);
        }

        int total = Math.min(valores.length, indicesReais.length);
        for (int indiceSemantico = 0;
                indiceSemantico < total; indiceSemantico++) {
            int indiceReal = indicesReais[indiceSemantico];
            if (indiceReal == indiceAlteradoReal) {
                indiceAlteradoSemantico = indiceSemantico;
            }
            if (indiceReal < 0 || indiceReal >= elementos.size()) {
                continue;
            }
            Integer valor = leitorValor.apply(elementos.get(indiceReal));
            valores[indiceSemantico] = valor;
            conhecidos[indiceSemantico] = valor != null;
        }
        return new ValoresCapturadosVergnaud(
                valores, conhecidos, indiceAlteradoSemantico);
    }
}
