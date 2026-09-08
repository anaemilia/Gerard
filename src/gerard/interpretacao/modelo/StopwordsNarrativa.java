package gerard.interpretacao.modelo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Palavras funcionais ignoradas como peças isoladas do editor narrativo. */
public final class StopwordsNarrativa {
    private static final Set<String> TERMOS = new HashSet<String>(Arrays.asList(
            "de", "da", "do", "das", "dos", "em", "na", "no", "nas", "nos",
            "com", "para", "por", "que", "qual", "quais", "quanto", "quantos",
            "quantas", "ele", "ela", "eles", "elas", "sua", "seu", "suas", "seus",
            "um", "uma", "e", "o", "a", "os", "as"));

    private StopwordsNarrativa() {
    }

    public static boolean contem(String texto) {
        String normalizado = VocabularioOrganizadoresInformacao.normalizar(texto);
        return normalizado.length() <= 1 || TERMOS.contains(normalizado);
    }
}
