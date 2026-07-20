package gerard.Scaffolding.questionamento;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.i18n.ServicoLocalizacao;

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
        String numeral = normalizarChavePapel(chavePapelNumeral);
        String alvo = normalizarChavePapel(chavePapelAlvo);

        if (numeral.length() == 0 || alvo.length() == 0) {
            return false;
        }

        if (numeral.equals(alvo)) {
            return true;
        }

        if ("papel.transformacao".equals(numeral) && alvo.startsWith("papel.transformacao")) {
            return true;
        }

        if ("papel.relacao".equals(numeral) && alvo.startsWith("papel.relacao")) {
            return true;
        }

        if ("papel.parte".equals(numeral) && alvo.startsWith("papel.parte")) {
            return true;
        }

        return false;
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

    public int obterIndiceElementoPorPapel(
            String chavePapel,
            TipoSituacaoAditiva tipo,
            boolean transformacaoCompostaEncadeada,
            int quantidadePassosTransformacaoComposta
    ) {
        return catalogoPapeis.obterIndiceElementoPorPapel(
                chavePapel, tipo, transformacaoCompostaEncadeada,
                quantidadePassosTransformacaoComposta);
    }

    public String obterChavePapelDoElemento(
            TipoSituacaoAditiva tipo,
            int indiceElemento,
            boolean transformacaoCompostaEncadeada,
            int quantidadePassosTransformacaoComposta
    ) {
        return catalogoPapeis.obterChavePapelDoElemento(
                tipo, indiceElemento, transformacaoCompostaEncadeada,
                quantidadePassosTransformacaoComposta);
    }

    private boolean papelValido(String chavePapel) {
        String chave = normalizarChavePapel(chavePapel);
        return chave.length() > 0 && !"papel.valor".equals(chave);
    }

    private String normalizarChavePapel(String chavePapel) {
        return chavePapel == null ? "" : chavePapel.trim();
    }
}
