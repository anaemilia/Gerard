package gerard.semantica.quantidade;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolve a grandeza da situação. Metadados explícitos têm prioridade; pistas
 * linguísticas são apenas fallback para registros legados.
 */
public final class ResolvedorPerfilQuantidadeSituacao {
    private static final Pattern META_GRANDEZA = Pattern.compile(
            "(?:GRANDEZA|TIPO_GRANDEZA)\\s*[:=]\\s*(MONETARIA|DINHEIRO|CONTAGEM)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern META_MOEDA = Pattern.compile(
            "(?:MOEDA|UNIDADE)\\s*[:=]\\s*(BRL|USD|EUR)",
            Pattern.CASE_INSENSITIVE);

    public PerfilQuantidadeSituacao resolver(SituacaoProblemaAditiva situacao) {
        if (situacao == null) {
            return contagem("", false, "padrao_sem_situacao");
        }
        String metadados = juntar(situacao.getRepresentacaoVisual(),
                situacao.getObservacoes());
        Matcher grandeza = META_GRANDEZA.matcher(metadados);
        if (grandeza.find()) {
            String valor = grandeza.group(1).toUpperCase(Locale.ROOT);
            if ("MONETARIA".equals(valor) || "DINHEIRO".equals(valor)) {
                return monetaria(resolverMoeda(metadados, situacao), true,
                        "metadado_curado");
            }
            return contagem(situacao.getContexto(), true, "metadado_curado");
        }

        String texto = normalizar(juntar(situacao.getContexto(),
                situacao.getEnunciado()));
        if (ehMonetario(texto, situacao.getEnunciado())) {
            return monetaria(resolverMoeda(metadados, situacao), false,
                    "fallback_linguistico_legado");
        }
        return contagem(situacao.getContexto(), false,
                "fallback_contagem_legado");
    }

    private PerfilQuantidadeSituacao monetaria(String codigo,
            boolean explicita, String origem) {
        String moeda = codigo == null ? "BRL" : codigo;
        String simbolo = "EUR".equals(moeda) ? "€"
                : ("USD".equals(moeda) ? "US$" : "R$");
        String singular = "EUR".equals(moeda) ? "euro"
                : ("USD".equals(moeda) ? "dólar" : "real");
        String plural = "EUR".equals(moeda) ? "euros"
                : ("USD".equals(moeda) ? "dólares" : "reais");
        return new PerfilQuantidadeSituacao(new GrandezaMonetaria(),
                new UnidadeQuantitativa(moeda, singular, plural, simbolo),
                explicita, origem);
    }

    private PerfilQuantidadeSituacao contagem(String contexto,
            boolean explicita, String origem) {
        String plural = contexto == null || contexto.trim().length() == 0
                ? "elementos" : contexto.trim();
        String singular = plural.endsWith("s") && plural.length() > 1
                ? plural.substring(0, plural.length() - 1) : plural;
        return new PerfilQuantidadeSituacao(new GrandezaContagem(),
                new UnidadeQuantitativa("CONTAGEM", singular, plural, ""),
                explicita, origem);
    }

    private String resolverMoeda(String metadados,
            SituacaoProblemaAditiva situacao) {
        Matcher moeda = META_MOEDA.matcher(metadados == null ? "" : metadados);
        if (moeda.find()) {
            return moeda.group(1).toUpperCase(Locale.ROOT);
        }
        String original = juntar(situacao.getContexto(), situacao.getEnunciado());
        if (original.contains("€") || normalizar(original).contains(" euro")) {
            return "EUR";
        }
        if (original.contains("US$") || normalizar(original).contains(" dollar")
                || normalizar(original).contains(" dolar")) {
            return "USD";
        }
        return "BRL";
    }

    private boolean ehMonetario(String normalizado, String original) {
        String bruto = original == null ? "" : original;
        return bruto.contains("R$") || bruto.contains("US$") || bruto.contains("€")
                || contemPalavra(normalizado, "real")
                || contemPalavra(normalizado, "reais")
                || contemPalavra(normalizado, "dinheiro")
                || contemPalavra(normalizado, "centavo")
                || contemPalavra(normalizado, "centavos")
                || contemPalavra(normalizado, "dollar")
                || contemPalavra(normalizado, "dollars")
                || contemPalavra(normalizado, "dolar")
                || contemPalavra(normalizado, "dolares")
                || contemPalavra(normalizado, "euro")
                || contemPalavra(normalizado, "euros")
                || contemPalavra(normalizado, "money");
    }

    private boolean contemPalavra(String texto, String palavra) {
        return (" " + texto + " ").contains(" " + palavra + " ");
    }

    private String normalizar(String texto) {
        String valor = texto == null ? "" : texto;
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    private String juntar(String a, String b) {
        return (a == null ? "" : a) + " " + (b == null ? "" : b);
    }
}
