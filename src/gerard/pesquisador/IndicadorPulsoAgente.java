package gerard.pesquisador;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * LED genérico que pulsa brevemente toda vez que pulsar() é chamado — mesmo
 * mecanismo de gerard.agente.monitor.IndicadorAgenteMonitor (ambient/
 * peripheral display), mas com a cor configurável no construtor em vez de
 * fixa, para reaproveitar entre os três agentes da Ajuda Adaptativa (Monitor,
 * ZDP, Modelador) sem duplicar a lógica do timer três vezes.
 *
 * Ao contrário de IndicadorAgenteMonitor, este componente não implementa
 * nenhuma interface de ouvinte de agente específica — quem instancia conecta
 * o ouvinte do agente correspondente e chama pulsar() de dentro dele. Isso
 * mantém este LED genérico e reutilizável.
 */
public final class IndicadorPulsoAgente extends JPanel {
    // Era 14 (círculo simples); o ícone de robô precisa de mais espaço pros
    // detalhes (antena, orelhas, olhos, boca) ficarem legíveis — decisão da
    // usuária, 2026-07-28.
    private static final int DIAMETRO = 22;
    private static final int DURACAO_PULSO_MS = 400;

    private final Color corApagado;
    private final Color corAceso;
    private final String tooltipBase;

    private boolean aceso;
    private Timer timerPulso;

    public IndicadorPulsoAgente(Color corAceso, Color corApagado, String tooltipBase) {
        this.corAceso = corAceso;
        this.corApagado = corApagado;
        this.tooltipBase = tooltipBase;
        setOpaque(false);
        setFocusable(false);
        setPreferredSize(new Dimension(DIAMETRO, DIAMETRO));
        setToolTipText(tooltipBase);
    }

    /** Chamado pelo ouvinte do agente correspondente a cada evento — acende o LED por DURACAO_PULSO_MS. */
    public void pulsar(String detalhe) {
        setToolTipText(detalhe == null || detalhe.isEmpty() ? tooltipBase : tooltipBase + " " + detalhe);
        aceso = true;
        repaint();
        if (timerPulso != null) {
            timerPulso.stop();
        }
        timerPulso = new Timer(DURACAO_PULSO_MS, e -> {
            aceso = false;
            repaint();
        });
        timerPulso.setRepeats(false);
        timerPulso.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        gerard.ui.IconeRoboAgente.desenhar(g2, 0, 0, DIAMETRO, aceso ? corAceso : corApagado);
        g2.dispose();
    }
}
