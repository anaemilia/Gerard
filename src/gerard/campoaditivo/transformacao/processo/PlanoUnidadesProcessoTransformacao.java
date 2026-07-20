package gerard.campoaditivo.transformacao.processo;

import gerard.semantica.quantidade.TipoGrandezaQuantitativa;
import java.math.BigDecimal;

/** Plano imutável das magnitudes concretizadas por quadradinhos. */
public final class PlanoUnidadesProcessoTransformacao {
    private final int[] quantidades;
    private final boolean[] conhecidos;
    private final String[] origens;
    private final TipoGrandezaQuantitativa tipoGrandeza;
    private final BigDecimal valorPorUnidadeVisual;
    private final String legendaEscala;
    private final String[] valoresFormatados;

    public PlanoUnidadesProcessoTransformacao(int[] quantidades,
            boolean[] conhecidos, String[] origens) {
        this(quantidades, conhecidos, origens,
                TipoGrandezaQuantitativa.CONTAGEM, BigDecimal.ONE, "", null);
    }

    public PlanoUnidadesProcessoTransformacao(int[] quantidades,
            boolean[] conhecidos, String[] origens,
            TipoGrandezaQuantitativa tipoGrandeza,
            BigDecimal valorPorUnidadeVisual, String legendaEscala,
            String[] valoresFormatados) {
        this.quantidades = quantidades == null
                ? new int[] {0, 0, 0} : quantidades.clone();
        this.conhecidos = conhecidos == null
                ? new boolean[] {false, false, false} : conhecidos.clone();
        this.origens = origens == null
                ? new String[] {"situacao_problema", "situacao_problema",
                    "situacao_problema"} : origens.clone();
        this.tipoGrandeza = tipoGrandeza == null
                ? TipoGrandezaQuantitativa.CONTAGEM : tipoGrandeza;
        this.valorPorUnidadeVisual = valorPorUnidadeVisual == null
                ? BigDecimal.ONE : valorPorUnidadeVisual;
        this.legendaEscala = legendaEscala == null ? "" : legendaEscala.trim();
        this.valoresFormatados = valoresFormatados == null
                ? new String[] {"", "", ""} : valoresFormatados.clone();
    }

    public int getQuantidade(int indice) {
        return indice >= 0 && indice < quantidades.length
                ? quantidades[indice] : 0;
    }
    public boolean isConhecido(int indice) {
        return indice >= 0 && indice < conhecidos.length && conhecidos[indice];
    }
    public String getOrigem(int indice) {
        return indice >= 0 && indice < origens.length
                ? origens[indice] : "situacao_problema";
    }
    public TipoGrandezaQuantitativa getTipoGrandeza() { return tipoGrandeza; }
    public BigDecimal getValorPorUnidadeVisual() { return valorPorUnidadeVisual; }
    public String getLegendaEscala() { return legendaEscala; }
    public boolean possuiEscalaAgrupada() { return legendaEscala.length() > 0; }
    public String getValorFormatado(int indice) {
        return indice >= 0 && indice < valoresFormatados.length
                ? valoresFormatados[indice] : "";
    }

    /** Converte a manipulação concreta novamente para o valor semântico. */
    public int converterUnidadesParaValor(int unidades) {
        try {
            return valorPorUnidadeVisual
                    .multiply(new BigDecimal(unidades)).intValueExact();
        } catch (ArithmeticException ex) {
            return unidades;
        }
    }
}
