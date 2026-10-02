import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
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
            exigir(ProjetorAjudaVisualWeb.projetar(tipo, "qualquer", foraDoLimite).isEmpty(),
                    tipo + ": fora do limite da escalada não projeta historinha");
            exigir(ProjetorAjudaVisualWeb.projetar(tipo, "qualquer", null).isEmpty(),
                    tipo + ": sem modelagem não projeta historinha");
            int esperadas = tipo.selecionarRepertorioAjudaVisual().getHistorinhas().size();
            exigir(ProjetorAjudaVisualWeb.projetar(tipo, "qualquer", noLimite).size() == esperadas,
                    tipo + ": no limite projeta exatamente o repertório da própria categoria");
        }
        exigir(ProjetorAjudaVisualWeb.projetar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                        "qualquer", noLimite).isEmpty(),
                "Transformação de Medidas não tem historinhas: nada é exibido");
        exigir(ProjetorAjudaVisualWeb.projetar(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                        "qualquer", noLimite).isEmpty(),
                "Composição de Medidas não tem historinhas (usa material concreto)");

        List<Object> transformacaoRelacao = ProjetorAjudaVisualWeb.projetar(
                TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                "PO_TRANSFORMACAO_RELACAO_bonecas_620955739", noLimite);
        exigir(transformacaoRelacao.size() == 2, "Transformação de Relação tem 2 historinhas");
        Map<String, Object> primeira = (Map<String, Object>) transformacaoRelacao.get(0);
        exigir("transformacao_relacao/01_julia_maria_bonecas_historinha".equals(primeira.get("referencia")),
                "a referência é do repertório da própria categoria (nunca de Composição de Transformações)");
        exigir(Boolean.TRUE.equals(primeira.get("da_situacao_atual")),
                "marca a historinha que corresponde à situação atual");
        exigir(Boolean.FALSE.equals(((Map<String, Object>) transformacaoRelacao.get(1)).get("da_situacao_atual")),
                "a outra historinha não é da situação atual");

        for (Object item : ProjetorAjudaVisualWeb.projetar(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, "x", noLimite)) {
            String referencia = String.valueOf(((Map<String, Object>) item).get("referencia"));
            exigir(referencia.startsWith("composicao_transformacoes/"),
                    "Composição de Transformações usa só as suas historinhas: " + referencia);
        }
        System.out.println("APROVADO: cada categoria projeta o próprio repertório de historinhas, só no limite.");
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
