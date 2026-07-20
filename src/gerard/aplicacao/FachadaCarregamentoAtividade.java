package gerard.aplicacao;

import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.modelo.ResultadoInterpretacao;

/**
 * Fachada para o fluxo de carregamento. A tela deixa de conhecer a sequência
 * repositório -> catálogo -> construção da interpretação curada.
 */
public final class FachadaCarregamentoAtividade {
    private final RepositorioSituacoesAditivas repositorio;
    private final CatalogoDefinicoesAditivas catalogo;
    private final ConstrutorResultadoCurado construtor;

    public FachadaCarregamentoAtividade(RepositorioSituacoesAditivas repositorio,
            CatalogoDefinicoesAditivas catalogo,
            ConstrutorResultadoCurado construtor) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.construtor = construtor;
    }

    public ContextoCarregamentoAtividade carregarNova(
            IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
        SituacaoProblemaAditiva situacao = repositorio.obter(idioma, tipo);
        return construirContexto(situacao, tipo);
    }

    public ContextoCarregamentoAtividade carregarCorrespondente(
            SituacaoProblemaAditiva atual,
            IdiomaInterface idioma,
            TipoSituacaoAditiva tipo,
            int[] valoresAtuais) {
        SituacaoProblemaAditiva situacao = repositorio.obterCorrespondente(
                atual, idioma, tipo, valoresAtuais);
        return construirContexto(situacao, tipo);
    }

    private ContextoCarregamentoAtividade construirContexto(
            SituacaoProblemaAditiva situacao, TipoSituacaoAditiva tipo) {
        DefinicaoDiagramaAditivo definicao = catalogo.obter(tipo);
        ResultadoInterpretacao interpretacao = situacao != null
                ? construtor.construir(situacao) : null;
        return new ContextoCarregamentoAtividade(situacao, definicao, interpretacao);
    }
}
