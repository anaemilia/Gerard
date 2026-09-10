package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.diagrama.modelo.CenaDiagramaAditivo;
import gerard.campoaditivo.diagrama.modelo.AreaDiagrama;
import gerard.campoaditivo.diagrama.modelo.DirecaoDeslocamentoDiagrama;
import gerard.campoaditivo.diagrama.modelo.FiguraDiagrama;
import gerard.campoaditivo.diagrama.modelo.ConectorDiagrama;
import gerard.campoaditivo.diagrama.modelo.EstadoFeedbackDiagrama;
import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.semantica.NormalizadorRotulosSemanticosDiagrama;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Rectangle;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.SegmentadorTextoSemantico;
import gerard.interpretacao.modelo.SegmentoTextoSemantico;
import gerard.interpretacao.modelo.VocabularioOrganizadoresInformacao;
import gerard.campoaditivo.diagrama.modelo.VocabularioTextoNarrativo;
import java.util.List;

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

    /** Produz uma nova cena com feedback sem distribuir estado pelos componentes. */
    public CenaDiagramaAditivo comFeedback(CenaDiagramaAditivo cena,
            EstadoFeedbackDiagrama estado) {
        return cena == null ? null : cena.comEstadoFeedback(estado);
    }

    public CenaDiagramaAditivo gerar(TipoSituacaoAditiva tipo, AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores) {
        return gerar(tipo, area, definicao, valores, false);
    }

    /**
     * @param estadoInicialDecomposto só tem efeito para COMPOSICAO_TRANSFORMACOES
     * (ver SituacaoProblemaAditiva.getEstadoInicialParte1) — quando true,
     * desenha o estado inicial como Parte1+Parte2=Todo (ver
     * RenderizadorComposicaoTransformacoes.criarCenaComEstadoInicialDecomposto).
     * Ignorado por qualquer outra categoria. Downcast confinado aqui — a
     * fábrica é o único lugar que já conhece o tipo concreto de cada
     * renderizador; a interface RenderizadorDiagramaAditivo compartilhada
     * pelos outros 5 renderizadores não precisa saber deste caso especial.
     */
    public CenaDiagramaAditivo gerar(TipoSituacaoAditiva tipo, AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores, boolean estadoInicialDecomposto) {
        RenderizadorDiagramaAditivo renderizador = fabrica.obter(tipo);
        if (renderizador == null) {
            renderizador = fabrica.obter(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        }
        DefinicaoDiagramaAditivo definicaoNormalizada =
                normalizadorRotulos.garantir(tipo, definicao,
                        ServicoLocalizacao.getInstancia());
        if (estadoInicialDecomposto
                && tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES
                && renderizador instanceof RenderizadorComposicaoTransformacoes) {
            return ((RenderizadorComposicaoTransformacoes) renderizador)
                    .criarCenaComEstadoInicialDecomposto(area, definicaoNormalizada, valores);
        }
        return renderizador.criarCena(area, definicaoNormalizada, valores);
    }

    /**
     * Cena do material concreto (grupos de quadradinhos) desta categoria —
     * gerada pelo mesmo renderizador da cena abstrata (fabrica.obter(tipo)),
     * nunca por um componente de interface à parte (ver CLAUDE.md, "não
     * inventar" aplicado aqui como "não duplicar a fonte da verdade
     * visual"). {@code null} quando a categoria ainda não tem essa cena
     * implementada (RenderizadorDiagramaAditivo.criarCenaMaterialConcreto
     * default) — o chamador deve tratar null como "sem material concreto
     * disponível para esta categoria", nunca cair para um layout genérico
     * inventado.
     */
    public CenaDiagramaAditivo gerarMaterialConcreto(TipoSituacaoAditiva tipo, AreaDiagrama area,
            DefinicaoDiagramaAditivo definicao, int[] valores, String chavePapelAlvo) {
        RenderizadorDiagramaAditivo renderizador = fabrica.obter(tipo);
        if (renderizador == null) {
            return null;
        }
        DefinicaoDiagramaAditivo definicaoNormalizada =
                normalizadorRotulos.garantir(tipo, definicao,
                        ServicoLocalizacao.getInstancia());
        return renderizador.criarCenaMaterialConcreto(area, definicaoNormalizada, valores, chavePapelAlvo);
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
    /**
     * Palavras do enunciado marcadas/arrastáveis, via o mesmo
     * SegmentadorTextoSemantico já usado por Main.java
     * (inicializarElementosTexto) e por ServicoSorteioAtividadeWeb — uma só
     * fonte para as duas plataformas, em vez de cada uma re-tokenizar por
     * conta própria. Sem geometria (x/y/largura/altura): quem consome decide
     * a apresentação (ver PainelEditorNarrativa no desktop).
     */
    public List<SegmentoTextoSemantico> gerarElementosTexto(String enunciadoExibido,
            ResultadoInterpretacao interpretacao) {
        return SegmentadorTextoSemantico.segmentar(enunciadoExibido, interpretacao);
    }

    /**
     * Nova cena com as palavras do enunciado anexadas (ver
     * CenaDiagramaAditivo.comElementosTexto). {@code modelagemConcluida}
     * decide aqui — na cena, não em cada plataforma — se a edição da
     * narrativa é permitida nesta cena (ver permiteEditarNarrativa).
     */
    public CenaDiagramaAditivo comElementosTextoNarrativa(CenaDiagramaAditivo cena,
            String enunciadoExibido, ResultadoInterpretacao interpretacao,
            boolean modelagemConcluida) {
        if (cena == null) {
            return null;
        }
        return cena.comElementosTexto(
                gerarElementosTexto(enunciadoExibido, interpretacao),
                gerarVocabularioTextoNarrativa(),
                permiteEditarNarrativa(modelagemConcluida));
    }

    /** Vocabulário de apoio (candidatos a organizador da informação) — mesmo repertório do protótipo web. */
    public VocabularioTextoNarrativo gerarVocabularioTextoNarrativa() {
        return new VocabularioTextoNarrativo(VocabularioOrganizadoresInformacao.expressoes());
    }

    /**
     * Decide se o editor de narrativa (botão e painel de edição) deve ficar
     * disponível nesta cena. Hoje a única condição é a modelagem já ter
     * sido concluída, mas o ponto de decisão fica aqui — no gerador,
     * compartilhado por desktop e web — em vez de cada plataforma checar
     * isoladamente (ver LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md).
     * Barato o bastante para ser chamado a cada quadro/requisição.
     */
    public boolean permiteEditarNarrativa(boolean modelagemConcluida) {
        return modelagemConcluida;
    }

    /**
     * Decide se os controles do diagrama abstrato (botão de ajuda do
     * Vergnaud/complementar, restaurar, dica de posicionamento) devem ficar
     * ocultos porque o editor de narrativa está aberto e ocupa a mesma área
     * da tela — mesmo raciocínio de permiteEditarNarrativa: o ponto de
     * decisão fica aqui, não recalculado localmente em cada plataforma. Sem
     * isso, esses controles (componentes reais de interface, não pintados
     * na cena) permanecem visíveis na posição do diagrama que o editor
     * substituiu, e por serem componentes reais acabam desenhados por cima
     * do conteúdo pintado do editor.
     */
    public boolean deveOcultarControlesDiagrama(boolean editandoNarrativa) {
        return editandoNarrativa;
    }

    /**
     * Cena mínima (sem figuras/conectores) carregando só os elementos de
     * texto do enunciado e o sinalizador de edição — para quem, como
     * Main.java no editor de narrativa, ainda não tem uma cena-base
     * previamente gerada nesse ponto do fluxo.
     */
    public CenaDiagramaAditivo gerarCenaNarrativa(String enunciadoExibido,
            ResultadoInterpretacao interpretacao, boolean modelagemConcluida) {
        CenaDiagramaAditivo base = new CenaDiagramaAditivo(null, enunciadoExibido,
                java.util.Collections.<FiguraDiagrama>emptyList(),
                java.util.Collections.<ConectorDiagrama>emptyList(),
                EstadoFeedbackDiagrama.NEUTRO);
        return comElementosTextoNarrativa(base, enunciadoExibido, interpretacao, modelagemConcluida);
    }
}
