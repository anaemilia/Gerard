import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A historinha pertence à categoria: cada TipoSituacaoAditiva projeta o próprio repertório
 * (ou lista vazia, como Transformação de Medidas), e só na escalada no limite. O cliente
 * não fixa o conteúdo de outra categoria (defeito de 2026-10-01: a historinha de Composição
 * de Transformações aparecia em Transformação de Medidas).
 */
public final class TesteAjudaVisualPorCategoriaWeb {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Map<String, Object> noLimite = modelagem(true);
        Map<String, Object> foraDoLimite = modelagem(false);

        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            exigir(projetar(tipo, "qualquer", foraDoLimite).isEmpty(),
                    tipo + ": fora do limite da escalada não projeta historinha");
            exigir(projetar(tipo, "qualquer", null).isEmpty(),
                    tipo + ": sem modelagem não projeta historinha");
            int esperadas = tipo.selecionarRepertorioAjudaVisual().getHistorinhas().size();
            int obtidas = projetar(tipo, "qualquer", noLimite).size();
            exigir(obtidas == esperadas || (obtidas == 1 && esperadas > 0),
                    tipo + ": no limite projeta o repertório da própria categoria, ou só a narrativa da situação");
        }
        exigir(projetar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        "qualquer", noLimite).isEmpty(),
                "Transformação de Medidas não tem historinhas: nada é exibido");
        exigir(projetar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                        "qualquer", noLimite).isEmpty(),
                "Composição de Medidas não tem historinhas (usa material concreto)");

        List<Object> transformacaoRelacao = projetar(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                "PO_TRANSFORMACAO_RELACAO_bonecas_620955739", noLimite);
        Map<String, Object> primeira = (Map<String, Object>) transformacaoRelacao.get(0);
        exigir("transformacao_relacao/01_julia_maria_bonecas_historinha".equals(primeira.get("referencia")),
                "a referência é do repertório da própria categoria (nunca de Composição de Transformações)");
        exigir(transformacaoRelacao.size() == 1,
                "o backend entrega só a narrativa da própria situação (o cliente não escolhe)");

        for (Object item : projetar(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, "x", noLimite)) {
            String referencia = String.valueOf(((Map<String, Object>) item).get("referencia"));
            exigir(referencia.startsWith("composicao_transformacoes/"),
                    "Composição de Transformações usa só as suas historinhas: " + referencia);
        }
        int verificadas = 0;
        for (SituacaoProblemaAditiva situacao : new RepositorioSituacoesAditivas().listarValidadas()) {
            boolean relativo = SemanticaCuradaSituacao.possuiNumeroRelativo(situacao);
            exigir(!ProjetorAjudaVisualWeb.deveAcionar(situacao, false), "antes do limite não aciona");
            exigir(ProjetorAjudaVisualWeb.deveAcionar(situacao, true) == relativo,
                    "decisão independente do acervo: " + situacao.getId());
            if (relativo) {
                exigir(new gerard.campoaditivo.representacao.SeletorRepresentacaoComplementar()
                        .selecionar(situacao.getTipo(), false, relativo)
                        == gerard.campoaditivo.representacao.TipoRepresentacaoComplementar.HISTORINHAS_CATEGORIA,
                        "número relativo seleciona historinha mesmo sem acervo");
            }
            exigir(ProjetorAjudaVisualWeb.projetar(situacao, false).isEmpty(), "sem limite não há historinha");
            int esperadas = SemanticaCuradaSituacao.possuiNumeroRelativo(situacao)
                    ? situacao.getTipo().selecionarRepertorioAjudaVisual()
                            .paraSituacao(situacao.getId()).getHistorinhas().size() : 0;
            exigir(ProjetorAjudaVisualWeb.projetar(situacao, true).size() == esperadas,
                    "gate estrutural: " + situacao.getId());
            exigir(situacao.getTipo().selecionarRepertorioAjudaVisual(false, true).estaVazio(),
                    "sem número relativo não há historinhas");
            verificadas++;
        }
        exigir(verificadas > 0, "curadoria carregada");
        System.out.println("APROVADO: repertório próprio e limite em " + verificadas + " situações curadas.");
    }

    private static List<Object> projetar(TipoSituacaoAditiva tipo, String id, Map<String, Object> modelagem) {
        for (SituacaoProblemaAditiva situacao : new RepositorioSituacoesAditivas().listarValidadas()) {
            if (situacao.getTipo() == tipo && ("qualquer".equals(id) || "x".equals(id) || id.equals(situacao.getId()))) {
                return ProjetorAjudaVisualWeb.projetar(situacao,
                        modelagem != null && Boolean.TRUE.equals(modelagem.get("escalada_no_limite")));
            }
        }
        throw new AssertionError("situação não encontrada: " + tipo + " / " + id);
    }

    private static Map<String, Object> modelagem(boolean escalada) {
        Map<String, Object> modelagem = new LinkedHashMap<String, Object>();
        modelagem.put("escalada_no_limite", Boolean.valueOf(escalada));
        return modelagem;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
