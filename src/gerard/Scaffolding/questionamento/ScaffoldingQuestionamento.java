package gerard.Scaffolding.questionamento;

import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.papel.DescritorPapelQuantitativo;

/**
 * Scaffolding responsável por questionar posicionamentos incompatíveis entre a
 * interpretação linguística do numeral/interrogação e o papel semântico do
 * elemento do diagrama.
 */
public class ScaffoldingQuestionamento {
    private final CatalogoPapeisSemanticosAditivos catalogoPapeis =
            new CatalogoPapeisSemanticosAditivos();


    public ResultadoQuestionamento avaliarPosicionamento(
            String chavePapelNumeral,
            String chavePapelAlvo,
            String papelDoElementoNoDiagrama,
            String categoriaEscolhida
    ) {
        if (!papelValido(chavePapelNumeral) || !papelValido(chavePapelAlvo)) {
            return ResultadoQuestionamento.naoAplicavel();
        }

        if (papeisCompativeis(chavePapelNumeral, chavePapelAlvo)) {
            return ResultadoQuestionamento.correto(chavePapelNumeral, chavePapelAlvo);
        }

        return ResultadoQuestionamento.incorreto(
                chavePapelNumeral,
                chavePapelAlvo,
                criarPerguntaConfirmacao(papelDoElementoNoDiagrama, categoriaEscolhida)
        );
    }

    public String criarPerguntaConfirmacao(String papelDoElementoNoDiagrama, String categoriaEscolhida) {
        String papel = papelDoElementoNoDiagrama == null ? "" : papelDoElementoNoDiagrama.trim();
        if (papel.length() == 0) {
            papel = "elemento semântico";
        }

        String categoria = categoriaEscolhida == null ? "" : categoriaEscolhida.trim();
        if (categoria.length() == 0) {
            categoria = "categoria escolhida";
        }

        String modelo = ServicoLocalizacao.getInstancia().texto("ui.question.semanticMismatch");
        return modelo
                .replace("{0}", "<b>" + papel + "</b>")
                .replace("{1}", "<b>" + categoria + "</b>");
    }

    public boolean papeisCompativeis(String chavePapelNumeral, String chavePapelAlvo) {
        DescritorPapelQuantitativo origem =
                catalogoPapeis.obterDescritor(chavePapelNumeral);
        DescritorPapelQuantitativo destino =
                catalogoPapeis.obterDescritor(chavePapelAlvo);
        return origem.podeOcupar(destino);
    }

    /**
     * Um questionamento anterior deve desaparecer assim que o item deixa de
     * representar uma incompatibilidade semântica. Isso inclui a correção do
     * posicionamento pelo próprio usuário.
     */
    public boolean deveLimparQuestionamentoPersistente(
            ResultadoQuestionamento resultado) {
        return resultado == null || !resultado.isAplicavel()
                || resultado.isCorreto();
    }

    public boolean deveBloquearMenuNumeroRelativo(ResultadoQuestionamento resultado, boolean itemSobreNumeroRelativo) {
        return itemSobreNumeroRelativo
                && resultado != null
                && resultado.isAplicavel()
                && !resultado.isCorreto();
    }

    private boolean papelValido(String chavePapel) {
        String chave = normalizarChavePapel(chavePapel);
        return chave.length() > 0 && !"papel.valor".equals(chave);
    }

    private String normalizarChavePapel(String chavePapel) {
        return chavePapel == null ? "" : chavePapel.trim();
    }
}
