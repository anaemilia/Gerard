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
        // Bug relatado pela usuária em 2026-08-17 ("o diagrama está errado"
        // / "antigamente funcionava"): havia duas chamadas put() para a
        // mesma chave COMPOSICAO_TRANSFORMACOES aqui. A segunda (o
        // renderizador genérico de Transformação de Medidas, 3 elementos)
        // sobrescrevia silenciosamente a primeira (o renderizador correto
        // e já implementado desta categoria, 6 elementos: duas
        // transformações + a transformação composta, e os 3 estados
        // inicial/intermediário/final com a seta curva ligando inicial a
        // final) — produzindo o diagrama truncado visto por ela. Provável
        // artefato do refactor "consolidar seis categorias aditivas"
        // (2026-08-08). Corrigido removendo a segunda linha duplicada.
        renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES, new RenderizadorComposicaoTransformacoes());
        renderizadores.put(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, new RenderizadorTransformacaoRelacao());
        renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_RELACOES, new RenderizadorComposicaoRelacoes());
    }

    public RenderizadorDiagramaAditivo obter(TipoSituacaoAditiva tipo) {
        return renderizadores.get(tipo);
    }
}
