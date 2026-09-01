package gerard.dominio.campoaditivo.situacao;

/** Participante explicitamente identificado pela curadoria humana. */
public final class ParticipanteNarrativo {
    private final String id;
    private final String nomeExibicao;

    public ParticipanteNarrativo(String id, String nomeExibicao) {
        this.id = FamiliaObjeto.obrigatorio(id, "id do participante não pode ser vazio");
        this.nomeExibicao = FamiliaObjeto.obrigatorio(
                nomeExibicao, "nome de exibição do participante não pode ser vazio");
    }

    public String getId() { return id; }
    public String getNomeExibicao() { return nomeExibicao; }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof ParticipanteNarrativo
                && id.equals(((ParticipanteNarrativo) outro).id));
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return nomeExibicao; }
}
