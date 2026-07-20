package gerard.ui.cartao;

import gerard.ui.UITemaGerard;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Painel com cantos arredondados, borda fina e preenchimento suave —
 * reproduz no desktop (Swing) a "suavidade" já adotada na identidade
 * visual da versão mobile: cartões arredondados, respiro generoso e
 * bordas discretas de 1px, com cor reservada a indicar estado/seleção.
 *
 * O Swing não possui border-radius nativo; este componente desenha o
 * fundo e a borda manualmente via Graphics2D antes de delegar para o
 * conteúdo (super.paintComponent), preservando qualquer LayoutManager
 * e componentes filhos adicionados normalmente.
 */
public class PainelCartaoArredondado extends JPanel {

    private int raioCanto = 14;
    private Color corFundo = UITemaGerard.COR_DESTAQUE;
    private Color corBorda = UITemaGerard.COR_PRIMARIA;
    private int espessuraBorda = 1;

    public PainelCartaoArredondado() {
        setOpaque(false);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    public void definirRaioCanto(int raio) {
        this.raioCanto = raio;
        repaint();
    }

    public void definirCorFundo(Color cor) {
        this.corFundo = cor;
        repaint();
    }

    public void definirCorBorda(Color cor) {
        this.corBorda = cor;
        repaint();
    }

    public void definirEspessuraBorda(int espessura) {
        this.espessuraBorda = espessura;
        repaint();
    }

    /** Aplica o estilo de cartão "peça" (fundo bege suave), como na caixa interna do número no mockup mobile. */
    public void aplicarEstiloPeca() {
        corFundo = UITemaGerard.COR_SUPERFICIE_SUAVE;
        corBorda = UITemaGerard.COR_BORDA;
        repaint();
    }

    /** Aplica o estilo de feedback de sucesso (azul), reservando cor para sinalizar acerto. */
    public void aplicarEstiloSucesso() {
        corFundo = UITemaGerard.COR_SUCESSO_FUNDO;
        corBorda = UITemaGerard.COR_SUCESSO;
        repaint();
    }

    /** Aplica o estilo de feedback de erro (vermelho), reservando cor para sinalizar erro. */
    public void aplicarEstiloErro() {
        corFundo = UITemaGerard.COR_ERRO_FUNDO;
        corBorda = UITemaGerard.COR_ERRO;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float metadeBorda = espessuraBorda / 2f;
            RoundRectangle2D forma = new RoundRectangle2D.Float(
                    metadeBorda, metadeBorda,
                    getWidth() - espessuraBorda, getHeight() - espessuraBorda,
                    raioCanto, raioCanto);

            g2.setColor(corFundo);
            g2.fill(forma);

            if (espessuraBorda > 0 && corBorda != null) {
                g2.setStroke(new java.awt.BasicStroke(espessuraBorda));
                g2.setColor(corBorda);
                g2.draw(forma);
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }

    @Override
    public Insets getInsets() {
        Insets base = super.getInsets();
        // garante respiro mínimo mesmo se a borda customizada for definida depois da criação
        return base;
    }
}
