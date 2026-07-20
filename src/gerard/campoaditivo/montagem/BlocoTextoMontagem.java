package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

/**
 * Trecho textual usado na reconstrução de uma situação-problema.
 *
 * Os blocos corretos preservam literalmente fragmentos do enunciado curado.
 * Os blocos não compatíveis mantêm os mesmos personagens, contexto e valores,
 * mas alteram a relação semântica entre as quantidades.
 */
public final class BlocoTextoMontagem {
    private final String id;
    private final String texto;
    private final boolean correto;
    private final int ordemCorreta;
    private final TipoSituacaoAditiva categoriaSemantica;
    private final String papelSemantico;

    public BlocoTextoMontagem(String id, String texto, boolean correto, int ordemCorreta,
                              TipoSituacaoAditiva categoriaSemantica, String papelSemantico) {
        this.id = id == null ? "" : id;
        this.texto = texto == null ? "" : texto.trim();
        this.correto = correto;
        this.ordemCorreta = ordemCorreta;
        this.categoriaSemantica = categoriaSemantica;
        this.papelSemantico = papelSemantico == null ? "" : papelSemantico;
    }

    public String getId() { return id; }
    public String getTexto() { return texto; }
    public boolean isCorreto() { return correto; }
    public int getOrdemCorreta() { return ordemCorreta; }
    public TipoSituacaoAditiva getCategoriaSemantica() { return categoriaSemantica; }
    public String getPapelSemantico() { return papelSemantico; }

    @Override
    public String toString() {
        return texto;
    }
}
