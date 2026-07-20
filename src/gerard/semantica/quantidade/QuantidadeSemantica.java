package gerard.semantica.quantidade;

import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.papel.PapelQuantitativo;
import java.math.BigDecimal;

/** Valor, grandeza, unidade e papel reunidos em um único objeto semântico. */
public final class QuantidadeSemantica {
    private final BigDecimal valor;
    private final PerfilQuantidadeSituacao perfil;
    private final PapelQuantitativo papel;

    public QuantidadeSemantica(BigDecimal valor,
            PerfilQuantidadeSituacao perfil, PapelQuantitativo papel) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor quantitativo ausente.");
        }
        this.perfil = perfil == null
                ? new ResolvedorPerfilQuantidadeSituacao().resolver(null) : perfil;
        this.papel = papel;
        if (!this.perfil.getGrandeza().aceitaMagnitude(valor)) {
            throw new IllegalArgumentException(
                    "Magnitude incompatível com a grandeza quantitativa.");
        }
        if (papel != null && papel.getDominio() == DominioNumerico.NATURAIS
                && valor.signum() < 0) {
            throw new IllegalArgumentException(
                    "O papel quantitativo natural não aceita valor negativo.");
        }
        this.valor = valor.stripTrailingZeros();
    }

    public BigDecimal getValor() { return valor; }
    public PerfilQuantidadeSituacao getPerfil() { return perfil; }
    public PapelQuantitativo getPapel() { return papel; }
    public boolean ehMonetaria() {
        return perfil.getTipo() == TipoGrandezaQuantitativa.MONETARIA;
    }
}
