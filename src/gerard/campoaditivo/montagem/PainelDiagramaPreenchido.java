package gerard.campoaditivo.montagem;

import gerard.campoaditivo.diagrama.apresentacao.RenderizadorSwingDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.servico.CatalogoDefinicoesAditivas;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.conclusao.TipConclusaoModelagem;
import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

/** Painel somente de leitura para apresentar a instância preenchida do diagrama. */
public final class PainelDiagramaPreenchido extends JPanel {
    private static final int LARGURA_BASE = 590;
    private static final int ALTURA_BASE = 390;

    private final GeradorCenaDiagramaAditivo gerador = new GeradorCenaDiagramaAditivo();
    private final RenderizadorSwingDiagramaAditivo renderizador = new RenderizadorSwingDiagramaAditivo();
    private final CatalogoDefinicoesAditivas catalogo = new CatalogoDefinicoesAditivas();

    private SituacaoProblemaAditiva situacao;
    private String[] valores = new String[0];
    private Rectangle limitesTituloCategoria = new Rectangle();
    private String descricaoCategoriaAtual = "";
    private JLabel rotuloFeedback;
    private TipConclusaoModelagem tipParabens;
    private boolean sucesso;
    private int limiteDireitoConteudo = -1;

    public PainelDiagramaPreenchido() {
        setBackground(gerard.ui.UITemaGerard.COR_SUPERFICIE);
        setOpaque(true);
        setPreferredSize(new Dimension(540, 370));
        setMinimumSize(new Dimension(410, 300));
        enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
        ToolTipManager.sharedInstance().registerComponent(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                atualizarCursor(e.getPoint());
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    public void definirSituacao(SituacaoProblemaAditiva situacao, String[] valores) {
        this.situacao = situacao;
        this.valores = valores == null ? new String[0] : valores.clone();
        this.limitesTituloCategoria = new Rectangle();
        this.descricaoCategoriaAtual = "";
        setCursor(Cursor.getDefaultCursor());
        definirSucesso(false);
        reposicionarRotuloFeedback();
        repaint();
    }

    /**
     * Sinaliza que a construção foi validada como compatível com o diagrama:
     * pinta o diagrama de azul e troca o rótulo de feedback pelo tip de
     * "Parabéns, podemos passar para a próxima tarefa?" com Sim/Não.
     */
    public void definirSucesso(boolean sucesso) {
        this.sucesso = sucesso;
        if (rotuloFeedback != null) {
            rotuloFeedback.setVisible(!sucesso);
        }
        if (tipParabens != null) {
            if (sucesso) {
                reposicionarTipParabens();
            } else {
                tipParabens.ocultar();
            }
        }
        repaint();
    }

    /**
     * Sobrepõe o rótulo de feedback diretamente sobre este painel, colado ao
     * contorno realmente desenhado do diagrama — não ao fim da área alocada
     * pelo layout, que pode ser bem maior que o diagrama em si. O mesmo
     * princípio de posicionamento usado pelo link "Próxima tarefa" abaixo do
     * diagrama de Vergnaud (ver SeloConclusaoModelagem).
     */
    public void anexarRotuloFeedback(JLabel rotulo) {
        this.rotuloFeedback = rotulo;
        setLayout(null);
        add(rotulo);
        reposicionarRotuloFeedback();
    }

    /** Sobrepõe o tip de conclusão (Parabéns + Sim/Não), oculto até definirSucesso(true). */
    public void anexarTipParabens(TipConclusaoModelagem tip) {
        this.tipParabens = tip;
        setLayout(null);
        add(tip);
        setComponentZOrder(tip, 0);
    }

    private void reposicionarRotuloFeedback() {
        if (rotuloFeedback == null || sucesso) {
            return;
        }
        Rectangle area = calcularAreaVisualDiagrama();
        Dimension preferido = rotuloFeedback.getPreferredSize();
        int largura = Math.max(preferido.width, 1);
        int altura = Math.max(preferido.height, 1);
        int centroX = area == null ? getWidth() / 2 : area.x + area.width / 2;
        int x = Math.max(6, Math.min(Math.max(6, getWidth() - largura - 6), centroX - largura / 2));
        int baseDiagrama = area == null ? getHeight() / 2 : area.y + area.height;
        int y = Math.max(6, Math.min(getHeight() - altura - 6, baseDiagrama + 10));
        rotuloFeedback.setBounds(x, y, largura, altura);
    }

    /**
     * Mantém o tip próximo do diagrama, à direita do conteúdo desenhado e na
     * mesma linha vertical do rótulo que nomeia a categoria da situação —
     * em vez de abaixo de toda a área lógica do diagrama (bem mais alta que
     * o conteúdo real, o que afastava demais o tip).
     */
    private void reposicionarTipParabens() {
        if (tipParabens == null || !sucesso) {
            return;
        }
        int y = limitesTituloCategoria.height > 0 ? limitesTituloCategoria.y : 10;
        int x = limiteDireitoConteudo >= 0 ? limiteDireitoConteudo + 16 : getWidth() / 2;
        tipParabens.mostrarEm(x, y, getWidth(), getHeight());
    }

    /** Área visual (nas coordenadas deste painel) ocupada pelo diagrama desenhado, ou null se vazio. */
    private Rectangle calcularAreaVisualDiagrama() {
        if (situacao == null) {
            return null;
        }
        double escala = Math.min((getWidth() - 18.0) / LARGURA_BASE, (getHeight() - 18.0) / ALTURA_BASE);
        escala = Math.max(0.62, Math.min(1.15, escala));
        double largura = LARGURA_BASE * escala;
        double altura = ALTURA_BASE * escala;
        double dx = (getWidth() - largura) / 2.0;
        double dy = (getHeight() - altura) / 2.0;
        return new Rectangle((int) Math.round(dx), (int) Math.round(dy),
                (int) Math.round(largura), (int) Math.round(altura));
    }

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);
        if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(gerard.ui.UITemaGerard.COR_BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, Math.max(1, getWidth() - 3), Math.max(1, getHeight() - 3), 18, 18);

            if (situacao == null) {
                desenharMensagemCentral(g2, ServicoLocalizacao.getInstancia().texto("montagem.diagram.empty"));
                reposicionarRotuloFeedback();
                reposicionarTipParabens();
                return;
            }

            Rectangle areaVisual = calcularAreaVisualDiagrama();
            double escala = areaVisual.width / (double) LARGURA_BASE;
            double dx = areaVisual.x;
            double dy = areaVisual.y;
            g2.translate(dx, dy);
            g2.scale(escala, escala);

            Rectangle area = new Rectangle(18, 18, 550, 350);
            DefinicaoDiagramaAditivo definicao = catalogo.obter(situacao.getTipo());
            CenaDiagramaAditivo cena = gerador.gerar(situacao.getTipo(), area, definicao, new int[] {0, 0, 0});
            // Nesta atividade, a definição não fica permanentemente exposta:
            // ela é apresentada somente ao passar o mouse sobre o nome da categoria.
            renderizador.renderizar(g2, area, cena, false, sucesso);
            atualizarAreaInterativaTitulo(cena, area, escala, dx, dy);
            atualizarLimiteDireitoConteudo(cena, escala, dx);
            desenharValores(g2, cena);
            reposicionarRotuloFeedback();
            reposicionarTipParabens();
        } finally {
            g2.dispose();
        }
    }


    @Override
    public String getToolTipText(MouseEvent evento) {
        if (evento == null || descricaoCategoriaAtual == null
                || descricaoCategoriaAtual.trim().length() == 0
                || !limitesTituloCategoria.contains(evento.getPoint())) {
            return null;
        }
        return "<html><div style='width:330px;'>" + escaparHtml(descricaoCategoriaAtual) + "</div></html>";
    }

    private void atualizarAreaInterativaTitulo(CenaDiagramaAditivo cena, Rectangle area,
                                                double escala, double dx, double dy) {
        if (cena == null || cena.getTitulo() == null) {
            limitesTituloCategoria = new Rectangle();
            descricaoCategoriaAtual = "";
            return;
        }
        Font fonteTitulo = new Font("Arial", Font.BOLD, 16);
        FontMetrics fm = getFontMetrics(fonteTitulo);
        int xBase = area.x + 18;
        int linhaBase = area.y + 28;
        int yBase = linhaBase - fm.getAscent();
        int larguraBase = Math.max(1, fm.stringWidth(cena.getTitulo()));
        int alturaBase = Math.max(1, fm.getHeight());

        int x = (int) Math.floor(dx + xBase * escala) - 4;
        int y = (int) Math.floor(dy + yBase * escala) - 3;
        int largura = (int) Math.ceil(larguraBase * escala) + 8;
        int altura = (int) Math.ceil(alturaBase * escala) + 6;
        limitesTituloCategoria = new Rectangle(x, y, largura, altura);
        descricaoCategoriaAtual = cena.getDescricao() == null ? "" : cena.getDescricao();
    }

    /**
     * Borda direita real (neste painel) ocupada pelas figuras desenhadas, não
     * pela área lógica inteira do diagrama — que costuma ser bem mais larga
     * que o conteúdo de fato, deixando o tip de sucesso longe do diagrama se
     * usada como referência de posicionamento.
     */
    private void atualizarLimiteDireitoConteudo(CenaDiagramaAditivo cena, double escala, double dx) {
        int maxX = -1;
        if (cena != null) {
            for (FiguraDiagrama figura : cena.getFiguras()) {
                int direita = figura.getX() + figura.getLargura();
                if (direita > maxX) {
                    maxX = direita;
                }
            }
        }
        limiteDireitoConteudo = maxX < 0 ? -1 : (int) Math.round(dx + maxX * escala);
    }

    private void atualizarCursor(Point ponto) {
        boolean sobreTitulo = ponto != null && limitesTituloCategoria.contains(ponto)
                && descricaoCategoriaAtual != null && descricaoCategoriaAtual.trim().length() > 0;
        setCursor(sobreTitulo
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    private String escaparHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }

    private void desenharValores(Graphics2D g2, CenaDiagramaAditivo cena) {
        if (cena == null || valores.length < 3) {
            return;
        }
        for (int i = 0; i < cena.getFiguras().size() && i < 3; i++) {
            FiguraDiagrama figura = cena.getFiguras().get(i);
            String valor = valorParaFigura(i);
            if (valor == null || valor.trim().length() == 0) {
                valor = "?";
            }
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            Color corValor = SimboloDesconhecido.eh(valor)
                    ? new Color(21, 128, 61)
                    : (sucesso ? gerard.ui.UITemaGerard.COR_SUCESSO : gerard.ui.UITemaGerard.COR_TEXTO);
            g2.setColor(corValor);
            FontMetrics fm = g2.getFontMetrics();
            int x = figura.getX() + (figura.getLargura() - fm.stringWidth(valor)) / 2;
            int y = figura.getY() + (figura.getAltura() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(valor, x, y);
        }
    }

    private String valorParaFigura(int indiceFigura) {
        if (situacao.getTipo() == gerard.campoaditivo.modelo.TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            if (indiceFigura == 0) return valores[0];
            if (indiceFigura == 1) return valores[1];
            return valores[2];
        }
        return valores[indiceFigura];
    }

    private void desenharMensagemCentral(Graphics2D g2, String mensagem) {
        g2.setColor(gerard.ui.UITemaGerard.COR_TEXTO_SECUNDARIO);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();
        int x = Math.max(10, (getWidth() - fm.stringWidth(mensagem)) / 2);
        int y = Math.max(20, getHeight() / 2);
        g2.drawString(mensagem, x, y);
    }
}
