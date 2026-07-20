package gerard.semantica.quantidade;

import java.math.BigDecimal;

/** Quantidades monetárias, preservadas em decimal exato. */
public final class GrandezaMonetaria implements GrandezaQuantitativa {
    public TipoGrandezaQuantitativa getTipo() {
        return TipoGrandezaQuantitativa.MONETARIA;
    }

    public boolean permiteFracao() { return true; }
    public int getCasasDecimaisMaximas() { return 2; }
    public BigDecimal getPassoPadrao() { return new BigDecimal("0.01"); }

    public boolean aceitaMagnitude(BigDecimal valor) {
        if (valor == null) {
            return false;
        }
        return Math.max(0, valor.stripTrailingZeros().scale())
                <= getCasasDecimaisMaximas();
    }
}
