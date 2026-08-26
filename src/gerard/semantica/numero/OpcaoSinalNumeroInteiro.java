package gerard.semantica.numero;

/**
 * As duas opções apresentadas para representar o sinal de um número inteiro.
 *
 * <p>MAIS inclui o zero por compatibilidade com a representação binária já
 * usada pelo Gérard. Isso não classifica zero como número positivo; apenas
 * explicita qual das duas opções gráficas representa valores não negativos.</p>
 */
public enum OpcaoSinalNumeroInteiro {
    MAIS("+"),
    MENOS("-");

    private final String simbolo;

    OpcaoSinalNumeroInteiro(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public static OpcaoSinalNumeroInteiro doSimbolo(String simbolo) {
        if ("+".equals(simbolo)) {
            return MAIS;
        }
        if ("-".equals(simbolo)) {
            return MENOS;
        }
        throw new IllegalArgumentException("sinal deve ser + ou -");
    }
}
