package gerard.ui.vergnaud;

import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.interacao.arraste.AlvoInteracaoPaineisEixosRelacoes;
import java.awt.Rectangle;

/**
 * Adaptador desktop dos paineis de eixo das Relacoes. Concentra a traducao
 * entre Rectangle/dimensoes reais/hit-testing e a porta neutra usada pelo
 * handler portatil.
 */
public final class AdaptadorInteracaoPaineisEixosRelacoes
        implements AlvoInteracaoPaineisEixosRelacoes {

    private final PaineisEixosRelacoes paineis;
    private final FonteGeometriaInteracaoPaineisEixosRelacoes fonteGeometria;
    private PaineisEixosRelacoes.Painel painelManipulado;
    private boolean ocultadoPorInteracao;

    public AdaptadorInteracaoPaineisEixosRelacoes(
            PaineisEixosRelacoes paineis,
            FonteGeometriaInteracaoPaineisEixosRelacoes fonteGeometria) {
        if (paineis == null) {
            throw new IllegalArgumentException("paineis obrigatorios");
        }
        if (fonteGeometria == null) {
            throw new IllegalArgumentException("fonte de geometria obrigatoria");
        }
        this.paineis = paineis;
        this.fonteGeometria = fonteGeometria;
    }

    @Override
    public NaturezaInteracao identificarNatureza(int posicaoX, int posicaoY) {
        ScaffoldingGraficoInteiros.NaturezaInteracao natureza =
                paineis.identificarNaturezaInteracao(
                        posicaoX, posicaoY,
                        fonteGeometria.obterLarguraTela(),
                        fonteGeometria.obterAlturaTela(),
                        copiarAreaDiagrama());
        if (natureza
                == ScaffoldingGraficoInteiros.NaturezaInteracao.VALOR_SEMANTICO) {
            return NaturezaInteracao.VALOR_SEMANTICO;
        }
        if (natureza
                == ScaffoldingGraficoInteiros.NaturezaInteracao.COMPONENTE_TELA) {
            return NaturezaInteracao.COMPONENTE_VISUAL;
        }
        return NaturezaInteracao.NENHUMA;
    }

    @Override
    public boolean processarPressionamento(int posicaoX, int posicaoY) {
        painelManipulado = null;
        ocultadoPorInteracao = false;
        if (!paineis.processarPressionamento(
                posicaoX, posicaoY,
                fonteGeometria.obterLarguraTela(),
                fonteGeometria.obterAlturaTela(),
                copiarAreaDiagrama())) {
            return false;
        }

        painelManipulado = paineis.encontrarArrastando();
        PaineisEixosRelacoes.Painel painelOcultado =
                paineis.encontrarComOcultacaoPorInteracao();
        if (painelOcultado != null) {
            paineis.ocultarRevelacao(painelOcultado);
            ocultadoPorInteracao = true;
        }
        return true;
    }

    @Override
    public ModoManipulacao obterModoManipulacao() {
        if (painelManipulado == null) {
            return ModoManipulacao.NENHUM;
        }
        if (painelManipulado.grafico.estaArrastandoPontoControle()) {
            return ModoManipulacao.PONTO_CONTROLE;
        }
        if (painelManipulado.grafico.estaArrastandoPainel()) {
            return ModoManipulacao.PAINEL;
        }
        return ModoManipulacao.NENHUM;
    }

    @Override
    public boolean foiOcultadoPorInteracao() {
        return ocultadoPorInteracao;
    }

    @Override
    public void moverPara(int posicaoX, int posicaoY) {
        paineis.arrastarPara(
                posicaoX, posicaoY,
                fonteGeometria.obterLarguraTela(),
                fonteGeometria.obterAlturaTela());
    }

    @Override
    public void finalizarManipulacao() {
        paineis.finalizarArraste();
        painelManipulado = null;
    }

    public Rectangle obterAreaVisualPontoControle() {
        if (painelManipulado == null) {
            return new Rectangle();
        }
        return new Rectangle(
                painelManipulado.grafico.obterAreaVisualPontoControle());
    }

    public Rectangle obterAreaVisualPainel() {
        if (painelManipulado == null) {
            return new Rectangle();
        }
        return new Rectangle(painelManipulado.grafico.obterAreaVisualPainel());
    }

    private Rectangle copiarAreaDiagrama() {
        Rectangle area = fonteGeometria.obterAreaDiagrama();
        return area == null ? null : new Rectangle(area);
    }
}
