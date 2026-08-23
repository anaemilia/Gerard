import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual;
import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;
import gerard.ui.ajuda.PainelAjudaNarrativaVisualCategoria;

/** Verifica a propriedade semântica e a materialização dos três repertórios. */
public final class TesteAjudaAnimadaCategoriasRelacoes {

    public static void main(String[] args) {
        verificarSemRepertorio(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        verificarSemRepertorio(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        verificarSemRepertorio(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);

        verificarRepertorio(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, 4);
        verificarRepertorio(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, 2);
        verificarRepertorio(TipoSituacaoAditiva.COMPOSICAO_RELACOES, 2);
        System.out.println("OK TesteAjudaAnimadaCategoriasRelacoes");
    }

    private static void verificarSemRepertorio(TipoSituacaoAditiva categoria) {
        exigir(!categoria.possuiAjudaVisual(), "Medida não deve declarar historinha: " + categoria);
        RepertorioAjudaVisual repertorio = categoria.selecionarRepertorioAjudaVisual();
        exigir(repertorio.estaVazio(), "Repertório de Medidas deve permanecer vazio");
        exigir(PainelAjudaNarrativaVisualCategoria.criarSeDisponivel(
                repertorio, FormatoAjudaNarrativaVisual.ANIMACAO) == null,
                "Interface não deve inventar repertório para " + categoria);
    }

    private static void verificarRepertorio(TipoSituacaoAditiva categoria, int quantidade) {
        exigir(categoria.possuiAjudaVisual(), "Relação deve possuir repertório: " + categoria);
        RepertorioAjudaVisual repertorio = categoria.selecionarRepertorioAjudaVisual();
        exigir(repertorio.getHistorinhas().size() == quantidade,
                "Quantidade semântica divergente em " + categoria);
        for (HistorinhaAjudaVisual historinha : repertorio.getHistorinhas()) {
            exigir(historinha.getIdSituacaoCurada().length() > 0,
                    "Historinha sem vínculo curado em " + categoria);
            exigir(historinha.getReferenciaConteudo().length() > 0,
                    "Historinha sem referência de mídia em " + categoria);
        }
        verificarMaterializacao(categoria, quantidade, repertorio,
                FormatoAjudaNarrativaVisual.ANIMACAO);
        verificarMaterializacao(categoria, quantidade, repertorio,
                FormatoAjudaNarrativaVisual.HISTORIA_EM_QUADRINHOS);
    }

    private static void verificarMaterializacao(
            TipoSituacaoAditiva categoria,
            int quantidade,
            RepertorioAjudaVisual repertorio,
            FormatoAjudaNarrativaVisual formato) {
        PainelAjudaNarrativaVisualCategoria painel =
                PainelAjudaNarrativaVisualCategoria.criarSeDisponivel(repertorio, formato);
        exigir(painel != null, "Recursos não materializados para " + categoria + " em " + formato);
        exigir(painel.getFormato() == formato, "Formato divergente em " + categoria);
        exigir(painel.getQuantidadeHistorias() == quantidade,
                "Quantidade divergente em " + categoria + ": " + painel.getQuantidadeHistorias());
        exigir(painel.getIndiceAtual() == 0, "Índice inicial divergente");
        painel.exibirProxima();
        exigir(painel.getIndiceAtual() == 1, "Navegação seguinte não avançou");
        painel.exibirAnterior();
        exigir(painel.getIndiceAtual() == 0, "Navegação anterior não voltou");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
