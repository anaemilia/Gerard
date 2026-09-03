import gerard.aplicacao.ContextoCarregamentoAtividade;
import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.curadoria.RepositorioCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ServicoSituacaoProblemaRicaCurada;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import java.io.File;

public final class TesteContextoCarregamentoSituacaoRica {
    public static void main(String[] args) {
        File diretorioAusente = new File(System.getProperty("java.io.tmpdir"),
                "gerard-narrativa-rica-ausente-" + System.nanoTime());
        FachadaCarregamentoAtividade fachada = new FachadaCarregamentoAtividade(
                new RepositorioSituacoesAditivas(),
                new CatalogoDefinicoesAditivas(),
                new ConstrutorResultadoCurado(),
                new ServicoSituacaoProblemaRicaCurada(
                        new RepositorioCuradoriaNarrativaRica(diretorioAusente)));

        ContextoCarregamentoAtividade contexto = fachada.carregarNova(
                IdiomaInterface.PORTUGUES,
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO);
        exigir(contexto.possuiSituacaoExibivel(),
                "ausência do sidecar não impede exibir a situação tabular");
        exigir(!contexto.possuiSituacaoRicaValida(),
                "estrutura rica ausente não pode ser declarada válida");
        ResultadoConversaoSituacaoProblemaRica resultado =
                contexto.getResultadoSituacaoRica();
        exigir(resultado != null && resultado.possuiCodigo(
                        "conversao.narrativa_persistida.ausente"),
                "contexto deve transportar o diagnóstico da ponte rica");
        System.out.println("APROVADO: contexto transporta indisponibilidade rica diagnosticada.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
