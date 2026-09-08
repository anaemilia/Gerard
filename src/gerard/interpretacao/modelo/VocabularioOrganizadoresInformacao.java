package gerard.interpretacao.modelo;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Candidatos abertos a organizadores da informação.
 *
 * A fonte fornecida pela usuária exemplifica somente agora, antes e depois.
 * Estar neste repertório não atribui automaticamente essa função à ocorrência.
 */
public final class VocabularioOrganizadoresInformacao {
    private static final List<String> EXPRESSOES = ordenarPorMaiorExpressao(
            Arrays.asList("agora", "antes", "depois"));

    private VocabularioOrganizadoresInformacao() {
    }

    public static List<String> expressoes() {
        return EXPRESSOES;
    }

    public static String normalizar(String texto) {
        String semAcentos = Normalizer.normalize(texto == null ? "" : texto,
                Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcentos.toLowerCase(Locale.ROOT)
                .replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "")
                .replaceAll("\\s+", " ").trim();
    }

    private static List<String> ordenarPorMaiorExpressao(List<String> fonte) {
        java.util.ArrayList<String> copia = new java.util.ArrayList<String>(fonte);
        Collections.sort(copia, Comparator.comparingInt(String::length).reversed());
        return Collections.unmodifiableList(copia);
    }
}
