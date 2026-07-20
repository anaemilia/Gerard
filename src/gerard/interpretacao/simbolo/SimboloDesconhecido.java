package gerard.interpretacao.simbolo;

/**
 * Representação canônica do item desconhecido nos quatro idiomas suportados
 * pelo Gérard: português, inglês, francês e espanhol.
 */
public final class SimboloDesconhecido {
    public static final String CANONICO = "?";

    private SimboloDesconhecido() {
    }

    public static boolean eh(String valor) {
        return valor != null && CANONICO.equals(valor.trim());
    }

    public static String canonizar(String valor) {
        return eh(valor) ? CANONICO : valor;
    }

    public static String regexClasse() {
        return "[?]";
    }
}
