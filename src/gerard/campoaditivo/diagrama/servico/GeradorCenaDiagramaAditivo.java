package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.DirecaoDeslocamentoDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
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

    /**
     * Direção em que o diagrama deve ser visualmente deslocado (via margem
     * assimétrica no viewport, ver ServicoSorteioAtividadeWeb.projetarViewport)
     * quando o material concreto desta categoria está disponível — nunca uma
     * posição fixa igual para todas. Cada valor aqui vem de uma instrução
     * explícita da usuária, comparando a referência do desktop (onde o
     * "indicador de sucesso" colide com o material concreto em pontos
     * diferentes por categoria) com o protótipo web:
     * - COMPOSICAO_MEDIDAS: ESQUERDA (2026-09-04, captura do desktop —
     *   indicador de sucesso sobrepõe o material concreto à direita).
     * - TRANSFORMACAO_MEDIDAS: DIREITA (2026-09-04, captura do desktop —
     *   mesma ideia, mas a colisão real fica do lado oposto nesta categoria).
     * As demais categorias ainda não têm material concreto portado para o
     * web (ver comentário em ServicoSorteioAtividadeWeb.projetarAjudaContextual)
     * e não têm referência confirmada de direção — permanecem SEM_DESLOCAMENTO
     * até serem verificadas contra o desktop, em vez de inventar um valor.
     */
    public DirecaoDeslocamentoDiagrama direcaoDeslocamentoParaMaterialConcreto(
            TipoSituacaoAditiva tipo) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return DirecaoDeslocamentoDiagrama.PARA_ESQUERDA;
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            return DirecaoDeslocamentoDiagrama.PARA_DIREITA;
        }
        return DirecaoDeslocamentoDiagrama.SEM_DESLOCAMENTO;
    }

    /** Deriva uma célula de interação das posições relativas da cena. */
    public AreaDiagrama projetarZonaFigura(CenaDiagramaAditivo cena,
            AreaDiagrama limite, int indiceFigura) {
        if (cena == null || limite == null || indiceFigura < 0
                || indiceFigura >= cena.getFiguras().size()) return limite;
        FiguraDiagrama figura = cena.getFiguras().get(indiceFigura);
        int centroX = figura.getX() + figura.getLargura() / 2;
        int centroY = figura.getY() + figura.getAltura() / 2;
        int esquerda = limite.x, direita = limite.x + limite.largura;
        int topo = limite.y, base = limite.y + limite.altura;
        for (int i = 0; i < cena.getFiguras().size(); i++) {
            if (i == indiceFigura) continue;
            FiguraDiagrama outra = cena.getFiguras().get(i);
            int dx = outra.getX() + outra.getLargura() / 2 - centroX;
            int dy = outra.getY() + outra.getAltura() / 2 - centroY;
            if (Math.abs(dx) >= Math.abs(dy)) {
                int fronteira = centroX + dx / 2;
                if (dx < 0) esquerda = Math.max(esquerda, fronteira);
                else if (dx > 0) direita = Math.min(direita, fronteira);
            } else {
                int fronteira = centroY + dy / 2;
                if (dy < 0) topo = Math.max(topo, fronteira);
                else if (dy > 0) base = Math.min(base, fronteira);
            }
        }
        return recortarIncluindoFigura(limite, figura,
                esquerda, topo, direita, base);
    }

    public AreaDiagrama projetarZonaConector(CenaDiagramaAditivo cena,
            AreaDiagrama limite, int indiceConector) {
        if (cena == null || limite == null || indiceConector < 0
                || indiceConector >= cena.getConectores().size()) return limite;
        ConectorDiagrama conector = cena.getConectores().get(indiceConector);
        int margemX = Math.max(8, limite.largura / 20);
        int margemY = Math.max(8, limite.altura / 20);
        int esquerda = Math.min(conector.getX1(), conector.getX2());
        int direita = Math.max(conector.getX1(), conector.getX2());
        int topo = Math.min(conector.getY1(), conector.getY2());
        int base = Math.max(conector.getY1(), conector.getY2());
        if (conector.temAlvo()) {
            esquerda = Math.min(esquerda, conector.getXAlvo());
            direita = Math.max(direita, conector.getXAlvo());
            topo = Math.min(topo, conector.getYAlvo());
            base = Math.max(base, conector.getYAlvo());
        }
        return recortar(limite, esquerda - margemX, topo - margemY,
                direita + margemX, base + margemY);
    }

    private static AreaDiagrama recortarIncluindoFigura(AreaDiagrama limite,
            FiguraDiagrama figura, int esquerda, int topo, int direita,
            int base) {
        esquerda = Math.min(esquerda, figura.getX());
        direita = Math.max(direita, figura.getX() + figura.getLargura());
        topo = Math.min(topo, figura.getY());
        base = Math.max(base, figura.getY() + figura.getAltura());
        return recortar(limite, esquerda, topo, direita, base);
    }

    private static AreaDiagrama recortar(AreaDiagrama limite, int esquerda,
            int topo, int direita, int base) {
        int x = Math.max(limite.x, esquerda);
        int y = Math.max(limite.y, topo);
        int maxX = Math.min(limite.x + limite.largura, direita);
        int maxY = Math.min(limite.y + limite.altura, base);
        return new AreaDiagrama(x, y, Math.max(0, maxX - x),
                Math.max(0, maxY - y));
    }
}
