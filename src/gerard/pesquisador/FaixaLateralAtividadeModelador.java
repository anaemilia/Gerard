package gerard.pesquisador;

import gerard.agente.modelador.AgenteModelador;
import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BooleanSupplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Faixa autenticada para acompanhar a atividade factual do Modelador. */
public final class FaixaLateralAtividadeModelador extends JPanel {

    private static final int LARGURA_COLAPSADA = 10;
    private static final int LARGURA_EXPANDIDA = 380;
    private static final int TOPO_LIVRE_BARRA_PRINCIPAL = 44;

    private final JPanel faixaColapsada;
    private final JPanel painelExpandido;
    private boolean expandido;
    private int ultimaLargura;
    private int ultimaAltura;

    public FaixaLateralAtividadeModelador(
            AgenteModelador agenteModelador,
            BooleanSupplier autorizador) {
        super(new BorderLayout());
        setOpaque(false);
        faixaColapsada = criarFaixaColapsada(autorizador);
        painelExpandido = criarPainelExpandido(agenteModelador);
        add(faixaColapsada, BorderLayout.CENTER);
    }

    private JPanel criarFaixaColapsada(final BooleanSupplier autorizador) {
        JPanel faixa = new JPanel();
        faixa.setBackground(UITemaGerard.COR_TRACEJADO);
        faixa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        faixa.setToolTipText("Atividade do Modelador (pesquisador)");
        faixa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (autorizador.getAsBoolean()) {
                    alternar(true);
                }
            }
        });
        return faixa;
    }

    private JPanel criarPainelExpandido(AgenteModelador agenteModelador) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(UITemaGerard.COR_SUPERFICIE);
        painel.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA, 2));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(UITemaGerard.COR_DESTAQUE);
        cabecalho.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 6));
        JLabel titulo = new JLabel(
                ServicoLocalizacao.getInstancia().texto("pesq.tab.agentActivity"));
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
        recolher.addActionListener(evento -> alternar(false));
        cabecalho.add(recolher, BorderLayout.EAST);

        painel.add(cabecalho, BorderLayout.NORTH);
        painel.add(new PainelAtividadeModelador(agenteModelador), BorderLayout.CENTER);
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

    public void reposicionar(int larguraJanela, int alturaJanela) {
        ultimaLargura = larguraJanela;
        ultimaAltura = alturaJanela;
        int largura = expandido ? LARGURA_EXPANDIDA : LARGURA_COLAPSADA;
        int y = expandido ? TOPO_LIVRE_BARRA_PRINCIPAL : 0;
        int altura = Math.max(0, alturaJanela - y);
        setBounds(Math.max(0, larguraJanela - largura), y, largura, altura);
    }
}
