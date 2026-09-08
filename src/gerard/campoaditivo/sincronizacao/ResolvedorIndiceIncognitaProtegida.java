package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.conclusao.PoliticaPreenchimentoIncognita;
import java.util.function.IntFunction;

/**
 * Resolve a posição semântica da incógnita dentro da janela de elementos que
 * alimenta o estado compartilhado. Não conhece componentes nem geometria.
 */
public final class ResolvedorIndiceIncognitaProtegida {

    private final PoliticaPreenchimentoIncognita politicaIncognita;

    public ResolvedorIndiceIncognitaProtegida(
            PoliticaPreenchimentoIncognita politicaIncognita) {
        if (politicaIncognita == null) {
            throw new IllegalArgumentException(
                    "politicaIncognita não pode ser nula");
        }
        this.politicaIncognita = politicaIncognita;
    }

    public int resolver(int[] indicesReais, int quantidadeElementos,
            IntFunction<String> papelPorIndiceReal, String papelIncognita) {
        if (indicesReais == null || papelPorIndiceReal == null
                || quantidadeElementos <= 0) {
            return -1;
        }
        for (int indiceSemantico = 0;
                indiceSemantico < indicesReais.length; indiceSemantico++) {
            int indiceReal = indicesReais[indiceSemantico];
            if (indiceReal < 0 || indiceReal >= quantidadeElementos) {
                continue;
            }
            if (politicaIncognita.ehPapelDaIncognita(
                    papelPorIndiceReal.apply(indiceReal), papelIncognita)) {
                return indiceSemantico;
            }
        }
        return -1;
    }
}
