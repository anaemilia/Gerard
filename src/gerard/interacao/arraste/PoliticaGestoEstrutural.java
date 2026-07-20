package gerard.interacao.arraste;

/** Regras de gesto para figuras estruturais do diagrama. */
public final class PoliticaGestoEstrutural {
    public boolean ehPressionamentoDeDuploClique(int quantidadeCliques) {
        return quantidadeCliques >= 2;
    }
}
