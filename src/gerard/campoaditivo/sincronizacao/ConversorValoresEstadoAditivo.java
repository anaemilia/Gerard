package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.PoliticaValoresAditivos;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.ValorNumerico;

/**
 * Converte valores brutos para o domínio numérico do papel correspondente no
 * estado compartilhado. Não armazena valores nem decide propagação.
 */
public final class ConversorValoresEstadoAditivo {
    private final PoliticaValoresAditivos politicaValores;
    private final CatalogoEsquemasCategoriasAditivas esquemas;
    private final FabricaValoresNumericos fabricaValores;

    public ConversorValoresEstadoAditivo() {
        this.politicaValores = new PoliticaValoresAditivos();
        this.esquemas = new CatalogoEsquemasCategoriasAditivas();
        this.fabricaValores = new FabricaValoresNumericos();
    }

    public ValorNumerico desconhecido(TipoSituacaoAditiva tipo, int indice) {
        return fabricaValores.desconhecido(dominioDoIndice(tipo, indice));
    }

    /** Entrada inválida de uma representação é mantida como desconhecida. */
    public ValorNumerico normalizarEntrada(TipoSituacaoAditiva tipo, int indice,
            Integer valorBruto, boolean conhecido) {
        boolean conhecimentoValido = conhecido
                && politicaValores.valorEhValidoNoEstadoCompartilhado(
                        tipo, indice, valorBruto);
        try {
            return fabricaValores.criar(dominioDoIndice(tipo, indice),
                    valorBruto, conhecimentoValido);
        } catch (IllegalArgumentException valorIncompativel) {
            return desconhecido(tipo, indice);
        }
    }

    /**
     * Resultado automático inválido não deve substituir o estado anterior.
     * Retorna null para que o chamador preserve o valor existente.
     */
    public ValorNumerico criarCalculadoOuNull(TipoSituacaoAditiva tipo,
            int indice, int valorCalculado) {
        try {
            return fabricaValores.conhecido(
                    dominioDoIndice(tipo, indice), valorCalculado);
        } catch (IllegalArgumentException valorIncompativel) {
            return null;
        }
    }

    private DominioNumerico dominioDoIndice(TipoSituacaoAditiva tipo,
            int indice) {
        return esquemas.obter(tipo).obterDominioCompartilhado(indice);
    }
}
