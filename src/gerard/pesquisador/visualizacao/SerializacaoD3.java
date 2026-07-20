package gerard.pesquisador.visualizacao;

/**
 * Funções puras de escape e serialização usadas ao montar o HTML/JSON das
 * visualizações D3 da tela do pesquisador (legenda de cores, JSON do log
 * bruto, etc.).
 *
 * Extraído de TelaVisaoPesquisador como parte da Fase 7 do plano de
 * refatoração — ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md.
 *
 * Métodos originais em TelaVisaoPesquisador mantidos como wrappers de 1
 * linha; nenhum call site foi alterado.
 */
public final class SerializacaoD3 {

    private SerializacaoD3() {
    }

    /** Converte um valor arbitrário em um número JSON (0 se não for interpretável como inteiro). */
    public static String numeroJson(Object valor) {
        if (valor == null) {
            return "0";
        }
        if (valor instanceof Number) {
            return String.valueOf(((Number) valor).intValue());
        }
        try {
            return String.valueOf(Integer.parseInt(valor.toString().trim()));
        } catch (Exception ex) {
            return "0";
        }
    }

    /** Serializa uma string como literal JSON entre aspas, escapando os caracteres especiais. */
    public static String jsonString(String valor) {
        if (valor == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            if (c == '"') { sb.append("\\\""); }
            else if (c == '\\') { sb.append("\\\\"); }
            else if (c == '\n') { sb.append("\\n"); }
            else if (c == '\r') { sb.append("\\r"); }
            else if (c == '\t') { sb.append("\\t"); }
            else if (c < 32) { sb.append(' '); }
            else { sb.append(c); }
        }
        sb.append('"');
        return sb.toString();
    }

    /** Escapa um texto para uso seguro dentro de HTML (&, <, >, "). */
    public static String htmlEscape(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /**
     * Monta um item da legenda de cores do D3 (bolinha colorida + rótulo),
     * sanitizando o tipo (só letras/números/traço/underscore) e a cor
     * (só #RRGGBB), com fallback cinza-neutro para cor inválida/vazia.
     */
    public static String itemLegendaCorD3(String tipo, String cor, String texto) {
        String tipoSeguro = tipo == null ? "" : tipo.replaceAll("[^A-Za-z0-9_-]", "");
        String corSegura = cor == null ? "#9aa5b1" : cor.replaceAll("[^#A-Fa-f0-9]", "");
        if (corSegura.length() == 0) {
            corSegura = "#9aa5b1";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<span class=\"legend-item\" data-type=\"");
        sb.append(htmlEscape(tipoSeguro));
        sb.append("\"><span class=\"legend-square\" style=\"background:");
        sb.append(htmlEscape(corSegura));
        sb.append("\"></span>");
        sb.append(htmlEscape(texto));
        sb.append("</span>");
        return sb.toString();
    }
}
