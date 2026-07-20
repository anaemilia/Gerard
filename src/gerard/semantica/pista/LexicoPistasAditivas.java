package gerard.semantica.pista;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Léxico configurável em português, inglês e francês. */
public final class LexicoPistasAditivas {
    private final List<PistaLinguistica> pistas = new ArrayList<PistaLinguistica>();

    public LexicoPistasAditivas() {
        registrarPadrao();
    }

    public void registrar(PistaLinguistica pista) {
        if (pista != null && pista.getExpressao().length() > 0) {
            pistas.add(pista);
        }
    }

    public List<OcorrenciaPista> localizar(String texto, String idioma) {
        String original = texto == null ? "" : texto;
        String normalizado = original.toLowerCase(Locale.ROOT);
        String codigo = idioma == null ? "" : idioma.trim().toLowerCase(Locale.ROOT);
        List<OcorrenciaPista> resultado = new ArrayList<OcorrenciaPista>();
        for (PistaLinguistica pista : pistas) {
            if (codigo.length() > 0 && !codigo.equals(pista.getIdioma())) {
                continue;
            }
            int inicio = normalizado.indexOf(pista.getExpressao());
            while (inicio >= 0) {
                int fim = inicio + pista.getExpressao().length();
                resultado.add(new OcorrenciaPista(
                        pista, inicio, fim, original.substring(inicio, fim)));
                inicio = normalizado.indexOf(pista.getExpressao(), fim);
            }
        }
        return Collections.unmodifiableList(resultado);
    }

    private void registrarPadrao() {
        registrar(new PistaLinguistica("pt", "ontem", TipoPistaLinguistica.TEMPORAL_INICIAL));
        registrar(new PistaLinguistica("pt", "antes", TipoPistaLinguistica.TEMPORAL_INICIAL));
        registrar(new PistaLinguistica("pt", "hoje", TipoPistaLinguistica.TEMPORAL_FINAL));
        registrar(new PistaLinguistica("pt", "depois", TipoPistaLinguistica.TEMPORAL_FINAL));
        registrar(new PistaLinguistica("pt", "ganhou", TipoPistaLinguistica.TRANSFORMACAO_POSITIVA));
        registrar(new PistaLinguistica("pt", "recebeu", TipoPistaLinguistica.TRANSFORMACAO_POSITIVA));
        registrar(new PistaLinguistica("pt", "perdeu", TipoPistaLinguistica.TRANSFORMACAO_NEGATIVA));
        registrar(new PistaLinguistica("pt", "gastou", TipoPistaLinguistica.TRANSFORMACAO_NEGATIVA));
        registrar(new PistaLinguistica("pt", "juntos", TipoPistaLinguistica.COMPOSICAO));
        registrar(new PistaLinguistica("pt", "ao todo", TipoPistaLinguistica.TOTALIZACAO));
        registrar(new PistaLinguistica("pt", "a mais que", TipoPistaLinguistica.COMPARACAO_SUPERIOR));
        registrar(new PistaLinguistica("pt", "a menos que", TipoPistaLinguistica.COMPARACAO_INFERIOR));

        registrar(new PistaLinguistica("en", "before", TipoPistaLinguistica.TEMPORAL_INICIAL));
        registrar(new PistaLinguistica("en", "after", TipoPistaLinguistica.TEMPORAL_FINAL));
        registrar(new PistaLinguistica("en", "gained", TipoPistaLinguistica.TRANSFORMACAO_POSITIVA));
        registrar(new PistaLinguistica("en", "lost", TipoPistaLinguistica.TRANSFORMACAO_NEGATIVA));
        registrar(new PistaLinguistica("en", "altogether", TipoPistaLinguistica.TOTALIZACAO));
        registrar(new PistaLinguistica("en", "more than", TipoPistaLinguistica.COMPARACAO_SUPERIOR));
        registrar(new PistaLinguistica("en", "less than", TipoPistaLinguistica.COMPARACAO_INFERIOR));

        registrar(new PistaLinguistica("fr", "avant", TipoPistaLinguistica.TEMPORAL_INICIAL));
        registrar(new PistaLinguistica("fr", "après", TipoPistaLinguistica.TEMPORAL_FINAL));
        registrar(new PistaLinguistica("fr", "a gagné", TipoPistaLinguistica.TRANSFORMACAO_POSITIVA));
        registrar(new PistaLinguistica("fr", "a perdu", TipoPistaLinguistica.TRANSFORMACAO_NEGATIVA));
        registrar(new PistaLinguistica("fr", "en tout", TipoPistaLinguistica.TOTALIZACAO));
        registrar(new PistaLinguistica("fr", "de plus que", TipoPistaLinguistica.COMPARACAO_SUPERIOR));
        registrar(new PistaLinguistica("fr", "de moins que", TipoPistaLinguistica.COMPARACAO_INFERIOR));
    }
}
