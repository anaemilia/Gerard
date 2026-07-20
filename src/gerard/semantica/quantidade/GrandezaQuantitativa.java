package gerard.semantica.quantidade;

import java.math.BigDecimal;

/** Regras próprias da grandeza, sem duplicar a regra de sinal dos papéis. */
public interface GrandezaQuantitativa {
    TipoGrandezaQuantitativa getTipo();
    boolean permiteFracao();
    int getCasasDecimaisMaximas();
    BigDecimal getPassoPadrao();
    boolean aceitaMagnitude(BigDecimal valor);
}
