package gerard.ui.janela;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JDialog;

/**
 * Dimensiona a comparação entre categorias a partir da janela principal e da
 * área útil do monitor. O diálogo permanece integrado ao Gérard, sem assumir
 * dimensões fixas nem ultrapassar a área disponível.
 */
public final class DimensionadorJanelaComparacaoCategorias {
    static final double FATOR_LARGURA_PROPRIETARIA = 0.92d;
    static final double FATOR_ALTURA_PROPRIETARIA = 0.85d;
    static final double FATOR_MAXIMO_MONITOR = 0.90d;
    private static final Dimension MINIMO_DESEJADO = new Dimension(960, 620);
    private static Dimension ultimaDimensaoDaSessao;

    private DimensionadorJanelaComparacaoCategorias() {
    }

    public static void aplicar(final JDialog dialogo, Window proprietaria) {
        if (dialogo == null) {
            return;
        }
        Rectangle areaUtil = obterAreaUtil(proprietaria, dialogo);
        Rectangle limitesProprietaria = obterLimitesProprietaria(
                proprietaria, areaUtil);
        Dimension dimensao = calcularDimensao(
                limitesProprietaria,
                areaUtil,
                ultimaDimensaoDaSessao);

        dialogo.setMinimumSize(calcularMinimoPossivel(areaUtil));
        dialogo.setSize(dimensao);
        Rectangle posicao = calcularPosicaoCentralizada(
                limitesProprietaria, areaUtil, dimensao);
        dialogo.setLocation(posicao.x, posicao.y);
        registrarMemoriaDaSessao(dialogo, areaUtil);
    }

    public static Dimension calcularDimensao(
            Rectangle proprietaria,
            Rectangle areaUtil,
            Dimension dimensaoMemorizada) {
        Rectangle tela = normalizarArea(areaUtil,
                new Rectangle(0, 0, 1280, 720));
        Rectangle dona = normalizarArea(proprietaria, tela);

        int maxLargura = Math.max(1,
                (int) Math.floor(tela.width * FATOR_MAXIMO_MONITOR));
        int maxAltura = Math.max(1,
                (int) Math.floor(tela.height * FATOR_MAXIMO_MONITOR));

        int largura;
        int altura;
        if (dimensaoMemorizada != null
                && dimensaoMemorizada.width > 0
                && dimensaoMemorizada.height > 0) {
            largura = dimensaoMemorizada.width;
            altura = dimensaoMemorizada.height;
        } else {
            largura = (int) Math.round(
                    dona.width * FATOR_LARGURA_PROPRIETARIA);
            altura = (int) Math.round(
                    dona.height * FATOR_ALTURA_PROPRIETARIA);
        }

        int minimoLargura = Math.min(MINIMO_DESEJADO.width, maxLargura);
        int minimoAltura = Math.min(MINIMO_DESEJADO.height, maxAltura);
        largura = limitar(largura, minimoLargura, maxLargura);
        altura = limitar(altura, minimoAltura, maxAltura);
        return new Dimension(largura, altura);
    }

    public static Rectangle calcularPosicaoCentralizada(
            Rectangle proprietaria,
            Rectangle areaUtil,
            Dimension dimensao) {
        Rectangle tela = normalizarArea(areaUtil,
                new Rectangle(0, 0, 1280, 720));
        Rectangle dona = normalizarArea(proprietaria, tela);
        Dimension tamanho = dimensao == null
                ? new Dimension(1, 1) : dimensao;

        int x = dona.x + (dona.width - tamanho.width) / 2;
        int y = dona.y + (dona.height - tamanho.height) / 2;
        x = limitar(x, tela.x,
                tela.x + Math.max(0, tela.width - tamanho.width));
        y = limitar(y, tela.y,
                tela.y + Math.max(0, tela.height - tamanho.height));
        return new Rectangle(x, y, tamanho.width, tamanho.height);
    }

    private static void registrarMemoriaDaSessao(
            final JDialog dialogo, final Rectangle areaUtilInicial) {
        dialogo.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evento) {
                Component componente = evento.getComponent();
                if (!(componente instanceof JDialog)
                        || !componente.isShowing()) {
                    return;
                }
                Rectangle area = obterAreaUtil(null, dialogo);
                Rectangle referencia = area == null
                        ? areaUtilInicial : area;
                Dimension tamanho = componente.getSize();
                int maxLargura = Math.max(1,
                        (int) Math.floor(referencia.width
                                * FATOR_MAXIMO_MONITOR));
                int maxAltura = Math.max(1,
                        (int) Math.floor(referencia.height
                                * FATOR_MAXIMO_MONITOR));
                ultimaDimensaoDaSessao = new Dimension(
                        Math.min(tamanho.width, maxLargura),
                        Math.min(tamanho.height, maxAltura));
            }
        });
    }

    private static Dimension calcularMinimoPossivel(Rectangle areaUtil) {
        int maxLargura = Math.max(1,
                (int) Math.floor(areaUtil.width * FATOR_MAXIMO_MONITOR));
        int maxAltura = Math.max(1,
                (int) Math.floor(areaUtil.height * FATOR_MAXIMO_MONITOR));
        return new Dimension(
                Math.min(MINIMO_DESEJADO.width, maxLargura),
                Math.min(MINIMO_DESEJADO.height, maxAltura));
    }

    private static Rectangle obterLimitesProprietaria(
            Window proprietaria, Rectangle areaUtil) {
        if (proprietaria != null
                && proprietaria.getWidth() > 0
                && proprietaria.getHeight() > 0) {
            return proprietaria.getBounds();
        }
        return new Rectangle(areaUtil);
    }

    private static Rectangle obterAreaUtil(
            Window proprietaria, Window fallback) {
        GraphicsConfiguration configuracao = proprietaria == null
                ? null : proprietaria.getGraphicsConfiguration();
        if (configuracao == null && fallback != null) {
            configuracao = fallback.getGraphicsConfiguration();
        }
        if (configuracao == null) {
            Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
            return new Rectangle(0, 0, tela.width, tela.height);
        }
        Rectangle limites = new Rectangle(configuracao.getBounds());
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuracao);
        limites.x += insets.left;
        limites.y += insets.top;
        limites.width -= insets.left + insets.right;
        limites.height -= insets.top + insets.bottom;
        return limites;
    }

    private static Rectangle normalizarArea(
            Rectangle area, Rectangle fallback) {
        if (area == null || area.width <= 0 || area.height <= 0) {
            return new Rectangle(fallback);
        }
        return new Rectangle(area);
    }

    private static int limitar(int valor, int minimo, int maximo) {
        if (maximo < minimo) {
            return maximo;
        }
        return Math.max(minimo, Math.min(maximo, valor));
    }
}
