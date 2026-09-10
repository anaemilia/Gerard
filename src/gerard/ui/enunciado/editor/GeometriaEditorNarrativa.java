package gerard.ui.enunciado.editor;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.List;

/**
 * Geometria e disposição em grade (quebra de linha) das três áreas do editor
 * de enunciado — frase, saco de palavras comuns, saco de candidatos a
 * organizadores da informação — todas desenhadas dentro da própria cena
 * (mesmo card do enunciado), no estilo de
 * {@code gerard.ui.enunciado.GeometriaAreaEnunciado}: fonte única da
 * geometria, sem conhecer Swing/pintura, só retângulos e posições de peça.
 *
 * Cada quadro recalcula a grade inteira a partir do zero (as peças não têm
 * deslocamento livre persistente como {@code ElementoTextoMovel} — só
 * ocupam a próxima vaga da grade), o que é suficiente aqui porque o editor
 * é um rascunho efêmero, não uma manipulação livre de posição.
 */
public final class GeometriaEditorNarrativa {
    private static final int ESPACAMENTO_ENTRE_SECOES = 34;
    private static final int PADDING_PECA_HORIZONTAL = 14;

    private Rectangle areaFrase = new Rectangle();
    private Rectangle areaComuns = new Rectangle();
    private Rectangle areaOrganizadores = new Rectangle();
    private int alturaTotal;

    /**
     * Recalcula a posição de cada peça nas três listas e as áreas de cada
     * seção. Deve ser chamado a cada quadro, antes de desenhar ou de testar
     * cliques/soltura contra as áreas.
     */
    public void recalcular(FontMetrics fm, int margemX, int yTopo, int larguraMaxima,
            List<PecaPalavraRascunho> frase, List<PecaPalavraRascunho> comuns,
            List<PecaPalavraRascunho> organizadores) {
        int y = yTopo;

        int inicioFrase = y;
        y = layoutLinha(frase, fm, margemX, y, larguraMaxima);
        areaFrase = new Rectangle(margemX, inicioFrase, larguraMaxima, Math.max(y - inicioFrase, obterAlturaLinha(fm)));

        y += ESPACAMENTO_ENTRE_SECOES;
        int inicioComuns = y;
        y = layoutLinha(comuns, fm, margemX, y, larguraMaxima);
        areaComuns = new Rectangle(margemX, inicioComuns, larguraMaxima, Math.max(y - inicioComuns, obterAlturaLinha(fm)));

        y += ESPACAMENTO_ENTRE_SECOES;
        int inicioOrganizadores = y;
        y = layoutLinha(organizadores, fm, margemX, y, larguraMaxima);
        areaOrganizadores = new Rectangle(margemX, inicioOrganizadores, larguraMaxima,
                Math.max(y - inicioOrganizadores, obterAlturaLinha(fm)));

        alturaTotal = (y - yTopo) + obterAlturaLinha(fm);
    }

    /** Posiciona as peças em grade (quebra de linha), devolvendo o y logo abaixo da última linha usada. */
    private int layoutLinha(List<PecaPalavraRascunho> pecas, FontMetrics fm,
            int margemX, int yTopo, int larguraMaxima) {
        int alturaLinha = obterAlturaLinha(fm);
        int x = margemX;
        int y = yTopo + fm.getHeight();
        for (PecaPalavraRascunho peca : pecas) {
            peca.atualizarTamanho(fm);
            int larguraPeca = peca.largura + PADDING_PECA_HORIZONTAL;
            if (x + larguraPeca > margemX + larguraMaxima && x > margemX) {
                x = margemX;
                y += alturaLinha;
            }
            peca.x = x + PADDING_PECA_HORIZONTAL / 2;
            peca.y = y;
            x += larguraPeca;
        }
        return pecas.isEmpty() ? yTopo + alturaLinha : y;
    }

    public int obterAlturaLinha(FontMetrics fm) {
        return fm.getHeight() + 12;
    }

    public Rectangle obterAreaFrase() { return areaFrase; }
    public Rectangle obterAreaComuns() { return areaComuns; }
    public Rectangle obterAreaOrganizadores() { return areaOrganizadores; }
    public int obterAlturaTotal() { return alturaTotal; }

    /** "FRASE", "COMUM", "ORGANIZADORES" ou null se o ponto não está em nenhuma área conhecida. */
    public String obterSacoNoPonto(int x, int y) {
        if (areaFrase.contains(x, y)) return "FRASE";
        if (areaComuns.contains(x, y)) return "COMUM";
        if (areaOrganizadores.contains(x, y)) return "ORGANIZADORES";
        return null;
    }

    /**
     * Índice de inserção dentro de uma lista já disposta em grade por
     * {@link #recalcular}, a partir da posição do mouse — heurística de
     * linha/coluna: primeira peça cuja linha está na altura do mouse (ou
     * depois dela) e cujo centro horizontal está à direita do mouse.
     */
    public static int calcularIndiceInsercao(List<PecaPalavraRascunho> pecas, int mouseX, int mouseY, int alturaLinha) {
        for (int i = 0; i < pecas.size(); i++) {
            PecaPalavraRascunho peca = pecas.get(i);
            if (mouseY < peca.y - alturaLinha) {
                return i;
            }
            boolean mesmaLinha = mouseY >= peca.y - alturaLinha && mouseY <= peca.y + 6;
            if (mesmaLinha && mouseX < peca.x + peca.largura / 2) {
                return i;
            }
        }
        return pecas.size();
    }
}
