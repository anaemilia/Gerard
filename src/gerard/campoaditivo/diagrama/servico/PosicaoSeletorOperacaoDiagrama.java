package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;

/**
 * Ponto central (cx,cy) onde o seletor Soma/Subtração de
 * gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno.ativar() se posiciona no
 * desktop — mesmas fórmulas (constantes replicadas de propósito, mesmos
 * valores), só reescritas em termos de FiguraDiagrama/ConectorDiagrama (o
 * modelo de cena compartilhado com o cliente web) em vez de ElementoVergnaud
 * (modelo exclusivo do desktop). Serve de fonte única tanto para o cálculo
 * do viewport da cena (ServicoSorteioAtividadeWeb.projetarViewport) quanto
 * para a posição real dos botões enviada ao cliente — o cliente não deriva
 * mais essa geometria por conta própria.
 */
public final class PosicaoSeletorOperacaoDiagrama {
    private static final int RAIO_BOTAO = 9;
    private static final int ESPACAMENTO_BOTOES = 70;
    private static final int ELEVACAO_ACIMA_DO_SEGMENTO = RAIO_BOTAO * 7;
    private static final int DESLOCAMENTO_ESQUERDA_ESTADO_TRANSFORMACAO = 110;
    private static final int DESLOCAMENTO_TRACO_CHAVE = 18;

    private PosicaoSeletorOperacaoDiagrama() {
    }

    public static final class Centro {
        public final int cx;
        public final int cy;

        public Centro(int cx, int cy) {
            this.cx = cx;
            this.cy = cy;
        }
    }

    /** Acima dos dois círculos superiores — primeira operação de Composição de Transformações. */
    public static Centro entreTransformacoes(FiguraDiagrama t1, FiguraDiagrama t2) {
        int cx = (centroX(t1) + centroX(t2)) / 2;
        int cy = Math.min(t1.getY(), t2.getY()) - ELEVACAO_ACIMA_DO_SEGMENTO;
        return new Centro(cx, cy);
    }

    /** À esquerda do círculo inferior (transformação resultante) — segunda operação. */
    public static Centro entreEstadoETransformacao(FiguraDiagrama transformacaoFinal) {
        return new Centro(transformacaoFinal.getX() - DESLOCAMENTO_ESQUERDA_ESTADO_TRANSFORMACAO,
                centroY(transformacaoFinal));
    }

    /** Acima do segmento/haste até a relação final — Transformação de Relação e Composição de Relações. */
    public static Centro relacao(ConectorDiagrama conectorParaRelacaoFinal) {
        int meioX = (conectorParaRelacaoFinal.getX1() + conectorParaRelacaoFinal.getX2()) / 2;
        int meioY = (conectorParaRelacaoFinal.getY1() + conectorParaRelacaoFinal.getY2()) / 2;
        int cx, cy;
        if (conectorParaRelacaoFinal.temAlvo()) {
            meioX += DESLOCAMENTO_TRACO_CHAVE;
            cx = (meioX + conectorParaRelacaoFinal.getXAlvo()) / 2;
            cy = (meioY + conectorParaRelacaoFinal.getYAlvo()) / 2;
        } else {
            cx = meioX;
            cy = meioY;
        }
        return new Centro(cx, cy - ELEVACAO_ACIMA_DO_SEGMENTO);
    }

    /** Metade da largura ocupada pelos dois botões (centro até a borda externa de qualquer um deles). */
    public static int meiaLargura() {
        return ESPACAMENTO_BOTOES / 2 + RAIO_BOTAO;
    }

    public static int raioBotao() {
        return RAIO_BOTAO;
    }

    public static int espacamentoBotoes() {
        return ESPACAMENTO_BOTOES;
    }

    private static int centroX(FiguraDiagrama f) {
        return f.getX() + f.getLargura() / 2;
    }

    private static int centroY(FiguraDiagrama f) {
        return f.getY() + f.getAltura() / 2;
    }
}
