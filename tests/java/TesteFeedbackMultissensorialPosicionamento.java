import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;

public class TesteFeedbackMultissensorialPosicionamento {
    public static void main(String[] args) {
        ServicoLocalizacao.getInstancia().definirIdioma(IdiomaInterface.PORTUGUES);
        ScaffoldingQuestionamento s = new ScaffoldingQuestionamento();
        CatalogoPapeisSemanticosAditivos catalogo = new CatalogoPapeisSemanticosAditivos();
        int categorias = 0;

        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            boolean encadeada = false;
            int quantidade = encadeada ? 2 : 1;
            int max = encadeada ? 6 : quantidadeElementos(tipo);
            if (max <= 0) continue;

            String categoria = ServicoLocalizacao.getInstancia().descricaoTipo(tipo);
            boolean verificouIncompatibilidade = false;
            for (int i = 0; i < max; i++) {
                String alvo = catalogo.obterChavePapelDoElemento(tipo, i, encadeada, quantidade);
                if ("papel.valor".equals(alvo)) continue;
                String incorreto = papelDiferente(alvo);
                ResultadoQuestionamento r = s.avaliarPosicionamento(
                        incorreto, alvo, ServicoLocalizacao.getInstancia().texto(alvo), categoria);
                exigir(r.isAplicavel() && !r.isCorreto(), "incompatibilidade não detectada em " + tipo + " índice " + i);
                exigir(r.getMensagem().contains("Tem certeza que esse número corresponde ao"), "texto-base ausente");
                exigir(r.getMensagem().contains(categoria), "categoria ausente: " + categoria);
                exigir(r.getMensagem().contains(ServicoLocalizacao.getInstancia().texto(alvo)), "papel ausente: " + alvo);
                verificouIncompatibilidade = true;
            }
            exigir(verificouIncompatibilidade, "categoria sem verificação: " + tipo);
            categorias++;
        }

        exigir(categorias == TipoSituacaoAditiva.values().length, "nem todas as categorias foram cobertas");
        System.out.println("OK: feedback semântico cobre " + categorias + " categorias.");
    }

    private static int quantidadeElementos(TipoSituacaoAditiva tipo) {
        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
            case TRANSFORMACAO_MEDIDAS:
            case COMPARACAO_MEDIDAS:
            case COMPOSICAO_TRANSFORMACOES:
            case TRANSFORMACAO_RELACAO:
            case COMPOSICAO_RELACOES: return 3;
            default: return 0;
        }
    }

    private static String papelDiferente(String alvo) {
        if (!"papel.estadoInicial".equals(alvo)) return "papel.estadoInicial";
        return "papel.todo";
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
