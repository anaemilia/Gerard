package gerard.ui.vergnaud;

import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.interacao.arraste.AlvoInteracaoEixoInteiros;
import java.awt.Rectangle;

/**
 * Adaptador da representacao desktop para o protocolo portatil do eixo dos
 * inteiros. Mantem Rectangle, dimensoes da tela e hit-testing fora do handler.
 */
public final class AdaptadorInteracaoEixoInteiros
        implements AlvoInteracaoEixoInteiros {

    private final ScaffoldingGraficoInteiros grafico;
    private final FonteGeometriaInteracaoEixoInteiros fonteGeometria;

    public AdaptadorInteracaoEixoInteiros(ScaffoldingGraficoInteiros grafico,
            FonteGeometriaInteracaoEixoInteiros fonteGeometria) {
        if (grafico == null) {
            throw new IllegalArgumentException("grafico obrigatorio");
        }
        if (fonteGeometria == null) {
            throw new IllegalArgumentException("fonte de geometria obrigatoria");
        }
        this.grafico = grafico;
        this.fonteGeometria = fonteGeometria;
    }

    @Override
    public NaturezaInteracao identificarNatureza(int posicaoX, int posicaoY) {
        ScaffoldingGraficoInteiros.NaturezaInteracao natureza =
                grafico.identificarNaturezaInteracao(posicaoX, posicaoY,
                        fonteGeometria.obterLarguraTela(),
                        fonteGeometria.obterAlturaTela(),
                        copiarAreaDiagrama());
        if (natureza == ScaffoldingGraficoInteiros.NaturezaInteracao.VALOR_SEMANTICO) {
            return NaturezaInteracao.VALOR_SEMANTICO;
        }
        if (natureza == ScaffoldingGraficoInteiros.NaturezaInteracao.COMPONENTE_TELA) {
            return NaturezaInteracao.COMPONENTE_VISUAL;
        }
        return NaturezaInteracao.NENHUMA;
    }

    @Override
    public boolean processarPressionamento(int posicaoX, int posicaoY) {
        return grafico.processarPressionamento(posicaoX, posicaoY,
                fonteGeometria.obterLarguraTela(),
                fonteGeometria.obterAlturaTela(),
                copiarAreaDiagrama());
    }

    @Override
    public ModoManipulacao obterModoManipulacao() {
        if (grafico.estaArrastandoPontoControle()) {
            return ModoManipulacao.PONTO_CONTROLE;
        }
        if (grafico.estaArrastandoPainel()) {
            return ModoManipulacao.PAINEL;
        }
        return ModoManipulacao.NENHUM;
    }

    @Override
    public boolean foiOcultadoPorInteracao() {
        return grafico.foiOcultadoPorInteracao();
    }

    @Override
    public boolean houveAlteracaoValorPorInteracao() {
        return grafico.houveAlteracaoValorPorInteracao();
    }

    @Override
    public void moverPara(int posicaoX, int posicaoY) {
        grafico.arrastarPara(posicaoX, posicaoY,
                fonteGeometria.obterLarguraTela(),
                fonteGeometria.obterAlturaTela());
    }

    @Override
    public void finalizarManipulacao() {
        grafico.finalizarArraste();
    }

    public Rectangle obterAreaVisualPontoControle() {
        return grafico.obterAreaVisualPontoControle();
    }

    public Rectangle obterAreaVisualPainel() {
        return grafico.obterAreaVisualPainel();
    }

    private Rectangle copiarAreaDiagrama() {
        Rectangle area = fonteGeometria.obterAreaDiagrama();
        return area == null ? null : new Rectangle(area);
    }
}
