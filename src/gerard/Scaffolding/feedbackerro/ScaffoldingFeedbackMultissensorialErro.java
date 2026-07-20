package gerard.Scaffolding.feedbackerro;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.CirculoVenn;
import gerard.campoaditivo.diagrama.elementos.QuadradinhoVenn;
import java.util.ArrayList;
import java.util.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Feedback multissensorial discreto para uma tentativa semanticamente
 * incompatível: tremor curto no item e som padrão sutil do sistema.
 * A mensagem textual permanece sob responsabilidade da tela, para que seja
 * posicionada ao lado do elemento manipulado e atualizada conforme a categoria.
 */
public final class ScaffoldingFeedbackMultissensorialErro {

    private static final int[] DESLOCAMENTOS_X = {-3, 3, -2, 2, -1, 1, 0};
    private static final int INTERVALO_MS = 45;

    private Timer temporizadorTremor;
    private ItemTextoArrastavel itemEmTremor;
    private CirculoVenn agrupamentoEmTremor;
    private final List<QuadradinhoVenn> quadradinhosEmTremor = new ArrayList<QuadradinhoVenn>();
    private final List<Integer> quadradinhosXBase = new ArrayList<Integer>();
    private int xBase;
    private int indice;
    private Runnable acaoRepaint;
    private Runnable acaoAoConcluir;

    public void sinalizarErro(ItemTextoArrastavel item, Runnable repaint) {
        sinalizarErro(item, repaint, null);
    }

    /**
     * Variante que executa uma ação somente depois de restaurar a posição
     * original do item. É usada por proxies temporários, que precisam continuar
     * visíveis durante todo o tremor antes de serem descartados.
     */
    public void sinalizarErro(ItemTextoArrastavel item, Runnable repaint, Runnable aoConcluir) {
        if (item == null) {
            return;
        }

        pararTremor();
        emitirSomSutil();

        itemEmTremor = item;
        xBase = item.x;
        indice = 0;
        acaoRepaint = repaint;
        acaoAoConcluir = aoConcluir;

        temporizadorTremor = new Timer(INTERVALO_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executarEtapaTremor();
            }
        });
        temporizadorTremor.setRepeats(true);
        temporizadorTremor.start();
    }


    public void sinalizarErro(CirculoVenn agrupamento,
            List<QuadradinhoVenn> quadradinhos, Runnable repaint) {
        if (agrupamento == null) {
            return;
        }

        pararTremor();
        emitirSomSutil();

        agrupamentoEmTremor = agrupamento;
        xBase = agrupamento.x;
        quadradinhosEmTremor.clear();
        quadradinhosXBase.clear();
        if (quadradinhos != null) {
            for (QuadradinhoVenn quadradinho : quadradinhos) {
                if (quadradinho != null) {
                    quadradinhosEmTremor.add(quadradinho);
                    quadradinhosXBase.add(Integer.valueOf(quadradinho.x));
                }
            }
        }
        indice = 0;
        acaoRepaint = repaint;
        acaoAoConcluir = null;

        temporizadorTremor = new Timer(INTERVALO_MS, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executarEtapaTremor();
            }
        });
        temporizadorTremor.setRepeats(true);
        temporizadorTremor.start();
    }

    public void pararTremor() {
        if (temporizadorTremor != null) {
            temporizadorTremor.stop();
            temporizadorTremor = null;
        }
        if (itemEmTremor != null) {
            itemEmTremor.x = xBase;
        }
        if (agrupamentoEmTremor != null) {
            agrupamentoEmTremor.x = xBase;
            for (int i = 0; i < quadradinhosEmTremor.size()
                    && i < quadradinhosXBase.size(); i++) {
                quadradinhosEmTremor.get(i).x = quadradinhosXBase.get(i).intValue();
            }
        }

        Runnable repaintPendente = acaoRepaint;
        Runnable conclusaoPendente = acaoAoConcluir;
        itemEmTremor = null;
        agrupamentoEmTremor = null;
        quadradinhosEmTremor.clear();
        quadradinhosXBase.clear();
        acaoRepaint = null;
        acaoAoConcluir = null;
        indice = 0;

        if (repaintPendente != null) {
            repaintPendente.run();
        }
        if (conclusaoPendente != null) {
            conclusaoPendente.run();
        }
    }

    private void executarEtapaTremor() {
        if ((itemEmTremor == null && agrupamentoEmTremor == null)
                || indice >= DESLOCAMENTOS_X.length) {
            pararTremor();
            return;
        }

        int deslocamento = DESLOCAMENTOS_X[indice];
        if (itemEmTremor != null) {
            itemEmTremor.x = xBase + deslocamento;
        }
        if (agrupamentoEmTremor != null) {
            agrupamentoEmTremor.x = xBase + deslocamento;
            for (int i = 0; i < quadradinhosEmTremor.size()
                    && i < quadradinhosXBase.size(); i++) {
                quadradinhosEmTremor.get(i).x = quadradinhosXBase.get(i).intValue() + deslocamento;
            }
        }
        indice++;
        solicitarRepaint();

        if (indice >= DESLOCAMENTOS_X.length) {
            pararTremor();
        }
    }

    private void emitirSomSutil() {
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (RuntimeException ex) {
            // Ambientes sem dispositivo de áudio mantêm o tremor e o tooltip.
        }
    }

    private void solicitarRepaint() {
        if (acaoRepaint != null) {
            acaoRepaint.run();
        }
    }
}
