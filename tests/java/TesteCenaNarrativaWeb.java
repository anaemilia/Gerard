import gerard.aplicacao.FachadaCarregamentoAtividade;
import gerard.aplicacao.PoliticaSorteioSituacoesAditivas;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.campoaditivo.curadoria.ConstrutorResultadoCurado;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.EstadoFeedbackDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.idioma.IdiomaInterface;
import java.util.Map;
import java.util.Random;

/** Exercita a cena compartilhada com uma situação real da curadoria. */
public final class TesteCenaNarrativaWeb {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        RepositorioSituacoesAditivas repositorio = new RepositorioSituacoesAditivas() {
            @Override public SituacaoProblemaAditiva obter(IdiomaInterface idioma, TipoSituacaoAditiva tipo) {
                for (SituacaoProblemaAditiva s : listarTodas()) {
                    if ("PO_COMPOSICAO_MEDIDAS_bolas_1098018440".equals(s.getId())) return s;
                }
                throw new AssertionError("Situação curada de referência ausente");
            }
        };
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                new PoliticaSorteioSituacoesAditivas(),
                new FachadaCarregamentoAtividade(repositorio,
                        new CatalogoDefinicoesAditivas(), new ConstrutorResultadoCurado()),
                new Random(0) { @Override public int nextInt(int limite) { return 0; } },
                IdiomaInterface.PORTUGUES);
        exigir(!servico.sortearMedidas().containsKey("cena"), "não antecipar a cena antes da classificação");
        Map<String, Object> estado = estado(servico.escolherCategoria("COMPOSICAO_MEDIDAS"));
        verificarCena(estado, false);
        servico.posicionarValorConhecido("papel.parte1", "papel.parte1");
        servico.posicionarValorConhecido("papel.parte2", "papel.parte2");
        verificarCena(estado(servico.engatarIncognita("papel.todo", "papel.todo")), false);
        verificarCena(estado(servico.proporValor("papel.todo", 13)), false);
        estado = estado(servico.proporValor("papel.todo", 14));
        verificarCena(estado, true);

        // Feedback não deve apagar palavras, vocabulário ou permissão da cena.
        String texto = String.valueOf(estado.get("enunciado"));
        CenaDiagramaAditivo narrativa = new GeradorCenaDiagramaAditivo().gerarCenaNarrativa(texto, null, true);
        CenaDiagramaAditivo comFeedback = narrativa.comEstadoFeedback(EstadoFeedbackDiagrama.NEUTRO);
        exigir(comFeedback.isPermiteEditarNarrativa(), "feedback preserva a permissão");
        exigir(comFeedback.getElementosTexto().equals(narrativa.getElementosTexto()), "feedback preserva os segmentos");
        exigir(comFeedback.getVocabularioTexto() == narrativa.getVocabularioTexto(), "feedback preserva o vocabulário");
        try {
            comFeedback.getElementosTexto().clear();
            throw new AssertionError("lista da cena deve ser imutável");
        } catch (UnsupportedOperationException esperado) { }

        verificarCena(servico.reiniciarAtividadeAtual(), false);
        exigir(!servico.sortearMedidas().containsKey("cena"), "novo sorteio não reutiliza a cena concluída");
        System.out.println("APROVADO: narrativa web compartilha cena e preserva conclusão/reinício.");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> estado(Map<String, Object> resultado) {
        return (Map<String, Object>) resultado.get("estado");
    }

    @SuppressWarnings("unchecked")
    private static void verificarCena(Map<String, Object> estado, boolean permitida) {
        Map<String, Object> cena = (Map<String, Object>) estado.get("cena");
        exigir(Boolean.valueOf(permitida).equals(cena.get("permite_editar_narrativa")), "permissão publicada pela cena");
        exigir(cena.get("elementos_texto") == estado.get("elementos_texto"), "alias v1 preserva os mesmos elementos");
        exigir(cena.get("vocabulario_texto") == estado.get("vocabulario_texto"), "alias v1 preserva o mesmo vocabulário");
        exigir(cena.get("elementos_texto") instanceof java.util.List, "cena contém palavras");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
