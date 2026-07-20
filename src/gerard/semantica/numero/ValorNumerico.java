package gerard.semantica.numero;

/** Valor matemático independente de qualquer representação visual. */
public interface ValorNumerico {
    DominioNumerico getDominio();
    boolean ehConhecido();
    Integer valorOuNull();
    String formatar(boolean explicitarSinalPositivo);
}
