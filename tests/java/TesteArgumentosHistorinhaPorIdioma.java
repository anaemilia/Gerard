import gerard.aplicacao.DecisorAjudaVisual;
import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * O idioma não decide a historinha: a decisão (e a ilustração) é a mesma para o original e
 * para as traduções. O idioma só determina os textos que o renderizador da categoria, dentro
 * do criador de cena, entrega como argumento da historinha (os trechos curados da própria
 * situação). Sem trechos curados no idioma, não há texto: nada é inventado.
 */
public class TesteArgumentosHistorinhaPorIdioma {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
        int comTrechos = 0;
        int idiomasComTrechos = 0;
        java.util.Set<String> idiomas = new java.util.TreeSet<String>();
        for (SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            List<gerard.dominio.campoaditivo.ajuda.ApoioVisual> decididos = DecisorAjudaVisual.decidir(s, true);
            if (decididos.isEmpty()) {
                continue;
            }
            CenaDiagramaAditivo base = new CenaDiagramaAditivo(null, "",
                    new ArrayList<FiguraDiagrama>(), new ArrayList<ConectorDiagrama>());
            CenaDiagramaAditivo cena = gerador.comApoiosVisuais(base, decididos, s.getTipo(), s, s.getCodigoIdioma());
            Map<String, Object> item = (Map<String, Object>) ProjetorAjudaVisualWeb.projetar(cena).get(0);
            String[] esperados = {s.getFragmentoTexto1(), s.getFragmentoTexto2(), s.getFragmentoTexto3(),
                    s.getFragmentoTexto4(), s.getFragmentoTexto5(), s.getFragmentoTexto6()};
            List<String> curados = new ArrayList<String>();
            for (String e : esperados) {
                if (e != null && !e.trim().isEmpty()) curados.add(e.trim());
            }
            if (curados.isEmpty()) {
                exigir(!item.containsKey("trechos"), "sem trechos curados, nada é inventado: " + s.getId());
            } else {
                exigir(curados.equals(item.get("trechos")), "trechos curados do idioma da situação: " + s.getId());
                exigir(s.getCodigoIdioma().equals(item.get("idioma")), "idioma do argumento: " + s.getId());
                comTrechos++;
                idiomas.add(s.getCodigoIdioma());
            }
            // O idioma pedido diferente do da situação não empresta texto de outro idioma.
            CenaDiagramaAditivo outra = gerador.comApoiosVisuais(base, decididos, s.getTipo(), s,
                    "xx-YY");
            exigir(!((Map<String, Object>) ProjetorAjudaVisualWeb.projetar(outra).get(0)).containsKey("trechos"),
                    "idioma diferente do da situação não entrega trechos: " + s.getId());
            // A decisão em si não muda com o idioma.
            exigir(item.get("tipo").equals("HISTORINHA_ILUSTRADA"), "ilustração independente do idioma: " + s.getId());
        }
        // Os trechos das traduções são completados pela pessoa na curadoria: vazios, nada é entregue.
        exigir(comTrechos > 0 && idiomas.contains("pt-BR"),
                "trechos curados entregues no idioma da própria situação (achados: " + idiomas + ")");
        System.out.println("APROVADO: " + comTrechos + " historinhas com trechos curados em " + idiomas
                + "; a decisão é a mesma em todos os idiomas.");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
