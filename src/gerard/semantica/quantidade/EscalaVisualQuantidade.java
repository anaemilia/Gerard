package gerard.semantica.quantidade;

import java.math.BigDecimal;

/** Escala comum usada para projetar quantidades em unidades concretas. */
public final class EscalaVisualQuantidade {
    private final BigDecimal valorPorUnidade;
    private final String legenda;

    public EscalaVisualQuantidade(BigDecimal valorPorUnidade, String legenda) {
        this.valorPorUnidade = valorPorUnidade == null
                || valorPorUnidade.signum() <= 0 ? BigDecimal.ONE
                : valorPorUnidade.stripTrailingZeros();
        this.legenda = legenda == null ? "" : legenda.trim();
    }

    public BigDecimal getValorPorUnidade() { return valorPorUnidade; }
    public String getLegenda() { return legenda; }

    public int converterExato(int valor) {
        BigDecimal quociente = new BigDecimal(Math.abs(valor))
                .divide(valorPorUnidade);
        return quociente.intValueExact();
    }
}
