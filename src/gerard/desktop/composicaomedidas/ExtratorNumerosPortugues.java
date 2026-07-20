package gerard.desktop.composicaomedidas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai, em ordem de aparição, os números presentes num enunciado em
 * português — sejam dígitos ("12") ou por extenso ("seis", "vinte e
 * cinco") — além da interrogação final da pergunta ("?").
 *
 * O log curado (situacoes_vergnaud.tsv) ainda não tem as colunas
 * quantidade_1/quantidade_2/resultado preenchidas para estes registros;
 * esta classe supre essa lacuna extraindo os valores diretamente do
 * texto do enunciado, como pedido nas instruções.
 */
public final class ExtratorNumerosPortugues {

    private ExtratorNumerosPortugues() {
    }

    private static final Map<String, Integer> UNIDADES = new HashMap<String, Integer>();
    private static final Map<String, Integer> DEZ_A_DEZENOVE = new HashMap<String, Integer>();
    private static final Map<String, Integer> DEZENAS = new HashMap<String, Integer>();

    static {
        String[] unidades = {"zero", "um", "uma", "dois", "duas", "três", "tres",
                "quatro", "cinco", "seis", "sete", "oito", "nove"};
        int[] valoresUnidades = {0, 1, 1, 2, 2, 3, 3, 4, 5, 6, 7, 8, 9};
        for (int i = 0; i < unidades.length; i++) {
            UNIDADES.put(unidades[i], valoresUnidades[i]);
        }

        String[] dezANove = {"dez", "onze", "doze", "treze", "catorze", "quatorze",
                "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"};
        int[] valoresDez = {10, 11, 12, 13, 14, 14, 15, 16, 17, 18, 19};
        for (int i = 0; i < dezANove.length; i++) {
            DEZ_A_DEZENOVE.put(dezANove[i], valoresDez[i]);
        }

        String[] dezenas = {"vinte", "trinta", "quarenta", "cinquenta",
                "sessenta", "setenta", "oitenta", "noventa"};
        int[] valoresDezenas = {20, 30, 40, 50, 60, 70, 80, 90};
        for (int i = 0; i < dezenas.length; i++) {
            DEZENAS.put(dezenas[i], valoresDezenas[i]);
        }
    }

    // dígitos, ou "vinte e cinco" / "dezenove" / "sete", ou o "?"
    private static final Pattern PADRAO = Pattern.compile(
            "\\d+"
                    + "|(?i:" + juntarPalavras(DEZENAS.keySet()) + ")(\\s+e\\s+(?i:" + juntarPalavras(UNIDADES.keySet()) + "))?"
                    + "|(?i:" + juntarPalavras(DEZ_A_DEZENOVE.keySet()) + ")"
                    + "|(?i:" + juntarPalavras(UNIDADES.keySet()) + ")"
                    + "|\\?");

    private static String juntarPalavras(Iterable<String> palavras) {
        StringBuilder sb = new StringBuilder();
        for (String p : palavras) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(p);
        }
        return sb.toString();
    }

    public static List<NumeroTextoExtraido> extrair(String enunciado) {
        List<NumeroTextoExtraido> encontrados = new ArrayList<NumeroTextoExtraido>();
        if (enunciado == null) {
            return encontrados;
        }
        Matcher m = PADRAO.matcher(enunciado);
        while (m.find()) {
            String trecho = m.group();
            Integer valor = interpretar(trecho);
            if ("?".equals(trecho)) {
                encontrados.add(new NumeroTextoExtraido("?", null, m.start(), m.end()));
            } else if (valor != null) {
                encontrados.add(new NumeroTextoExtraido(trecho, valor, m.start(), m.end()));
            }
        }
        return encontrados;
    }

    private static Integer interpretar(String trecho) {
        if (trecho.matches("\\d+")) {
            try {
                return Integer.valueOf(trecho);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        String normalizado = trecho.toLowerCase();
        if (normalizado.contains(" e ")) {
            String[] partes = normalizado.split("\\s+e\\s+");
            Integer dezena = DEZENAS.get(partes[0]);
            Integer unidade = partes.length > 1 ? UNIDADES.get(partes[1]) : null;
            if (dezena != null) {
                return dezena + (unidade != null ? unidade : 0);
            }
        }
        if (DEZENAS.containsKey(normalizado)) {
            return DEZENAS.get(normalizado);
        }
        if (DEZ_A_DEZENOVE.containsKey(normalizado)) {
            return DEZ_A_DEZENOVE.get(normalizado);
        }
        if (UNIDADES.containsKey(normalizado)) {
            return UNIDADES.get(normalizado);
        }
        return null;
    }
}
