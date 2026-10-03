import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.dominio.campoaditivo.ajuda.ApoioVisual;
import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import java.util.List;
import java.util.Map;

/**
 * Regra de 2026-10-02: onde há número relativo ou transformação, há historinha após o limite.
 * Se existe ilustração cadastrada para a própria situação, ela é o apoio; senão, a historinha
 * é o texto da situação-problema. O backend entrega UM apoio decidido; o cliente não escolhe.
 */
public class TesteAjudaVisualPorCategoriaWeb {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        int verificadas = 0;
        int ilustradas = 0;
        int textuais = 0;
        for (SituacaoProblemaAditiva situacao : new RepositorioSituacoesAditivas().listarValidadas()) {
            TipoSituacaoAditiva tipo = situacao.getTipo();
            boolean relativo = SemanticaCuradaSituacao.possuiNumeroRelativo(situacao);
            boolean esperada = relativo || tipo.envolveTransformacao();
            String id = situacao.getId();
            exigir(!ProjetorAjudaVisualWeb.deveAcionar(situacao, false), "antes do limite não aciona: " + id);
            exigir(ProjetorAjudaVisualWeb.projetar(situacao, false).isEmpty(), "sem limite não há apoio: " + id);
            exigir(ProjetorAjudaVisualWeb.deveAcionar(situacao, true) == esperada,
                    "aciona com número relativo ou transformação, independente do acervo: " + id);
            List<Object> apoios = ProjetorAjudaVisualWeb.projetar(situacao, true);
            exigir(apoios.size() == (esperada ? 1 : 0),
                    "o backend entrega exatamente um apoio decidido quando aciona: " + id);
            if (!esperada) {
                continue;
            }
            Map<String, Object> apoio = (Map<String, Object>) apoios.get(0);
            // O idioma não decide: a tradução herda a ilustração da versão original.
            String idIlustracao = "traducao".equalsIgnoreCase(situacao.getTipoVersao())
                    && situacao.getVersaoOrigemId() != null && !situacao.getVersaoOrigemId().trim().isEmpty()
                    ? situacao.getVersaoOrigemId().trim() : id;
            boolean propria = false;
            for (HistorinhaAjudaVisual h : tipo.selecionarRepertorioAjudaVisual().getHistorinhas()) {
                propria |= h.getIdSituacaoCurada().equals(idIlustracao);
            }
            if (propria) {
                exigir(ApoioVisual.TIPO_HISTORINHA_ILUSTRADA.equals(apoio.get("tipo"))
                        && apoio.get("referencia") != null, "ilustração da própria situação: " + id);
                ilustradas++;
            } else {
                exigir(ApoioVisual.TIPO_HISTORINHA_TEXTUAL.equals(apoio.get("tipo")),
                        "sem ilustração própria, a historinha é o texto da situação: " + id);
                exigir(situacao.getEnunciado().trim().equals(apoio.get("texto")),
                        "o texto é o da própria situação-problema: " + id);
                textuais++;
            }
            verificadas++;
        }
        exigir(verificadas > 0 && ilustradas == verificadas && textuais == 0,
                "toda situação com número relativo/transformação, em qualquer idioma, tem a ilustração da sua situação");
        System.out.println("APROVADO: " + verificadas + " situações com apoio (" + ilustradas
                + " ilustradas, " + textuais + " de texto) decididas no backend.");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
