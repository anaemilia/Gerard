package gerard.campoaditivo.diagrama.servico;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.EnumMap;
import java.util.Map;

public class FabricaRenderizadoresDiagramaAditivo {
    private final Map<TipoSituacaoAditiva, RenderizadorDiagramaAditivo> renderizadores;

    public FabricaRenderizadoresDiagramaAditivo() {
        renderizadores = new EnumMap<TipoSituacaoAditiva, RenderizadorDiagramaAditivo>(TipoSituacaoAditiva.class);
        renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, new RenderizadorComposicaoMedidas());
        renderizadores.put(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, new RenderizadorTransformacaoMedidas());
        renderizadores.put(TipoSituacaoAditiva.COMPARACAO_MEDIDAS, new RenderizadorComparacaoMedidas());
        renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, new RenderizadorComposicaoTransformacoes());
        renderizadores.put(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, new RenderizadorTransformacaoMedidas());
        renderizadores.put(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, new RenderizadorTransformacaoRelacao());
        renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_RELACOES, new RenderizadorComposicaoRelacoes());
    }

    public RenderizadorDiagramaAditivo obter(TipoSituacaoAditiva tipo) {
        return renderizadores.get(tipo);
    }
}
