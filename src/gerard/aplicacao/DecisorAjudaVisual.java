package gerard.aplicacao;

import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;

/**
 * Decisão única, no backend, de qual ajuda visual (historinha) a situação em curso recebe.
 * Desktop e web consomem este mesmo resultado; nenhum cliente repete a regra nem escolhe
 * entre as narrativas. A regra mora nos objetos ricos (categoria, repertório); aqui só se
 * reúnem os fatos da situação e da tentativa.
 */
public final class DecisorAjudaVisual {

    private DecisorAjudaVisual() {
    }

    /** A ajuda visual foi acionada para esta situação neste momento (independe do acervo). */
    public static boolean acionada(SituacaoProblemaAditiva situacao, boolean escaladaNoLimite) {
        return situacao != null && situacao.getTipo() != null
                && situacao.getTipo().deveAcionarHistorinha(
                        SemanticaCuradaSituacao.possuiNumeroRelativo(situacao), escaladaNoLimite);
    }

    /**
     * Repertório a mostrar: vazio se não acionada; a narrativa da própria situação, se
     * existir; senão as da categoria.
     */
    public static RepertorioAjudaVisual decidir(SituacaoProblemaAditiva situacao,
            boolean escaladaNoLimite) {
        if (situacao == null || situacao.getTipo() == null) {
            return RepertorioAjudaVisual.vazio();
        }
        return situacao.getTipo().selecionarRepertorioAjudaVisual(
                SemanticaCuradaSituacao.possuiNumeroRelativo(situacao),
                escaladaNoLimite, situacao.getId());
    }
}
