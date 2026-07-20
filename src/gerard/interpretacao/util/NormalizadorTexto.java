package gerard.interpretacao.util;

import java.text.Normalizer;

public final class NormalizadorTexto {
    private NormalizadorTexto() {}

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }

        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return semAcentos.toLowerCase()
                .replace('\u2019', '\'')
                .replace('\u2018', '\'')
                .replace('\u201c', '"')
                .replace('\u201d', '"')
                .replaceAll("\\s+", " ")
                .trim();
    }
}
