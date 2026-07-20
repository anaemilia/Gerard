package gerard.idioma;

import java.util.Locale;

/** Idioma de uma versão linguística, restrito ao escopo latino do Gérard. */
public final class IdiomaSituacao {
    public static final String DIRECAO_PADRAO = "LTR";
    public static final String SCRIPT_PADRAO = "Latn";

    private final String codigo;
    private final String nome;

    public IdiomaSituacao(String codigo, String nome, String direcao) {
        this(codigo, nome, direcao, SCRIPT_PADRAO);
    }

    /**
     * Os parâmetros de direção e sistema de escrita são mantidos apenas para
     * compatibilidade com arquivos e chamadas antigas. No escopo atual, todos
     * os idiomas suportados usam escrita latina da esquerda para a direita.
     */
    public IdiomaSituacao(String codigo, String nome, String direcao, String script) {
        this.codigo = normalizarCodigo(codigo);
        this.nome = UnicodeTexto.normalizarNfc(nome == null || nome.trim().isEmpty() ? this.codigo : nome.trim());
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getDirecao() { return DIRECAO_PADRAO; }
    public String getScript() { return SCRIPT_PADRAO; }

    public String toString() { return nome + " (" + codigo + ")"; }

    public static String normalizarCodigo(String codigo) {
        String c = codigo == null ? "" : codigo.trim().replace('_', '-');
        if (c.isEmpty()) return "und";
        String[] partes = c.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < partes.length; i++) {
            if (i > 0) sb.append('-');
            String parte = partes[i];
            if (i == 0) sb.append(parte.toLowerCase(Locale.ROOT));
            else if (parte.length() == 2) sb.append(parte.toUpperCase(Locale.ROOT));
            else sb.append(parte);
        }
        return sb.toString();
    }

    public static IdiomaInterface paraIdiomaInterface(String codigo) {
        String c = normalizarCodigo(codigo).toLowerCase(Locale.ROOT);
        if (c.equals("pt") || c.startsWith("pt-") || c.equals("portugues")) return IdiomaInterface.PORTUGUES;
        if (c.equals("en") || c.startsWith("en-") || c.equals("ingles")) return IdiomaInterface.INGLES;
        if (c.equals("fr") || c.startsWith("fr-") || c.equals("frances")) return IdiomaInterface.FRANCES;
        if (c.equals("es") || c.startsWith("es-") || c.equals("espanhol") || c.equals("espanol")) return IdiomaInterface.ESPANHOL;
        try { return IdiomaInterface.valueOf(codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT)); }
        catch (Exception ex) { return null; }
    }

    public static String codigoPadrao(IdiomaInterface idioma) {
        if (idioma == IdiomaInterface.INGLES) return "en";
        if (idioma == IdiomaInterface.FRANCES) return "fr";
        if (idioma == IdiomaInterface.ESPANHOL) return "es";
        return "pt-BR";
    }
}
