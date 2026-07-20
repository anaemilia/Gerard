package gerard.Scaffolding.arraste;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Implementa o ciclo comum de uma mola amortecida. As subclasses definem a
 * intensidade da rigidez, do amortecimento e do atraso máximo permitido.
 */
public abstract class ControladorArrasteElasticoAbstrato
        implements ControladorArrasteElastico {

    private final Timer temporizador;
    private OuvinteArrasteElastico ouvinte;
    private boolean ativo;
    private double atualX;
    private double atualY;
    private double alvoX;
    private double alvoY;
    private double velocidadeX;
    private double velocidadeY;

    protected ControladorArrasteElasticoAbstrato() {
        temporizador = new Timer(obterIntervaloMilissegundos(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executarPasso();
            }
        });
        temporizador.setCoalesce(true);
    }

    protected abstract double obterRigidez();
    protected abstract double obterAmortecimento();
    protected abstract double obterMassa();
    protected abstract double obterVelocidadeMaxima();
    protected abstract double obterAtrasoMaximo();

    protected int obterIntervaloMilissegundos() {
        return 16;
    }

    @Override
    public final void iniciar(int x, int y, OuvinteArrasteElastico novoOuvinte) {
        cancelar();
        atualX = x;
        atualY = y;
        alvoX = x;
        alvoY = y;
        velocidadeX = 0.0d;
        velocidadeY = 0.0d;
        ouvinte = novoOuvinte;
        ativo = novoOuvinte != null;
        if (ativo) {
            temporizador.start();
        }
    }

    @Override
    public final void atualizarAlvo(int x, int y) {
        if (!ativo) {
            return;
        }
        alvoX = x;
        alvoY = y;
        limitarDistanciaVisual();
        // Um passo imediato mantém a resposta perceptível mesmo antes do
        // próximo ciclo de 16 ms do Swing.
        executarPasso();
    }

    private void limitarDistanciaVisual() {
        double dx = alvoX - atualX;
        double dy = alvoY - atualY;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        double limite = Math.max(1.0d, obterAtrasoMaximo());
        if (distancia <= limite) {
            return;
        }
        double proporcao = limite / distancia;
        atualX = alvoX - dx * proporcao;
        atualY = alvoY - dy * proporcao;
        velocidadeX = 0.0d;
        velocidadeY = 0.0d;
    }

    private void executarPasso() {
        if (!ativo || ouvinte == null) {
            return;
        }

        double dx = alvoX - atualX;
        double dy = alvoY - atualY;

        // Modelo massa-mola-amortecedor discreto. A massa torna explícita a
        // inércia visual, enquanto o amortecimento evita oscilações longas.
        double massa = Math.max(0.05d, obterMassa());
        double aceleracaoX = dx * obterRigidez() / massa;
        double aceleracaoY = dy * obterRigidez() / massa;
        velocidadeX = (velocidadeX + aceleracaoX) * obterAmortecimento();
        velocidadeY = (velocidadeY + aceleracaoY) * obterAmortecimento();
        limitarVelocidade();
        atualX += velocidadeX;
        atualY += velocidadeY;

        if (Math.abs(alvoX - atualX) < 0.18d
                && Math.abs(alvoY - atualY) < 0.18d
                && Math.abs(velocidadeX) < 0.18d
                && Math.abs(velocidadeY) < 0.18d) {
            atualX = alvoX;
            atualY = alvoY;
            velocidadeX = 0.0d;
            velocidadeY = 0.0d;
        }

        ouvinte.aoAtualizarPosicao((int) Math.round(atualX),
                (int) Math.round(atualY));
    }


    private void limitarVelocidade() {
        double limite = Math.max(1.0d, obterVelocidadeMaxima());
        double modulo = Math.sqrt(velocidadeX * velocidadeX
                + velocidadeY * velocidadeY);
        if (modulo <= limite) {
            return;
        }
        double proporcao = limite / modulo;
        velocidadeX *= proporcao;
        velocidadeY *= proporcao;
    }

    @Override
    public final void concluir(int x, int y) {
        if (!ativo) {
            return;
        }
        alvoX = x;
        alvoY = y;
        atualX = x;
        atualY = y;
        velocidadeX = 0.0d;
        velocidadeY = 0.0d;
        OuvinteArrasteElastico ouvinteAtual = ouvinte;
        temporizador.stop();
        ativo = false;
        ouvinte = null;
        if (ouvinteAtual != null) {
            ouvinteAtual.aoAtualizarPosicao(x, y);
        }
    }

    @Override
    public final void cancelar() {
        temporizador.stop();
        ativo = false;
        ouvinte = null;
        velocidadeX = 0.0d;
        velocidadeY = 0.0d;
    }

    @Override
    public final boolean estaAtivo() {
        return ativo;
    }

    @Override
    public final Point obterPosicaoVisual() {
        return new Point((int) Math.round(atualX), (int) Math.round(atualY));
    }
}
