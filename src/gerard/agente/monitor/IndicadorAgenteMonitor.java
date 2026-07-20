package gerard.agente.monitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Indicador ambiente (ambient/peripheral display — ver
 * instrucao-indicador-agente-monitor.pdf) que pulsa brevemente toda vez que
 * o AgenteMonitor produz um veredito, independente do resultado ser certo
 * ou errado. Comunica só que o agente atuou, não o resultado — isso já tem
 * canal próprio (tremor/som/cor do feedback principal). Por isso a cor do
 * pulso é deliberadamente neutra, diferente de UITemaGerard.COR_SUCESSO.
 *
 * Componente isolado, sem lógica de negócio: só pinta um círculo e reage à
 * notificação do AgenteMonitor. Pode ser removido inteiramente sem afetar
 * o resto do sistema.
 */
public final class IndicadorAgenteMonitor extends JPanel implements OuvinteVeredictoAgenteMonitor {
    private static final int DIAMETRO = 14;
    private static final int DURACAO_PULSO_MS = 400;
    private static final Color COR_APAGADO = new Color(0xBF, 0xBA, 0xAC);
    private static final Color COR_PULSO = new Color(0xB0, 0xC4, 0xDE);

    private boolean aceso;
    private String ultimoVeredito = "";
    private Timer timerPulso;

    public IndicadorAgenteMonitor() {
        setOpaque(false);
        setFocusable(false);
        setPreferredSize(new Dimension(DIAMETRO, DIAMETRO));
        setToolTipText("Agente Monitor");
    }

    @Override
    public void aoAvaliar(boolean correto) {
        ultimoVeredito = new SimpleDateFormat("HH:mm:ss").format(new Date());
        setToolTipText("Agente Monitor — último veredito: " + ultimoVeredito);
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(aceso ? COR_PULSO : COR_APAGADO);
        g2.fillOval(0, 0, DIAMETRO, DIAMETRO);
        g2.dispose();
    }
}
