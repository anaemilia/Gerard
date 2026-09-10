package gerard.campoaditivo.diagrama.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vocabulário de apoio à edição do enunciado: candidatos a organizador da
 * informação (arrastáveis para dentro do texto a partir de um "saco"), mesmo
 * repertório que {@code ServicoSorteioAtividadeWeb} publica em
 * "vocabulario_texto" para o protótipo web — agora também disponível ao
 * desktop através do gerador de cena (ver
 * {@code GeradorCenaDiagramaAditivo.gerarVocabularioTextoNarrativa}), em vez
 * de cada lado (desktop/web) manter sua própria cópia.
 */
public final class VocabularioTextoNarrativo {
    private final List<String> candidatosOrganizadoresInformacao;

    public VocabularioTextoNarrativo(List<String> candidatosOrganizadoresInformacao) {
        this.candidatosOrganizadoresInformacao = Collections.unmodifiableList(
                new ArrayList<String>(candidatosOrganizadoresInformacao));
    }

    /** Expressões candidatas (ex.: "agora", "antes", "depois") — estar aqui não atribui a função automaticamente. */
    public List<String> getCandidatosOrganizadoresInformacao() {
        return candidatosOrganizadoresInformacao;
    }
}
