package gerard.adaptacao.modelousuario;

import gerard.agente.modelousuario.NivelComplexidadeTarefa;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/** Fotografia imutável da complexidade de tarefa conhecida por categoria. */
public final class NiveisTarefasProjetados {

    private final Map<TipoSituacaoAditiva, NivelComplexidadeTarefa> porCategoria;

    public NiveisTarefasProjetados(
            Map<TipoSituacaoAditiva, NivelComplexidadeTarefa> porCategoria) {
        if (porCategoria == null || porCategoria.isEmpty()) {
            throw new IllegalArgumentException("níveis de tarefa não podem ser vazios");
        }
        EnumMap<TipoSituacaoAditiva, NivelComplexidadeTarefa> copia =
                new EnumMap<TipoSituacaoAditiva, NivelComplexidadeTarefa>(TipoSituacaoAditiva.class);
        for (Map.Entry<TipoSituacaoAditiva, NivelComplexidadeTarefa> entrada
                : porCategoria.entrySet()) {
            if (entrada.getKey() == null || entrada.getValue() == null) {
                throw new IllegalArgumentException("categoria e nível de tarefa são obrigatórios");
            }
            copia.put(entrada.getKey(), entrada.getValue());
        }
        this.porCategoria = Collections.unmodifiableMap(copia);
    }

    public Map<TipoSituacaoAditiva, NivelComplexidadeTarefa> getPorCategoria() {
        return porCategoria;
    }

    public Optional<NivelComplexidadeTarefa> obter(TipoSituacaoAditiva categoria) {
        return Optional.ofNullable(porCategoria.get(categoria));
    }
}
