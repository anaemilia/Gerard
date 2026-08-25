package gerard.adaptacao.modelousuario;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.NivelSuporte;

/**
 * Projeção operacional de um diagnóstico armazenado.
 *
 * <p>Internalização e probabilidade continuam ausentes porque o modelo legado
 * guarda {@code false}/{@code 0.0} sem informar se vieram de um cálculo. Sem
 * proveniência publicada pelo Modelador, esses padrões não podem orientar uma
 * decisão adaptativa.</p>
 */
public final class DiagnosticoTarefaProjetado {

    private static final String MOTIVO_INTERNALIZACAO_AUSENTE =
            "internalização sem proveniência de cálculo publicada pelo Modelador";
    private static final String MOTIVO_PROBABILIDADE_AUSENTE =
            "probabilidade de conhecimento sem cálculo Bayesiano publicado";

    private final ValorProjetado<String> tarefa;
    private final ValorProjetado<String> regraDeAcao;
    private final ValorProjetado<NivelSuporte> suporte;
    private final ValorProjetado<Boolean> internalizado;
    private final ValorProjetado<Double> probabilidadeSaberConteudo;

    public DiagnosticoTarefaProjetado(DiagnosticoTarefa origem) {
        if (origem == null) {
            throw new IllegalArgumentException("diagnóstico não pode ser nulo");
        }
        this.tarefa = textoProjetado(origem.getTarefa(), "tarefa não registrada");
        this.regraDeAcao = textoProjetado(origem.getRegraDeAcao(), "regra de ação não registrada");
        this.suporte = origem.getSuporte() == null
                ? ValorProjetado.<NivelSuporte>ausente("suporte anterior não registrado")
                : ValorProjetado.presente(origem.getSuporte());
        this.internalizado = ValorProjetado.ausente(MOTIVO_INTERNALIZACAO_AUSENTE);
        this.probabilidadeSaberConteudo = ValorProjetado.ausente(MOTIVO_PROBABILIDADE_AUSENTE);
    }

    public ValorProjetado<String> getTarefa() { return tarefa; }
    public ValorProjetado<String> getRegraDeAcao() { return regraDeAcao; }
    public ValorProjetado<NivelSuporte> getSuporte() { return suporte; }
    public ValorProjetado<Boolean> getInternalizado() { return internalizado; }
    public ValorProjetado<Double> getProbabilidadeSaberConteudo() {
        return probabilidadeSaberConteudo;
    }

    private static ValorProjetado<String> textoProjetado(String valor, String motivo) {
        String normalizado = valor == null ? "" : valor.trim();
        return normalizado.isEmpty()
                ? ValorProjetado.<String>ausente(motivo)
                : ValorProjetado.presente(normalizado);
    }
}
