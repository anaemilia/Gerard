import gerard.Scaffolding.arraste.ControladorArrasteElastico;
import gerard.Scaffolding.arraste.ControladorArrasteMolaMomento;
import gerard.Scaffolding.arraste.DesenhavelFantasmaOrigem;
import gerard.Scaffolding.arraste.MarcadorOrigemArraste;
import gerard.Scaffolding.arraste.MarcadorOrigemArrasteTracejado;
import gerard.Scaffolding.arraste.OuvinteArrasteElastico;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class TesteArrasteFisico {
    public static void main(String[] args) throws Exception {
        final Throwable[] erro = new Throwable[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    testarMola();
                    testarFantasma();
                    System.out.println("TesteArrasteFisico: OK");
                } catch (Throwable t) {
                    erro[0] = t;
                }
            }
        });
        if (erro[0] != null) {
            throw new RuntimeException(erro[0]);
        }
    }

    private static void testarMola() {
        final Point[] ultima = new Point[1];
        ControladorArrasteElastico controlador = new ControladorArrasteMolaMomento();
        controlador.iniciar(10, 20, new OuvinteArrasteElastico() {
            @Override
            public void aoAtualizarPosicao(int x, int y) {
                ultima[0] = new Point(x, y);
            }
        });
        controlador.atualizarAlvo(110, 20);
        Point visual = controlador.obterPosicaoVisual();
        exigir(visual.x > 10, "A posição visual deve perseguir o cursor.");
        exigir(visual.x < 110, "O seguimento deve manter atraso elástico discreto.");
        exigir(110 - visual.x <= 26,
                "O atraso não pode ultrapassar o limite de 26 px.");
        exigir(ultima[0] != null && ultima[0].equals(visual),
                "O ouvinte deve receber a posição suavizada.");

        int primeiraPosicao = visual.x;
        controlador.atualizarAlvo(110, 20);
        Point visualComMomento = controlador.obterPosicaoVisual();
        exigir(visualComMomento.x > primeiraPosicao,
                "A massa e a inércia devem manter o movimento em direção ao alvo.");
        exigir(visualComMomento.x < 110,
                "O segundo passo ainda deve preservar o atraso elástico.");

        controlador.atualizarAlvo(35, 20);
        Point visualReversao = controlador.obterPosicaoVisual();
        exigir(Math.abs(35 - visualReversao.x) <= 26,
                "A reversão do cursor deve respeitar o atraso máximo.");
        exigir(visualReversao.x != 35,
                "A reversão não deve teleportar o elemento para o cursor.");

        controlador.concluir(110, 20);
        exigir(!controlador.estaAtivo(), "A mola deve terminar na soltura.");
        exigir(ultima[0].equals(new Point(110, 20)),
                "A soltura deve usar a posição exata do cursor.");
    }

    private static void testarFantasma() {
        MarcadorOrigemArraste marcador = new MarcadorOrigemArrasteTracejado();
        marcador.iniciar(new DesenhavelFantasmaOrigem() {
            @Override
            public Rectangle obterLimitesOrigem() {
                return new Rectangle(18, 18, 30, 24);
            }

            @Override
            public void desenharContorno(Graphics2D g2) {
                g2.drawRoundRect(18, 18, 30, 24, 8, 8);
            }
        });
        exigir(marcador.estaAtivo(), "O marcador de origem deve ficar ativo.");

        BufferedImage imagem = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagem.createGraphics();
        marcador.desenhar(g2);
        g2.dispose();
        int pixels = 0;
        for (int y = 0; y < imagem.getHeight(); y++) {
            for (int x = 0; x < imagem.getWidth(); x++) {
                if ((imagem.getRGB(x, y) >>> 24) != 0) {
                    pixels++;
                }
            }
        }
        exigir(pixels > 20, "O contorno fantasma não foi desenhado.");
        marcador.limpar();
        exigir(!marcador.estaAtivo(), "O fantasma deve desaparecer após a soltura.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
