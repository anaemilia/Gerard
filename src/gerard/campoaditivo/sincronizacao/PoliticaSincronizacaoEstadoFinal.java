package gerard.campoaditivo.sincronizacao;

/** Decide semanticamente se um elemento habilita a sincronizacao do estado final. */
public final class PoliticaSincronizacaoEstadoFinal {

    public boolean aceita(boolean incognitaPrincipal, String chavePapel) {
        return incognitaPrincipal || "papel.estadoFinal".equals(chavePapel);
    }
}
