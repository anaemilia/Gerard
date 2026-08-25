package gerard.adaptacao;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Vocabulário operacional fechado das seis ajudas atualmente implementadas. */
public final class CodigosAjudaAdaptativa {

    private static final Set<String> CODIGOS = Collections.unmodifiableSet(
            new LinkedHashSet<String>(Arrays.asList(
                    "AG_EMLQ",
                    "AG_EMS",
                    "AG_EME",
                    "AG_EMCME",
                    "AG_AC",
                    "AG_AE")));

    private CodigosAjudaAdaptativa() { }

    public static String validar(String codigo) {
        String normalizado = codigo == null ? "" : codigo.trim();
        if (!CODIGOS.contains(normalizado)) {
            throw new IllegalArgumentException(
                    "código de ajuda fora do repertório operacional: " + normalizado);
        }
        return normalizado;
    }

    public static Set<String> todos() {
        return CODIGOS;
    }
}
