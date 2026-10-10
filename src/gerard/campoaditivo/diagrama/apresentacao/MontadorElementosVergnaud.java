package gerard.campoaditivo.diagrama.apresentacao;

import java.awt.Rectangle;
import java.util.List;

import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.servico.GeradorCenaDiagramaAditivo;

/**
 * Realiza a cena do diagrama como elementos e conectores manipuláveis: aplica o deslocamento de centralização,
 * calcula a zona permitida de cada um (projetada pelo gerador de cena e limitada à área) e marca a incógnita.
 * Não decide nada: a cena, a área, o deslocamento e o índice da incógnita chegam prontos.
 */
public final class MontadorElementosVergnaud {
    private final GeradorCenaDiagramaAditivo gerador;

    public MontadorElementosVergnaud(GeradorCenaDiagramaAditivo gerador) {
        this.gerador = gerador;
    }

    public void montar(CenaDiagramaAditivo cena, Rectangle area, int dx, int dy, int indiceIncognita,
            List<ConectorVergnaud> conectores, List<ElementoVergnaud> elementos) {
        for (int i = 0; i < cena.getConectores().size(); i++) {
            ConectorDiagrama conector = cena.getConectores().get(i);
            Rectangle zona = deslocarZona(zonaConector(cena, area, i), dx, dy, area);
            if (conector.temAlvo()) {
                conectores.add(new ConectorVergnaud(
                        conector.getTipo(),
                        conector.getX1() + dx,
                        conector.getY1() + dy,
                        conector.getX2() + dx,
                        conector.getY2() + dy,
                        conector.getLegenda(),
                        zona,
                        conector.getXAlvo() + dx,
                        conector.getYAlvo() + dy
                ));
            } else {
                conectores.add(new ConectorVergnaud(
                        conector.getTipo(),
                        conector.getX1() + dx,
                        conector.getY1() + dy,
                        conector.getX2() + dx,
                        conector.getY2() + dy,
                        conector.getLegenda(),
                        zona
                ));
            }
        }

        for (int i = 0; i < cena.getFiguras().size(); i++) {
            FiguraDiagrama figura = cena.getFiguras().get(i);
            Rectangle zona = deslocarZona(
                    zonaElemento(cena, area, i, figura.getLargura(), figura.getAltura()), dx, dy, area);
            elementos.add(new ElementoVergnaud(
                    figura.getX() + dx,
                    figura.getY() + dy,
                    figura.getLargura(),
                    figura.getAltura(),
                    figura.getTipo(),
                    figura.getRotulo(),
                    zona,
                    i == indiceIncognita,
                    figura.isExibirLupa(),
                    figura.getChavePapelSemantico()
            ));
        }
    }

    private Rectangle zonaElemento(CenaDiagramaAditivo cena, Rectangle limite,
            int indiceElemento, int largura, int altura) {
        AreaDiagrama projetada = gerador.projetarZonaFigura(cena,
                new AreaDiagrama(limite.x, limite.y, limite.width, limite.height), indiceElemento);
        Rectangle zonaBase = new Rectangle(projetada.x, projetada.y, projetada.largura, projetada.altura);
        int margemX = 12;
        int margemY = 12;
        int x = zonaBase.x + margemX;
        int y = zonaBase.y + margemY;
        int w = Math.max(largura + 18, zonaBase.width - (margemX * 2));
        int h = Math.max(altura + 18, zonaBase.height - (margemY * 2));
        int maxW = limite.x + limite.width - x;
        int maxH = limite.y + limite.height - y;
        return new Rectangle(x, y, Math.max(largura, Math.min(w, maxW)), Math.max(altura, Math.min(h, maxH)));
    }

    private Rectangle zonaConector(CenaDiagramaAditivo cena, Rectangle limite, int indiceConector) {
        AreaDiagrama projetada = gerador.projetarZonaConector(cena,
                new AreaDiagrama(limite.x, limite.y, limite.width, limite.height), indiceConector);
        return new Rectangle(projetada.x, projetada.y, projetada.largura, projetada.altura);
    }

    private static Rectangle deslocarZona(Rectangle zona, int dx, int dy, Rectangle limite) {
        Rectangle deslocada = new Rectangle(zona.x + dx, zona.y + dy, zona.width, zona.height);

        if (deslocada.width > limite.width) {
            deslocada.width = limite.width;
        }
        if (deslocada.height > limite.height) {
            deslocada.height = limite.height;
        }

        if (deslocada.x < limite.x) {
            deslocada.x = limite.x;
        }
        if (deslocada.y < limite.y) {
            deslocada.y = limite.y;
        }
        if (deslocada.x + deslocada.width > limite.x + limite.width) {
            deslocada.x = limite.x + limite.width - deslocada.width;
        }
        if (deslocada.y + deslocada.height > limite.y + limite.height) {
            deslocada.y = limite.y + limite.height - deslocada.height;
        }

        return deslocada;
    }
}
