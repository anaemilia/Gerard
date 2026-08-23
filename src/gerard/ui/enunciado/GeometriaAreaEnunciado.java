package gerard.ui.enunciado;

import gerard.campoaditivo.diagrama.elementos.ElementoTextoMovel;
import gerard.interacao.geometria.LimitesMovimento;
import gerard.ui.geometria.NoGeometriaRepresentacao;
import java.awt.Rectangle;

/**
 * Fonte única da geometria concreta do card do enunciado e dos limites de
 * movimento de seus elementos textuais.
 */
public final class GeometriaAreaEnunciado {
    private static final int MARGEM_HORIZONTAL_CARD = 15;
    private static final int TOPO_FAIXA_ATALHOS = 45;
    private static final int ESPACO_APOS_ATALHOS = 10;
    private static final int ALTURA_CARD = 135;
    private static final int MARGEM_ESQUERDA_MOVIMENTO = 20;
    private static final int MARGEM_DIREITA_MOVIMENTO = 25;

    private final int alturaPainelAtalhosCategoria;
    private final NoGeometriaRepresentacao raiz;
    private final NoGeometriaRepresentacao regiaoAtividade;
    private final NoGeometriaRepresentacao cardEnunciado;

    public GeometriaAreaEnunciado(int alturaPainelAtalhosCategoria) {
        this.alturaPainelAtalhosCategoria = alturaPainelAtalhosCategoria;
        raiz = new NoGeometriaRepresentacao(null, new Rectangle());
        regiaoAtividade = new NoGeometriaRepresentacao(
                raiz, new Rectangle());
        cardEnunciado = new NoGeometriaRepresentacao(
                regiaoAtividade, new Rectangle());
    }

    public Rectangle obterArea(int larguraConteiner) {
        atualizarArvore(larguraConteiner);
        return cardEnunciado.obterLimitesAbsolutos();
    }

    public boolean contem(int x, int y, int larguraConteiner) {
        Rectangle area = obterArea(larguraConteiner);
        return x >= area.x && x <= area.x + area.width
                && y >= area.y && y <= area.y + area.height;
    }

    /**
     * Retorna o intervalo permitido para x e para a linha de base y do texto.
     * A representação produz um parâmetro geométrico neutro para o protocolo
     * de interação; o handler não precisa conhecer Rectangle, AWT ou Swing.
     */
    public LimitesMovimento obterLimitesMovimento(ElementoTextoMovel elemento,
            int larguraConteiner) {
        if (elemento == null) {
            throw new IllegalArgumentException("elemento obrigatorio");
        }
        atualizarArvore(larguraConteiner);
        int minimoX = MARGEM_ESQUERDA_MOVIMENTO;
        int maximoX = larguraConteiner - MARGEM_DIREITA_MOVIMENTO
                - elemento.largura;
        int minimoY = obterTopo() + elemento.altura;
        int maximoY = obterBase();
        return new LimitesMovimento(
                minimoX, maximoX, minimoY, maximoY);
    }

    private int obterTopo() {
        return obterAreaAtual().y;
    }

    private int obterBase() {
        Rectangle area = obterAreaAtual();
        return area.y + area.height;
    }

    private Rectangle obterAreaAtual() {
        return cardEnunciado.obterLimitesAbsolutos();
    }

    private void atualizarArvore(int larguraConteiner) {
        raiz.atualizarLimitesLocais(
                new Rectangle(0, 0, larguraConteiner, 0));
        regiaoAtividade.atualizarLimitesLocais(new Rectangle(
                0,
                TOPO_FAIXA_ATALHOS + alturaPainelAtalhosCategoria,
                larguraConteiner,
                0));
        cardEnunciado.atualizarLimitesLocais(new Rectangle(
                MARGEM_HORIZONTAL_CARD,
                ESPACO_APOS_ATALHOS,
                larguraConteiner - 2 * MARGEM_HORIZONTAL_CARD,
                ALTURA_CARD));
    }
}
