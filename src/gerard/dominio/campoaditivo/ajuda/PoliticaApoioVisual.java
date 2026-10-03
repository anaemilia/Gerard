package gerard.dominio.campoaditivo.ajuda;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Política única do apoio visual da escalada no limite (decisão de 2026-10-02): onde há número
 * relativo ou transformação, há historinha com o texto da situação-problema. Se existir
 * ilustração cadastrada para a própria situação, ela é o apoio; senão, a historinha é o
 * texto da situação. Sem tecnologia de apresentação.
 */
public final class PoliticaApoioVisual {

    private PoliticaApoioVisual() {
    }

    public static boolean acionado(TipoSituacaoAditiva tipo, boolean possuiNumeroRelativo,
            boolean escaladaNoLimite) {
        return tipo != null && escaladaNoLimite
                && (possuiNumeroRelativo || tipo.envolveTransformacao());
    }

    public static List<ApoioVisual> decidir(TipoSituacaoAditiva tipo, boolean possuiNumeroRelativo,
            boolean escaladaNoLimite, String idSituacaoCurada, String enunciado) {
        if (!acionado(tipo, possuiNumeroRelativo, escaladaNoLimite)) {
            return Collections.<ApoioVisual>emptyList();
        }
        List<ApoioVisual> apoios = new ArrayList<ApoioVisual>();
        for (HistorinhaAjudaVisual historinha : tipo.selecionarRepertorioAjudaVisual().getHistorinhas()) {
            if (historinha.getIdSituacaoCurada().equals(idSituacaoCurada)) {
                apoios.add(new ApoioHistorinhaIlustrada(historinha));
                return apoios;
            }
        }
        if (enunciado != null && !enunciado.trim().isEmpty()) {
            apoios.add(new ApoioHistorinhaTextual(idSituacaoCurada, enunciado));
        }
        return apoios;
    }
}
