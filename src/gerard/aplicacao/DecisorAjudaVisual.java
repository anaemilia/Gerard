package gerard.aplicacao;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.ajuda.ApoioVisual;
import gerard.dominio.campoaditivo.ajuda.PoliticaApoioVisual;
import java.util.Collections;
import java.util.List;

/**
 * Decisão única, no backend, do apoio visual que a situação em curso recebe. Desktop e web
 * consomem este mesmo resultado; nenhum cliente repete a regra nem escolhe entre apoios.
 * A regra mora no domínio ({@link PoliticaApoioVisual}); aqui só se reúnem os fatos da
 * situação (número relativo, id, enunciado) e da tentativa (escalada no limite).
 */
public final class DecisorAjudaVisual {

    private DecisorAjudaVisual() {
    }

    /** O apoio foi acionado para esta situação neste momento (independe do acervo). */
    public static boolean acionada(SituacaoProblemaAditiva situacao, boolean escaladaNoLimite) {
        return situacao != null && PoliticaApoioVisual.acionado(situacao.getTipo(),
                SemanticaCuradaSituacao.possuiNumeroRelativo(situacao), escaladaNoLimite);
    }

    public static List<ApoioVisual> decidir(SituacaoProblemaAditiva situacao,
            boolean escaladaNoLimite) {
        if (situacao == null) {
            return Collections.<ApoioVisual>emptyList();
        }
        return PoliticaApoioVisual.decidir(situacao.getTipo(),
                SemanticaCuradaSituacao.possuiNumeroRelativo(situacao), escaladaNoLimite,
                situacao.getId(), situacao.getEnunciado());
    }
}
