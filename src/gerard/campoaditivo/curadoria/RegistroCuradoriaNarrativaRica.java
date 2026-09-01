package gerard.campoaditivo.curadoria;

import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conteúdo narrativo explicitamente declarado pelo pesquisador para uma
 * situação tabular. Não deriva participantes, objetos ou papéis do texto.
 */
public final class RegistroCuradoriaNarrativaRica {
    private final String idSituacao;
    private final StatusCuradoriaSituacao statusCuradoria;
    private final NarrativaCurada narrativa;
    private final List<CorrespondenciaPapelNarrativa> correspondencias;

    public RegistroCuradoriaNarrativaRica(
            String idSituacao,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias) {
        this(idSituacao, StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                narrativa, correspondencias);
    }

    public RegistroCuradoriaNarrativaRica(
            String idSituacao,
            StatusCuradoriaSituacao statusCuradoria,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias) {
        String id = idSituacao == null ? "" : idSituacao.trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id da situação é obrigatório");
        }
        if (statusCuradoria == null || narrativa == null
                || correspondencias == null) {
            throw new IllegalArgumentException(
                    "status, narrativa e correspondências são obrigatórios");
        }
        this.idSituacao = id;
        this.statusCuradoria = statusCuradoria;
        this.narrativa = narrativa;
        this.correspondencias = Collections.unmodifiableList(
                new ArrayList<CorrespondenciaPapelNarrativa>(correspondencias));
    }

    public String getIdSituacao() { return idSituacao; }
    public StatusCuradoriaSituacao getStatusCuradoria() {
        return statusCuradoria;
    }
    public NarrativaCurada getNarrativa() { return narrativa; }
    public List<CorrespondenciaPapelNarrativa> getCorrespondencias() {
        return correspondencias;
    }
}
