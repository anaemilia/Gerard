package gerard.pesquisador;

import gerard.agente.modelador.AgenteModelador;
import gerard.agente.monitor.AgenteMonitor;
import gerard.agente.zdp.AgenteZDP;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;

/**
 * Faixa embutida na tela principal (TelaGerard) para a pesquisadora observar
 * a atividade dos agentes de Ajuda Adaptativa em tempo real, sentada ao lado
 * do estudante — ver gerard-ajuda-adaptativa. Recolhida por padrão: uma
 * barra fina e neutra, sem animação, para não competir com o
 * IndicadorAgenteMonitor (o único sinal que o estudante deve perceber) nem
 * virar um elemento chamativo na tela do estudante. Expande só quando a
 * pesquisadora clica e se autentica (mesmo mecanismo já usado para abrir a
 * Visão de Pesquisador).
 *
 * Ao contrário de TelaVisaoPesquisador (recriada a cada abertura), esta
 * faixa é criada uma única vez e vive por toda a sessão do Gérard — por
 * isso o PainelAtividadeAgentes interno nunca chama desconectar().
 */
public final class FaixaLateralAtividadeAgentes extends JPanel {

    private static final int LARGURA_COLAPSADA = 10;
    private static final int LARGURA_EXPANDIDA = 380;
    private static final int TOPO_LIVRE_BARRA_PRINCIPAL = 44;

    private final JPanel faixaColapsada;
    private final JPanel painelExpandido;
    private boolean expandido;
    private int ultimaLargura;
    private int ultimaAltura;

    public FaixaLateralAtividadeAgentes(AgenteMonitor agenteMonitor, AgenteZDP agenteZDP,
                                         AgenteModelador agenteModelador, BooleanSupplier autorizador) {
        super(new BorderLayout());
        setOpaque(false);

        faixaColapsada = criarFaixaColapsada(autorizador);
        painelExpandido = criarPainelExpandido(agenteMonitor, agenteZDP, agenteModelador);
        add(faixaColapsada, BorderLayout.CENTER);
    }

    private JPanel criarFaixaColapsada(BooleanSupplier autorizador) {
        JPanel faixa = new JPanel();
        faixa.setBackground(UITemaGerard.COR_TRACEJADO);
        faixa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        faixa.setToolTipText("Atividade dos agentes (pesquisador)");
        faixa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (autorizador.getAsBoolean()) {
                    alternar(true);
                }
            }
        });
        return faixa;
    }

    private JPanel criarPainelExpandido(AgenteMonitor agenteMonitor, AgenteZDP agenteZDP,
                                         AgenteModelador agenteModelador) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(UITemaGerard.COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA, 2));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UITemaGerard.COR_DESTAQUE);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 6));
        JLabel titulo = new JLabel(ServicoLocalizacao.getInstancia().texto("pesq.tab.agentActivity"));
        titulo.setFont(new Font("Arial", Font.BOLD, 13));
        titulo.setForeground(UITemaGerard.COR_TEXTO);
        cabecalho.add(titulo, BorderLayout.CENTER);

        JButton recolher = new JButton("«");
        recolher.setFont(new Font("Arial", Font.BOLD, 13));
        recolher.setFocusable(false);
        recolher.setForeground(UITemaGerard.COR_TEXTO_SECUNDARIO);
        recolher.setBackground(UITemaGerard.COR_SUPERFICIE);
        recolher.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        recolher.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        recolher.setHorizontalAlignment(SwingConstants.CENTER);
        recolher.setToolTipText("Recolher");
        recolher.addActionListener(e -> alternar(false));
        cabecalho.add(recolher, BorderLayout.EAST);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(new PainelAtividadeAgentes(agenteMonitor, agenteZDP, agenteModelador), BorderLayout.CENTER);
        return painel;
    }

    private void alternar(boolean novoEstadoExpandido) {
        if (expandido == novoEstadoExpandido) {
            return;
        }
        expandido = novoEstadoExpandido;
        removeAll();
        add(expandido ? painelExpandido : faixaColapsada, BorderLayout.CENTER);
        reposicionar(ultimaLargura, ultimaAltura);
        revalidate();
        repaint();
    }

    /** Chamado na criação e a cada resize da janela principal. */
    public void reposicionar(int larguraJanela, int alturaJanela) {
        ultimaLargura = larguraJanela;
        ultimaAltura = alturaJanela;
        int largura = expandido ? LARGURA_EXPANDIDA : LARGURA_COLAPSADA;
        int y = expandido ? TOPO_LIVRE_BARRA_PRINCIPAL : 0;
        int altura = Math.max(0, alturaJanela - y);
        setBounds(Math.max(0, larguraJanela - largura), y, largura, altura);
    }
}
