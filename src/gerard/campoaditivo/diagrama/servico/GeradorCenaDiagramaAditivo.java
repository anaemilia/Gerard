package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.NormalizadorRotulosSemanticosDiagrama;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Rectangle;

public class GeradorCenaDiagramaAditivo {
    private final FabricaRenderizadoresDiagramaAditivo fabrica;
    private final NormalizadorRotulosSemanticosDiagrama normalizadorRotulos;

    public GeradorCenaDiagramaAditivo() {
        this.fabrica = new FabricaRenderizadoresDiagramaAditivo();
        this.normalizadorRotulos = new NormalizadorRotulosSemanticosDiagrama();
    }

    public CenaDiagramaAditivo gerar(TipoSituacaoAditiva tipo, Rectangle area, DefinicaoDiagramaAditivo definicao, int[] valores) {
        if (area == null) throw new IllegalArgumentException("área é obrigatória");
        return gerar(tipo, new AreaDiagrama(area.x, area.y, area.width, area.height),
                definicao, valores);
    }

    public CenaDiagramaAditivo gerar(TipoSituacaoAditiva tipo, AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores) {
        RenderizadorDiagramaAditivo renderizador = fabrica.obter(tipo);
        if (renderizador == null) {
            renderizador = fabrica.obter(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        }
        DefinicaoDiagramaAditivo definicaoNormalizada =
                normalizadorRotulos.garantir(tipo, definicao,
                        ServicoLocalizacao.getInstancia());
        return renderizador.criarCena(area, definicaoNormalizada, valores);
    }
}
