package gerard.aplicacao;

import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.MaterializadorEnunciadoCurado;
import gerard.campoaditivo.curadoria.RepositorioCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ServicoSituacaoProblemaRicaCurada;
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
    private final ServicoSituacaoProblemaRicaCurada servicoSituacaoRica;
    private final MaterializadorEnunciadoCurado materializador =
            new MaterializadorEnunciadoCurado();

    public FachadaCarregamentoAtividade(RepositorioSituacoesAditivas repositorio,
            CatalogoDefinicoesAditivas catalogo,
            ConstrutorResultadoCurado construtor) {
        this(repositorio, catalogo, construtor,
                new ServicoSituacaoProblemaRicaCurada(
                        new RepositorioCuradoriaNarrativaRica()));
    }

    public FachadaCarregamentoAtividade(RepositorioSituacoesAditivas repositorio,
            CatalogoDefinicoesAditivas catalogo,
            ConstrutorResultadoCurado construtor,
            ServicoSituacaoProblemaRicaCurada servicoSituacaoRica) {
        this.repositorio = repositorio;
        this.catalogo = catalogo;
        this.construtor = construtor;
        if (servicoSituacaoRica == null) {
            throw new IllegalArgumentException(
                    "serviço de situação rica é obrigatório");
        }
        this.servicoSituacaoRica = servicoSituacaoRica;
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
        String enunciadoExibido = situacao != null
                ? materializador.materializar(situacao) : "";
        ResultadoInterpretacao interpretacao = situacao != null
                ? construtor.construir(situacao, enunciadoExibido) : null;
        ResultadoConversaoSituacaoProblemaRica resultadoSituacaoRica =
                situacao == null ? null
                        : servicoSituacaoRica.converterDiagnosticado(situacao);
        return new ContextoCarregamentoAtividade(
                situacao, definicao, interpretacao, enunciadoExibido,
                resultadoSituacaoRica);
    }
}
