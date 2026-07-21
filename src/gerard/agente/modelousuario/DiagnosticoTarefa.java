package gerard.agente.modelousuario;

/**
 * Dimensão 5 do Modelo do Usuário (Quadro 5.60): diagnóstico de uma tentativa
 * de tarefa. É a entrada principal usada pelo Agente Modelador para inferir
 * regras (J48.PART + APRIORI). Ver gerard-modelo-usuario/SKILL.md e
 * gerard-ajuda-adaptativa/references/agente-modelador.md.
 *
 * Esta classe não calcula probabilidadeSaberConteudo (teorema de Bayes) nem
 * decide internalizado a partir dela — isso é responsabilidade do Agente
 * Modelador, que ainda não existe.
 */
public class DiagnosticoTarefa {
    private final String tarefa;
    private NivelSuporte suporte;
    private boolean internalizado;
    private double probabilidadeSaberConteudo;

    public DiagnosticoTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    public String getTarefa() {
        return tarefa;
    }

    public NivelSuporte getSuporte() {
        return suporte;
    }

    public void setSuporte(NivelSuporte suporte) {
        this.suporte = suporte;
    }

    public boolean isInternalizado() {
        return internalizado;
    }

    public void setInternalizado(boolean internalizado) {
        this.internalizado = internalizado;
    }

    public double getProbabilidadeSaberConteudo() {
        return probabilidadeSaberConteudo;
    }

    public void setProbabilidadeSaberConteudo(double probabilidadeSaberConteudo) {
        this.probabilidadeSaberConteudo = probabilidadeSaberConteudo;
    }
}
