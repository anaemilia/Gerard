import gerard.aplicacao.portabilidade.ProjetorAjudaVisualWeb;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import gerard.ui.ajuda.RenderizadorApoiosVisuaisSwing;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Regra: sempre que há número relativo ou transformação, a historinha (ilustrada ou textual)
 * é renderizada no limite. Percorre TODAS as situações validadas e, para cada uma, pinta o
 * componente real do Swing fora da tela, exigindo conteúdo desenhado (não só a moldura).
 */
public class TesteHistorinhaRenderizaParaTodoNumeroRelativo {
    public static void main(String[] args) throws Exception {
        final int[] contagem = new int[3];
        final StringBuilder falhas = new StringBuilder();
        for (final SituacaoProblemaAditiva s : new RepositorioSituacoesAditivas().listarValidadas()) {
            boolean esperada = SemanticaCuradaSituacao.possuiNumeroRelativo(s) || s.getTipo().envolveTransformacao();
            if (!esperada) continue;
            final List<Object> apoios = ProjetorAjudaVisualWeb.projetar(s, true);
            SwingUtilities.invokeAndWait(new Runnable() { public void run() {
                try {
                    Dimension d = new Dimension(780, 520);
                    JComponent c = RenderizadorApoiosVisuaisSwing.criar(apoios, d);
                    if (c == null) { falhas.append("sem componente: ").append(s.getId()).append('\n'); return; }
                    // O envoltório do fade-in começa transparente; o conteúdo é pintado direto.
                    if (c instanceof gerard.ui.ajuda.PainelAparicaoGradual) {
                        c = (JComponent) c.getComponent(0);
                    }
                    c.setBounds(0, 0, d.width, d.height);
                    c.doLayout();
                    int tinta = 0;
                    for (int tentativa = 0; tentativa < 40 && tinta == 0; tentativa++) {
                        BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g = img.createGraphics();
                        g.setColor(Color.WHITE); g.fillRect(0, 0, d.width, d.height);
                        c.paint(g); g.dispose();
                        for (int y = 0; y < d.height; y += 2) for (int x = 0; x < d.width; x += 2)
                            if ((img.getRGB(x, y) & 0xFFFFFF) != 0xFFFFFF) tinta++;
                        if (tinta == 0) { try { Thread.sleep(50); } catch (InterruptedException e) { } }
                    }
                    if (tinta == 0) falhas.append("nada desenhado: ").append(s.getId()).append('\n');
                    else contagem[0]++;
                } catch (Throwable t) { falhas.append("erro ").append(t).append(": ").append(s.getId()).append('\n'); }
            }});
            contagem[1]++;
        }
        if (falhas.length() > 0) { System.out.println(falhas); throw new AssertionError("há situações sem historinha renderizada"); }
        System.out.println("APROVADO: " + contagem[0] + " de " + contagem[1] + " situações com número relativo/transformação renderizam historinha.");
    }
}
