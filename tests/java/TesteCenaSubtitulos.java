import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Function;

/**
 * O gerador de cena só projeta o subtítulo que quem o conhece informou; a figura
 * entrega as linhas do bloco de rótulo já na ordem de exibição (subtítulo, rótulo),
 * e o adaptador só empilha e desenha.
 */
public class TesteCenaSubtitulos {
    public static void main(String[] args) {
        GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
        CatalogoDefinicoesAditivas catalogo = new CatalogoDefinicoesAditivas();
        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            CenaDiagramaAditivo base = gerador.gerar(tipo, new Rectangle(0, 0, 840, 480),
                    catalogo.obter(tipo), new int[] {3, 5, 8});
            Function<String, String> informante = chave -> chave.isEmpty() ? "" : "Pessoa " + chave;
            CenaDiagramaAditivo cena = gerador.comSubtitulos(base, informante);
            exigir(cena != base && cena.getFiguras().size() == base.getFiguras().size(),
                    tipo + ": cena nova e imutável, mesmas figuras");
            for (int i = 0; i < cena.getFiguras().size(); i++) {
                FiguraDiagrama original = base.getFiguras().get(i);
                FiguraDiagrama figura = cena.getFiguras().get(i);
                exigir("".equals(original.getSubtitulo()), tipo + ": a cena base não é alterada");
                exigir(figura.getX() == original.getX() && figura.getRotulo() == null
                        ? original.getRotulo() == null
                        : figura.getRotulo().equals(original.getRotulo()),
                        tipo + ": rótulo e geometria preservados");
                String esperado = original.getChavePapelSemantico().isEmpty() ? ""
                        : "Pessoa " + original.getChavePapelSemantico();
                exigir(esperado.equals(figura.getSubtitulo()), tipo + ": subtítulo projetado");
                List<String> linhas = figura.getLinhasRotulo();
                if (!esperado.isEmpty()) {
                    exigir(linhas.size() >= 1 && esperado.equals(linhas.get(0)),
                            tipo + ": subtítulo vem antes do rótulo");
                }
                if (original.getRotulo() != null && original.getRotulo().trim().length() > 0) {
                    exigir(linhas.get(linhas.size() - 1).equals(original.getRotulo().trim()),
                            tipo + ": o rótulo é a última linha do bloco");
                }
            }
            exigir(gerador.comSubtitulos(null, informante) == null, "cena nula permanece nula");
        }
        System.out.println("Teste aprovado: o gerador projeta o subtítulo e a figura ordena o bloco de rótulo.");
    }
    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
