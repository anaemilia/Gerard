package gerard.Scaffolding.feedbackerro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Controla a duração de anotações temporárias sem acoplar a regra de domínio
 * aos componentes concretos da tela.
 */
public final class ControladorAnotacaoTemporaria {
    private Timer temporizador;

    public void mostrar(int duracaoMilissegundos,
            Runnable acaoMostrar, final Runnable acaoOcultar) {
        cancelar();
        if (acaoMostrar != null) {
            acaoMostrar.run();
        }
        temporizador = new Timer(Math.max(1, duracaoMilissegundos),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (acaoOcultar != null) {
                            acaoOcultar.run();
                        }
                    }
                });
        temporizador.setRepeats(false);
        temporizador.start();
    }

    public void cancelar() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }

    public boolean estaAtivo() {
        return temporizador != null && temporizador.isRunning();
    }
}
