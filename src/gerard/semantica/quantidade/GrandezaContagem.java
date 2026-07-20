package gerard.semantica.quantidade;

import java.math.BigDecimal;

/** Quantidades discretas: bolas, carrinhos, figurinhas e demais elementos. */
public final class GrandezaContagem implements GrandezaQuantitativa {
    public TipoGrandezaQuantitativa getTipo() {
        return TipoGrandezaQuantitativa.CONTAGEM;
    }

    public boolean permiteFracao() { return false; }
    public int getCasasDecimaisMaximas() { return 0; }
    public BigDecimal getPassoPadrao() { return BigDecimal.ONE; }

    public boolean aceitaMagnitude(BigDecimal valor) {
        return valor != null && valor.stripTrailingZeros().scale() <= 0;
    }
}
